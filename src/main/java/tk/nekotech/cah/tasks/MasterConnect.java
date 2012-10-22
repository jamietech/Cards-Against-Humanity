package tk.nekotech.cah.tasks;

import java.util.TimerTask;
import tk.nekotech.cah.CardsAgainstHumanity;
import tk.nekotech.cah.bots.SpamBot;

public class MasterConnect extends TimerTask {
    private final CardsAgainstHumanity cah;

    public MasterConnect(final CardsAgainstHumanity cah) {
        this.cah = cah;
    }

    @Override
    public void run() {
        try {
            this.cah.spamBot = new SpamBot("CAH-Master", this.cah);
        } catch (final Exception e) {
            System.err.println("\n\n****************");
            System.err.println("Exception during startup of Master bot!\n");
            e.printStackTrace(System.err);
            System.err.println("****************\n\n");
        }
    }
}
