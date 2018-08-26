package tutorial.Ricky;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.security.auth.login.LoginException;

import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import net.dv8tion.jda.core.managers.GuildController;

public class RickyReloaded extends ListenerAdapter {
	
	/**
	 * Wes = 321798967669030912L
	 * Jared =
	 */
	private static final long WES_SNOWFLAKE = 321798967669030912L;
	private static final long[] advanced_commands_auth = {WES_SNOWFLAKE};
	public static final char command_symbol = '$';
	public static String[] quotes;
    private static final String FEED_URL = "https://www.youtube.com/feeds/videos.xml?channel_id=UCkOlmd_lMI9YHRcN1ffKbyQ";
    private static List<String> seen;
    private static Random rand;
    private static String quotes_path;

	public static void main(String[] args) throws IOException, ClassNotFoundException, LoginException, IllegalArgumentException, InterruptedException, RateLimitedException {
		Properties bot_conf = loadConfig(args[0]);
		RickyReloaded.quotes_path = bot_conf.getProperty("quotes");
		RickyReloaded.rand = new Random();
    	
   	 	//Load seen videos from disk
		try {
    	   	ObjectInputStream ois = new ObjectInputStream(new FileInputStream("retardo.obj"));
    	   	RickyReloaded.seen = (List<String>) ois.readObject();
           	ois.close();
       	} catch(FileNotFoundException e) {
       		//if the file doesn't exist just create a new ArrayList
       		RickyReloaded.seen = new ArrayList<String>();
       	}
       
		load_quotes_from_file();
    	
   		final JDA rickyBot = new JDABuilder(AccountType.BOT).setToken(bot_conf.getProperty("ricky")).buildBlocking();
        rickyBot.addEventListener(new RickyReloaded());
        
        Timer timer = new Timer ();
        TimerTask rickyVideoFeedUpdate = new TimerTask () {
            @Override
            public void run () {
            	TextChannel chan = rickyBot.getTextChannelsByName("cookingwith_rick", true).get(0);
            	RickyReloaded.readRSSFeed(FEED_URL, chan);
                //save seen list to disk
                ObjectOutputStream oos;
				try {
					oos = new ObjectOutputStream(new FileOutputStream("retardo.obj"));
					oos.writeObject(RickyReloaded.seen);
	                oos.close();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
            }
        };
        
        // schedule the task to run starting now and then every 30 min...
        timer.schedule (rickyVideoFeedUpdate, 0l, 1000*60*30);
	}
	
    @Override
    public void onMessageReceived(MessageReceivedEvent e) {
    	String message = e.getMessage().getContent();
    	if(message.length() == 0)
    			return;
    	MessageChannel objChannel = e.getChannel();
    	User objUser = e.getAuthor();
    	Guild objGuild = e.getGuild();
    	JDA ricardo = e.getJDA();
    	
    	//Use this to find out user unique ID
    	//System.out.println(objUser.getName() + ": " +objUser.getIdLong());
    	
    	if(message.charAt(0) == command_symbol) {
    		handleCommand(message, objChannel, objUser, objGuild, ricardo);
    		return;
    	}
    	
    	float chance = RickyReloaded.rand.nextFloat();

    	if(objChannel.getName().equalsIgnoreCase("general_discussion")){
    		if (chance <= 0.05f){
    			int randomIndex = RickyReloaded.rand.nextInt(RickyReloaded.quotes.length);
    			objChannel.sendMessage(RickyReloaded.quotes[randomIndex]).queue();
    		}
    	}
    }
    
	private void handleCommand(String message, MessageChannel objChannel, User objUser, Guild objGuild, JDA richard) {
		String[] strArgs = message.substring(1).split(" ");
		
		if(strArgs[0].equalsIgnoreCase("quote")){
			Random rand = new Random();
			int randomIndex = rand.nextInt(RickyReloaded.quotes.length);
			objChannel.sendMessage(RickyReloaded.quotes[randomIndex] + " " + objUser.getAsMention()).queue();
			
		} else if(strArgs[0].equals("subscribe")){	// pastes the link to subscribe in the chat
			objChannel.sendMessage("https://www.youtube.com/channel/UCkOlmd_lMI9YHRcN1ffKbyQ?sub_confirmation=1 " + objUser.getAsMention() + " Now like and subscribe!").queue();
			
		} else if(strArgs[0].equalsIgnoreCase("say")) {
			TextChannel chan = richard.getTextChannelsByName("general_discussion", true).get(0);
			chan.sendMessage(message.substring(message.indexOf(" "))).queue();
			
		} else if(strArgs[0].equalsIgnoreCase("commands")) {	// sends messages of the commands the bot uses
			String msg = "In the #help channel:\n\n";
			msg += "$DJ    (for the DJ role, @DJ )\n";
			msg += "$NSFW    (for the NSFW role, @NSFW )\n";
			msg += "$Meme-ber    (for the Meme-ber role, @Meme-ber )\n";
			msg += "$Media   (for the Media role, @Media )\n";
			msg += "$Games    (for the Games role, @Games )\n";
			msg += "$Member    (to get Member status in the server, @Member )\n\n";
			msg += "In any channel $subscibe will generate a link that allows you to subscribe to the Cookingwith Rick YouTube channel\n";
			msg += "$quote will grab a random quote that Rick has said himself\n\n";			
			objChannel.sendMessage(msg).queue();
		} else if(strArgs[0].equals("DJ") && objChannel.getName().equalsIgnoreCase("help")) {
			setRole("DJ", objGuild, objUser, objChannel);
			
		} else if(strArgs[0].equals("Meme-ber") && objChannel.getName().equalsIgnoreCase("help")) {
			setRole("Meme-ber", objGuild, objUser, objChannel);
			
		} else if(strArgs[0].equals("NSFW") && objChannel.getName().equalsIgnoreCase("help")) {
			setRole("NSFW", objGuild, objUser, objChannel);
			
		} else if(strArgs[0].equals("Media") && objChannel.getName().equalsIgnoreCase("help")) {
			setRole("Media", objGuild, objUser, objChannel);
			
		} else if(strArgs[0].equals("Games") && objChannel.getName().equalsIgnoreCase("help")) {
			setRole("Games", objGuild, objUser, objChannel);
			
		} else if(strArgs[0].equals("Member") && objChannel.getName().equalsIgnoreCase("help")) {
			setRole("Member", objGuild, objUser, objChannel);
			
		} else if(advanced_auth(objUser.getIdLong()) && strArgs[0].equals("list_quotes")) {
			//This code will combine 10 quotes per message separated by a newline so we don't get rate limited
			String msg = "Listing " + RickyReloaded.quotes.length + " quotes:\n";
			for (int i = 0; i < RickyReloaded.quotes.length; i++) {
				msg += RickyReloaded.quotes[i] + "\n";
				if(i%10 == 0) {
					objChannel.sendMessage(msg).queue();
					msg = "";
				} else if(i+1 == RickyReloaded.quotes.length) {
					objChannel.sendMessage(msg).queue();
				}
			}
			
		} else if(objUser.getIdLong() == WES_SNOWFLAKE && strArgs[0].equals("reload_quotes")) {
			//This command is limited to just Wes since no one else has access to the quotes file anyway
			//Also it causes disk activity on server
			try {
				objChannel.sendMessage("Attempting to reload quotes from " + RickyReloaded.quotes_path).queue();
				load_quotes_from_file();
				objChannel.sendMessage("Succesfully loaded " + RickyReloaded.quotes.length + " quotes.").queue();
			} catch (IOException e) {
				e.printStackTrace();
				objChannel.sendMessage("Ah shit, something's fucked...").queue();
			}
		}
	}
	
	/**
	 * Helper function that sets roles. Used by HandleCommand function.
	 * @param objGuild
	 * @param objUser
	 */
	private void setRole(String role_name, Guild objGuild, User objUser, MessageChannel objChannel) {
		Role role = objGuild.getRolesByName(role_name, true).get(0);
		Member m = objGuild.getMember(objUser);
		GuildController gc = new GuildController(objGuild);
		
		if(m.getRoles().contains(role)) {		// checks for the role on the user already (same for all role code)
			gc.removeRolesFromMember(m, role).queue();
			objChannel.sendMessage(objUser.getAsMention() + "Role " + role_name + " has been removed!").queue();
		} 
		else {
			gc.addRolesToMember(m, role).queue();
			objChannel.sendMessage(objUser.getAsMention() + "Role " + role_name + " has been added!").queue();			
		}
	}
	
	private static Properties loadConfig(String path) {
		Properties prop = new Properties();
		InputStream input = null;
		
		try {
			input = new FileInputStream(path);
			prop.load(input);			
			return prop;

		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}
	
	private static void load_quotes_from_file() throws IOException {
       	FileInputStream fs = null;
   		fs = new FileInputStream(RickyReloaded.quotes_path);
   		BufferedReader br = new BufferedReader(new InputStreamReader(fs));
   		ArrayList<String> array = new ArrayList<String>();
   		String line;
   		while((line = br.readLine()) != null)
   			array.add(line);
   		quotes = new String[array.size()];
   		array.toArray(RickyReloaded.quotes);
	}
	
    public static void readRSSFeed(String urlAddress, TextChannel chan){
        Pattern r = Pattern.compile("href=\"(.+)\"");
        try{
            URL rssUrl = new URL (urlAddress);
            BufferedReader in = new BufferedReader(new InputStreamReader(rssUrl.openStream()));
            String line;
            while((line=in.readLine())!=null){
                if(line.contains("watch")) {        //This is hacky af... yolo
                    Matcher m = r.matcher(line);
                    if(m.find()) {
                        if(RickyReloaded.seen.contains(m.group(1))) {
                            continue; //no URL detected; moving on...
                        } else {
                            //This would be where we'd make Ricky send a message to the channel
                            System.out.println("New Video: " + m.group(1));
                            chan.sendMessage("New Video: " + m.group(1)).queue();
                           
                            //Add this url to list of seen videos so we don't flood channel
                            RickyReloaded.seen.add(m.group(1));
                        }
                    }
                }
            }
            in.close();
        } catch (MalformedURLException ue){
            System.out.println("Malformed URL");
        } catch (IOException ioe){
            System.out.println("Something went wrong reading the contents");
        }
    }
    
    private static boolean advanced_auth(long id) {
    	for(long l : advanced_commands_auth) {
    		if(l == id)
    			return true;
    	}
    	return false;
    }
}
