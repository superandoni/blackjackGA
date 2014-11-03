package blackjack;

import java.util.ArrayList;
import java.util.List;

public class Player {
	private final List<Card> cards;
	private String name;
	private int startingMoney;
	private int money;
	protected Card dealerCard;
	private int bestScore; 

	public Player(final String name) {
		cards = new ArrayList<>(10);
		bestScore = 0;
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}
	
	public void addCard(final Card card) {
		cards.add(card);
		updateBestScore();
	}
	
	private void updateBestScore() {
		final List<Integer> points = getPoints();
		final int best = points.get(0).intValue();
		if (points.size() == 1) {
			bestScore = best;
		}
		for (int i = 1; i < points.size(); i ++) {
			if (points.get(i).intValue() == 21) {
				bestScore = 21;
			}
			bestScore = ((points.get(i).intValue() > best) && (points.get(i).intValue()) < 21) ? points.get(i).intValue() : best;
		}
	}
	
	public Card getCard(final int index) {
		return cards.get(index);
	}
	
	@SuppressWarnings("static-method")
	public boolean wantAnotherCard() {
		return false;
	}
	
	public void setDealerCard(final Card dealerCard) {
		this.dealerCard = dealerCard;
	}
	
	private String getPointsStr() {
		final int[] pointSums = new int[5];
		int pointCount = 1;
		for (final Card card : cards) {
			for (int i = 0; i < pointCount; i++) {
				pointSums[i] += card.getNumber() > 10 ? 10 : card.getNumber();
			}
			if (card.getNumber() == 1) {
				pointCount++;
				pointSums[pointCount-1] = pointSums[pointCount - 2] + 10;
			}
		}
		final StringBuilder sb = new StringBuilder();
		for (int i = 0; i < pointCount; i++) {
			sb.append(Integer.toString(pointSums[i])).append("/");
		}
		sb.setLength(sb.length() - 1);
		return sb.toString();
	}
	
	@SuppressWarnings("boxing")
	protected List<Integer> getPoints() {
		final int[] pointSums = new int[5];
		int pointCount = 1;
		for (final Card card : cards) {
			for (int i = 0; i < pointCount; i++) {
				pointSums[i] += card.getNumber() > 10 ? 10 : card.getNumber();
			}
			if (card.getNumber() == 1) {
				pointCount++;
				pointSums[pointCount-1] = new Integer(pointSums[pointCount - 2] + 10);
			}
		}
		final List<Integer> pointSumsList = new ArrayList<>(pointCount);
		for (int i = 0; i < pointCount; i++) {
			pointSumsList.add(new Integer(pointSums[i]));
		}
		return pointSumsList;
	}
	
	protected int getLowestScore() {
		final List<Integer> points = getPoints();
		int lowest = points.get(0).intValue();
		for (final Integer point : points) {
			if (point.intValue() < lowest) {
				lowest = point.intValue();
			}
		}
		return lowest;
	}
	
	protected int getHighestScore() {
		final List<Integer> points = getPoints();
		int highest = points.get(0).intValue();
		for (final Integer point : points) {
			if (point.intValue() > highest) {
				highest = point.intValue();
			}
		}
		return highest;
	}
	
	public int getBestScore() {
		return bestScore;
	}
	
	public String getCardsStr() {
		final StringBuilder sb = new StringBuilder();
		for (final Card card : cards) {
			sb.append(card.toString()).append(" - ");
		}
		sb.setLength(sb.length() - 3);
		return sb.toString();
	}
	
	public String getSummary() {
		return getName() + ": " + getPointsStr() + " ("+ getCardsStr()+")";		
	}

	public int getMoney() {
		return money;
	}

	public void addMoney(final int money) {
		this.money += money;
	}
	
	public void discard() {
		cards.clear();
		bestScore = 0;
	}

	public int getStartingMoney() {
		return startingMoney;
	}

	public void setStartingMoney(final int startingMoney) {
		this.startingMoney = startingMoney;
	}

}
