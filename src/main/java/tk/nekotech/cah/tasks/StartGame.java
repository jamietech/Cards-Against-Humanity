package tk.nekotech.cah.tasks;

import java.util.TimerTask;
import org.pircbotx.Colors;
import org.pircbotx.PircBotX;
import tk.nekotech.cah.CardsAgainstHumanity;
import tk.nekotech.cah.GameStatus;

public class StartGame extends TimerTask {
    private final PircBotX bot;
    private final CardsAgainstHumanity cah;

    public StartGame(final PircBotX bot, final CardsAgainstHumanity cah) {
        this.bot = bot;
        this.cah = cah;
    }

    @Override
    public void run() {
        if (this.cah.gameStatus != GameStatus.BOT_START && !this.cah.inSession()) {
            if (this.cah.players.size() >= 3) {
                this.cah.startGame();
            } else {
                this.bot.sendMessage(cah.channel, "There are not enough players to start the game. There are " + this.cah.players.size() + " players when a minimum of 3 are needed. Will retry in 1 minute. " + Colors.BOLD + "To join the game say 'join'!");
            }
        }
    }
}
