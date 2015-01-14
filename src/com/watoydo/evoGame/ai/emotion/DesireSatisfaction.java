package com.watoydo.evoGame.ai.emotion;

public class DesireSatisfaction {

	private static final int BREED_HAPPINESS = 30;

	public static final double DESIRE_DECREASE_RATE = 0.01;
	
	private int[] satisfaction;
	
	public DesireSatisfaction() {
		this.satisfaction = new int[]{50,50,50};
	}
	
	public void modSatisfaction(int index, int modifier) {
		this.satisfaction[index] += modifier;
		
		if(this.satisfaction[index] > 100)
			this.satisfaction[index] = 100;
		else if(this.satisfaction[index] < 0)
			this.satisfaction[index] = 0;
	}

	public boolean dying() {
		
		if(Math.random() > (this.satisfaction[0] + this.satisfaction[1] + this.satisfaction[2]) / 3.0)
			return true;
		
		return false;
	}

	public boolean wantsToBreed() {
		return (this.satisfaction[0] + this.satisfaction[1] + this.satisfaction[2]) / 3.0 > BREED_HAPPINESS;
	}

	public int[] getSatisfaction() {
		return this.satisfaction;
	}
	
}
