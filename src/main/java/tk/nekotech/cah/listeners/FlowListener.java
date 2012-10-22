package tk.nekotech.cah.listeners;

import org.pircbotx.Colors;
import org.pircbotx.PircBotX;
import org.pircbotx.User;
import org.pircbotx.hooks.events.KickEvent;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.hooks.events.PartEvent;
import org.pircbotx.hooks.events.QuitEvent;
import tk.nekotech.cah.CardsAgainstHumanity;
import tk.nekotech.cah.Player;

public class FlowListener extends MasterListener {
    private CardsAgainstHumanity cah;

    public FlowListener(final PircBotX bot, final CardsAgainstHumanity cah) {
        super(bot);
    }

    @Override
    @SuppressWarnings("rawtypes")
    public void onKick(final KickEvent event) {
        final Player player = this.cah.getPlayer(event.getRecipient().getNick());
        if (player != null) {
            this.cah.processLeave(player);
            this.cah.players.remove(player);
        }
    }

    @Override
    @SuppressWarnings("rawtypes")
    public void onMessage(final MessageEvent event) {
        final String message = event.getMessage();
        final String quick = message.toLowerCase();
        final User user = event.getUser();
        if (quick.equals("join")) {
            boolean playing = false;
            for (final Player player : this.cah.players) {
                if (player.getName().equals(user.getNick())) {
                    playing = true;
                }
            }
            if (playing) {
                this.bot.sendNotice(user, "You're already playing the game!");
            } else {
                final int maxPlayers = this.cah.whiteCards.size() / 10;
                if (this.cah.players.size() >= maxPlayers) {
                    this.bot.sendNotice(user, "Sorry, the maximum amount of players (" + maxPlayers + ") is currently playing. Please try rejoining later.");
                } else {
                    this.bot.sendNotice(user, "Welcome to " + Colors.BOLD + "Cards Against Humanity" + Colors.NORMAL + "! Your cards will be assigned next round.");
                    final Player player = new Player(user.getNick(), this.cah);
                    if (this.cah.inSession()) {
                        player.setWaiting(true);
                    }
                    this.cah.players.add(player);
                }
            }
        }
        if (quick.equals("quit")) {
            boolean playing = false;
            Player playa = null;
            for (final Player player : this.cah.players) {
                if (player.getName().equals(user.getNick())) {
                    playing = true;
                    playa = player;
                }
            }
            if (playing) {
                this.bot.sendNotice(user, "Good-bye! Your cards for this round have been dropped. You had " + playa.getScore() + " points!");
                this.cah.processLeave(playa);
                this.cah.players.remove(playa);
            } else {
                this.bot.sendNotice(user, "You're not currently playing the game!");
            }
        }
    }

    @Override
    @SuppressWarnings("rawtypes")
    public void onPart(final PartEvent event) {
        final Player player = this.cah.getPlayer(event.getUser().getNick());
        if (player != null) {
            this.cah.processLeave(player);
            this.cah.players.remove(player);
        }
    }

    @Override
    @SuppressWarnings("rawtypes")
    public void onQuit(final QuitEvent event) {
        final Player player = this.cah.getPlayer(event.getUser().getNick());
        if (player != null) {
            this.cah.processLeave(player);
            this.cah.players.remove(player);
        }
    }
}
