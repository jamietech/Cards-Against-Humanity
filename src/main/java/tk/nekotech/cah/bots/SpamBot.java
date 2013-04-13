package tk.nekotech.cah.bots;

import java.io.IOException;
import org.pircbotx.exception.IrcException;
import org.pircbotx.exception.NickAlreadyInUseException;
import tk.nekotech.cah.CardsAgainstHumanity;
import tk.nekotech.cah.listeners.FlowListener;

public class SpamBot extends MasterBot {
    public SpamBot(final String nick, final CardsAgainstHumanity cah) throws NickAlreadyInUseException, IOException, IrcException {
        super(nick);
        new FlowListener(this, cah);
    }
}
