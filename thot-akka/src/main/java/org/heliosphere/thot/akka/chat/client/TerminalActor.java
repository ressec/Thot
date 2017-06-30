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
package org.heliosphere.thot.akka.chat.client;

import java.awt.Color;
import java.time.LocalTime;
import java.util.Locale;

import org.apache.commons.collections4.ListUtils;
import org.heliosphere.thot.akka.chat.client.command.ChatCommandProtocol;
import org.heliosphere.thot.akka.chat.lobby.LobbyMessageProtocol;
import org.heliosphere.thot.akka.chat.room.RoomMessageProtocol;
import org.heliosphere.thot.akka.chat.supervisor.ChatSupervisorProtocol;
import org.heliosphere.thot.akka.chat.user.UserMessageProtocol;

import com.heliosphere.athena.base.command.internal.ICommand;
import com.heliosphere.athena.base.command.internal.ICommandListener;
import com.heliosphere.athena.base.command.internal.ICommandParameter;
import com.heliosphere.athena.base.command.internal.coordinator.CommandCoordinator;
import com.heliosphere.athena.base.command.internal.coordinator.ICommandCoordinator;
import com.heliosphere.athena.base.command.internal.exception.CommandNotFoundException;
import com.heliosphere.athena.base.command.processor.HelpCommandProcessor;
import com.heliosphere.athena.base.command.protocol.DefaultCommandCodeType;
import com.heliosphere.athena.base.command.protocol.DefaultCommandProtocol;
import com.heliosphere.athena.base.command.response.ICommandResponse;
import com.heliosphere.athena.base.file.internal.FileException;
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
	private Locale lobby = null;

	/**
	 * Current room.
	 */
	private String room = null;

	/**
	 * Underlying user for this chat client.
	 */
	private String user = null;

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
			terminal = new CommandTerminal(name, terminalConfigFilename, commandFileName);
			terminal.registerListener(this);

			output = new OutputTerminal("Discussion Console", "/config/terminal/terminal-discussion.properties");

			// Create a command coordinator to handle execution of pre-implemented commands.
			//coordinator = getContext().actorOf(CommandCoordinatorActor.create(terminal), "command-processor-normal");
			coordinator = new CommandCoordinator(terminal);

			// Register some pre-implemented commands.
			coordinator.registerExecutable(DefaultCommandProtocol.HELP, new HelpCommandProcessor(terminal.getInterpreter().getCommandDefinitions()));
			//coordinator.tell(new CommandCoordinatorProtocol.RegisterCommandProcessor(DefaultCommandProtocol.HELP, HelpCommandProcessor.class), getSelf());
			//getContext().watch(coordinator);

			// Contact the chat system supervisor and send him a message to get its time.
			ActorSelection selection = getContext().actorSelection("/user/chat-supervisor");
			selection.tell(new ChatSupervisorProtocol.InitiateConversation(), getSelf());

			terminal.start();
			output.start();
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
				//.match(ICommand.class, command -> handleAndDispatchCommand(command)) // Generic handler & dispatcher for commands.
				//.match(IMessage.class, message -> handleAndDispatchMessage(message))
				//.match(ICommandResponse.class, response -> handleResponse(response))
				.match(TerminalMessageProtocol.DisplayOnTerminal.class, message -> handleDisplayOnTerminal(message))
				.match(ChatSupervisorProtocol.ConversationInitiated.class, response -> handleConversationInitiated())
				.match(ChatSupervisorProtocol.UserRegistered.class, response -> handleUserRegistered(response))
				.match(LobbyMessageProtocol.LobbyList.class, response -> handleMessageLobbyList(response))
				.match(LobbyMessageProtocol.LobbyCreated.class, response -> handleMessageLobbyCreated(response))
				.match(LobbyMessageProtocol.LobbyDeleted.class, response -> handleMessageLobbyDeleted(response))
				.match(LobbyMessageProtocol.LobbyJoined.class, response -> handleMessageLobbyJoined(response))
				.match(RoomMessageProtocol.RoomList.class, response -> handleMessageRoomList(response))
				.match(RoomMessageProtocol.RoomCreated.class, response -> handleMessageRoomCreated(response))
				.match(RoomMessageProtocol.RoomJoined.class, response -> handleMessageRoomConnected(response))
				.match(RoomMessageProtocol.RoomLeft.class, response -> handleMessageRoomDisconnected(response))
				.match(UserMessageProtocol.UserList.class, response -> handleMessageUserList(response))
				.match(UserMessageProtocol.Said.class, message -> handleSaid(message))
				.match(Status.Failure.class, failure -> handleFailure(failure))
				.match(Exception.class, exception -> handleException(exception))
				.match(Terminated.class, this::onTerminated)
				.matchAny(any -> handleUnknown(any))
				.build();
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
	}

	/**
	 * Handles exceptions.
	 * <hr>
	 * @param exception Exception to handle.
	 */
	@SuppressWarnings("nls")
	private final void handleException(final Exception exception)
	{
		String message = String.format("Received an exception of type: %1s, cause is: %2s", exception.getClass().getSimpleName(), exception.getMessage());
		LOG.error(message);
		terminal.getTerminal().println(message);
		terminal.getTerminal().println();

		terminal.resume();
	}

	/**
	 * Handles a {@link org.heliosphere.thot.akka.chat.supervisor.ChatSupervisorProtocol.ConversationInitiated} message.
	 */
	private final void handleConversationInitiated()
	{
		chatSystem = getSender();
	}

	//	/**
	//	 * Handles and dispatch messages.
	//	 * <hr>
	//	 * @param message Message to process.
	//	 */
	//	@SuppressWarnings("nls")
	//	protected void handleAndDispatchMessage(final IMessage message)
	//	{
	//		LOG.info(String.format("Received message [category=%1s, type=%2s, sender=%3s]", message.getCategoryType(), message.getType(), getSender()));
	//
	//		try
	//		{
	//			switch (message.getCategoryType())
	//			{
	//				case REQUEST:
	//					// So then it to the server side to be processed.
	//					chatSystem.tell(message, getSelf());
	//					break;
	//
	//				case NOTIFICATION:
	//					// So then it to the server side to be processed.
	//					chatSystem.tell(message, getSelf());
	//					break;
	//
	//				case REPLY:
	//					// Reply messages are coming from server side.
	//					handleReplyMessage(message);
	//					break;
	//
	//				default:
	//					break;
	//			}
	//		}
	//		catch (Exception e)
	//		{
	//			// In case of a failure notify the end-user!
	//			terminal.getTerminal().println(String.format("An error occurred due to: %1s", e.getMessage()));
	//		}
	//	}

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

	@SuppressWarnings("nls")
	private final void handleLobbyCommand(final ICommand command)
	{
		ICommandParameter parameter = null;

		if (command.getParameter("list") != null)
		{
			parameter = command.getParameter("list");
			chatSystem.tell(new LobbyMessageProtocol.LobbyList(this.user, null), getSelf());
		}
		else if (command.getParameter("create") != null)
		{
			parameter = command.getParameter("create");
			Locale locale = new Locale((String) parameter.getValue());
			chatSystem.tell(new LobbyMessageProtocol.LobbyCreate(this.user, locale), getSelf());
		}
		else if (command.getParameter("delete") != null)
		{
			parameter = command.getParameter("delete");
			Locale locale = new Locale((String) parameter.getValue());
			chatSystem.tell(new LobbyMessageProtocol.LobbyDelete(this.user, locale), getSelf());
		}
		else if (command.getParameter("join") != null)
		{
			parameter = command.getParameter("join");
			Locale locale = new Locale((String) parameter.getValue());
			chatSystem.tell(new LobbyMessageProtocol.LobbyJoin(this.user, locale), getSelf());
		}
		else if (command.getParameter("leave") != null)
		{
			parameter = command.getParameter("leave");
			Locale locale = new Locale((String) parameter.getValue());
			chatSystem.tell(new LobbyMessageProtocol.LobbyLeave(this.user, locale), getSelf());
		}
		else
		{
			terminal.getTerminal().println("Error -> Unable to process command: " + command.getText());
			terminal.getTerminal().println();
			terminal.resume();
		}
	}

	@SuppressWarnings("nls")
	private final void handleRoomCommand(final ICommand command)
	{
		ICommandParameter parameter = null;

		if (command.getParameter("list") != null)
		{
			parameter = command.getParameter("list");
			lobbyProxy.tell(new RoomMessageProtocol.RoomList(this.user, lobby, null), getSelf());
		}
		else if (command.getParameter("create") != null)
		{
			parameter = command.getParameter("create");
			lobbyProxy.tell(new RoomMessageProtocol.RoomCreate(this.user, (String) parameter.getValue()), getSelf());
		}
		else if (command.getParameter("delete") != null)
		{
			parameter = command.getParameter("delete");
			lobbyProxy.tell(new RoomMessageProtocol.RoomDelete(this.user, (String) parameter.getValue()), getSelf());
		}
		else if (command.getParameter("join") != null)
		{
			parameter = command.getParameter("join");
			lobbyProxy.tell(new RoomMessageProtocol.RoomJoin(this.user, (String) parameter.getValue()), getSelf());
		}
		else if (command.getParameter("leave") != null)
		{
			parameter = command.getParameter("leave");
			roomProxy.tell(new RoomMessageProtocol.RoomLeave(this.user, (String) parameter.getValue()), getSelf());
		}
		else
		{
			// TODO Unknown parameter!
		}
	}

	@SuppressWarnings("nls")
	private final void handleUserCommand(final ICommand command)
	{
		ICommandParameter parameter = null;

		if (command.getParameter("list") != null)
		{
			parameter = command.getParameter("list");
			roomProxy.tell(new UserMessageProtocol.UserList(room, null), getSelf());
		}
		else if (command.getParameter("register") != null)
		{
			parameter = command.getParameter("register");
			chatSystem.tell(new ChatSupervisorProtocol.RegisterUser((String) parameter.getValue()), getSelf());
		}
		else if (command.getParameter("unregister") != null)
		{
			parameter = command.getParameter("unregister");
			lobbyProxy.tell(new RoomMessageProtocol.RoomDelete(this.user, (String) parameter.getValue()), getSelf());
		}
		else
		{
			// TODO Unknown parameter!
		}
	}

	@SuppressWarnings("nls")
	private final void handleSayCommand(final ICommand command)
	{
		ICommandParameter text = command.getParameter("text");

		if (text != null)
		{
			roomProxy.tell(new UserMessageProtocol.Say(user, (String) text.getValue()), getSelf());
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
			roomProxy.tell(new UserMessageProtocol.Whisper((String) user.getValue(), (String) text.getValue()), getSelf());
		}

		terminal.resume();
	}

	@Override
	public final void onCommand(final ICommand command)
	{
		// Pauses the terminal thread until the command response has been received.
		terminal.pause();

		if (command.getMetadata().getProtocolType() instanceof DefaultCommandProtocol)
		{
			handleDefaultCommandProtocol(command);
		}
		else if (command.getMetadata().getProtocolType() instanceof ChatCommandProtocol)
		{
			switch ((ChatCommandProtocol) command.getMetadata().getProtocolType())
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

				case SAY:
					handleSayCommand(command);
					break;

				case WHIPSER:
					handleWhisperCommand(command);
					break;

				default:
					break;
			}
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
				chatSystem.tell(new LobbyMessageProtocol.LobbyList(user, null), getSelf());
			}
		}
	}

	/**
	 * Handles a {@link org.heliosphere.thot.akka.chat.supervisor.ChatSupervisorProtocol.UserRegistered} message.
	 * <hr>
	 * @param response Message to handle.
	 */
	@SuppressWarnings("nls")
	private void handleUserRegistered(final ChatSupervisorProtocol.UserRegistered response)
	{
		this.user = response.getUser();

		terminal.setPrompt(String.format("Command (%1s):>", response.getUser()));
		terminal.getTerminal().println(String.format("User: %1s is registered with chat system.", response.getUser()));
		terminal.getTerminal().println();

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
	 * Handles a {@link org.heliosphere.thot.akka.chat.room.RoomMessageProtocol.RoomList} message.
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
	 * Handles a {@link org.heliosphere.thot.akka.chat.supervisor.LobbyMessageProtocol.LobbyCreated} message.
	 * <hr>
	 * @param response Message to handle.
	 */
	@SuppressWarnings("nls")
	private void handleMessageLobbyCreated(final LobbyMessageProtocol.LobbyCreated response)
	{
		terminal.getTerminal().println(String.format("Lobby: lobby-%1s has been created.", response.getLobby()));
		terminal.getTerminal().println();

		terminal.resume();
	}

	/**
	 * Handles a {@link org.heliosphere.thot.akka.chat.supervisor.LobbyMessageProtocol.LobbyDeleted} message.
	 * <hr>
	 * @param response Message to handle.
	 */
	@SuppressWarnings("nls")
	private void handleMessageLobbyDeleted(final LobbyMessageProtocol.LobbyDeleted response)
	{
		terminal.getTerminal().println(String.format("Lobby: %1s has been deleted.", response.getLobby()));
		terminal.getTerminal().println();

		terminal.resume();
	}

	/**
	 * Handles a {@link org.heliosphere.thot.akka.chat.lobby.LobbyMessageProtocol.LobbyJoined} message.
	 * <hr>
	 * @param response Message to handle.
	 */
	@SuppressWarnings("nls")
	private void handleMessageLobbyJoined(final LobbyMessageProtocol.LobbyJoined response)
	{
		lobbyProxy = getSender();
		lobby = response.getLobby();
		terminal.getTerminal().println(String.format("User: %1s has joined lobby: %2s.", response.getUser(), response.getLobby().toString()));
		terminal.getTerminal().println();

		output.setTitle(String.format("Chat window for user [%1s] on [lobby-%2s]", user, lobby.toString()));
		output.getTerminal().println(String.format("Welcome in: lobby-%2s", lobby.toString()));

		terminal.setPrompt(String.format("Command (%1s:%2s):>", response.getUser(), response.getLobby().toString()));

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
		terminal.getTerminal().println(String.format("Room: room-%1s has been created.", response.getRoom()));
		terminal.getTerminal().println();

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
		room = response.getRoom();
		terminal.getTerminal().println(String.format("User: %1s has joined room: %2s.", response.getUser(), response.getRoom()));
		terminal.getTerminal().println();

		output.setTitle(String.format("Chat window for user [%1s] on [lobby-%2s] in room [%3s]", user, lobby.toString(), room));
		output.getTerminal().println(String.format("Welcome in: room-%1s", room));

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
	 * Handles a {@link org.heliosphere.thot.akka.chat.supervisor.UserMessageProtocol.UserList} message.
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
			terminal.getTerminal().println(String.format("%1d existing user(s) in room: %2s...", response.getUsers().size(), response.getRoom()));
			for (String user : response.getUsers())
			{
				terminal.getTerminal().println(String.format("|   %1s", user));
			}
			terminal.getTerminal().println();
		}

		terminal.resume();
	}

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
	 * Handles unknown commands.
	 * <hr>
	 * @param command Unknown command to process.
	 */
	protected void handleCommandUnknown(final @NonNull ICommand command)
	{
		// Empty, must be overridden by subclasses.
	}

	@SuppressWarnings("nls")
	private void onTerminated(final Terminated t)
	{
		LOG.info("Actor {} has been terminated", t.getActor());
	}

	/**
	 * Handles a {@link TerminalMessageProtocol.DisplayOnTerminal} message.
	 * <hr>
	 * @param message Message to process.
	 */
	private void handleDisplayOnTerminal(final TerminalMessageProtocol.DisplayOnTerminal message)
	{
		terminal.getTerminal().print(message.getMessages());
		terminal.resume();
	}
}
