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

import org.apache.commons.collections4.ListUtils;
import org.heliosphere.thot.akka.chat.protocol.ChatMessageType;
import org.heliosphere.thot.akka.chat.protocol.data.ChatMessageData;

import com.heliosphere.athena.base.command.internal.ICommand;
import com.heliosphere.athena.base.command.internal.ICommandListener;
import com.heliosphere.athena.base.command.internal.exception.CommandException;
import com.heliosphere.athena.base.command.protocol.DefaultCommandCategoryType;
import com.heliosphere.athena.base.command.protocol.DefaultCommandCodeType;
import com.heliosphere.athena.base.command.response.ICommandResponse;
import com.heliosphere.athena.base.file.internal.FileException;
import com.heliosphere.athena.base.message.Message;
import com.heliosphere.athena.base.message.internal.IMessage;
import com.heliosphere.athena.base.message.protocol.data.DefaultMessageData;
import com.heliosphere.athena.base.terminal.CommandTerminal;

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
public class Terminal extends AbstractActor implements ICommandListener
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
	 * Normal command processor actor.
	 */
	private ActorRef normalCommandProcessor = null;

	/**
	 * Reference to the chat system actor.
	 */
	private ActorRef chatSystem = null;

	/**
	 * Static creation pattern for a {@link Terminal}.
	 * <hr>
	 * @param commandFileName Command file name.
	 * @return {@link Props}.
	 */
	public static Props props(final String commandFileName)
	{
		return Props.create(Terminal.class, commandFileName);
	}

	/**
	 * Creates a new terminal actor.
	 * <hr>
	 * @param commandFileName Command XML file name.
	 * @throws FileException Thrown in case an error occurred while trying to access the XML commands file.
	 */
	@SuppressWarnings("nls")
	public Terminal(final String commandFileName) throws FileException
	{
		try
		{
			terminal = new CommandTerminal(commandFileName);
			terminal.registerListener(this);
			terminal.start();

			// Create an actor to handle processing of normal ('/') commands.
			normalCommandProcessor = getContext().actorOf(NormalCommandProcessor.props(), "command-processor-normal");
			getContext().watch(normalCommandProcessor);

			// Contact the chat system supervisor and send him a message to get its time.
			ActorSelection selection = getContext().actorSelection("/user/chat-supervisor");
			selection.tell(Message.createRequest(ChatMessageType.QUERY_SERVER_TIME, null), getSelf());
			//chatSystem = getContext().actorSelection("akka://chatSystem/user/chat-supervisor*").anchor();
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
				.match(ICommand.class, command -> handleAndDispatchCommand(command))
				.match(IMessage.class, message -> handleAndDispatchMessage(message))
				.match(ICommandResponse.class, response -> handleResponse(response))
				.match(Status.Failure.class, failure -> handleFailure(failure))
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
	 * Handles and dispatch messages.
	 * <hr>
	 * @param message Message to process.
	 */
	@SuppressWarnings("nls")
	protected void handleAndDispatchMessage(final IMessage message)
	{
		LOG.info(String.format("Received message [category=%1s, type=%2s, sender=%3s]", message.getCategoryType(), message.getType(), getSender()));

		try
		{
			switch (message.getCategoryType())
			{
				case REQUEST:
					// So then it to the server side to be processed.
					chatSystem.tell(message, getSelf());
					break;

				case NOTIFICATION:
					// So then it to the server side to be processed.
					chatSystem.tell(message, getSelf());
					break;

				case REPLY:
					// Reply messages are coming from server side.
					handleReplyMessage(message);
					break;

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
	 * Handles reply {@link Message}.
	 * <hr>
	 * @param message Reply message to process.
	 * @throws Exception Thrown in case an error occurred while processing a message.
	 */
	@SuppressWarnings("nls")
	private final void handleReplyMessage(final IMessage message) throws Exception
	{
		switch ((ChatMessageType) message.getType())
		{
			case QUERY_SERVER_TIME:
				// This is the first request made to the server to get its reference.
				chatSystem = getSender();
				System.out.println("Server time is: " + ((DefaultMessageData) message.getContent()).getData());
				break;

			case REGISTER_USER:
				terminal.getTerminal().println(String.format("User: %1s is registered with chat system.", ((ChatMessageData) message.getContent()).getUserName()));
				break;

			case QUERY_WHO:
			case STATUS_AFK:

			case NONE:
			default:
				LOG.warning(this + " does not handle message (protocol) of type: " + message.getType());
				break;
		}

		// Resume the terminal so that end-user can continue working with it.
		terminal.resume();
	}

	/**
	 * Handles and dispatch (normal) commands.
	 * <hr>
	 * @param command Command to process.
	 */
	@SuppressWarnings("nls")
	protected void handleAndDispatchCommand(final ICommand command)
	{
		// Check the command type.
		if (command.getMetadata().getCategoryType() == DefaultCommandCategoryType.NORMAL)
		{
			int i = 0;
		}
		else
		{
			CommandException exception = new CommandException(String.format("Cannot process command: %1s, expected a 'normal' command category type!", command.getMetadata().getCategoryType()));
			getSender().tell(new Status.Failure(exception), getSelf());
		}
	}

	@Override
	public final void onCommand(final ICommand command)
	{
		// Pauses the terminal thread until the command response has been received.
		terminal.pause();

		switch ((DefaultCommandCategoryType) command.getMetadata().getCategoryType())
		{
			case NORMAL:
				handleCommandNormal(command);
				break;

			case ADMINISTRATION:
				handleCommandAdministration(command);
				break;

			case DEBUG:
				handleCommandDebug(command);
				break;

			case SUPER_ADMINISTRATION:
				handleCommandSuperAdministration(command);
				break;

			case SYSTEM:
				handleCommandSystem(command);
				break;

			default:
				handleCommandUnknown(command);
				break;
		}
	}

	/**
	 * Handles normal commands.
	 * <hr>
	 * @param command Command to process.
	 */
	protected void handleCommandNormal(final ICommand command)
	{
		normalCommandProcessor.tell(command, getSelf());
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
	 * Handles unknown commands.
	 * <hr>
	 * @param command Command to process.
	 */
	protected void handleCommandUnknown(final @NonNull ICommand command)
	{
		// Empty, must be overridden by subclasses.
	}

	private void onTerminated(final Terminated t)
	{
		LOG.info("Actor {} has been terminated", t.getActor());
	}
}
