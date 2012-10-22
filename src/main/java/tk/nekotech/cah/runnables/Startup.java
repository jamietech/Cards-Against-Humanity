package tk.nekotech.cah.runnables;

import java.util.Scanner;
import org.pircbotx.Channel;
import tk.nekotech.cah.CardsAgainstHumanity;
import tk.nekotech.cah.Player;

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
        final Channel channel = this.cah.spamBot.getChannel("#CAH");
        this.cah.spamBot.setTopic(channel, this.cah.topic + " | Bot currently offline.");
        StringBuilder sb = new StringBuilder();
        String mode = "-";
        for (Player player : this.cah.players) {
            if (!player.isWaiting()) {
                sb.append(player.getName() + " ");
                mode += "v";
            }
        }
        if (sb.length() != 0) {
            sb.delete(sb.length() - 1, sb.length());
            this.cah.cardBot.setMode(this.cah.cardBot.getChannel("#CAH"), mode + " " + sb.toString());
        }
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
