package blackjack.ga;

import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import blackjack.Croupier;
import blackjack.Deck;
import blackjack.Game;
import blackjack.Player;

public class GA {
	
	private static final int NUM_GENERATIONS = 1000;
	public static final int POP_SIZE = 100;
	public static final int POP_PERCENT_ELIMINATED = 20;
	public static final int POP_SIZE_ELIMINATED = (int) (POP_SIZE * ( POP_PERCENT_ELIMINATED / 100.0));
	public static final double GENE_MUTATION_CHANCE = 0.001;
	public static final boolean CROSSOVER = false;
	
	public static final String AVERAGE_FITNESS_FILEPATH = "Z:\\Programming\\blackjack\\data\\averageFitness";
	
	private static final Logger logger = LoggerFactory.getLogger(GA.class.getName());
	
	private static final DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
	
	public static void mutatePopulation(final Individual[] population) {
		BlackjackChromosome chromosome;
		for (final Individual individual : population) {
			chromosome = individual.getChromosome();
			for (int i = 0; i < BlackjackChromosome.CHROMOSOME_ROWS; i++) {
				for (int j = 0; j < BlackjackChromosome.CHROMOSOME_COLS; j++) {
					if (Math.random() < GENE_MUTATION_CHANCE) {
						if (chromosome.getGene(i, j) == 1) {
							chromosome.setGene(i, j, 0);
						} else {
							chromosome.setGene(i, j, 1);
						}
						logger.debug("{} has mutated gene {},{}", individual.getName(), i, j);
					}
				}
			}
		}
	}
	
	public static Individual breedIndividual(final Individual parent1, final Individual parent2) {
		return new Individual(parent1.getChromosome(), parent2.getChromosome());
	}
	
	private static void singleIndividualRun(final Player player, final int numRounds) {
		final int numPlayers = 1;
		final Player[] players = new Player[numPlayers];
		players[0] = player;
		List<Integer> gameScores;
		int roundCount = 0;
		
		final Game game = new Game(new Deck(), new Croupier(), players);
		while (roundCount < numRounds) {
			logger.debug("Round " + roundCount);
			gameScores = game.playGame(); // Dealer's is last
			logger.debug("Round scores: " + gameScores.toString());
			final int dealersScore = gameScores.get(gameScores.size() - 1).intValue();
			final List<Integer> winners = new ArrayList<>(numPlayers);
			final Integer[] allFalses = new Integer[numPlayers]; 
			Arrays.fill(allFalses, Integer.valueOf(-1));
			winners.addAll(Arrays.asList(allFalses));
			if (dealersScore == 21) {
				logger.debug("Dealer won");
			} else {
				int score;
				for (int i = 0; i < numPlayers; i++) {
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
			logger.debug("Round results: " + winners.toString());
			final StringBuilder sb = new StringBuilder();
			for(final Player p : players) {
				sb.append(p.getMoney());
				sb.append(",");
			}
			sb.setLength(sb.length() - 1);
			logger.debug("Accumulated results: " + sb.toString());
			roundCount++;
		}
		final StringBuilder sb = new StringBuilder();
		sb.append("END round " + roundCount + " - Total money: ");
		for (final Player p : players) {
			sb.append(p.getMoney()).append(",");
		}
		sb.setLength(sb.length() - 1);
		logger.debug(sb.toString());
	}
	
	public static void main(final String[] args) {
		final Date runDate = new Date();
		final long startTime = runDate.getTime();
		
		final Individual[] population = new Individual[POP_SIZE];
		final Map<Integer, Integer> fitnesses = new HashMap<>(POP_SIZE);
		final int numRounds = 100;
		int currentGeneration = 0;
		int individualsCreatedCount = 0;
		double averageFitness = 0.0;
		int fittestFitness = 0;
		final StringBuilder sb = new StringBuilder();
		FileWriter fw = null;
		
		try {
			fw = new FileWriter(AVERAGE_FITNESS_FILEPATH + dateFormat.format(runDate) + ".csv", true);
			fw.write(Double.toString(averageFitness));
			fw.write("\n");
		} catch (final IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		// First generation
		for (int i = 0; i < POP_SIZE; i++) {
			population[i] = new Individual(Integer.toString(i)); // TODO check if genome exists
			individualsCreatedCount++;
		}
		while (currentGeneration < NUM_GENERATIONS)
		{
			logger.info("Generation {} with {} rounds with {} population size", currentGeneration, numRounds, POP_SIZE);
			
			// Evaluate fitness by playing single player rounds
			for (int i = 0; i < POP_SIZE; i++) {
				logger.debug("Individual " + i + " runs");
				singleIndividualRun(population[i], numRounds);
				//fitnesses[i] = population[i].getFitness();
				fitnesses.put(Integer.valueOf(i), Integer.valueOf(population[i].getFitness())); // Momentuz money
			}
			logger.info("Population fitnesses: {}", fitnesses.toString());
			averageFitness = 0.0;
			for (final Individual individual : population) {
				averageFitness += individual.getFitness();
			}
			averageFitness /= POP_SIZE;
			logger.info("AVERAGE FITNESS: " + averageFitness);
			try {
				fw.write(Double.toString(averageFitness));
			} catch (final IOException e) {
				e.printStackTrace();
			}
			
			// Ordering fitnesses
			sb.setLength(0);
			sb.append("Ordered fitnesses: ");
		    final List<Map.Entry<Integer, Integer>> fitnessEntries = new ArrayList<>();
		    fitnessEntries.addAll(fitnesses.entrySet());
			final Comparator<Entry<Integer, Integer>> byValue = (entry1, entry2) -> entry1.getValue().compareTo(entry2.getValue());
			Collections.sort(fitnessEntries, byValue);
			logger.debug(sb.append(fitnessEntries.toString()).toString());
			
			fittestFitness = fitnessEntries.get(fitnessEntries.size() - 1).getValue().intValue();
			logger.info("FITTEST'S FITNESS: " + fittestFitness);
			try {
				fw.write(","+Integer.toString(fittestFitness));
				fw.write("\n");
			} catch (final IOException e) {
				e.printStackTrace();
			}
			
			// Eliminate the less fit individuals and generate new ones
			sb.setLength(0);
			sb.append("Individuals to be eliminated ("+POP_SIZE_ELIMINATED+"): ");
			for (int i = 0; i < POP_SIZE_ELIMINATED; i++) {
				sb.append(fitnessEntries.get(i)).append(", ");
				Individual newIndividual;
				if (CROSSOVER) {
					//newIndividual = 
				} else {
					newIndividual = new Individual(Integer.toString(individualsCreatedCount++));
				}
				population[fitnessEntries.get(i).getKey().intValue()] = newIndividual;
			}
			sb.setLength(sb.length() - 2);
			logger.debug(sb.toString());
			
			mutatePopulation(population);
			
			currentGeneration++;
		}
		
		try {
			fw.close();
		} catch (final IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		final long endTime = System.currentTimeMillis();
		
		logger.info("Time: {} ms", endTime - startTime);
		
		System.out.println("Bye!");

	}

}

///* 1. Init population */
//POP_SIZE = number of individuals in the population
//pop = newPop = []
//for i=1 to POP_SIZE {
//    pop.add( getRandomIndividual() )
//}
//
///* 2. evaluate current population */
//totalFitness = 0
//for i=1 to POP_SIZE {
//    fitness = pop[i].evaluate()
//    totalFitness += fitness
//}
//
//while not end_condition (best fitness, #iterations, no improvement...)
//{
//    // build new population
//    // optional: Elitism: copy best K from current pop to newPop
//    while newPop.size()<POP_SIZE
//    {
//        /* 3. roulette wheel selection */
//        // select 1st individual
//        rnd = getRandomDouble([0,1]) * totalFitness
//        for(idx=0; idx<POP_SIZE && rnd>0; idx++) {
//            rnd -= pop[idx].fitness
//        }
//        indiv1 = pop[idx-1]
//        // select 2nd individual
//        rnd = getRandomDouble([0,1]) * totalFitness
//        for(idx=0; idx<POP_SIZE && rnd>0; idx++) {
//            rnd -= pop[idx].fitness
//        }
//        indiv2 = pop[idx-1]
//
//        /* 4. crossover */
//        indiv1, indiv2 = crossover(indiv1, indiv2)
//
//        /* 5. mutation */
//        indiv1.mutate()
//        indiv2.mutate()
//
//        // add to new population
//        newPop.add(indiv1)
//        newPop.add(indiv2)
//    }
//    pop = newPop
//    newPop = []
//
//    /* re-evaluate current population */
//    totalFitness = 0
//    for i=1 to POP_SIZE {
//        fitness = pop[i].evaluate()
//        totalFitness += fitness
//    }
//}
//
//// return best genome
//bestIndividual = pop.bestIndiv()     // max/min fitness indiv

