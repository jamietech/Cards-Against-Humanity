package tk.nekotech.cah.card;

import org.pircbotx.Colors;

public class BlackCard {
	private String full;
	private String color = '\u0003' + "00,01";
	private int answers;
	
	public BlackCard(String full) {
		this.full = full;
		for (char character : full.toCharArray()) {
			if (character == '_') {
				this.answers++;
			}
		}
	}
	
	public String getFull() {
		return full;
	}
	
	public int getAnswers() {
		return answers;
	}
	
	public String getColored() {
		return this.color + getFull() + Colors.NORMAL;
	}
}
