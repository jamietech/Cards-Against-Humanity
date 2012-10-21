package tk.nekotech.cah.tasks;

import java.util.TimerTask;
import org.pircbotx.Colors;
import org.pircbotx.PircBotX;
import tk.nekotech.cah.CardsAgainstHumanity;
import tk.nekotech.cah.GameStatus;
import tk.nekotech.cah.Player;

public class StartGame extends TimerTask {
    private final PircBotX bot;
    private int run = 0;

    public StartGame(final PircBotX bot) {
        this.bot = bot;
    }

    @Override
    public void run() {
        if (CardsAgainstHumanity.gameStatus != GameStatus.BOT_START && CardsAgainstHumanity.gameStatus != GameStatus.IN_SESSION) {
            if (CardsAgainstHumanity.players.size() >= 3) {
                CardsAgainstHumanity.startGame();
            } else {
                this.bot.sendMessage("#CAH", "There are not enough players to start the game. There are " + CardsAgainstHumanity.players.size() + " players when a minimum of 3 are needed. Will retry in 15 seconds. " + Colors.BOLD + "To join the game say 'join'!");
            }
        }
        if (CardsAgainstHumanity.gameStatus == GameStatus.IN_SESSION) {
            synchronized (CardsAgainstHumanity.players) {
                final StringBuilder sb = new StringBuilder();
                int waiting = 0;
                int playing = 0;
                for (final Player player : CardsAgainstHumanity.players) {
                    playing++;
                    if (!player.hasPlayedCards()) {
                        sb.append(player.getName() + ", ");
                        waiting++;
                    }
                }
                if (waiting == playing) {
                } else if (sb.length() != 0 && this.run == 1) {
                    sb.delete(sb.length() - 2, sb.length());
                    this.bot.sendMessage("#CAH", Colors.BOLD + "Still waiting on cards from: " + Colors.NORMAL + sb.toString());
                    this.run = 0;
                }
            }
            this.run++;
        }
    }
}
