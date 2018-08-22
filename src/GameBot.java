import java.io.IOException;

import javax.security.auth.login.LoginException;

public class GameBot extends ListenerAdapter {
	
	public static final char command_symbol = '^';
	

	public static void main( String[] args ) throws LoginException, IllegalArgumentException, InterruptedException, RateLimitedException, IOException, ClassNotFoundException {
	
        final JDA GameBot = new JDABuilder(AccountType.BOT).setToken("NDgxNjE3MTc3MDExNTUyMjU2.Dl5F0A.c_A9VRttf9uXTmBDZPDXt3oh20s").buildBlocking();
        GameBot.addEventListener(new GameBot());
        
	{
}