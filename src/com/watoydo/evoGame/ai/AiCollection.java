package com.watoydo.evoGame.ai;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;

import com.watoydo.evoGame.world.WorldMap;
import com.watoydo.evoGame.world.WorldStates;
import com.watoydo.utils.Maths;

public class AiCollection {

	private static final int LOGIC_DELAY = 1;
	
	private static final int CROWD = 3;
	private static final int LONELY = 1;
	
	private static final double BREED_RATE = 0.1;
	
	private static final double CROWDED_DEATH_RATE = 0.08;
	private static final double LONELY_DEATH_RATE = 0.05;
	private static final double CONTENT_DEATH_RATE = 0.01;
	private static final double GREEN_DEATH_RATE = 0;
	
	private static final int MAX_POP = 100;
	
	private WorldMap map;
	
	private List<ImmortalityBlob> blobs;
	private List<Ai> aiList;
	private int logicTime;
	
	private Queue<Integer> lifetimes;
	
	public AiCollection(WorldMap map) {
		this.aiList = new ArrayList<Ai>();
		this.blobs = new ArrayList<ImmortalityBlob>();
		this.logicTime = 0;
		this.map = map;
		
		for(int i = 0; i < 3; i++) {
			ImmortalityBlob immortalityBlob = new ImmortalityBlob(new Random().nextInt(this.map.worldBlocks.length), new Random().nextInt(this.map.worldBlocks[0].length));
			this.blobs.add(immortalityBlob);
		}
		this.lifetimes = new LinkedList<Integer>();
	}
	
	public void populate() {
		Ai ai = new Ai(map);
		ai.setRules(Ai.randomRules(new Random()));
		ai.setX(1);
		ai.setY(1);
		this.aiList.add(ai);
	}
	
	public void populate(Ai ai1, Ai ai2) {
		Ai ai = new Ai(map);
		ai.setRules(Ai.crossover(ai1, ai2));
		ai.setX(ai1.getX());
		ai.setY(ai1.getY());
		this.aiList.add(ai);
	}
	
	public void breed() {
		
		if (getPopulationCount() < MAX_POP) {
		
			// Make babies
			Ai[] listCopy = aiList.toArray(new Ai[aiList.size()]);
			Ai[] listCopy1 = aiList.toArray(new Ai[aiList.size()]);
			
			for(Ai ai1:listCopy) {
				for(Ai ai2:listCopy1) {
					if(ai1 != ai2 && Math.random() < BREED_RATE && Maths.getDistance(ai1.getX(), ai1.getY(), ai2.getX(), ai2.getY()) < Ai.BREED) {
						populate(ai1, ai2);
						
						if (getPopulationCount() > MAX_POP) {
							return;
						}
						break;
					}
				}
			}
		}
		
		// Kill random ai
		Iterator<Ai> iterator = aiList.iterator();
		while(iterator.hasNext()) {
			Ai next = iterator.next();
			
			int crowded = 0;
			
			Ai[] listCopy = aiList.toArray(new Ai[aiList.size()]);
			for(Ai ai:listCopy) {
				if(next != ai && Maths.getDistance(ai.getX(), ai.getY(), next.getX(), next.getY()) < Ai.BREED)
					crowded++;
			}
			
			if(crowded > CROWD)
				next.setMood(1);
			else if(crowded < LONELY)
				next.setMood(-1);
			else
				next.setMood(0);
			
			WorldStates worldLocation = next.getWorldLocation();
			
			if(map.hasBlob(new int[]{(int)next.getX(), (int)next.getY()}))
				worldLocation = WorldStates.IMMORTAL;
			
			if(worldLocation == WorldStates.IMMORTAL && Math.random() < GREEN_DEATH_RATE)
				killMember(next, iterator);
			else if(crowded > CROWD && worldLocation != WorldStates.IMMORTAL && Math.random() < CROWDED_DEATH_RATE)
				killMember(next, iterator);
			else if(crowded < LONELY && worldLocation != WorldStates.IMMORTAL && Math.random() < LONELY_DEATH_RATE)
				killMember(next, iterator);
			else if(crowded <= CROWD && crowded >= LONELY && worldLocation != WorldStates.IMMORTAL && Math.random() < CONTENT_DEATH_RATE)
				killMember(next, iterator);
		}
	}
	
	private void killMember(Ai ai, Iterator<Ai> iterator) {
		iterator.remove();
		if(this.lifetimes.size() > 500)
			this.lifetimes.poll();
		
		this.lifetimes.add(ai.getLifetime());
		
		System.out.println("Average Life " + averageLifetime());
	}

	private int averageLifetime() {
		int life = 0;
		
		for(Integer value:this.lifetimes) {
			life += value;
		}
		return life / this.lifetimes.size();
	}

	public void applyRules(WorldMap map) {
		
		map.setAiLocations(aiList);
		map.setBlobLocations(blobs);
		
		if(logicTime <= 0) {
			logicTime = LOGIC_DELAY;
			
			for (Ai ai : aiList) {
				ai.applyRules(map.getGridView(ai));
			}
		}
		else {
			for (Ai ai : aiList) {
				ai.act();
			}
			for (ImmortalityBlob ai : blobs) {
				ai.act();
			}
		}
		
		this.logicTime--;
	}

	public Iterator<Ai> getIterator() {
		return this.aiList.iterator();
	}

	public int getPopulationCount() {
		return this.aiList.size();
	}

}
