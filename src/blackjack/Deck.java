package blackjack;

import java.util.Random;

public class Deck {
	
	public static final int DECK_SIZE = 52;

	private final Card[] cards = new Card[DECK_SIZE];
	private final String suits[] = {"spades", "hearts", "clovers", "diamonds"};
	//private final String numbers[] = {"Ace", "2", "3", "4", "5", "6", "7", "8", "9", "10", "Joker", "Queen", "King"};
	private final int numbers[] = {1,2,3,4,5,6,7,8,9,10,11,12,13};
	private int cardsInDeck;
	
	public Deck() {
		cardsInDeck = 0;
		for (final String suit : suits) {
			for (final int number : numbers) {
				cards[cardsInDeck] = new Card(number, suit);
				cardsInDeck++;
			}
		}
	}
	
	public void shuffle() {
	    int newI;
	    Card temp;
	    final Random randIndex = new Random();
	    cardsInDeck = DECK_SIZE;

	    for (int i = 0; i < cardsInDeck; i++) {
	        // pick a random index between 0 and cardsInDeck - 1
	        newI = randIndex.nextInt(cardsInDeck);
	        // swap cards[i] and cards[newI]
	        temp = cards[i];
	        cards[i] = cards[newI];
	        cards[newI] = temp;
	    }
	}
	
	public Card getCard() {
		return cards[--cardsInDeck];
	}

}
