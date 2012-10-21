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
    public static ArrayList<Player> players;
    public static List<WhiteCard> whiteCards;
    public static ArrayList<BlackCard> blackCards;
    public static Player czar;
    public static BlackCard blackCard;

    public static Player getPlayer(final String username) {
        for (final Player player : CardsAgainstHumanity.players) {
            if (player.getName().equals(username)) {
                return player;
            }
        }
        return null;
    }

    private static void ifNotExists(final File... files) {
        for (final File file : files) {
            if (file.exists()) {
                continue;
            }
            System.out.println("Saving " + file);
            final InputStream inputStream = CardsAgainstHumanity.class.getClassLoader().getResourceAsStream(file.getName());
            try {
                file.createNewFile();
                final FileOutputStream outputStream = new FileOutputStream(file);
                final byte buffer[] = new byte[1024];
                int length;
                while ((length = inputStream.read(buffer)) > 0) {
                    outputStream.write(buffer, 0, length);
                }
                outputStream.close();
                inputStream.close();
            } catch (final IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(final String[] args) throws Exception {
        System.out.println("Starting...");
        CardsAgainstHumanity.gameStatus = GameStatus.BOT_START;
        CardsAgainstHumanity.setupCards();
        CardsAgainstHumanity.connect = new Timer();
        CardsAgainstHumanity.connect.schedule(new MasterConnect(), 5000);
        CardsAgainstHumanity.connect.schedule(new CardConnect(), 10000);
    }

    public static void ready() {
        CardsAgainstHumanity.spamBot.sendMessage("#CAH", Colors.BOLD + "Welcome to Cards Against Humanity!");
        CardsAgainstHumanity.spamBot.sendMessage("#CAH", "To join the game simply say 'join' in chat (without quotes) and you will be added next round!");
        CardsAgainstHumanity.gameStatus = GameStatus.NOT_ENOUGH_PLAYERS;
        CardsAgainstHumanity.connect.scheduleAtFixedRate(new StartGame(CardsAgainstHumanity.spamBot), 15000, 15000);
        CardsAgainstHumanity.spamBot.sendMessage("#CAH", "Running version " + CardsAgainstHumanity.spamBot.getCAHVersion() + " with " + CardsAgainstHumanity.whiteCards.size() + " white cards and " + CardsAgainstHumanity.blackCards.size() + " black cards.");
    }

    private static void setupCards() throws IOException {
        CardsAgainstHumanity.players = new ArrayList<Player>();
        CardsAgainstHumanity.whiteCards = Collections.synchronizedList(new ArrayList<WhiteCard>());
        CardsAgainstHumanity.blackCards = new ArrayList<BlackCard>();
        final File blackFile = new File("black.txt");
        final File whiteFile = new File("white.txt");
        CardsAgainstHumanity.ifNotExists(blackFile, whiteFile);
        FileReader fileReader = new FileReader(blackFile);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            CardsAgainstHumanity.blackCards.add(new BlackCard(line));
        }
        fileReader.close();
        bufferedReader.close();
        fileReader = new FileReader("white.txt");
        bufferedReader = new BufferedReader(fileReader);
        while ((line = bufferedReader.readLine()) != null) {
            CardsAgainstHumanity.whiteCards.add(new WhiteCard(line));
        }
        fileReader.close();
        bufferedReader.close();
    }

    public static void startGame() {
        CardsAgainstHumanity.gameStatus = GameStatus.IN_SESSION;
        for (final Player player : CardsAgainstHumanity.players) {
            player.drawCardsForStart();
        }
        Collections.shuffle(CardsAgainstHumanity.blackCards);
        final BlackCard card = CardsAgainstHumanity.blackCards.get(0);
        CardsAgainstHumanity.blackCard = card;
        CardsAgainstHumanity.spamBot.sendMessage("#CAH", "Fill in the " + (card.getAnswers() > 1 ? "blanks" : "blank") + ": " + Colors.BOLD + card.getColored() + " [Play your white " + (card.getAnswers() > 1 ? "cards by saying their numbers" : "card by saying it's number") + "]");
        CardsAgainstHumanity.cardBot.messageAllCards();
    }
}
