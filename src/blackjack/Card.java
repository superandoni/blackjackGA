package blackjack;

public class Card {
	private int number = 0;
	private String suit = "";
	
	public Card() {
	}
	
	public int getNumber() {
		return number;
	}

	public void setNumber(final int number) {
		this.number = number;
	}

	public String getSuit() {
		return suit;
	}

	public void setSuit(final String suit) {
		this.suit = suit;
	}
	
	public Card(final int number, final String suit) {
		this.number = number;
		this.suit = suit;
	}
	
	@Override
	public String toString() {
		String numberStr = "";
		switch (number) {
			case 11: numberStr = "J"; break;
			case 12: numberStr = "Q"; break;
			case 13: numberStr = "K"; break;
			default: numberStr = Integer.toString(number);
		}
		return numberStr + " " + suit;
	}
}
