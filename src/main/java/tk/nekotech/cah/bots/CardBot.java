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
    public CardBot(final String nick) throws NickAlreadyInUseException, IOException, IrcException {
        super(nick);
        new PlayerListener(this);
        CardsAgainstHumanity.ready();
    }

    public void messageAllCards() {
        for (final Player player : CardsAgainstHumanity.players) {
            final StringBuilder sb = new StringBuilder();
            int i = 1;
            for (final WhiteCard card : player.getCards()) {
                sb.append(i + " [" + Colors.BOLD + card.getColored() + "] ");
                i++;
            }
            this.sendNotice(player.getName(), "Your cards: " + sb.toString());
        }
    }
}
