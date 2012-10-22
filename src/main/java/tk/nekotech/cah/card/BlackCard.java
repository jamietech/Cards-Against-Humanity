package tk.nekotech.cah.card;

import org.pircbotx.Colors;

public class BlackCard {
    private final String full;
    private final String color = '\u0003' + "00,01";
    private int answers;

    public BlackCard(final String full) {
        this.full = full;
        for (final char character : full.toCharArray()) {
            if (character == '_') {
                this.answers++;
            }
        }
    }

    public int getAnswers() {
        return this.answers;
    }

    public String getColored() {
        String ret = this.color;
        ret += this.getFull().replace("_", "_" + this.color);
        ret += Colors.NORMAL;
        return ret;
    }

    public String getFull() {
        return this.full;
    }
}
