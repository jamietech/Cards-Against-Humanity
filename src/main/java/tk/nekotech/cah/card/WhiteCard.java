package tk.nekotech.cah.card;

import org.pircbotx.Colors;

public class WhiteCard {
	private String full;
	private String color = '\u0003' + "01,00";
	
	public WhiteCard(String full) {
		this.full = full;
	}
	
	public String getFull() {
		return full;
	}
	
	public String getColored() {
		return this.color + getFull() + Colors.NORMAL;
	}

}
