package blackjack;


public class DumbPlayer extends Player {

	public DumbPlayer(final String name) {
		super(name);
	}
	
	@Override
	public boolean wantAnotherCard() {
		if (getBestScore() < 21) {
			return Math.random() > 0.5;
		}
		return false;
	}

}
