package tk.nekotech.cah;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import tk.nekotech.cah.card.WhiteCard;

public class Player {
    private String name;
    private final ArrayList<WhiteCard> whiteCards = new ArrayList<WhiteCard>();
    private int awesomePoints = 0;
    private WhiteCard[] playedCards = null;
    private boolean czar;
    private boolean isWaiting;

    public Player(final String name) {
        this.name = name;
        this.setWaiting(false);
        this.setCzar(false);
    }

    public void addPoint() {
        this.awesomePoints++;
    }

    public void drawCardsForStart() {
        Collections.shuffle(CardsAgainstHumanity.whiteCards);
        int i = 0;
        final List<WhiteCard> newCards = new ArrayList<WhiteCard>(CardsAgainstHumanity.whiteCards);
        for (final WhiteCard card : CardsAgainstHumanity.whiteCards) {
            if (i < 10) {
                this.whiteCards.add(card);
                newCards.remove(card);
                i++;
            }
        }
        CardsAgainstHumanity.whiteCards = newCards;
    }

    @Override
    public boolean equals(final Object object) {
        if (object instanceof Player) {
            return ((Player) object).name.equalsIgnoreCase(this.name);
        }
        if (object instanceof String) {
            return ((String) object).equalsIgnoreCase(this.name);
        }
        return false;
    }

    public ArrayList<WhiteCard> getCards() {
        return this.whiteCards;
    }

    public String getName() {
        return this.name;
    }

    public WhiteCard[] getPlayedCards() {
        return this.playedCards;
    }

    public int getScore() {
        return this.awesomePoints;
    }

    @Override
    public int hashCode() {
        return this.name.toLowerCase().hashCode();
    }

    public boolean hasPlayedCards() {
        return this.playedCards != null;
    }

    public void newRound() {
        this.playedCards = null;
        if (this.whiteCards.size() > 10) {
            Collections.shuffle(CardsAgainstHumanity.whiteCards);
            System.out.println("Replenishing " + (10 - this.whiteCards.size()) + " cards for " + this.getName() + ".");
            for (int i = 0; i < (10 - this.whiteCards.size()); i++) {
                final WhiteCard whiteCard = CardsAgainstHumanity.whiteCards.get(i);
                this.whiteCards.add(whiteCard);
                CardsAgainstHumanity.whiteCards.remove(whiteCard);
            }
        }
    }

    public boolean playCard(final WhiteCard card) {
        if (!this.whiteCards.contains(card)) {
            return false;
        }
        this.whiteCards.remove(card);
        this.playedCards = new WhiteCard[] { card };
        return true;
    }

    public boolean playCards(final WhiteCard[] cards) {
        for (final WhiteCard card : cards) {
            if (!this.whiteCards.contains(card)) {
                return false;
            }
        }
        this.whiteCards.removeAll(Arrays.asList(cards));
        CardsAgainstHumanity.whiteCards.addAll(Arrays.asList(cards));
        this.playedCards = cards;
        return true;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public boolean isCzar() {
        return this.czar;
    }

    public void setCzar(boolean czar) {
        this.czar = czar;
    }

    public boolean isWaiting() {
        return this.isWaiting;
    }

    public void setWaiting(boolean waiting) {
        this.isWaiting = waiting;
    }

    @Override
    public String toString() {
        return "Player{" + "name=" + this.getName() + '}';
    }
}
