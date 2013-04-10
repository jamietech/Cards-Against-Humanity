package tk.nekotech.cah.runnables;

import java.util.Scanner;
import org.pircbotx.Channel;
import tk.nekotech.cah.CardsAgainstHumanity;

public class Startup extends Thread {
    private final CardsAgainstHumanity cah;

    public Startup(final CardsAgainstHumanity cah) {
        this.cah = cah;
    }

    @Override
    public void run() {
        final Scanner scanner = new Scanner(System.in);
        scanner.nextLine();
        System.out.println("Shutting down...");
        final Channel channel = this.cah.spamBot.getChannel("#MIASACraft");
        this.cah.spamBot.setTopic(channel, this.cah.topic + " | Bot currently offline.");
        String[] users = new String[this.cah.players.size()];
        for (int i = 0; i < this.cah.players.size(); i++) {
            users[i] = this.cah.players.get(i).getName();
        }
        this.cah.deVoiceUsers(users);
        System.out.println("Nearly done!");
        try {
            Thread.sleep(1000);
        } catch (final InterruptedException e) {
        }
        this.cah.spamBot.quitServer("Game shutting down.");
        this.cah.cardBot.quitServer("Game shutting down.");
        try {
            Thread.sleep(3000);
        } catch (final InterruptedException e) {
        }
        System.exit(1);
    }
}
