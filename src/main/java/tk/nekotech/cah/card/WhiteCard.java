package tk.nekotech.cah.card;

import org.pircbotx.Colors;

public class WhiteCard {
    private final String full;
    private final String color = '\u0003' + "01,00";

    public WhiteCard(final String full) {
        this.full = full;
    }

    public String getColored() {
        return this.color + this.getFull() + Colors.NORMAL;
    }

    public String getFull() {
        return this.full;
    }
}
