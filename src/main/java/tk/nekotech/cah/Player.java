package tk.nekotech.cah;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import tk.nekotech.cah.card.WhiteCard;

public class Player {
    private String name;
    private final CardsAgainstHumanity cah;
    private final ArrayList<WhiteCard> whiteCards = new ArrayList<WhiteCard>();
    private int awesomePoints = 0;
    private int changedCards = 0;
    private int warnings = 0;
    private WhiteCard[] playedCards = null;
    private boolean czar;
    private boolean isWaiting;

    public Player(final String name, final CardsAgainstHumanity cah) {
        this.name = name;
        this.cah = cah;
        this.setWaiting(false);
        this.setCzar(false);
    }

    public void addChanged() {
        this.changedCards++;
    }

    public void addPoint() {
        this.awesomePoints++;
    }

    public void addWarning() {
        this.warnings++;
    }

    public void drawCardsForStart() {
        Collections.shuffle(this.cah.whiteCards);
        int i = 0;
        final List<WhiteCard> newCards = new ArrayList<WhiteCard>(this.cah.whiteCards);
        for (final WhiteCard card : this.cah.whiteCards) {
            if (i < 10) {
                this.whiteCards.add(card);
                newCards.remove(card);
                i++;
            }
        }
        this.cah.whiteCards = newCards;
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

    public int getChanged() {
        return this.changedCards;
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

    public int getWarnings() {
        return this.warnings;
    }

    @Override
    public int hashCode() {
        return this.name.toLowerCase().hashCode();
    }

    public boolean hasPlayedCards() {
        return this.playedCards != null;
    }

    public boolean isCzar() {
        return this.czar;
    }

    public boolean isWaiting() {
        return this.isWaiting;
    }

    public void newRound() {
        this.playedCards = null;
        this.changedCards = 0;
        this.warnings = 0;
    }

    public boolean playCard(final WhiteCard card) {
        if (!this.whiteCards.contains(card)) {
            return false;
        }
        this.whiteCards.remove(card);
        this.playedCards = new WhiteCard[] { card };
        Collections.shuffle(this.cah.whiteCards);
        final WhiteCard whiteCard = this.cah.whiteCards.get(0);
        this.whiteCards.add(whiteCard);
        this.cah.whiteCards.remove(whiteCard);
        return true;
    }

    public boolean playCards(final WhiteCard[] cards) {
        for (final WhiteCard card : cards) {
            if (!this.whiteCards.contains(card)) {
                return false;
            }
        }
        this.whiteCards.removeAll(Arrays.asList(cards));
        this.cah.whiteCards.addAll(Arrays.asList(cards));
        this.playedCards = cards;
        Collections.shuffle(this.cah.whiteCards);
        WhiteCard whiteCard = this.cah.whiteCards.get(0);
        this.whiteCards.add(whiteCard);
        this.cah.whiteCards.remove(whiteCard);
        whiteCard = this.cah.whiteCards.get(0);
        this.whiteCards.add(whiteCard);
        this.cah.whiteCards.remove(whiteCard);
        return true;
    }

    public void setCzar(final boolean czar) {
        this.czar = czar;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public void setWaiting(final boolean waiting) {
        this.isWaiting = waiting;
    }

    public void subtractPoint() {
        this.awesomePoints--;
    }

    @Override
    public String toString() {
        return "Player{" + "name=" + this.getName() + '}';
    }
}
