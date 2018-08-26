package tutorial.GameBot;

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
	
	public static final char command_symbol = '^';
    private static final String Bethesda_URL = "https://www.youtube.com/feeds/videos.xml?channel_id=UCvZHe-SP3xC7DdOk4Ri8QBw";	//done
    private static final String Xbox_URL = "https://www.youtube.com/feeds/videos.xml?channel_id=UCjBp_7RuDBUYbd1LegWEJ8g";	//done
    private static final String Playstation_URL = "https://www.youtube.com/feeds/videos.xml?channel_id=UC-2Y8dQb0S6DtpxNgAKoJKA";	//done
    private static final String Ubisoft_URL = "https://www.youtube.com/feeds/videos.xml?channel_id=UCBMvc6jvuTxH6TNo9ThpYjg"; //done
    private static final String Minecraft_URL = "https://www.youtube.com/feeds/videos.xml?channel_id=UC1sELGmy5jp5fQUugmuYlXQ";	//done
    private static final String Blizzard_URL = "https://www.youtube.com/feeds/videos.xml?channel_id=UC3GriadTkHBnfgd2UFETGOA";	//done
    private static final String Battlefield_URL = "https://www.youtube.com/feeds/videos.xml?channel_id=UCvNBXWGykQrWb7kPAn5eLUQ";	//done
    private static final String CoD_URL = "https://www.youtube.com/feeds/videos.xml?channel_id=UC9YydG57epLqxA9cTzZXSeQ";	//done
    private static final String EaStarWars_URL = "https://www.youtube.com/feeds/videos.xml?channel_id=UCOsVSkmXD1tc6uiJ2hc0wYQ";	//done
    private static List<String> seen;
	
	@SuppressWarnings("deprecation")
	public static void main( String[] args ) throws LoginException, IllegalArgumentException, InterruptedException, RateLimitedException, IOException, ClassNotFoundException {
	    
		Properties bot_conf = loadConfig(args[0]);
    	
   	 	//Load seen videos from disk
		try {
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream("seen.obj"));
			App.seen = (List<String>) ois.readObject();
			ois.close();
		} catch(FileNotFoundException e) {
			//if the file doesn't exist just create a new ArrayList
			App.seen = new ArrayList<String>();
		}		

		final JDA GameBot = new JDABuilder(AccountType.BOT).setToken(bot_conf.getProperty("gamebot")).buildBlocking();
		GameBot.addEventListener(new App());	    

        Timer timer = new Timer ();

        // Xbox TASK
        TimerTask xboxTask = new TimerTask () {
            @Override
            public void run () {
            	TextChannel chan = GameBot.getTextChannelsByName("consoles", true).get(0);
                App.readRSSFeed(Xbox_URL, chan);
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
        
        //playstation task
        TimerTask playstationTask = new TimerTask () {
            @Override
            public void run () {
            	TextChannel chan = GameBot.getTextChannelsByName("consoles", true).get(0);
                App.readRSSFeed(Playstation_URL, chan);
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
        
        //Bethesda Task
        TimerTask bethesdaTask = new TimerTask () {
            @Override
            public void run () {
            	TextChannel chan = GameBot.getTextChannelsByName("bethesda_games", true).get(0);
                App.readRSSFeed(Bethesda_URL, chan);
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

    	//Battlefield Task
        TimerTask battlefieldTask = new TimerTask () {
            @Override
            public void run () {
            	TextChannel chan = GameBot.getTextChannelsByName("battlefield_or_cod", true).get(0);
                App.readRSSFeed(Battlefield_URL, chan);
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
        
    	//CoD Task
        TimerTask codTask = new TimerTask () {
            @Override
            public void run () {
            	TextChannel chan = GameBot.getTextChannelsByName("battlefield_or_cod", true).get(0);
                App.readRSSFeed(CoD_URL, chan);
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
        
    	//Ubisoft Task
        TimerTask ubisoftTask = new TimerTask () {
            @Override
            public void run () {
            	TextChannel chan = GameBot.getTextChannelsByName("ubisoft_games", true).get(0);
                App.readRSSFeed(Ubisoft_URL, chan);
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
        
    	//Blizzard Task
        TimerTask blizzardTask = new TimerTask () {
            @Override
            public void run () {
            	TextChannel chan = GameBot.getTextChannelsByName("blizzard_games", true).get(0);
                App.readRSSFeed(Blizzard_URL, chan);
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
        
    	//EaStarWars Task
        TimerTask eaTask = new TimerTask () {
            @Override
            public void run () {
            	TextChannel chan = GameBot.getTextChannelsByName("star_wars", true).get(0);
                App.readRSSFeed(EaStarWars_URL, chan);
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
        
    	//Minecraft Task
        TimerTask minecraftTask = new TimerTask () {
            @Override
            public void run () {
            	TextChannel chan = GameBot.getTextChannelsByName("minecraft", true).get(0);
                App.readRSSFeed(Minecraft_URL, chan);
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

        //Schedule the task to run starting now and then every hour... Xbox
        timer.schedule (xboxTask, 0l, 1000*60*30);
        //Playstation Task
        timer.schedule (playstationTask, 0l, 1000*60*30);     
        //Bethesda schedule
        timer.schedule (bethesdaTask, 0l, 1000*60*30);
        //Battlefield schedule
        timer.schedule (battlefieldTask, 0l, 1000*60*30);
        //CoD schedule
        timer.schedule (codTask, 0l, 1000*60*30);
        //Ubisoft schedule
        timer.schedule (ubisoftTask, 0l, 1000*60*30);
        //Blizzard schedule
        timer.schedule (blizzardTask, 0l, 1000*60*30);
        //EAStarWars schedule
        timer.schedule (eaTask, 0l, 1000*60*30);
        //Minecraft schedule
        timer.schedule (minecraftTask, 0l, 1000*60*30);		
        
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
                            //This would be where we'd make GameBot send a message to the channel
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
            System.out.println(chan + ": Something went wrong reading the contents");
        }
    }
	
    @Override
    public void onMessageReceived(MessageReceivedEvent e)
    {
    	String message = e.getMessage().getContentRaw();
    	MessageChannel objChannel = e.getChannel();
    	User objUser = e.getAuthor();
    	Guild objGuild = e.getGuild();
    	
    	if(message.charAt(0) == command_symbol) {
    		handleCommand(message, objChannel, objUser, objGuild);
    		return;
    	}
	
	
    }

	private void handleCommand(String message, MessageChannel objChannel, User objUser, Guild objGuild) {
		String[] strArgs = message.substring(1).split(" ");		
		
		if(strArgs[0].equals("help")) {	//tells the user how it is when they ask for help
			objChannel.sendMessage("Get Good! " + objUser.getAsMention()).queue();
		} else if(strArgs[0].equals("Games") && objChannel.getName().equalsIgnoreCase("help")) { // Games role
				
			Role games = objGuild.getRolesByName("Games", true).get(0);
			Member m = objGuild.getMember(objUser);
			GuildController gc = new GuildController(objGuild);
			if(m.getRoles().contains(games)) {		// checks for the role on the user already (same for all role code)
				gc.removeRolesFromMember(m, games).queue();
				objChannel.sendMessage(objUser.getAsMention() + " Role Games has been removed!").queue();
			} else {
				gc.addRolesToMember(m, games).queue();
				objChannel.sendMessage(objUser.getAsMention() + " Role Games has been added!").queue();			
			}
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
}