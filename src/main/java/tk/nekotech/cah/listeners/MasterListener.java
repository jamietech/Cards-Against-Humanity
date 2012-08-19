package tk.nekotech.cah.listeners;

import org.pircbotx.PircBotX;
import org.pircbotx.hooks.ListenerAdapter;

public class MasterListener extends ListenerAdapter {
	protected PircBotX bot;
	
	public MasterListener(PircBotX bot) {
		this.bot = bot;
		this.bot.getListenerManager().addListener(this);
	}

}
