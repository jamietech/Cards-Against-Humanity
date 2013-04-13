package tk.nekotech.cah.tasks;

import java.util.TimerTask;
import tk.nekotech.cah.CardsAgainstHumanity;
import tk.nekotech.cah.bots.CardBot;

public class CardConnect extends TimerTask {
    private final CardsAgainstHumanity cah;

    public CardConnect(final CardsAgainstHumanity cah) {
        this.cah = cah;
    }

    @Override
    public void run() {
        try {
            this.cah.cardBot = new CardBot("CAH-Cards", this.cah);
        } catch (final Exception e) {
            System.err.println("\n\n****************");
            System.err.println("Exception during startup of Cards bot!\n");
            e.printStackTrace(System.err);
            System.err.println("****************\n\n");
        }
    }
}
