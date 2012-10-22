package tk.nekotech.cah.tasks;

import java.util.TimerTask;
import org.pircbotx.Colors;
import tk.nekotech.cah.CardsAgainstHumanity;
import tk.nekotech.cah.Player;

public class Nag extends TimerTask {
    private final CardsAgainstHumanity cah;

    public Nag(final CardsAgainstHumanity cah) {
        this.cah = cah;
    }

    @Override
    public void run() {
        final StringBuilder sb = new StringBuilder();
        for (final Player player : this.cah.players) {
            if (!player.hasPlayedCards() && !player.isCzar() && !player.isWaiting()) {
                player.addWarning();
                if (player.getWarnings() == 3) {
                    this.cah.spamBot.kick(this.cah.spamBot.getChannel("#CAH"), this.cah.spamBot.getUser(player.getName()), "You were idle after 3 warnings. Rejoin when you want. :)");
                } else if (player.getWarnings() == 2) {
                    sb.append(Colors.BOLD + player.getName() + Colors.NORMAL + ", ");
                } else {
                    sb.append(player.getName() + ", ");
                }
            }
        }
        if (sb.length() != 0) {
            sb.delete(sb.length() - 2, sb.length());
            this.cah.spamBot.sendMessage("#CAH", "Are you still there? You need to play your cards - " + sb.toString());
        }
    }
}
