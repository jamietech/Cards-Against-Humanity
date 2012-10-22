package tk.nekotech.cah.bots;

import java.io.IOException;
import org.pircbotx.Colors;
import org.pircbotx.exception.IrcException;
import org.pircbotx.exception.NickAlreadyInUseException;
import tk.nekotech.cah.CardsAgainstHumanity;
import tk.nekotech.cah.Player;
import tk.nekotech.cah.card.WhiteCard;
import tk.nekotech.cah.listeners.PlayerListener;

public class CardBot extends MasterBot {
    private CardsAgainstHumanity cah;

    public CardBot(final String nick, final CardsAgainstHumanity cah) throws NickAlreadyInUseException, IOException, IrcException {
        super(nick);
        this.cah = cah;
        new PlayerListener(this, cah);
        try {
            cah.ready();
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    public void messageAllCards() {
        for (final Player player : this.cah.players) {
            if (this.cah.czar == player) {
                continue;
            }
            final StringBuilder sb = new StringBuilder();
            int i = 1;
            for (final WhiteCard card : player.getCards()) {
                sb.append(i + " [" + Colors.BOLD + card.getColored() + "] ");
                i++;
            }
            this.sendNotice(player.getName(), player.getName() + ": your cards are " + sb.toString());
        }
    }
}
