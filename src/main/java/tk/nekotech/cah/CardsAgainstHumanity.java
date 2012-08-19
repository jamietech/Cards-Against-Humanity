package tk.nekotech.cah;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;

import org.pircbotx.Colors;
import org.pircbotx.PircBotX;
import org.pircbotx.User;

import tk.nekotech.cah.bots.CardBot;
import tk.nekotech.cah.bots.SpamBot;
import tk.nekotech.cah.card.BlackCard;
import tk.nekotech.cah.card.WhiteCard;
import tk.nekotech.cah.tasks.CardConnect;
import tk.nekotech.cah.tasks.MasterConnect;
import tk.nekotech.cah.tasks.StartGame;

public class CardsAgainstHumanity extends PircBotX {
    
    public static CardBot cardBot;
    public static SpamBot spamBot;
    private static Timer connect;
    public static GameStatus gameStatus;

    public static void main(String[] args) throws Exception {
    	System.out.println("Starting...");
    	gameStatus = GameStatus.BOT_START;
    	setupCards();
    	connect = new Timer();
    	connect.schedule(new MasterConnect(), 5000);
    	connect.schedule(new CardConnect(), 10000);
    }
    
    public static ArrayList<Player> players;
    public static List<WhiteCard> whiteCards;
    public static ArrayList<BlackCard> blackCards;
    public static Player czar;
    public static BlackCard blackCard;
    
    private static void setupCards() throws IOException {
    	players = new ArrayList<Player>();
    	whiteCards = Collections.synchronizedList(new ArrayList<WhiteCard>());
    	blackCards = new ArrayList<BlackCard>();
    	File blackFile = new File("black.txt");
        File whiteFile = new File("white.txt");
        ifNotExists(blackFile, whiteFile);
        FileReader fileReader = new FileReader(blackFile);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            blackCards.add(new BlackCard(line));
        }
        fileReader.close();
        bufferedReader.close();
        fileReader = new FileReader("white.txt");
        bufferedReader = new BufferedReader(fileReader);
        while ((line = bufferedReader.readLine()) != null) {
            whiteCards.add(new WhiteCard(line));
        }
        fileReader.close();
        bufferedReader.close();
    }

    private static void ifNotExists(File... files) {
        for (File file : files) {
            if (file.exists()) {
                continue;
            }
            System.out.println("Saving " + file);
            InputStream inputStream = CardsAgainstHumanity.class.getClassLoader().getResourceAsStream(file.getName());
            try {
                file.createNewFile();
                FileOutputStream outputStream = new FileOutputStream(file);
                byte buffer[] = new byte[1024];
                int length;
                while ((length = inputStream.read(buffer)) > 0) {
                    outputStream.write(buffer, 0, length);
                }
                outputStream.close();
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    public static void ready() {
    	spamBot.sendMessage("#CAH", Colors.BOLD + "Welcome to Cards Against Humanity!");
    	spamBot.sendMessage("#CAH", "To join the game simply say 'join' in chat (without quotes) and you will be added next round!");
    	gameStatus = GameStatus.NOT_ENOUGH_PLAYERS;
    	connect.scheduleAtFixedRate(new StartGame(spamBot), 15000, 15000);
    	spamBot.sendMessage("#CAH", "Running version " + spamBot.getCAHVersion() + " with " + whiteCards.size() + " white cards and " + blackCards.size() + " black cards.");
    }
    
    public static void startGame() {
    	gameStatus = GameStatus.IN_SESSION;
    	for (Player player : players) {
    		player.drawCardsForStart();
    	}
    	cardBot.messageAllCards();
    	Collections.shuffle(blackCards);
    	BlackCard card = blackCards.get(0);
    	blackCard = card;
    	spamBot.sendMessage("#CAH", "Fill in the " + (card.getAnswers() > 1 ? "blanks" : "blank") + ": " + Colors.BOLD + card.getColored() + " [Play your white " + (card.getAnswers() > 1 ? "cards by saying their numbers" : "card by saying it's number") + "]");
    }
    
    public static Player getPlayer(String username) {
    	for (Player player : players) {
    		if (player.getName().equals(username)) {
    			return player;
    		}
    	}
    	return null;
    }

}
