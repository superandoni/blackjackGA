package blackjack;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Game {

	Player[] players;
	Croupier croupier;
	int numplayers;
	Deck deck;
	
	private static final Logger logger = LoggerFactory.getLogger(Game.class.getName());
	
	public Game(final Deck deck, final Croupier croupier, final Player[] players) {
		this.deck = deck;
		this.croupier = croupier;
		this.players = players;
	}
	
	public List<Integer> playGame() {
		logger.debug("Shuffling deck");
		deck.shuffle();
		logger.debug("Dealing cards to " + croupier.getName());
		croupier.addCard(deck.getCard());
		for (final Player player : players) {
			player.setDealerCard(croupier.getCard(0));
			logger.debug("Dealing cards to player " + player.getName());
			player.addCard(deck.getCard());
			player.addCard(deck.getCard());
		}
		
		// Game flow
		logger.debug("Croupier card1: " + croupier.getCard(0));
		
		final Player[] playersAndCroupier = new Player[players.length + 1];
		for (int i = 0; i < players.length; i++) {
			playersAndCroupier[i] = players[i];
		}
		playersAndCroupier[players.length] = croupier;
		
		for (final Player player : playersAndCroupier) {
			final StringBuilder sb = new StringBuilder();
			final String wantsStr = " wants another card\n";
			final String wantsNotStr = " does not want another card";
			
			boolean want = player.wantAnotherCard();
			while (want) {
				sb.append(player.getSummary()).append(wantsStr);
				player.addCard(deck.getCard());
				want = player.wantAnotherCard();
			}
			sb.append(player.getSummary()).append(wantsNotStr);
			logger.debug(sb.toString());
			
		}
		
		final List<Integer> scores = new ArrayList<>(players.length + 1);
		
		for (final Player player : playersAndCroupier) {
			scores.add(new Integer(player.getBestScore()));
			player.discard();
		}
		
		return scores;
	}
	
//	public static int getCardScore(final Card card) {
//		if (card.getNumber() == 1) {
//			return 11; // TODO hobetu hau
//		}
//		return card.getNumber() > 10 ? 10 : card.getNumber();
//	}
	
	
	public static void main(final String[] args) {
		FileWriter fw = null;
		try {
			fw = new FileWriter(new File("z:\\blackjack.csv"));
		} catch (final IOException e) {
			e.printStackTrace();
		}
		final Deck deck = new Deck();
		final Croupier croupier = new Croupier();
		final int NUM_PLAYERS = 5;
		final int NUM_ROUNDS = 1;
		final Player[] players = new Player[NUM_PLAYERS];
		for (int i = 0; i < NUM_PLAYERS; i++) {
			players[i] = new DumbPlayer(Integer.toString(i));
		}
		
		final Game game = new Game(deck, croupier, players);
		List<Integer> gameScores;
		int gameCount = 0;
		while (gameCount++ < NUM_ROUNDS) {
			System.out.println("Round number " + gameCount);
			gameScores = game.playGame(); // Dealer's is last
			System.out.println(gameScores.toString());
			final int dealersScore = gameScores.get(gameScores.size() - 1).intValue();
			final List<Integer> winners = new ArrayList<>(NUM_PLAYERS);
			final Integer[] allFalses = new Integer[NUM_PLAYERS]; 
			Arrays.fill(allFalses, Integer.valueOf(-1));
			winners.addAll(Arrays.asList(allFalses));
			if (dealersScore == 21) {
				System.out.println("Dealer won");
			} else {
				int score;
				for (int i = 0; i < NUM_PLAYERS; i++) {
					score = gameScores.get(i).intValue();
					if (score <= 21) {
						if (score > dealersScore || dealersScore > 21) {
							winners.set(i, Integer.valueOf(1));
						} else if (score == dealersScore) {
							winners.set(i, Integer.valueOf(0)); // Push, player does not win or lose money
						}
					}
				}
			}
			int i = 0;
			for (final Integer win : winners) {
				players[i++].addMoney(win.intValue());
			}
		
			System.out.println("Round results: " + winners.toString());
			final StringBuilder sb = new StringBuilder();
			for(final Player player : players) {
				sb.append(player.getMoney());
				sb.append(",");
			}
			sb.setLength(sb.length() - 1);
			System.out.println("Accumulated results: " + sb.toString());
			try {
				fw.append(sb.toString()).append('\n');
			} catch (final IOException e) {
				e.printStackTrace();
			}
			
		}
		final StringBuilder sb = new StringBuilder();
		sb.append("END\nTotal money: ");
		for (final Player player : players) {
			sb.append(player.getMoney()).append(",");
		}
		sb.setLength(sb.length() - 1);
		System.out.println(sb.toString());
		try {
			fw.flush();
			fw.close();
		} catch (final IOException e) {
			e.printStackTrace();
		}
		System.out.println("Bye!");
	}
}
