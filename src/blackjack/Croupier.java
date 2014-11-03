package blackjack;

public class Croupier extends Player {

	public Croupier() {
		super("croupier");		
	}
	
	@Override
	public boolean wantAnotherCard() {
		if (getLowestScore() < 17) {
			return true;
		}
		return false;
	}

}
