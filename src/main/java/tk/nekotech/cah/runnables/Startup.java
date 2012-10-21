package tk.nekotech.cah.runnables;

import java.util.Scanner;
import org.pircbotx.Channel;
import tk.nekotech.cah.CardsAgainstHumanity;

public class Startup extends Thread {
    public void run() {
        Scanner scanner = new Scanner(System.in);
        scanner.nextLine();
        System.out.println("Shutting down...");
        Channel channel = CardsAgainstHumanity.spamBot.getChannel("#CAH");
        CardsAgainstHumanity.spamBot.setTopic(channel, CardsAgainstHumanity.topic + " | Bot currently offline.");
        CardsAgainstHumanity.spamBot.removeModerated(channel);
        System.out.println("Nearly done!");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
        }
        CardsAgainstHumanity.spamBot.quitServer("Game shutting down.");
        CardsAgainstHumanity.cardBot.quitServer("Game shutting down.");
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
        }
        System.exit(1);
    }
}
