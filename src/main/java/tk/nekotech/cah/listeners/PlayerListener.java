package tk.nekotech.cah.listeners;

import org.pircbotx.PircBotX;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.hooks.events.NickChangeEvent;
import tk.nekotech.cah.CardsAgainstHumanity;
import tk.nekotech.cah.GameStatus;
import tk.nekotech.cah.Player;
import tk.nekotech.cah.card.WhiteCard;

public class PlayerListener extends MasterListener {
    public PlayerListener(final PircBotX bot) {
        super(bot);
    }

    @Override
    @SuppressWarnings("rawtypes")
    public void onMessage(final MessageEvent event) {
        if (event.getUser().getNick().contains("CAH-Master") || CardsAgainstHumanity.gameStatus != GameStatus.IN_SESSION) {
            return;
        }
        final Player player = CardsAgainstHumanity.getPlayer(event.getUser().getNick());
        if (player == null) {
            return;
        }
        final String[] message = event.getMessage().split(" ");
        if (message.length == 1 && CardsAgainstHumanity.blackCard.getAnswers() == 1) {
            int answer = 0;
            try {
                answer = Integer.parseInt(message[0]);
                if (answer == 0 || answer > 10) {
                    this.bot.sendNotice(event.getUser(), "Use a number that you actually have!");
                } else {
                    if (player.hasPlayedCards()) {
                        this.bot.sendNotice(event.getUser(), "You've already played a card this round!");
                    } else {
                        final WhiteCard card = player.getCards().get(answer--);
                        this.bot.sendNotice(event.getUser(), "Saved answer " + card.getFull() + "!");
                        player.playCard(card);
                    }
                }
            } catch (final NumberFormatException e) {
                this.bot.sendNotice(event.getUser(), "You can't answer with that! Try use a number instead.");
            }
        } else if (message.length == 2 && CardsAgainstHumanity.blackCard.getAnswers() == 2) {
            final int[] answers = new int[2];
            for (int i = 0; i < 2; i++) {
                try {
                    answers[i - 1] = Integer.parseInt(message[i - 1]);
                } catch (final NumberFormatException e) {
                    answers[i - 1] = -55;
                }
            }
            if (answers[0] == -55 || answers[1] == -55) {
                this.bot.sendNotice(event.getUser(), "You can't answer with that! Ensure you entered two numbers.");
            } else if (answers[0] == 0 || answers[0] > 10 || answers[1] == 0 || answers[1] > 10) {
                this.bot.sendNotice(event.getUser(), "Use a number that you actually have!");
            } else {
                if (player.hasPlayedCards()) {
                    this.bot.sendNotice(event.getUser(), "You've already played your cards this round!");
                } else {
                    final WhiteCard[] cards = new WhiteCard[2];
                    for (int i = 0; i < 2; i++) {
                        cards[i - 1] = player.getCards().get(i - 1);
                    }
                    this.bot.sendNotice(event.getUser(), "Saved answers " + cards[0].getFull() + ", " + cards[1].getFull() + "!");
                    player.playCards(cards);
                }
            }
        } else {
            final boolean one = CardsAgainstHumanity.blackCard.getAnswers() == 1;
            this.bot.sendNotice(event.getUser(), "You answered incorrectly! Enter the " + (one ? "number" : "numbers") + " in relation to the " + (one ? "card" : "cards") + " you wish to play.");
        }
    }

    @Override
    @SuppressWarnings("rawtypes")
    public void onNickChange(final NickChangeEvent event) {
        for (final Player player : CardsAgainstHumanity.players) {
            if (player.getName().equals(event.getOldNick())) {
                player.setName(event.getNewNick());
            }
        }
    }
}
