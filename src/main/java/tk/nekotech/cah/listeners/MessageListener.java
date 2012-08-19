package tk.nekotech.cah.listeners;

import org.pircbotx.Colors;
import org.pircbotx.PircBotX;
import org.pircbotx.User;
import org.pircbotx.hooks.events.MessageEvent;

import tk.nekotech.cah.CardsAgainstHumanity;
import tk.nekotech.cah.Player;

public class MessageListener extends MasterListener {

	public MessageListener(PircBotX bot) {
		super(bot);
	}

	public void onMessage(MessageEvent event) {
		String message = event.getMessage();
		String quick = message.toLowerCase();
		User user = event.getUser();
		if (quick.equals("join")) {
			boolean playing = false;
			for (Player player : CardsAgainstHumanity.players) {
				if (player.getName().equals(user.getNick())) {
					playing = true;
				}
			}
			if (playing) {
				bot.sendNotice(user, "You're already playing the game!");
			} else {
				bot.sendNotice(user, "Welcome to " + Colors.BOLD + "Cards Against Humanity" + Colors.NORMAL + "! Your cards will be assigned next round.");
				CardsAgainstHumanity.players.add(new Player(user.getNick()));
			}
		}
		if (quick.equals("quit")) {
			boolean playing = false;
			Player playa = null;
			for (Player player : CardsAgainstHumanity.players) {
				if (player.getName().equals(user.getNick())) {
					playing = true;
					playa = player;
				}
			}
			if (playing) {
				bot.sendNotice(user, "Good-bye! Your cards for this round have been dropped. You had " + playa.getScore() + " points!");
			} else {
				bot.sendNotice(user, "You're not currently playing the game!");
			}
		}
	}
	
}
