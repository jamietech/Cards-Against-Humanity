package tk.nekotech.cah;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import tk.nekotech.cah.card.WhiteCard;

public class Player {
    private String name;
    private ArrayList<WhiteCard> whiteCards = new ArrayList<WhiteCard>();
    private int awesomePoints = 0;
    private WhiteCard[] playedCards = null;

    public Player(String name) {
        this.name = name;
    }

    public void addPoint() {
        awesomePoints++;
    }

    public String getName() {
        return name;
    }
    
    public int getScore() {
        return awesomePoints;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public void drawCardsForStart() {
    	Collections.shuffle(CardsAgainstHumanity.whiteCards);
    	int i = 0;
    	List<WhiteCard> newCards = new ArrayList<WhiteCard>(CardsAgainstHumanity.whiteCards);
    	for (WhiteCard card : CardsAgainstHumanity.whiteCards) {
    		if (i < 10) {
    			whiteCards.add(card);
        		newCards.remove(card);
        		i++;
    		}
    	}
    	CardsAgainstHumanity.whiteCards = newCards;
    }
    
    public boolean playCard(WhiteCard card) {
    	if (!whiteCards.contains(card)) {
    		return false;
    	}
    	whiteCards.remove(card);
    	Collections.shuffle(CardsAgainstHumanity.whiteCards);
    	WhiteCard whiteCard = CardsAgainstHumanity.whiteCards.get(0);
    	whiteCards.add(whiteCard);
    	CardsAgainstHumanity.whiteCards.remove(whiteCard);
    	return true;
    }
    
    public boolean playCards(WhiteCard[] cards) {
    	for (WhiteCard card : cards) {
    		if (!whiteCards.contains(card)) {
    			return false;
    		}
    	}
    	whiteCards.removeAll(Arrays.asList(cards));
    	CardsAgainstHumanity.whiteCards.addAll(Arrays.asList(cards));
    	playedCards = cards;
    	return true;
    }
    
    public ArrayList<WhiteCard> getCards() {
    	return whiteCards;
    }
    
    public WhiteCard[] getPlayedCards() {
    	return playedCards;
    }
    
    public boolean hasPlayedCards() {
    	return playedCards != null;
    }
    
    public void newRound() {
    	playedCards = null;
    }

    @Override
    public String toString() {
        return "Player{" + "name=" + getName() + '}';
    }
    
    @Override
    public boolean equals(Object object) {
        if(object instanceof Player) {
            return ((Player) object).name.equalsIgnoreCase(this.name);
        }
        if(object instanceof String) {
            return ((String) object).equalsIgnoreCase(this.name);
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return name.toLowerCase().hashCode();
    }

}
