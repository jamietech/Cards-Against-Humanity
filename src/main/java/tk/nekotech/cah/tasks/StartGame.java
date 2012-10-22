package tk.nekotech.cah.tasks;

import java.util.TimerTask;
import org.pircbotx.Colors;
import org.pircbotx.PircBotX;
import tk.nekotech.cah.CardsAgainstHumanity;
import tk.nekotech.cah.GameStatus;

public class StartGame extends TimerTask {
    private final PircBotX bot;

    public StartGame(final PircBotX bot) {
        this.bot = bot;
    }

    @Override
    public void run() {
        if (CardsAgainstHumanity.gameStatus != GameStatus.BOT_START && !CardsAgainstHumanity.inSession()) {
            if (CardsAgainstHumanity.players.size() >= 3) {
                CardsAgainstHumanity.startGame();
            } else {
                this.bot.sendMessage("#CAH", "There are not enough players to start the game. There are " + CardsAgainstHumanity.players.size() + " players when a minimum of 3 are needed. Will retry in 1 minute. " + Colors.BOLD + "To join the game say 'join'!");
            }
        }
    }
}
