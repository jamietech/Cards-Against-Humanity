package tk.nekotech.cah.listeners;

import java.util.Collections;
import org.pircbotx.PircBotX;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.hooks.events.NickChangeEvent;
import tk.nekotech.cah.CardsAgainstHumanity;
import tk.nekotech.cah.GameStatus;
import tk.nekotech.cah.Player;
import tk.nekotech.cah.card.WhiteCard;

public class PlayerListener extends MasterListener {
    private final CardsAgainstHumanity cah;

    public PlayerListener(final PircBotX bot, final CardsAgainstHumanity cah) {
        super(bot);
        this.cah = cah;
    }

    @Override
    @SuppressWarnings("rawtypes")
    public void onMessage(final MessageEvent event) {
        if (event.getUser().getNick().contains("CAH-Master") || !this.cah.inSession() || event.getMessage().equalsIgnoreCase("join") || event.getMessage().equalsIgnoreCase("quit")) {
            return;
        }
        final Player player = this.cah.getPlayer(event.getUser().getNick());
        if (player == null) {
            return;
        }
        final String[] message = event.getMessage().toLowerCase().split(" ");
        if (message.length == 1 && message[0].equalsIgnoreCase("info")) {
            this.bot.sendNotice(event.getUser(), "You currently have " + player.getScore() + " awesome points. The black card is: " + this.cah.blackCard.getColored());
            this.bot.sendNotice(event.getUser(), "Your cards are: " + this.cah.getCards(player));
            return;
        }
        if (player.isCzar()) {
            if (this.cah.gameStatus == GameStatus.IN_SESSION && message.length == 1) {
                this.bot.sendNotice(event.getUser(), "You're the czar; please wait until it's time for voting.");
            } else {
                if (message.length == 1) {
                    int chosen = 0;
                    try {
                        chosen = Integer.parseInt(message[0]);
                    } catch (final NumberFormatException e) {
                        this.bot.sendNotice(event.getUser(), "Uh-oh! I couldn't find that answer. Try a number instead.");
                    }
                    if (chosen > this.cah.playerIter.size()) {
                        this.bot.sendNotice(event.getUser(), "I couldn't find that answer.");
                    } else {
                        chosen = chosen - 1;
                        final Player win = this.cah.playerIter.get(chosen);
                        final StringBuilder send = new StringBuilder();
                        send.append(win.getName() + " wins this round; card was ");
                        if (this.cah.blackCard.getAnswers() == 1) {
                            send.append(this.cah.blackCard.getColored().replace("_", win.getPlayedCards()[0].getColored()));
                        } else {
                            send.append(this.cah.blackCard.getColored().replaceFirst("_", win.getPlayedCards()[0].getColored()).replaceFirst("_", win.getPlayedCards()[1].getColored()));
                        }
                        this.cah.spamBot.sendMessage("#CAH", send.toString());
                        win.addPoint();
                        this.cah.nextRound();
                    }
                }
            }
            return;
        }
        if (this.cah.gameStatus == GameStatus.CZAR_TURN) {
            return;
        }
        if (message.length == 1 && this.cah.blackCard.getAnswers() == 1) {
            if (player.isWaiting()) {
                this.bot.sendNotice(event.getUser(), "You're currently waiting for the next round. Hold tight!");
            }
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
                        this.cah.checkNext();
                    }
                }
            } catch (final NumberFormatException e) {
                this.bot.sendNotice(event.getUser(), "You can't answer with that! Try use a number instead.");
            }
        } else if (message.length == 2 && this.cah.blackCard.getAnswers() == 2) {
            if (player.isWaiting()) {
                this.bot.sendNotice(event.getUser(), "You're currently waiting for the next round. Hold tight!");
            }
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
                    this.cah.checkNext();
                }
            }
        } else if (message.length == 2 && message[0].equals("drop")) {
            int card = 0;
            try {
                card = Integer.parseInt(message[1]);
                if (player.getScore() < 1) {
                    this.bot.sendNotice(event.getUser(), "You don't have enough awesome points to do that.");
                } else if (card > 10 || card < 1) {
                    this.bot.sendNotice(event.getUser(), "You don't have that card! Pick a card between 1-10.");
                } else if (player.getChanged() == 10) {
                    this.bot.sendNotice(event.getUser(), "You've already changed your full deck this round. Wait until next round before changing again.");
                } else {
                    card = card - 1;
                    final WhiteCard old = player.getCards().get(card);
                    player.getCards().remove(old);
                    Collections.shuffle(this.cah.whiteCards);
                    final WhiteCard newc = this.cah.whiteCards.get(0);
                    player.getCards().add(newc);
                    this.cah.whiteCards.remove(newc);
                    this.cah.whiteCards.add(old);
                    player.subtractPoint();
                    this.bot.sendNotice(event.getUser(), "You dropped card [" + old.getColored() + "] and picked up [" + newc.getColored() + "]; you now have " + player.getScore() + " awesome points.");
                }
            } catch (final NumberFormatException e) {
                this.bot.sendNotice(event.getUser(), "You need to specify an amount of cards to drop.");
            }
        }
    }

    @Override
    @SuppressWarnings("rawtypes")
    public void onNickChange(final NickChangeEvent event) {
        final Player player = this.cah.getPlayer(event.getOldNick());
        if (player != null) {
            player.setName(event.getNewNick());
        }
    }
}
