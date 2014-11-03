package blackjack.ga;

import blackjack.Player;

public class Individual extends Player {
	private final BlackjackChromosome chromosome;
	private Player playerIndividual;
	
	public Individual(final String name) {
		super("GAplayer " + name);
		chromosome = new BlackjackChromosome();
	}
	
	public Individual(final BlackjackChromosome parentChromosome1, final BlackjackChromosome parentChromosome2) {
		super("GA individual");
		chromosome = new BlackjackChromosome(parentChromosome1, parentChromosome2);
	}
	
	public BlackjackChromosome getChromosome() {
		return chromosome;
	}
	
	public void setPlayer(final Player player) {
		playerIndividual = player;
	}
	
	public int getFitness() {
		return getMoney();
		//return (double) getMoney() / (double) getStartingMoney();
	}
	
	@Override
	public boolean wantAnotherCard() {
		if (getBestScore() > 20) {
			return false;
		}
		final int chromosomeCol = dealerCard.getNumber() > 10 ? 9 : dealerCard.getNumber() - 1;
		final int allele = chromosome.getGene(getBestScore(), chromosomeCol);
		return allele == 1;
	}
}
