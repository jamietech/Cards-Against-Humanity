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
import org.pircbotx.Channel;
import org.pircbotx.Colors;
import org.pircbotx.PircBotX;
import tk.nekotech.cah.bots.CardBot;
import tk.nekotech.cah.bots.SpamBot;
import tk.nekotech.cah.card.BlackCard;
import tk.nekotech.cah.card.WhiteCard;
import tk.nekotech.cah.runnables.Startup;
import tk.nekotech.cah.tasks.CardConnect;
import tk.nekotech.cah.tasks.MasterConnect;
import tk.nekotech.cah.tasks.Nag;
import tk.nekotech.cah.tasks.StartGame;

public class CardsAgainstHumanity extends PircBotX {
    public static CardBot cardBot;
    public static SpamBot spamBot;
    private static Timer connect;
    private static Timer nagger;
    public static Nag nag;
    public static GameStatus gameStatus;
    public static ArrayList<Player> players;
    public static ArrayList<Player> playerIter;
    public static List<WhiteCard> whiteCards;
    public static ArrayList<BlackCard> blackCards;
    public static Player czar;
    public static BlackCard blackCard;
    public static String topic = Colors.BOLD + "Cards Against Humanity" + Colors.NORMAL;

    public static void checkNext() {
        if (CardsAgainstHumanity.proceedToNext()) {
            CardsAgainstHumanity.cardBot.sendMessage("#CAH", Colors.BOLD + "All players have submitted their cards." + Colors.NORMAL + " Time for " + CardsAgainstHumanity.czar.getName() + " to pick the winning card.");
            CardsAgainstHumanity.cardBot.sendMessage("#CAH", CardsAgainstHumanity.blackCard.getColored());
            CardsAgainstHumanity.cardBot.sendNotice(CardsAgainstHumanity.czar.getName(), "Say the number of the card you wish to win.");
            Collections.shuffle(CardsAgainstHumanity.players);
            CardsAgainstHumanity.playerIter = new ArrayList<Player>(CardsAgainstHumanity.players);
            CardsAgainstHumanity.playerIter.remove(CardsAgainstHumanity.czar);
            for (int i = 0; i < CardsAgainstHumanity.playerIter.size(); i++) {
                final Player player = CardsAgainstHumanity.playerIter.get(i);
                if (player.isWaiting()) {
                    CardsAgainstHumanity.playerIter.remove(player);
                }
            }
            for (int i = 0; i < CardsAgainstHumanity.playerIter.size(); i++) {
                final Player player = CardsAgainstHumanity.playerIter.get(i);
                if (CardsAgainstHumanity.blackCard.getAnswers() == 1) {
                    CardsAgainstHumanity.cardBot.sendMessage("#CAH", i + 1 + ": " + player.getPlayedCards()[0].getColored());
                } else {
                    CardsAgainstHumanity.cardBot.sendMessage("#CAH", i + 1 + ": " + player.getPlayedCards()[0].getColored() + " | " + player.getPlayedCards()[1].getColored());
                }
            }
            CardsAgainstHumanity.gameStatus = GameStatus.CZAR_TURN;
        }
    }

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

    public static boolean inSession() {
        return CardsAgainstHumanity.gameStatus == GameStatus.IN_SESSION || CardsAgainstHumanity.gameStatus == GameStatus.CZAR_TURN;
    }

    public static void main(final String[] args) throws Exception {
        System.out.println("Starting...");
        CardsAgainstHumanity.gameStatus = GameStatus.BOT_START;
        CardsAgainstHumanity.setupCards();
        CardsAgainstHumanity.connect = new Timer();
        CardsAgainstHumanity.connect.schedule(new MasterConnect(), 5000);
        CardsAgainstHumanity.connect.schedule(new CardConnect(), 10000);
        new Startup().start();
    }

    public static void nextRound() {
        CardsAgainstHumanity.nagger.cancel();
        CardsAgainstHumanity.nagger = new Timer();
        CardsAgainstHumanity.gameStatus = GameStatus.IN_SESSION;
        if (CardsAgainstHumanity.players.size() < 3) {
            CardsAgainstHumanity.spamBot.sendMessage("#CAH", "Uh-oh! There aren't enough players to continue. Say 'quit' to quit, 'join' to join.");
            CardsAgainstHumanity.gameStatus = GameStatus.NOT_ENOUGH_PLAYERS;
            return;
        }
        for (final Player player : CardsAgainstHumanity.players) {
            if (player.isWaiting()) {
                player.drawCardsForStart();
                player.setWaiting(false);
            }
            player.newRound();
        }
        CardsAgainstHumanity.czar.setCzar(false);
        Collections.shuffle(CardsAgainstHumanity.players);
        CardsAgainstHumanity.czar = CardsAgainstHumanity.players.get(0);
        CardsAgainstHumanity.czar.setCzar(true);
        CardsAgainstHumanity.spamBot.sendMessage("#CAH", "The new czar is " + Colors.BOLD + CardsAgainstHumanity.czar.getName());
        Collections.shuffle(CardsAgainstHumanity.blackCards);
        final BlackCard card = CardsAgainstHumanity.blackCards.get(0);
        CardsAgainstHumanity.blackCard = card;
        CardsAgainstHumanity.spamBot.sendMessage("#CAH", "Fill in the " + (card.getAnswers() > 1 ? "blanks" : "blank") + ": " + Colors.BOLD + card.getColored() + " [Play your white " + (card.getAnswers() > 1 ? "cards by saying their numbers" : "card by saying its number") + "]");
        CardsAgainstHumanity.cardBot.messageAllCards();
        CardsAgainstHumanity.cardBot.sendNotice(CardsAgainstHumanity.czar.getName(), "You don't have cards because you're the czar! Once everyone has played their cards you will be prompted to choose the best.");
        CardsAgainstHumanity.nagger.schedule(CardsAgainstHumanity.nag, 60000, 60000);
    }

    public static boolean proceedToNext() {
        for (final Player player : CardsAgainstHumanity.players) {
            if (!player.hasPlayedCards() && !player.isCzar() && !player.isWaiting()) {
                return false;
            }
        }
        return true;
    }

    public static void processLeave(final Player player) {
        if (player.isCzar()) {
            Collections.shuffle(CardsAgainstHumanity.players);
            player.setCzar(false);
            CardsAgainstHumanity.czar = CardsAgainstHumanity.players.get(0);
            CardsAgainstHumanity.czar.setCzar(true);
            CardsAgainstHumanity.spamBot.sendMessage("#CAH", "The current czar, " + player.getName() + " quit the game and " + CardsAgainstHumanity.czar.getName() + " is now the new czar for this round.");
            CardsAgainstHumanity.czar.newRound();
            if (CardsAgainstHumanity.gameStatus == GameStatus.CZAR_TURN) {
                if (CardsAgainstHumanity.players.size() - 1 < 3) {
                    CardsAgainstHumanity.spamBot.sendMessage("#CAH", "Uh-oh! There aren't enough players to continue. Say 'quit' to quit, 'join' to join.");
                    CardsAgainstHumanity.gameStatus = GameStatus.NOT_ENOUGH_PLAYERS;
                    return;
                }
                CardsAgainstHumanity.cardBot.sendNotice(CardsAgainstHumanity.czar.getName(), "Say the number of the card you wish to win.");
            }
        } else if (CardsAgainstHumanity.gameStatus == GameStatus.CZAR_TURN) {
            CardsAgainstHumanity.cardBot.sendMessage("#CAH", "Uh-oh! " + player.getName() + " quit the game. Let's start a new round.");
            CardsAgainstHumanity.nextRound();
        }
    }

    public static void ready() {
        final Channel chan = CardsAgainstHumanity.spamBot.getChannel("#CAH");
        CardsAgainstHumanity.nagger = new Timer();
        CardsAgainstHumanity.spamBot.setTopic(chan, CardsAgainstHumanity.topic + " | Say 'join' without quotes to join the game. | Report issues at https://github.com/soaringcats/Cards-Against-Humanity/issues");
        CardsAgainstHumanity.spamBot.sendMessage("#CAH", Colors.BOLD + "Welcome to Cards Against Humanity!");
        CardsAgainstHumanity.spamBot.sendMessage("#CAH", "To join the game simply say 'join' in chat (without quotes) and you will be added next round!");
        CardsAgainstHumanity.gameStatus = GameStatus.NOT_ENOUGH_PLAYERS;
        CardsAgainstHumanity.connect.scheduleAtFixedRate(new StartGame(CardsAgainstHumanity.spamBot), 60000, 60000);
        CardsAgainstHumanity.spamBot.sendMessage("#CAH", "Running version " + CardsAgainstHumanity.spamBot.getCAHVersion() + " with " + CardsAgainstHumanity.whiteCards.size() + " white cards and " + CardsAgainstHumanity.blackCards.size() + " black cards.");
        CardsAgainstHumanity.nag = new Nag();
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
        CardsAgainstHumanity.czar = CardsAgainstHumanity.players.get(0);
        CardsAgainstHumanity.nextRound();
    }
}
