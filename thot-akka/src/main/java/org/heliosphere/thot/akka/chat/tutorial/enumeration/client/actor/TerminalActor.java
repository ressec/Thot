/*
 * Copyright(c) 2017 - Heliosphere Corp.
 * ---------------------------------------------------------------------------
 * This file is part of the Heliosphere's project which is licensed under the 
 * Apache license version 2 and use is subject to license terms.
 * You should have received a copy of the license with the project's artifact
 * binaries and/or sources.
 * 
 * License can be consulted at http://www.apache.org/licenses/LICENSE-2.0
 * ---------------------------------------------------------------------------
 */
package org.heliosphere.thot.akka.chat.tutorial.enumeration.client.actor;

import java.awt.Color;
import java.time.LocalTime;
import java.util.List;
import java.util.Locale;

import org.apache.commons.collections4.ListUtils;
import org.heliosphere.thot.akka.chat.client.command.ChatCommandDomainType;
import org.heliosphere.thot.akka.chat.client.command.ChatCommandProtocol;
import org.heliosphere.thot.akka.chat.server.lobby.LobbyMessageProtocol;
import org.heliosphere.thot.akka.chat.server.room.RoomMessageProtocol;
import org.heliosphere.thot.akka.chat.server.supervisor.ChatSupervisorProtocol;
import org.heliosphere.thot.akka.chat.server.user.UserMessageProtocol;
import org.heliosphere.thot.akka.chat.tutorial.enumeration.data.ILobby;
import org.heliosphere.thot.akka.chat.tutorial.enumeration.data.IRoom;
import org.heliosphere.thot.akka.chat.tutorial.enumeration.data.IUser;

import com.heliosphere.athena.base.command.internal.ICommand;
import com.heliosphere.athena.base.command.internal.ICommandListener;
import com.heliosphere.athena.base.command.internal.ICommandParameter;
import com.heliosphere.athena.base.command.internal.coordinator.ICommandCoordinator;
import com.heliosphere.athena.base.command.internal.exception.CommandException;
import com.heliosphere.athena.base.command.internal.exception.CommandNotFoundException;
import com.heliosphere.athena.base.command.internal.protocol.ICommandProtocolType;
import com.heliosphere.athena.base.command.processor.HelpCommandProcessor;
import com.heliosphere.athena.base.command.protocol.DefaultCommandCodeType;
import com.heliosphere.athena.base.command.protocol.DefaultCommandProtocol;
import com.heliosphere.athena.base.command.response.ICommandResponse;
import com.heliosphere.athena.base.file.internal.FileException;
import com.heliosphere.athena.base.message.Message;
import com.heliosphere.athena.base.message.internal.IMessage;
import com.heliosphere.athena.base.message.internal.protocol.MessageProtocolCategory;
import com.heliosphere.athena.base.message.protocol.DefaultMessageProtocolUsingClasses;
import com.heliosphere.athena.base.message.protocol.TestMessageProtocol;
import com.heliosphere.athena.base.message.protocol.TestMessageProtocolDomain;
import com.heliosphere.athena.base.message.protocol.TestMessageProtocolGroup;
import com.heliosphere.athena.base.terminal.CommandTerminal;
import com.heliosphere.athena.base.terminal.OutputTerminal;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.actor.Props;
import akka.actor.Status;
import akka.actor.Terminated;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import lombok.NonNull;

/**
 * A actor managing a {@code terminal / console}.
 * <p>
 * Terminal actor is responsible to create the actor tree to handle the processing of commands. Typically it will
 * receive commands that have been validated by the underlying terminal object. It will then dispatch the commands 
 * according to their type to a specialized actor. When it receives the command's response, it takes the appropriate 
 * action such as quitting or displaying the result of the command on the console.
 * <hr>
 * @author <a href="mailto:christophe.resse@gmail.com">Christophe Resse - Heliosphere</a>
 * @version 1.0.0
 */
public class TerminalActor extends AbstractActor implements ICommandListener
{
	/**
	 * Akka logging adapter.
	 */
	private final LoggingAdapter LOG = Logging.getLogger(getContext().getSystem(), this);

	/**
	 * Terminal.
	 */
	private CommandTerminal terminal = null;

	/**
	 * Output terminal for the discussion.
	 */
	private OutputTerminal output = null;

	/**
	 * Command coordinator actor.
	 */
	private ICommandCoordinator coordinator = null;

	/**
	 * Reference to the chat system actor.
	 */
	private ActorRef chatSystem = null;

	/**
	 * Reference to the lobby actor.
	 */
	private ActorRef lobbyProxy = null;

	/**
	 * Reference to the room actor.
	 */
	private ActorRef roomProxy = null;

	/**
	 * Current lobby.
	 */
	private ILobby lobby = null;

	/**
	 * Current room.
	 */
	private IRoom room = null;

	/**
	 * Underlying user for this chat client.
	 */
	private IUser user = null;

	/**
	 * Static creation pattern for a {@link TerminalActor}.
	 * <hr>
	 * @param name Terminal's session name.
	 * @param terminalConfigFilename Terminal configuration file name.
	 * @param commandFileName Command file name.
	 * @return {@link Props}.
	 */
	public static Props props(final String name, final String terminalConfigFilename, final String commandFileName)
	{
		return Props.create(TerminalActor.class, name, terminalConfigFilename, commandFileName);
	}

	/**
	 * Creates a new terminal actor.
	 * <hr>
	 * @param name Terminal's session name.
	 * @param terminalConfigFilename Terminal configuration file name.
	 * @param commandFileName Command XML file name.
	 * @throws FileException Thrown in case an error occurred while trying to access the XML commands file.
	 */
	@SuppressWarnings("nls")
	public TerminalActor(String name, String terminalConfigFilename, final String commandFileName) throws FileException
	{
		try
		{
			// Create the command terminal.
			terminal = new CommandTerminal(name, terminalConfigFilename, commandFileName);
			terminal.registerListener(this);

			// Create the chat window console (output only)
			output = new OutputTerminal("Discussion Console", "/config/terminal/terminal-discussion.properties");

			// Register some pre-implemented commands.
			terminal.getCoordinator().registerExecutable(new HelpCommandProcessor(terminal.getInterpreter().getCommandDefinitions()));

			// Contact the chat system supervisor and send him a message to get its time.
			//ActorSelection selection = getContext().actorSelection("/user/chat-supervisor");
			ActorSelection selection = getContext().actorSelection("akka.tcp://ChatSystem@127.0.0.1:2551/user/chat-supervisor");

			selection.tell(Message.createMessage(TestMessageProtocol.CONVERSATION_INITIATE), getSelf());
		}
		catch (FileException e)
		{
			LOG.error(String.format("Unable to load file: %1s due to: %2s", commandFileName, e.getMessage()));
		}
	}

	@Override
	public Receive createReceive()
	{
		return receiveBuilder()
				.match(IMessage.class, message -> handleAndDispatchMessage(message))
				.match(DefaultMessageProtocolUsingClasses.SubmitCommand.class, message -> handleSubmitCommand(message))
				.match(Status.Failure.class, failure -> handleFailure(failure))
				.match(Terminated.class, this::onTerminated)
				.matchAny(any -> handleUnknown(any)).build();
	}

	/**
	 * Handles unknown objects.
	 * <hr>
	 * @param any Object received.
	 */
	@SuppressWarnings("nls")
	private final void handleUnknown(Object any)
	{
		LOG.info(this + "Received an unknown object: " + any);
		terminal.appendToPane("Received an unknown object: " + any + "\n", Color.ORANGE);
		terminal.resume();
	}

	/**
	 * Handles failures.
	 * <hr>
	 * @param failure Failure.
	 */
	@SuppressWarnings("nls")
	private final void handleFailure(final Status.Failure failure)
	{
		LOG.error("Received a failure: " + failure.cause().getMessage());
		terminal.appendToPane("Failure: " + failure.cause().getMessage() + "\n\n", Color.ORANGE);
		terminal.resume();
	}

	/**
	 * Handles a {@link org.heliosphere.thot.akka.chat.server.supervisor.ChatSupervisorProtocol.ConversationInitiated} message.
	 */
	@SuppressWarnings("nls")
	private final void handleConversationInitiated()
	{
		chatSystem = getSender();
		terminal.appendToPane("Chat server has been contacted and has answered.\nPlease register with a user.\n\n", Color.WHITE);

		terminal.start();
		output.start();
	}

	/**
	 * Handles and dispatch incoming message.
	 * <hr>
	 * @param message Incoming message to handle.
	 */
	@SuppressWarnings("nls")
	protected void handleAndDispatchMessage(final IMessage message)
	{
		LOG.info(String.format("Received message [category=%1s, group=%2s, domain=%3s, sender=%4s]", message.getCategory(), message.getGroup(), message.getDomain(), getSender()));

		try
		{
			switch ((MessageProtocolCategory) message.getCategory())
			{
				case APPLICATION:
					handleApplicationMessage(message);
					break;

				case AUDIT:
				case SECURITY:
				case AUTHENTICATION:
				case SYSTEM:
				case BATCH:
				case INFO:

				default:
					break;
			}
		}
		catch (Exception e)
		{
			// In case of a failure notify the end-user!
			terminal.getTerminal().println(String.format("An error occurred due to: %1s", e.getMessage()));
		}
	}

	/**
	 * Handles and dispatch incoming application message.
	 * <hr>
	 * @param message Incoming message to handle.
	 */
	protected void handleApplicationMessage(final IMessage message)
	{
		// Category is: MessageProtocolCategory#APPLICATION
		switch ((TestMessageProtocolGroup) message.getGroup())
		{
			case CHAT:
				handleApplicationChatMessage(message);
				break;

			case MESSAGE:

			default:
				break;
		}
	}

	/**
	 * Handles and dispatch incoming application chat message.
	 * <hr>
	 * @param message Incoming message to handle.
	 */
	protected void handleApplicationChatMessage(final IMessage message)
	{
		// Group is: TestMessageProtocolGroup#CHAT
		switch ((TestMessageProtocolDomain) message.getDomain())
		{
			case CONVERSATION:
				handleApplicationChatConversationMessage(message);
				break;

			case LOBBY:
				handleApplicationChatConversationMessage(message);
				break;

			case MESSAGE:
			case ROOM:
			case USER:

			default:
				break;
		}
	}

	/**
	 * Handles and dispatch incoming application chat conversation message.
	 * <hr>
	 * @param message Incoming message to handle.
	 */
	@SuppressWarnings({ "incomplete-switch", "nls" })
	protected void handleApplicationChatConversationMessage(final IMessage message)
	{
		// Domain is: TestMessageProtocolDomain#CONVERSATION
		switch ((TestMessageProtocol) message.getProtocol())
		{
			case CONVERSATION_INITIATED:
				chatSystem = getSender();
				terminal.appendToPane("Chat server has been contacted and has answered.\nPlease register with a user.\n\n", Color.WHITE);

				output.start();
				terminal.start();
				break;

			default:
				break;
		}
	}

	/**
	 * Handles and dispatch incoming application chat lobby message.
	 * <hr>
	 * @param message Incoming message to handle.
	 */
	@SuppressWarnings({ "incomplete-switch", "nls" })
	protected void handleApplicationChatLobbyMessage(final IMessage message)
	{
		ILobby lobby = null;

		// Domain is: TestMessageProtocolDomain#LOBBY
		switch ((TestMessageProtocol) message.getProtocol())
		{
			case LOBBY_CREATED:
				lobby = (ILobby) message.getContent();
				terminal.appendToPane(String.format("Lobby: lobby-%1s has been created.\n\n", lobby.getName()), Color.WHITE);
				break;

			case LOBBY_DELETED:
				lobby = (ILobby) message.getContent();
				terminal.appendToPane(String.format("Lobby: %1s has been deleted.", message.getContent()), Color.WHITE);
				break;

			case LOBBY_JOINED:
				lobbyProxy = getSender();
				this.lobby = (ILobby) message.getContent();

				terminal.appendToPane(String.format("User: %1s has joined lobby-%2s.\n\n", user.getFirstName(), this.lobby.getName()), Color.WHITE);

				output.setTitle(String.format("Chat window for user [%1s] on [lobby-%2s]", user, this.lobby.getName()));
				output.appendToPane(String.format("Welcome %1s in: lobby-%2s\n\n", user.getFirstName(), this.lobby.getName()), Color.WHITE);

				terminal.setPrompt(String.format("Command (%1s:%2s):>", user.getFirstName(), this.lobby.getName()));

				output.getIO().getFrame().setVisible(true);
				terminal.getIO().getFrame().setVisible(true); // Give back focus to terminal window.
				terminal.resume();
				break;

			case LOBBY_LEFT:
				break;

			case LOBBY_LISTED:
				if (message.getContent() == null)
				{
					terminal.getTerminal().println("No lobby!");
				}
				else
				{
					List<ILobby> lobbies = (List<ILobby>) message.getContent();
					terminal.getTerminal().println(String.format("%1d existing lobby(ies):", lobbies.size()));
					for (ILobby element : lobbies)
					{
						terminal.getTerminal().println(String.format("|   %1d - %2s", element.getId(), element.getName()));
					}
					terminal.getTerminal().println();
				}

				break;

			default:
				break;
		}

		terminal.resume();
	}

	//	/**
	//	 * Handles reply {@link Message}.
	//	 * <hr>
	//	 * @param message Reply message to process.
	//	 * @throws Exception Thrown in case an error occurred while processing a message.
	//	 */
	//	@SuppressWarnings("nls")
	//	private final void handleReplyMessage(final IMessage message) throws Exception
	//	{
	//		switch ((ChatMessageType) message.getType())
	//		{
	//			case QUERY_SERVER_TIME:
	//				// This is the first request made to the server to get its reference.
	//				chatSystem = getSender();
	//				System.out.println("Server time is: " + ((DefaultMessageData) message.getContent()).getData());
	//				break;
	//
	//			case REGISTER_USER:
	//				break;
	//
	//			case QUERY_WHO:
	//			case STATUS_AFK:
	//
	//			case NONE:
	//			default:
	//				LOG.warning(this + " does not handle message (protocol) of type: " + message.getType());
	//				break;
	//		}
	//
	//		// Resume the terminal so that end-user can continue working with it.
	//		terminal.resume();
	//	}

	//	/**
	//	 * Handles and dispatch (normal) commands.
	//	 * <hr>
	//	 * @param command Command to process.
	//	 */
	//	@SuppressWarnings("nls")
	//	protected void handleAndDispatchCommand(final ICommand command)
	//	{
	//		if (command.getMetadata().getProtocolType() instanceof ChatCommandProtocol)
	//		{
	//			switch ((ChatCommandProtocol) command.getMetadata().getProtocolType())
	//			{
	//				case LOBBY:
	//					if (chatSystem != null && user != null)
	//					{
	//						handleLobbyCommand(command);
	//					}
	//					break;
	//
	//				case USER_REGISTER:
	//
	//				default:
	//					LOG.warning("Does not handle command (protocol) of type: " + command.getMetadata().getProtocolType());
	//					break;
	//			}
	//		}
	//
	//		//		// Check the command type.
	//		//		if (command.getMetadata().getProtocolCategory() == DefaultCommandCategoryType.NORMAL)
	//		//		{
	//		//		}
	//		//		else
	//		//		{
	//		//			CommandException exception = new CommandException(String.format("Cannot process command: %1s, expected a 'normal' command category type!", command.getMetadata().getProtocolCategory()));
	//		//			getSender().tell(new Status.Failure(exception), getSelf());
	//		//		}
	//	}

	/**
	 * @param command
	 * @throws CommandException
	 */
	@SuppressWarnings({ "nls", "incomplete-switch" })
	private final void handleMessageCommand(final ICommand command) throws CommandException
	{
		String text = null;
		String user = null;

		if (roomProxy == null)
		{
			throw new CommandException("Please join a room first!");
		}

		switch ((ChatCommandProtocol) command.getMetadata().getProtocolType())
		{
			case MESSAGE_SAY:
				text = (String) command.getParameter("text").getValue();
				if (text == null)
				{
					throw new CommandException("Cannot send a null message!");
				}

				//roomProxy.tell(Message.createMessage(TestMessageProtocol.MESSAGE_SAY, new TextMessage(text, this.user, null)), getSelf());
				break;

			case MESSAGE_WHISPER:
				text = (String) command.getParameter("text").getValue();
				user = (String) command.getParameter("user").getValue();
				if (user == null)
				{
					throw new CommandException("Recipient user name cannot be null!");
				}
				else if (text == null)
				{
					throw new CommandException("Cannot send a null message!");
				}
				//roomProxy.tell(new UserMessageProtocol.Whisper(user, text, this.user), getSelf());
				break;

			default:
				break;
		}

		terminal.resume();
	}

	/**
	 * @param command
	 */
	@SuppressWarnings("nls")
	private final void handleLobbyCommand(final ICommand command)
	{
		Locale locale = null;

		switch ((ChatCommandProtocol) command.getProtocol())
		{
			case LOBBY_LIST:
				//chatSystem.tell(new LobbyMessageProtocol.LobbyList(this.user, null), getSelf());
				break;

			case LOBBY_CREATE:
				locale = new Locale((String) command.getParameter("create").getValue());
				//chatSystem.tell(new LobbyMessageProtocol.LobbyCreate(this.user, locale), getSelf());
				break;

			case LOBBY_DELETE:
				locale = new Locale((String) command.getParameter("delete").getValue());
				//chatSystem.tell(new LobbyMessageProtocol.LobbyDelete(this.user, locale), getSelf());
				break;

			case LOBBY_JOIN:
				locale = new Locale((String) command.getParameter("join").getValue());
				//chatSystem.tell(new LobbyMessageProtocol.LobbyJoin(this.user, locale), getSelf());
				break;

			case LOBBY_LEAVE:
				locale = new Locale((String) command.getParameter("leave").getValue());
				//chatSystem.tell(new LobbyMessageProtocol.LobbyLeave(this.user, locale), getSelf());
				break;

			default:
				terminal.getTerminal().println("Error -> Unable to process command: " + command.getText());
				terminal.getTerminal().println();
				terminal.resume();
				break;
		}
	}

	/**
	 * @param command
	 * @throws CommandException
	 */
	@SuppressWarnings({ "nls", "incomplete-switch" })
	private final void handleRoomCommand(final ICommand command) throws CommandException
	{
		String room = null;

		switch ((ChatCommandProtocol) command.getProtocol())
		{
			case ROOM_LIST:
				if (lobbyProxy == null)
				{
					throw new CommandException("You must have joined a lobby first before querying for rooms!");
				}
				//lobbyProxy.tell(new RoomMessageProtocol.RoomList(this.user, lobby, null), getSelf());
				break;

			case ROOM_CREATE:
				room = (String) command.getParameter("create").getValue();
				if (lobbyProxy == null)
				{
					throw new CommandException("You must have joined a lobby first to create a room!");
				}
				//lobbyProxy.tell(new RoomMessageProtocol.RoomCreate(this.user, room), getSelf());
				break;

			case ROOM_DELETE:
				room = (String) command.getParameter("delete").getValue();
				if (lobbyProxy == null)
				{
					throw new CommandException("You must have joined a lobby first to delete a room!");
				}
				//lobbyProxy.tell(new RoomMessageProtocol.RoomDelete(this.user, room), getSelf());
				break;

			case ROOM_JOIN:
				room = (String) command.getParameter("join").getValue();
				if (lobbyProxy == null)
				{
					throw new CommandException("You must have joined a lobby first to join a room!");
				}
				//lobbyProxy.tell(new RoomMessageProtocol.RoomJoin(this.user, room), getSelf());
				break;

			case ROOM_LEAVE:
				room = (String) command.getParameter("leave").getValue();
				if (roomProxy == null)
				{
					throw new CommandException("You must have joined a room first to leave it!");
				}
				//roomProxy.tell(new RoomMessageProtocol.RoomLeave(this.user, room), getSelf());
				break;
		}
	}

	@SuppressWarnings({ "nls", "incomplete-switch" })
	private final void handleUserCommand(final ICommand command) throws CommandException
	{
		String user = null;

		switch ((ChatCommandProtocol) command.getProtocol())
		{
			case USER_LIST:
				if (roomProxy == null)
				{
					throw new CommandException("You must have joined a room first before querying for users!");
				}

				//roomProxy.tell(new UserMessageProtocol.UserList(room, null), getSelf());
				break;

			case USER_REGISTER:
				user = (String) command.getParameter("register").getValue();
				chatSystem.tell(new ChatSupervisorProtocol.RegisterUser(user), getSelf());
				break;

			case USER_UNREGISTER:
				if (lobbyProxy == null)
				{
					throw new CommandException("You must have joined a lobby first to delete a room!");
				}

				//lobbyProxy.tell(new ChatSupervisorProtocol.UnregisterUser(this.user), getSelf());
				break;
		}
	}

	@SuppressWarnings("nls")
	private final void handleSayCommand(final ICommand command)
	{
		ICommandParameter text = command.getParameter("text");

		if (text != null)
		{
			//roomProxy.tell(new UserMessageProtocol.Say(user, (String) text.getValue()), getSelf());
		}
		else
		{
			// TODO Throw exception as message!
		}

		terminal.resume();
	}

	@SuppressWarnings("nls")
	private final void handleWhisperCommand(final ICommand command)
	{
		ICommandParameter user = command.getParameter("user");
		ICommandParameter text = command.getParameter("text");

		if (user == null)
		{
			// TODO Throw Exception message!
		}
		else if (text == null)
		{
			// TODO Throw Exception message!
		}
		else
		{
			//roomProxy.tell(new UserMessageProtocol.Whisper((String) user.getValue(), (String) text.getValue(), this.user), getSelf());
		}

		terminal.resume();
	}

	@SuppressWarnings("nls")
	@Override
	public final void onCommand(final ICommand command)
	{
		// Pauses the terminal thread until the command response has been received.
		terminal.pause();

		try
		{
			// Before issuing a command, the end-user must register its user name.
			if (user == null && command.getProtocol() != ChatCommandProtocol.USER_REGISTER)
			{
				throw new CommandException("Please register a user before trying to issue a command!");
			}

			if (command.getProtocol() instanceof DefaultCommandProtocol)
			{
				handleDefaultCommandProtocol(command);
			}
			else if (command.getProtocol() instanceof ChatCommandProtocol)
			{
				switch ((ChatCommandDomainType) ((ICommandProtocolType) command.getProtocol()).getDomain())
				{
					case USER:
						handleUserCommand(command);
						break;

					case LOBBY:
						handleLobbyCommand(command);
						break;

					case ROOM:
						handleRoomCommand(command);
						break;

					case MESSAGE:
						handleMessageCommand(command);
						break;

					default:
						break;
				}
			}
		}
		catch (CommandException e)
		{
			terminal.appendToPane(String.format("Error-> %1s\n\n", e.getMessage()), Color.ORANGE);
			terminal.resume();
		}
	}

	/**
	 * Handles command from protocol: {@link DefaultCommandProtocol}.
	 * <hr>
	 * @param command Command to handle.
	 */
	@SuppressWarnings("nls")
	private final void handleDefaultCommandProtocol(final ICommand command)
	{
		switch ((DefaultCommandProtocol) command.getMetadata().getProtocolType())
		{
			case HELP:
				// This command is handled by a pre-defined command processor.
				try
				{
					coordinator.execute(command);
				}
				catch (CommandNotFoundException e)
				{
					terminal.appendToPane(String.format("[ERROR] %1s\n", e.getMessage()), Color.ORANGE);
				}
				finally
				{
					terminal.resume();
				}
				break;

			case AFK:
				break;

			case QUIT:
				break;

			default:
				handleCommandUnknown(command);
				break;
		}
	}

	//	/**
	//	 * Handles command from protocol: {@link ChatCommandProtocol}.
	//	 * <hr>
	//	 * @param command Command to handle.
	//	 */
	//	private final void handleChatCommandProtocol(final ICommand command)
	//	{
	//		switch ((ChatCommandProtocol) command.getMetadata().getProtocolType())
	//		{
	//			case USER_REGISTER:
	//				handleCommandUserRegister(command);
	//				break;
	//
	//			case LOBBY_LIST:
	//				handleCommandLobbyList(command);
	//				break;
	//
	//			case LOBBY_CONNECT:
	//			case LOBBY_CREATE:
	//			case LOBBY_DELETE:
	//
	//			default:
	//				handleCommandUnknown(command);
	//				break;
	//		}
	//	}

	/**
	 * Handles normal commands.
	 * <hr>
	 * @param command Command to process.
	 */
	protected void handleCommandNormal(final ICommand command)
	{
		//coordinator.tell(command, getSelf());
	}

	/**
	 * Handles administration commands.
	 * <hr>
	 * @param command Command to process.
	 */
	protected void handleCommandAdministration(final @NonNull ICommand command)
	{
		// Empty, must be overridden by subclasses.
	}

	/**
	 * Handles debug commands.
	 * <hr>
	 * @param command Command to process.
	 */
	protected void handleCommandDebug(final @NonNull ICommand command)
	{
		// Empty, must be overridden by subclasses.
	}

	/**
	 * Handles super administration commands.
	 * <hr>
	 * @param command Command to process.
	 */
	protected void handleCommandSuperAdministration(final @NonNull ICommand command)
	{
		// Empty, must be overridden by subclasses.
	}

	/**
	 * Handles system commands.
	 * <hr>
	 * @param command Command to process.
	 */
	protected void handleCommandSystem(final @NonNull ICommand command)
	{
		// Empty, must be overridden by subclasses.
	}

	/**
	 * Handles a command response.
	 * <hr>
	 * @param response Command response to handle.
	 */
	@SuppressWarnings("nls")
	protected void handleResponse(final @NonNull ICommandResponse response)
	{
		switch ((DefaultCommandCodeType) response.getOrder())
		{
			case DISPLAY_TERMINAL:

				// Display the status of the command execution then the exceptions and then the messages.
				terminal.getTerminal().println("[status: " + response.getStatus() + " for command: " + response.getCommand().getMetadata().getName() + "]");

				for (Exception e : ListUtils.emptyIfNull(response.getExceptions()))
				{
					terminal.getTerminal().println("   Error: " + e.getMessage());
				}

				for (String element : ListUtils.emptyIfNull(response.getMessages()))
				{
					terminal.getTerminal().println(element);
				}
				break;

			case QUIT:
				getContext().system().terminate();
				terminal.getTerminal().println("System is shutting down...");
				break;

			default:
				break;
		}

		// Resumes the terminal thread.
		terminal.resume();
	}

	/**
	 * Handles {@link ChatCommandProtocol#LOBBY_LIST} command.
	 * <hr>
	 * @param command Command to handle.
	 */
	private void handleCommandLobbyList(final ICommand command)
	{
		if (chatSystem != null)
		{
			if (user != null)
			{
				//chatSystem.tell(new LobbyMessageProtocol.LobbyList(user, null), getSelf());
			}
		}
	}

	/**
	 * Handles a {@link org.heliosphere.thot.akka.chat.server.supervisor.ChatSupervisorProtocol.UserRegistered} message.
	 * <hr>
	 * @param response Message to handle.
	 */
	@SuppressWarnings("nls")
	private void handleUserRegistered(final ChatSupervisorProtocol.UserRegistered response)
	{
		//this.user = response.getUser();

		terminal.setPrompt(String.format("Command (%1s):>", response.getUser()));
		terminal.appendToPane(String.format("User: %1s is registered with chat system. \n\n", response.getUser()), Color.WHITE);

		terminal.resume();
	}

	/**
	 * Handles a {@link org.heliosphere.thot.akka.chat.supervisor.ChatSupervisorProtocol.LobbyList} message.
	 * <hr>
	 * @param response Message to handle.
	 */
	@SuppressWarnings("nls")
	private void handleMessageLobbyList(final LobbyMessageProtocol.LobbyList response)
	{
		if (response.getLobbies().isEmpty())
		{
			terminal.getTerminal().println("No lobby!");
		}
		else
		{
			terminal.getTerminal().println(String.format("%1d existing lobby(ies):", response.getLobbies().size()));
			for (Locale locale : response.getLobbies())
			{
				terminal.getTerminal().println(String.format("|   %1s", locale.toString()));
			}
			terminal.getTerminal().println();
		}

		terminal.resume();
	}

	/**
	 * Handles a {@link org.heliosphere.thot.akka.chat.server.room.RoomMessageProtocol.RoomList} message.
	 * <hr>
	 * @param response Message to handle.
	 */
	@SuppressWarnings("nls")
	private void handleMessageRoomList(final RoomMessageProtocol.RoomList response)
	{
		if (response.getRooms().isEmpty())
		{
			terminal.getTerminal().println("No room for lobby: lobby-" + response.getLobby());
			terminal.getTerminal().println();
		}
		else
		{
			terminal.getTerminal().println(String.format("%1d existing room(s):", response.getRooms().size()));
			for (String room : response.getRooms())
			{
				terminal.getTerminal().println(String.format("|   %1s", room));
			}
			terminal.getTerminal().println();
		}

		terminal.resume();
	}

	/**
	 * Handles a {@link org.heliosphere.thot.akka.chat.server.lobby.LobbyMessageProtocol.LobbyCreated} message.
	 * <hr>
	 * @param response Message to handle.
	 */
	@SuppressWarnings("nls")
	private void handleMessageLobbyCreated(final LobbyMessageProtocol.LobbyCreated response)
	{
		terminal.appendToPane(String.format("Lobby: lobby-%1s has been created.\n\n", response.getLobby()), Color.WHITE);

		terminal.resume();
	}

	/**
	 * Handles a {@link org.heliosphere.thot.akka.chat.server.lobby.LobbyMessageProtocol.LobbyDeleted} message.
	 * <hr>
	 * @param response Message to handle.
	 */
	@SuppressWarnings("nls")
	private void handleMessageLobbyDeleted(final LobbyMessageProtocol.LobbyDeleted response)
	{
		terminal.appendToPane(String.format("Lobby: %1s has been deleted.", response.getLobby()), Color.WHITE);

		terminal.resume();
	}

	/**
	 * Handles a {@link org.heliosphere.thot.akka.chat.server.lobby.LobbyMessageProtocol.LobbyJoined} message.
	 * <hr>
	 * @param response Message to handle.
	 */
	@SuppressWarnings("nls")
	private void handleMessageLobbyJoined(final LobbyMessageProtocol.LobbyJoined response)
	{
		lobbyProxy = getSender();
		//lobby = response.getLobby();

		terminal.appendToPane(String.format("User: %1s has joined lobby-%2s.\n\n", response.getUser(), response.getLobby().toString()), Color.WHITE);

		output.setTitle(String.format("Chat window for user [%1s] on [lobby-%2s]", user, lobby.toString()));
		output.appendToPane(String.format("Welcome %1s in: lobby-%2s\n\n", response.getUser(), lobby.toString()), Color.WHITE);

		terminal.setPrompt(String.format("Command (%1s:%2s):>", response.getUser(), response.getLobby().toString()));

		output.getIO().getFrame().setVisible(true);
		terminal.getIO().getFrame().setVisible(true); // Give back focus to terminal window.
		terminal.resume();
	}

	/**
	 * Handles a {@link org.heliosphere.thot.akka.chat.supervisor.ChatSupervisorProtocol.RoomCreated} message.
	 * <hr>
	 * @param response Message to handle.
	 */
	@SuppressWarnings("nls")
	private void handleMessageRoomCreated(final RoomMessageProtocol.RoomCreated response)
	{
		terminal.appendToPane(String.format("Room: room-%1s has been created.\n\n", response.getRoom()), Color.WHITE);

		terminal.resume();
	}

	/**
	 * Handles a {@link org.heliosphere.thot.akka.chat.supervisor.ChatSupervisorProtocol.RoomJoined} message.
	 * <hr>
	 * @param response Message to handle.
	 */
	@SuppressWarnings("nls")
	private void handleMessageRoomConnected(final RoomMessageProtocol.RoomJoined response)
	{
		roomProxy = getSender();
		//room = response.getRoom();
		terminal.appendToPane(String.format("User: %1s has joined room: %2s on lobby-%3s.\n\n", response.getUser(), response.getRoom(), lobby), Color.WHITE);

		output.setTitle(String.format("Chat window for user [%1s] on [lobby-%2s] in room [%3s]", user, lobby.toString(), room));
		output.appendToPane(String.format("Welcome %1s in room %2s.\n\n", response.getUser(), response.getRoom()), Color.WHITE);

		terminal.setPrompt(String.format("Command (%1s:%2s@%3s):>", response.getUser(), lobby.toString(), response.getRoom()));

		terminal.resume();
	}

	/**
	 * Handles a {@link org.heliosphere.thot.akka.chat.supervisor.ChatSupervisorProtocol.RoomLeft} message.
	 * <hr>
	 * @param response Message to handle.
	 */
	@SuppressWarnings("nls")
	private void handleMessageRoomDisconnected(final RoomMessageProtocol.RoomLeft response)
	{
		roomProxy = null;
		room = null;
		terminal.getTerminal().println(String.format("User: %1s has left room: %2s.", response.getUser(), response.getRoom()));
		terminal.getTerminal().println();

		terminal.setPrompt(String.format("Command (%1s:%2s):>", response.getUser(), lobby.toString()));

		terminal.resume();
	}

	/**
	 * Handles a {@link org.heliosphere.thot.akka.chat.server.user.UserMessageProtocol.UserList} message.
	 * <hr>
	 * @param response Message to handle.
	 */
	@SuppressWarnings("nls")
	private void handleMessageUserList(final UserMessageProtocol.UserList response)
	{
		if (response.getUsers().isEmpty())
		{
			terminal.getTerminal().println("No user in room: " + response.getRoom());
			terminal.getTerminal().println();
		}
		else
		{
			terminal.getTerminal().println(String.format("%1d existing user(s) in room: %2s...", Integer.valueOf(response.getUsers().size()), response.getRoom()));
			for (String user : response.getUsers())
			{
				terminal.getTerminal().println(String.format("|   %1s", user));
			}
			terminal.getTerminal().println();
		}

		terminal.resume();
	}

	//	private final void executeAndStart()
	//	{
	//		// Do we have some submitted commands to execute first?
	//		if (terminal.hasCommandToSubmit()) 
	//		{
	//			terminal.doSubmitCommand();			
	//		}
	//		
	//		// User can start entering some commands.
	//		terminal.start();
	//		output.start();
	//
	//	}

	/**
	 * Handles a {@code said} message.
	 * <hr>
	 * @param message Message to handle.
	 */
	private void handleSaid(final UserMessageProtocol.Said message)
	{
		output.printSay(LocalTime.now().toString(), message.getUser(), message.getMessage());
	}

	/**
	 * Handles a {@code whispered} message.
	 * <hr>
	 * @param message Message to handle.
	 */
	private void handleWhispered(final UserMessageProtocol.Whispered message)
	{
		output.printWhisper(LocalTime.now().toString(), message.getUser(), message.getMessage(), message.getSender());
	}

	/**
	 * Handles a {@code submit command} message.
	 * <hr>
	 * @param message Message to handle.
	 */
	private void handleSubmitCommand(final DefaultMessageProtocolUsingClasses.SubmitCommand message)
	{
		terminal.submitCommand(message);
	}

	/**
	 * Handles unknown commands.
	 * <hr>
	 * @param command Unknown command to process.
	 */
	protected void handleCommandUnknown(final @NonNull ICommand command)
	{
		// Empty, must be overridden by subclasses.
	}

	/**
	 * Called when a child actor is terminated.
	 * <hr>
	 * @param message Message. 
	 */
	@SuppressWarnings("nls")
	private void onTerminated(final Terminated message)
	{
		LOG.info("Actor {} has been terminated", message.getActor());
	}
}
