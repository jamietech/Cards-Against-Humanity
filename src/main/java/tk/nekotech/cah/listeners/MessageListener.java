package tk.nekotech.cah.listeners;

import org.pircbotx.Colors;
import org.pircbotx.PircBotX;
import org.pircbotx.User;
import org.pircbotx.hooks.events.MessageEvent;
import tk.nekotech.cah.CardsAgainstHumanity;
import tk.nekotech.cah.Player;

public class MessageListener extends MasterListener {
    public MessageListener(final PircBotX bot) {
        super(bot);
    }

    @Override
    public void onMessage(final MessageEvent event) {
        final String message = event.getMessage();
        final String quick = message.toLowerCase();
        final User user = event.getUser();
        if (quick.equals("join")) {
            boolean playing = false;
            for (final Player player : CardsAgainstHumanity.players) {
                if (player.getName().equals(user.getNick())) {
                    playing = true;
                }
            }
            if (playing) {
                this.bot.sendNotice(user, "You're already playing the game!");
            } else {
                this.bot.sendNotice(user, "Welcome to " + Colors.BOLD + "Cards Against Humanity" + Colors.NORMAL + "! Your cards will be assigned next round.");
                Player player = new Player(user.getNick());
                if (CardsAgainstHumanity.inSession()) {
                    player.setWaiting(true);
                }
                CardsAgainstHumanity.players.add(player);
            }
        }
        if (quick.equals("quit")) {
            boolean playing = false;
            Player playa = null;
            for (final Player player : CardsAgainstHumanity.players) {
                if (player.getName().equals(user.getNick())) {
                    playing = true;
                    playa = player;
                }
            }
            if (playing) {
                this.bot.sendNotice(user, "Good-bye! Your cards for this round have been dropped. You had " + playa.getScore() + " points!");
                CardsAgainstHumanity.players.remove(playa);
            } else {
                this.bot.sendNotice(user, "You're not currently playing the game!");
            }
        }
    }
}
