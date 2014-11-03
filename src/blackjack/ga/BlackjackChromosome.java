package blackjack.ga;


public class BlackjackChromosome {
	private final int[][] chromosome;
	public static final int CHROMOSOME_ROWS = 21; // Player's possible scores (including zero)
	public static final int CHROMOSOME_COLS = 10; // Dealer's possible cards
	

	public BlackjackChromosome() {
		chromosome = new int[CHROMOSOME_ROWS][CHROMOSOME_COLS];
		double auxRand;
		for (int i = 0; i < CHROMOSOME_ROWS; i++) {
			for (int j = 0; j < CHROMOSOME_COLS; j++) {
				auxRand = Math.random();
				if (auxRand < 0.25) {
					chromosome[i][j] = 0; // stand
				} else if (auxRand < 0.5) {
					chromosome[i][j] = 1; // hit
				} else if (auxRand < 0.75) {
					chromosome[i][j] = 2; // TODO double-down
				} else {
					chromosome[i][j] = 3; // TODO split
				}
			}
		}
	}
	
	public BlackjackChromosome(final BlackjackChromosome chromosome1, final BlackjackChromosome chromosome2) {
		chromosome = new int[CHROMOSOME_ROWS][CHROMOSOME_COLS];
		double auxRand;
		for (int i = 0; i < CHROMOSOME_ROWS; i++) {
			for (int j = 0; j < CHROMOSOME_COLS; j++) {
				auxRand = Math.random();
				if (auxRand < 0.5) {
					System.arraycopy(chromosome1, i, chromosome, i, BlackjackChromosome.CHROMOSOME_COLS);
				} else {
					System.arraycopy(chromosome2, i, chromosome, i, BlackjackChromosome.CHROMOSOME_COLS);
				}
			}
		}
	}
	
	public int getGene(final int row, final int col) {
		return chromosome[row][col];
	}
	
	public void setGene(final int row, final int col, final int allele) {
		chromosome[row][col] = allele;
	}
	
}
