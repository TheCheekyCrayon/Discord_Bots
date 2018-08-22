package tutorial.Ricky;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
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



public class App extends ListenerAdapter {
	
	public static final char command_symbol = '$';
	public static final String quotes_file = "a.txt";
	public static String[] quotes;
    private static final String FEED_URL = "https://www.youtube.com/feeds/videos.xml?channel_id=UCkOlmd_lMI9YHRcN1ffKbyQ";
    private static List<String> seen;
    private static Random rand;
	
    @SuppressWarnings("unchecked")
	public static void main( String[] args ) throws LoginException, IllegalArgumentException, InterruptedException, RateLimitedException, IOException, ClassNotFoundException {
    	
    	App.rand = new Random();
    	
    	 //Load seen videos from disk
        try {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream("seen.obj"));
            App.seen = (List<String>) ois.readObject();
            ois.close();
        } catch(FileNotFoundException e) {
            //if the file doesn't exist just create a new ArrayList
            App.seen = new ArrayList<String>();
        }
    	
    	FileInputStream fs = null;
    	fs = new FileInputStream(quotes_file);
    	BufferedReader br = new BufferedReader(new InputStreamReader(fs));
    	ArrayList<String> array = new ArrayList<String>();
    	String line;
    	while((line = br.readLine()) != null)
    		array.add(line);
    	quotes = new String[array.size()];
    	array.toArray(quotes);
    		
    	
        final JDA rickyBot = new JDABuilder(AccountType.BOT).setToken("Mzg3Nzk4NTc4OTc1ODY2ODk1.DQj7iw.wb5283aO3asfDjNej3a5OVcs5Ys").buildBlocking();
        rickyBot.addEventListener(new App());
        
        Timer timer = new Timer ();
        TimerTask hourlyTask = new TimerTask () {
            @Override
            public void run () {
            	TextChannel chan = rickyBot.getTextChannelsByName("cookingwith_Rick", true).get(0);
                App.readRSSFeed(FEED_URL, chan);
                //save seen list to disk
                ObjectOutputStream oos;
				try {
					oos = new ObjectOutputStream(new FileOutputStream("seen.obj"));
					oos.writeObject(App.seen);
	                oos.close();
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
        };

        // schedule the task to run starting now and then every hour...
        timer.schedule (hourlyTask, 0l, 1000*60*30);
        
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
                        if(App.seen.contains(m.group(1))) {
                            continue; //no URL detected; moving on...
                        } else {
                            //This would be where we'd make Ricky send a message to the channel
                            System.out.println("New Video: " + m.group(1));
                            chan.sendMessage("New Video: " + m.group(1)).queue();
                           
                            //Add this url to list of seen videos so we don't flood channel
                            App.seen.add(m.group(1));
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
    
    @Override
    public void onMessageReceived(MessageReceivedEvent e)
    {
    	String message = e.getMessage().getContent();
    	MessageChannel objChannel = e.getChannel();
    	User objUser = e.getAuthor();
    	Guild objGuild = e.getGuild();
    	
    	if(message.charAt(0) == command_symbol) {
    		handleCommand(message, objChannel, objUser, objGuild);
    		return;
    	}
    	
    	float chance = App.rand.nextFloat();

    	if(objChannel.getName().equalsIgnoreCase("general_chat")){
    		if (chance <= 0.03f){	// checks for a message and gives a 3% chance to comment
    			int randomIndex = App.rand.nextInt(App.quotes.length);
    			objChannel.sendMessage(App.quotes[randomIndex]).queue();    	
    		}
    	}
    }

	private void handleCommand(String message, MessageChannel objChannel, User objUser, Guild objGuild) {
		
		String[] strArgs = message.substring(1).split(" ");
		
		if(strArgs[0].equals("quote")){
			Random rand = new Random();
			int randomIndex = rand.nextInt(App.quotes.length);
			objChannel.sendMessage(App.quotes[randomIndex] + " " + objUser.getAsMention()).queue();
		}
		
		else if(strArgs[0].equals("subscribe")){	// pastes the link to subscribe in the chat
			objChannel.sendMessage("https://www.youtube.com/channel/UCkOlmd_lMI9YHRcN1ffKbyQ?sub_confirmation=1 " + objUser.getAsMention() + " Now like and subscribe!").queue();
		}
		
		else if(strArgs[0].equals("commands")){	// sends messages of the commands the bot uses
			objChannel.sendMessage("In the #help channel:");
			objChannel.sendMessage("$DJ    (for the DJ role, @DJ )");
			objChannel.sendMessage("$NSFW    (for the NSFW role, @NSFW )");
			objChannel.sendMessage("$Meme-ber    (for the Meme-ber role, @Meme-ber )");
			objChannel.sendMessage("$Siege   (for the Siege role, @Siege )");
			objChannel.sendMessage("$Rust    (for the Rust role, @Rust )");
			objChannel.sendMessage("$Member    (to get Member status in the server, @Member )");
			objChannel.sendMessage(" ");
			objChannel.sendMessage("In any channel $subscibe will generate a link that allows you to subscribe to the Cookingwith Rick YouTube channel");
			objChannel.sendMessage("$quote will grab a random quote that Rick has said himself");
			objChannel.sendMessage(" ");
			
		}
		
		else if(strArgs[0].equals("DJ") && objChannel.getName().equalsIgnoreCase("help")) { // DJ role
			
			Role dj = objGuild.getRolesByName("DJ", true).get(0);
			Member m = objGuild.getMember(objUser);
			GuildController gc = new GuildController(objGuild);
			if(m.getRoles().contains(dj)) {		// checks for the role on the user already (same for all role code)
				gc.removeRolesFromMember(m, dj).queue();
				objChannel.sendMessage(objUser.getAsMention() + "Role DJ has been removed!").queue();
			} 
			else {
				gc.addRolesToMember(m, dj).queue();
				objChannel.sendMessage(objUser.getAsMention() + "Role DJ has been added!").queue();			
			}
		}
		
		else if(strArgs[0].equals("NSFW") && objChannel.getName().equalsIgnoreCase("help")) { // NSFW role
			
			Role nsfw = objGuild.getRolesByName("NSFW", true).get(0);
			Member m = objGuild.getMember(objUser);
			GuildController gc = new GuildController(objGuild);
			if(m.getRoles().contains(nsfw)) {
				gc.removeRolesFromMember(m, nsfw).queue();
				objChannel.sendMessage(objUser.getAsMention() + " Role NSFW has been removed!").queue();
			} 
			else {
				gc.addRolesToMember(m, nsfw).queue();
				objChannel.sendMessage(objUser.getAsMention() + " Role NSFW has been added!").queue();			
			}
		}
		
		else if(strArgs[0].equals("Meme-ber") && objChannel.getName().equalsIgnoreCase("help")) { // Meme-ber role
			
			Role meme_ber = objGuild.getRolesByName("Meme-ber", true).get(0);
			Member m = objGuild.getMember(objUser);
			GuildController gc = new GuildController(objGuild);
			if(m.getRoles().contains(meme_ber)) {
				gc.removeRolesFromMember(m, meme_ber).queue();
				objChannel.sendMessage(objUser.getAsMention() + " Role Meme-ber has been removed!").queue();
			} 
			else {
				gc.addRolesToMember(m, meme_ber).queue();
				objChannel.sendMessage(objUser.getAsMention() + " Role Meme-ber has been added!").queue();			
			}
		}
		
		else if(strArgs[0].equals("Member") && objChannel.getName().equalsIgnoreCase("help")) { // Member role
			
			Role member = objGuild.getRolesByName("Member", true).get(0);
			Member m = objGuild.getMember(objUser);
			GuildController gc = new GuildController(objGuild);
			if(m.getRoles().contains(member)) {
				gc.removeRolesFromMember(m, member).queue();
				objChannel.sendMessage(objUser.getAsMention() + " Role Member has been removed!").queue();
			} 
			else {
				gc.addRolesToMember(m, member).queue();
				objChannel.sendMessage(objUser.getAsMention() + " Role Member has been added!").queue();			
			}
		}
		
		else if(strArgs[0].equals("Siege") && objChannel.getName().equalsIgnoreCase("help")) { // Siege role
			
			Role siege = objGuild.getRolesByName("Siege", true).get(0);
			Member m = objGuild.getMember(objUser);
			GuildController gc = new GuildController(objGuild);
			if(m.getRoles().contains(siege)) {
				gc.removeRolesFromMember(m, siege).queue();
				objChannel.sendMessage(objUser.getAsMention() + " Role Siege has been removed!").queue();
			} 
			else {
				gc.addRolesToMember(m, siege).queue();
				objChannel.sendMessage(objUser.getAsMention() + " Role Siege has been added!").queue();			
			}
		}
		
		else if(strArgs[0].equals("Rust") && objChannel.getName().equalsIgnoreCase("help")) { // Rust role
			
			Role rust = objGuild.getRolesByName("Rust", true).get(0);
			Member m = objGuild.getMember(objUser);
			GuildController gc = new GuildController(objGuild);
			if(m.getRoles().contains(rust)) {
				gc.removeRolesFromMember(m, rust).queue();
				objChannel.sendMessage(objUser.getAsMention() + " Role Rust has been removed!").queue();
			} 
			else {
				gc.addRolesToMember(m, rust).queue();
				objChannel.sendMessage(objUser.getAsMention() + " Role Rust has been added!").queue();			
			}
		}
		
		
	}
    
}
