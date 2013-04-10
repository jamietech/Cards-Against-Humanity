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
import tk.nekotech.cah.bots.CardBot;
import tk.nekotech.cah.bots.SpamBot;
import tk.nekotech.cah.card.BlackCard;
import tk.nekotech.cah.card.WhiteCard;
import tk.nekotech.cah.runnables.Startup;
import tk.nekotech.cah.tasks.CardConnect;
import tk.nekotech.cah.tasks.MasterConnect;
import tk.nekotech.cah.tasks.Nag;
import tk.nekotech.cah.tasks.StartGame;

public class CardsAgainstHumanity {
    public static void main(final String[] args) {
        System.out.println("Starting...");
        new CardsAgainstHumanity();
    }

    public CardBot cardBot;
    public SpamBot spamBot;
    private final Timer connect;
    private Timer nagger;
    public GameStatus gameStatus;
    public ArrayList<Player> players;
    public ArrayList<Player> playerIter;
    public List<WhiteCard> whiteCards;
    public ArrayList<BlackCard> blackCards;
    public Player czar;
    public BlackCard blackCard;
    public String topic = Colors.BOLD + "Cards Against Humanity" + Colors.NORMAL + " | Report issues at https://github.com/soaringcats/cards-against-humanity/issues";

    public CardsAgainstHumanity() {
        this.gameStatus = GameStatus.BOT_START;
        try {
            this.setupCards();
        } catch (final IOException e) {
            e.printStackTrace();
        }
        this.connect = new Timer();
        this.connect.schedule(new MasterConnect(this), 5000);
        this.connect.schedule(new CardConnect(this), 10000);
        new Startup(this).start();
    }

    public void checkNext() {
        if (this.proceedToNext()) {
            this.cardBot.sendMessage("#MIASACraft", Colors.BOLD + "All players have submitted their cards." + Colors.NORMAL + " Time for " + this.czar.getName() + " to pick the winning card.");
            this.cardBot.sendMessage("#MIASACraft", this.blackCard.getColored());
            this.cardBot.sendNotice(this.czar.getName(), "Say the number of the card you wish to win.");
            Collections.shuffle(this.players);
            this.playerIter = new ArrayList<Player>(this.players);
            this.playerIter.remove(this.czar);
            for (int i = 0; i < this.playerIter.size(); i++) {
                final Player player = this.playerIter.get(i);
                if (player.isWaiting()) {
                    this.playerIter.remove(player);
                }
            }
            for (int i = 0; i < this.playerIter.size(); i++) {
                final Player player = this.playerIter.get(i);
                if (this.blackCard.getAnswers() == 1) {
                    this.cardBot.sendMessage("#MIASACraft", i + 1 + ": " + player.getPlayedCards()[0].getColored());
                } else {
                    this.cardBot.sendMessage("#MIASACraft", i + 1 + ": " + player.getPlayedCards()[0].getColored() + " | " + player.getPlayedCards()[1].getColored());
                }
            }
            this.gameStatus = GameStatus.CZAR_TURN;
        }
    }

    public Player getPlayer(final String username) {
        for (final Player player : this.players) {
            if (player.getName().equals(username)) {
                return player;
            }
        }
        return null;
    }

    public String getCards(final Player player) {
        final StringBuilder sb = new StringBuilder();
        int i = 1;
        for (final WhiteCard card : player.getCards()) {
            sb.append(i + " [" + Colors.BOLD + card.getColored() + "] ");
            i++;
        }
        return sb.toString();
    }

    public Player getCzar() {
        Collections.shuffle(this.players);
        if (this.players.get(0) == this.czar) {
            return this.getCzar();
        }
        return this.players.get(0);
    }

    private void ifNotExists(final File... files) {
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

    public boolean inSession() {
        return this.gameStatus == GameStatus.IN_SESSION || this.gameStatus == GameStatus.CZAR_TURN;
    }

    public void nextRound() {
        this.nagger.cancel();
        this.nagger = new Timer();
        this.gameStatus = GameStatus.IN_SESSION;
        if (this.players.size() < 3) {
            this.spamBot.sendMessage("#MIASACraft", "Uh-oh! There aren't enough players to continue. Say 'quit' to quit, 'join' to join.");
            this.gameStatus = GameStatus.NOT_ENOUGH_PLAYERS;
            return;
        }
        int winning = 0;
        for (final Player player : this.players) {
            if (player.isWaiting()) {
                player.drawCardsForStart();
                player.setWaiting(false);
                this.spamBot.voice(this.spamBot.getChannel("#MIASACraft"), this.spamBot.getUser(player.getName()));
            }
            if (player.getScore() > winning) {
                winning = player.getScore();
            }
            player.newRound();
        }
        final StringBuilder sb = new StringBuilder();
        for (final Player player : this.players) {
            if (player.getScore() == winning) {
                sb.append(player.getName() + ", ");
            }
        }
        sb.delete(sb.length() - 2, sb.length());
        final String win = (sb.toString().contains(", ") ? "Winners" : "Winner") + ": " + sb.toString();
        if (winning > 0)
            this.cardBot.setTopic(this.cardBot.getChannel("#MIASACraft"), this.topic + " | " + win + " (" + winning + ")");
        this.czar.setCzar(false);
        this.czar = this.getCzar();
        this.czar.setCzar(true);
        this.spamBot.sendMessage("#MIASACraft", "The new czar is " + Colors.BOLD + this.czar.getName());
        Collections.shuffle(this.blackCards);
        final BlackCard card = this.blackCards.get(0);
        this.blackCard = card;
        this.spamBot.sendMessage("#MIASACraft", "Fill in the " + (card.getAnswers() > 1 ? "blanks" : "blank") + ": " + Colors.BOLD + card.getColored() + " [Play your white " + (card.getAnswers() > 1 ? "cards by saying their numbers" : "card by saying its number") + "]");
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
        }
        this.cardBot.messageAllCards();
        this.cardBot.sendNotice(this.czar.getName(), "You don't have cards because you're the czar! Once everyone has played their cards you will be prompted to choose the best.");
        this.nagger.schedule(new Nag(this), 60000, 60000);
    }

    public boolean proceedToNext() {
        for (final Player player : this.players) {
            if (!player.hasPlayedCards() && !player.isCzar() && !player.isWaiting()) {
                return false;
            }
        }
        return true;
    }

    public void processLeave(final Player player) {
        this.spamBot.deVoice(this.spamBot.getChannel("#MIASACraft"), this.spamBot.getUser(player.getName()));
        this.players.remove(player);
        if (player.isCzar()) {
            Collections.shuffle(this.players);
            player.setCzar(false);
            this.czar = this.players.get(0);
            this.czar.setCzar(true);
            this.spamBot.sendMessage("#MIASACraft", "The current czar, " + player.getName() + " quit the game and " + this.czar.getName() + " is now the new czar for this round.");
            this.czar.newRound();
            if (this.gameStatus == GameStatus.CZAR_TURN) {
                if (this.players.size() - 1 < 3) {
                    this.spamBot.sendMessage("#MIASACraft", "Uh-oh! There aren't enough players to continue. Say 'quit' to quit, 'join' to join.");
                    this.gameStatus = GameStatus.NOT_ENOUGH_PLAYERS;
                    return;
                }
                this.cardBot.sendNotice(this.czar.getName(), "Say the number of the card you wish to win.");
            }
        } else if (this.gameStatus == GameStatus.CZAR_TURN) {
            this.cardBot.sendMessage("#MIASACraft", "Uh-oh! " + player.getName() + " quit the game. Let's start a new round.");
            this.nextRound();
        } else {
            if (this.players.size() < 3) {
                this.nextRound();
            }
        }
        this.checkNext();
    }

    public void ready() {
        final Channel chan = this.spamBot.getChannel("#MIASACraft");
        this.nagger = new Timer();
        this.spamBot.setTopic(chan, this.topic + " | Say 'join' without quotes to join the game.");
        this.spamBot.sendMessage("#MIASACraft", Colors.BOLD + "Welcome to Cards Against Humanity!");
        this.spamBot.sendMessage("#MIASACraft", "To join the game simply say 'join' in chat (without quotes) and you will be added next round!");
        this.gameStatus = GameStatus.NOT_ENOUGH_PLAYERS;
        this.connect.scheduleAtFixedRate(new StartGame(this.spamBot, this), 60000, 60000);
        this.spamBot.sendMessage("#MIASACraft", "Running version " + this.spamBot.getCAHVersion() + " with " + this.whiteCards.size() + " white cards and " + this.blackCards.size() + " black cards.");
    }

    private void setupCards() throws IOException {
        this.players = new ArrayList<Player>();
        this.whiteCards = Collections.synchronizedList(new ArrayList<WhiteCard>());
        this.blackCards = new ArrayList<BlackCard>();
        final File blackFile = new File("black.txt");
        final File whiteFile = new File("white.txt");
        this.ifNotExists(blackFile, whiteFile);
        FileReader fileReader = new FileReader(blackFile);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            this.blackCards.add(new BlackCard(line));
        }
        fileReader.close();
        bufferedReader.close();
        fileReader = new FileReader("white.txt");
        bufferedReader = new BufferedReader(fileReader);
        while ((line = bufferedReader.readLine()) != null) {
            this.whiteCards.add(new WhiteCard(line));
        }
        fileReader.close();
        bufferedReader.close();
    }

    public void startGame() {
        this.gameStatus = GameStatus.IN_SESSION;
        for (final Player player : this.players) {
            if (player.getCards().size() > 0) {
                for (int i = 0; i < player.getCards().size(); i++) {
                    this.whiteCards.add(player.getCards().get(0));
                    player.getCards().remove(0);
                }
            }
            player.drawCardsForStart();
        }
        String[] users = new String[players.size()];
        for (int i = 0; i < players.size(); i++) {
            users[i] = players.get(i).getName();
        }
        this.voiceUsers(users);
        this.czar = this.players.get(0);
        this.nextRound();
    }

    public void voiceUsers(String... users) {
        if (users.length < 1) {
            return;
        }
        Channel chan = this.spamBot.getChannel("#MIASACraft");
        String mode = "+";
        StringBuilder sb = new StringBuilder();
        for (String user : users) {
            mode += "v";
            sb.append(user + " ");
            if (("MODE #MIASACraft :" + mode + " " + sb.toString()).length() >= 482) {
                this.spamBot.setMode(chan, mode + " " + sb.toString());
                mode = "+";
                sb = new StringBuilder();
            }
        }
        this.spamBot.setMode(chan, mode + " " + sb.toString());
    }

    public void deVoiceUsers(String... users) {
        if (users.length < 1) {
            return;
        }
        Channel chan = this.spamBot.getChannel("#MIASACraft");
        String mode = "-";
        StringBuilder sb = new StringBuilder();
        for (String user : users) {
            mode += "v";
            sb.append(user + " ");
            if (("MODE #MIASACraft :" + mode + " " + sb.toString()).length() >= 482) {
                this.spamBot.setMode(chan, mode + " " + sb.toString());
                mode = "-";
                sb = new StringBuilder();
            }
        }
        this.spamBot.setMode(chan, mode + " " + sb.toString());
    }
}
