package tk.nekotech.cah.bots;

import java.io.IOException;
import org.pircbotx.exception.IrcException;
import org.pircbotx.exception.NickAlreadyInUseException;
import tk.nekotech.cah.listeners.MessageListener;

public class SpamBot extends MasterBot {
    public SpamBot(final String nick) throws NickAlreadyInUseException, IOException, IrcException {
        super(nick);
        new MessageListener(this);
    }
}
