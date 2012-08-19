package tk.nekotech.cah.listeners;

import org.pircbotx.PircBotX;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.hooks.events.NickChangeEvent;

import tk.nekotech.cah.CardsAgainstHumanity;
import tk.nekotech.cah.GameStatus;
import tk.nekotech.cah.Player;
import tk.nekotech.cah.card.WhiteCard;

public class PlayerListener extends MasterListener {

	public PlayerListener(PircBotX bot) {
		super(bot);
	}

	@SuppressWarnings("rawtypes")
	public void onNickChange(NickChangeEvent event) {
		for (Player player : CardsAgainstHumanity.players) {
			if (player.getName().equals(event.getOldNick())) {
				player.setName(event.getNewNick());
			}
		}
	}
	
	@SuppressWarnings("rawtypes")
	public void onMessage(MessageEvent event) {
		if (event.getUser().getNick().contains("CAH-Master") || CardsAgainstHumanity.gameStatus != GameStatus.IN_SESSION) {
			return;
		}
		Player player = CardsAgainstHumanity.getPlayer(event.getUser().getNick());
		if (player == null) {
			return;
		}
		String[] message = event.getMessage().split(" ");
		if (message.length == 1 && CardsAgainstHumanity.blackCard.getAnswers() == 1) {
			int answer = 0;
			try {
				answer = Integer.parseInt(message[0]);
				if (answer == 0 || answer > 10) {
					bot.sendNotice(event.getUser(), "Use a number that you actually have!");
				} else {
					WhiteCard card = player.getCards().get(answer--);
					bot.sendNotice(event.getUser(), "Saved answer " + card.getFull() + "!");
					player.playCard(card);
				}
			} catch (NumberFormatException e) {
				bot.sendNotice(event.getUser(), "You can't answer with that! Try use a number instead.");
			}
		} else if (message.length == 2 && CardsAgainstHumanity.blackCard.getAnswers() == 2) {
			int[] answers = new int[2];
			for (int i = 0; i < 2; i++) {
				try {
					answers[i-1] = Integer.parseInt(message[i-1]);
				} catch (NumberFormatException e) {
					answers[i-1] = -55;
				}
			}
			if (answers[0] == -55 || answers[1] == -55) {
				bot.sendNotice(event.getUser(), "You can't answer with that! Ensure you entered two numbers.");
			} else if (answers[0] == 0 || answers[0] > 10 || answers[1] == 0 || answers[1] > 10) { 
				bot.sendNotice(event.getUser(), "Use a number that you actually have!");
			} else {
				WhiteCard[] cards = new WhiteCard[2];
				for (int i = 0; i < 2; i++) {
					cards[i-1] = player.getCards().get(i-1);
				}
				bot.sendNotice(event.getUser(), "Saved answers " + cards[0].getFull() + ", " + cards[1].getFull() + "!");
				player.playCards(cards);
			}
		} else {
			boolean one = CardsAgainstHumanity.blackCard.getAnswers() == 1;
			bot.sendNotice(event.getUser(), "You answered incorrectly! Enter the " + (one ? "number" : "numbers") + " in relation to the " + (one ? "card" : "cards") + " you wish to play.");
		}
	}
	
}
