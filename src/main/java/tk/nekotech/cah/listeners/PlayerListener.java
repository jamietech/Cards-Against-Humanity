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
        if (event.getUser().getNick().contains("CAH-Master") || !CardsAgainstHumanity.inSession() || event.getMessage().equalsIgnoreCase("join") || event.getMessage().equalsIgnoreCase("quit")) {
            return;
        }
        final Player player = CardsAgainstHumanity.getPlayer(event.getUser().getNick());
        if (player == null) {
            return;
        }
        final String[] message = event.getMessage().split(" ");
        if (player.isCzar()) {
            if (CardsAgainstHumanity.gameStatus == GameStatus.IN_SESSION) {
                bot.sendNotice(event.getUser(), "You're the czar; please wait until it's time for voting.");
            } else {
                if (message.length == 1) {
                    int chosen = 0;
                    try {
                        chosen = Integer.parseInt(message[0]);
                    } catch (NumberFormatException e) {
                        this.bot.sendNotice(event.getUser(), "Uh-oh! I couldn't find that answer. Try a number instead.");
                    }
                    if (chosen > (CardsAgainstHumanity.players.size() - 1)) {
                        this.bot.sendNotice(event.getUser(), "I couldn't find that answer.");
                    } else {
                        chosen = chosen - 1;
                        Player win = CardsAgainstHumanity.players.get(chosen);
                        StringBuilder send = new StringBuilder();
                        send.append(win.getName() + " wins this round; card was ");
                        if (CardsAgainstHumanity.blackCard.getAnswers() == 1) {
                            send.append(CardsAgainstHumanity.blackCard.getColored().replace("_", win.getPlayedCards()[0].getColored()));
                        } else {
                            send.append(CardsAgainstHumanity.blackCard.getColored().replaceFirst("_", win.getPlayedCards()[0].getColored()).replaceFirst("_", win.getPlayedCards()[1].getFull()));
                        }
                        this.bot.sendMessage("#CAH", send.toString());
                        win.addPoint();
                        CardsAgainstHumanity.nextRound();
                    }
                } else {
                    this.bot.sendNotice(event.getUser(), "Try sending 1 number to pick the winner.");
                }
            }
            return;
        }
        if (message.length == 1 && CardsAgainstHumanity.blackCard.getAnswers() == 1) {
            int answer = 0;
            try {
                answer = Integer.parseInt(message[0]);
                if (answer < 1 || answer > 10) {
                    this.bot.sendNotice(event.getUser(), "Use a number that you actually have!");
                } else {
                    if (player.hasPlayedCards()) {
                        this.bot.sendNotice(event.getUser(), "You've already played a card this round!");
                    } else {
                        answer = answer - 1;
                        final WhiteCard card = player.getCards().get(answer);
                        this.bot.sendNotice(event.getUser(), "Saved answer " + card.getFull() + "!");
                        player.playCard(card);
                        CardsAgainstHumanity.checkNext();
                    }
                }
            } catch (final NumberFormatException e) {
                this.bot.sendNotice(event.getUser(), "You can't answer with that! Try use a number instead.");
            }
        } else if (message.length == 2 && CardsAgainstHumanity.blackCard.getAnswers() == 2) {
            final int[] answers = new int[2];
            for (int i = 0; i < 2; i++) {
                try {
                    answers[i] = Integer.parseInt(message[i]);
                } catch (final NumberFormatException e) {
                    answers[i] = -55;
                }
            }
            if (answers[0] == -55 || answers[1] == -55) {
                this.bot.sendNotice(event.getUser(), "You can't answer with that! Ensure you entered two numbers.");
            } else if (answers[0] < 1 || answers[0] > 10 || answers[1] < 1 || answers[1] > 10) {
                this.bot.sendNotice(event.getUser(), "Use a number that you actually have!");
            } else {
                if (player.hasPlayedCards()) {
                    this.bot.sendNotice(event.getUser(), "You've already played your cards this round!");
                } else {
                    final WhiteCard[] cards = new WhiteCard[2];
                    for (int i = 0; i < 2; i++) {
                        answers[i] = answers[i] - 1;
                        cards[i] = player.getCards().get(answers[i]);
                    }
                    this.bot.sendNotice(event.getUser(), "Saved answers " + cards[0].getFull() + ", " + cards[1].getFull() + "!");
                    player.playCards(cards);
                    CardsAgainstHumanity.checkNext();
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
