package babybot;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.RequestBuffer;

/**
 * Original Class Created by declan on 03/04/2017.
 */
class BotUtils {
	
    // Constants for use throughout the bot
    static String BOT_PREFIX = ";f";
	static Map<String,String> postData = new HashMap<>();
	static Map<String,String> cookies = new HashMap<>();

    // Initiates contact with Ao3 and starts a session
    
    // Retrieves current session data OR initiates process to grab new data and returns that
    static Map<String, String> getCookies() throws IOException{
    	if(postData.isEmpty()) {
    	Document doc = null;
    	String authenticity_token = "";
		String url = "https://archiveofourown.org/";
		Response loginForm = Jsoup.connect(url+"users/login")
			    .userAgent("Mozila")
			    .followRedirects(true)
			    .method(Method.GET)
			    .execute();
		
		doc = loginForm.parse();

		authenticity_token = doc.child(0).child(1).child(0).child(2).child(1).child(1).child(0).child(1).attr("value");
		
		postData.put("user[login]", "shardinspector");
		postData.put("user[password]", "262322548");
		postData.put("authenticity_token", authenticity_token);
		postData.put("user[remember_me]", "1");
		postData.put("commit", "Log In");
		postData.put("utf8", "&#x2713;");

		cookies.putAll(loginForm.cookies());
		loginForm = Jsoup.connect(url+"users/login")
			    .userAgent("Mozila")
			    .followRedirects(true)
			    .data(postData)
			    .cookies(cookies)
				.method(Method.POST)
				.timeout(10000)
				.execute();
		
		cookies = loginForm.cookies();
    	}
		return cookies;
		
	}
    // Handles the creation and getting of a IDiscordClient object for a token
    static IDiscordClient getBuiltDiscordClient(String token){

        // The ClientBuilder object is where you will attach your params for configuring the instance of your bot.
        // Such as withToken, setDaemon etc
        return new ClientBuilder()
                .withToken(token)
                .withRecommendedShardCount()
                .build();

    }

    // Helper functions to make certain aspects of the bot easier to use.
    static void sendMessage(IChannel channel, String message){

        // This might look weird but it'll be explained in another page.
        RequestBuffer.request(() -> {
            try{
                channel.sendMessage(message);
            } catch (DiscordException e){
                System.err.println("Message could not be sent with error: ");
                e.printStackTrace();
            }
        });

    }
    
    static void killBot() {
    	System.exit(0);
    }
}
