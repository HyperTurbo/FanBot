package babybot;

import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IVoiceChannel;
import sx.blah.discord.util.EmbedBuilder;
import sx.blah.discord.util.RequestBuffer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.*;

import org.apache.commons.lang3.math.NumberUtils;
import org.jsoup.Connection;
import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Original Class Created by declan on 04/04/2017.
 */
public class CommandHandler {

	// A static map of commands mapping from command string to the functional impl
	private static Map<String, Command> commandMap = new HashMap<>();

	// Statically populate the commandMap with the intended functionality
	// Might be better practise to do this from an instantiated objects constructor
	static {
		commandMap.put("h", (event, args) -> {
			commandMap.get("help").runCommand(event, args);

		});
		/**
		 * 
		 * This needs more documentation
		 * 
		 */
		commandMap.put("help", (event, args) -> {
			BotUtils.sendMessage(event.getChannel(), "Help Documentation.");

			EmbedBuilder builder = new EmbedBuilder();

			builder.withAuthorName("BabyBot");

			builder.withTitle("How To Use BabyBot");
			builder.appendField("Syntax is", "`;f <command> <arg1> <arg2> ...`", true);
			builder.appendField("Example", "`;f works 1589819`", true);

			event.getChannel().sendMessage(builder.build());

		});
		commandMap.put("w", (event, args) -> {
			commandMap.get("work").runCommand(event, args);

		});

		commandMap.put("works", (event, args) -> {
			commandMap.get("work").runCommand(event, args);

		});

		commandMap.put("work", (event, args) -> {
			args.remove(0);
			if (args.isEmpty()) {
				BotUtils.sendMessage(event.getChannel(),
						"Correct usage is ;f work <#work>. You can find that in the fic's URL, after /works/.");
			} else {
				Document doc = null;
				String title = "";
				String author = "";
				String ships = "";
				String url = "";

				try {
					if (args.get(0).matches("((http|https):\\/\\/archiveofourown.org\\/works\\/\\d*)")) {
						url = args.get(0);
					} else if (NumberUtils.isCreatable(args.get(0))) {
						url = "https://archiveofourown.org/works/" + args.get(0);
					}
				} catch (Exception e) {
					BotUtils.sendMessage(event.getChannel(),
							"Correct usage is ;f work <#work>. You can find that in the fic's URL, after /works/.");
					System.out.println(e.getStackTrace().toString());
				}

				try {
					doc = Jsoup.connect(url)
							.userAgent("Mozila")
							.followRedirects(true)
							.cookies(BotUtils.getCookies())
							.get();
				} catch (Exception e) {
					BotUtils.sendMessage(event.getChannel(), "Connection error to url: " + url);
					System.out.println(e.getStackTrace().toString());
				}

				title = DocUtils.getTitle(doc);
				author = DocUtils.getAuthor(doc);
				ships = DocUtils.getShips(doc);

				EmbedBuilder builder = new EmbedBuilder();

				builder.withTitle(title + " - " + author);
				builder.withUrl(url);
				builder.appendField("Relationship Tags", ships, true);
				event.getChannel().sendMessage(builder.build());

			}

		});

		commandMap.put("greet", (event, args) -> {
			BotUtils.sendMessage(event.getChannel(), "Hi, " + event.getAuthor());

		});

		commandMap.put("shutdown", (event, args) -> {
			BotUtils.sendMessage(event.getChannel(), "Bye, " + event.getAuthor());
			if (event.getGuild().getOwner() == event.getMessage().getAuthor()) {
				BotUtils.sendMessage(event.getChannel(), "lol jk idk how!");
			}
		});

	}

	@EventSubscriber
	public void onMessageReceived(MessageReceivedEvent event) {

		// Note for error handling, you'll probably want to log failed commands with a
		// logger or sout
		// In most cases it's not advised to annoy the user with a reply incase they
		// didn't intend to trigger a
		// command anyway, such as a user typing ?notacommand, the bot should not say
		// "notacommand" doesn't exist in
		// most situations. It's partially good practise and partially developer
		// preference

		// Given a message ";f arg1 arg2", argArray will contain [";f", "arg1", "arg"]
		String[] argArray = event.getMessage().getContent().split(" ");

		// First ensure at least the command and prefix is present, the arg length can
		// be handled by your command func
		if (argArray.length < 2)
			return;

		// Check if the first arg matches prefix defined in the utils class
		if (!argArray[0].equals(BotUtils.BOT_PREFIX))
			return;

		// Load the rest of the args in the array into a List for safer access
		List<String> argsList = new ArrayList<>(Arrays.asList(argArray));
		argsList.remove(0); // Remove the command

		// Instead of delegating the work to a switch, automatically do it via calling
		// the mapping if it exists

		if (commandMap.containsKey(argsList.get(0)))
			commandMap.get(argsList.get(0)).runCommand(event, argsList);

	}

}
