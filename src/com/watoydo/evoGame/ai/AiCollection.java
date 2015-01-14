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

	private static final int LOGIC_DELAY = 3;
	
	private static final double BREED_RATE = 0.1;
	
	private static final int MAX_POP = 100;
	
	private WorldMap map;
	
	private List<WorldBlob> blobs;
	private List<Ai> aiList;
	private int logicTime;
	
	private Queue<Integer> lifetimes;
	
	public AiCollection(WorldMap map) {
		this.aiList = new ArrayList<Ai>();
		this.blobs = new ArrayList<WorldBlob>();
		this.logicTime = 0;
		this.map = map;
		
		WorldBlob immortalityBlob1 = new WorldBlob(WorldStates.IMMORTAL1, new Random().nextInt(this.map.worldBlocks.length), new Random().nextInt(this.map.worldBlocks[0].length));
		this.blobs.add(immortalityBlob1);
		WorldBlob immortalityBlob2 = new WorldBlob(WorldStates.IMMORTAL2, new Random().nextInt(this.map.worldBlocks.length), new Random().nextInt(this.map.worldBlocks[0].length));
		this.blobs.add(immortalityBlob2);
		WorldBlob immortalityBlob3 = new WorldBlob(WorldStates.IMMORTAL3, new Random().nextInt(this.map.worldBlocks.length), new Random().nextInt(this.map.worldBlocks[0].length));
		this.blobs.add(immortalityBlob3);
		
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
					if(ai1 != ai2 && ai1.wantsToBreed() && Math.random() < BREED_RATE && Maths.getDistance(ai1.getX(), ai1.getY(), ai2.getX(), ai2.getY()) < Ai.BREED) {
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
			
			if(next.dying())
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
			
			Ai[] aiArray = aiList.toArray(new Ai[aiList.size()]);
			
			for (Ai ai : aiArray) {
				ai.applyRules(map.getGridView(ai));
			}
		}
		else {
			
			Ai[] aiArray = aiList.toArray(new Ai[aiList.size()]);
			
			for (Ai ai : aiArray) {
				ai.act();
			}
			
			WorldBlob[] imArray = blobs.toArray(new WorldBlob[blobs.size()]);
			
			for (WorldBlob blob : imArray) {
				blob.act();
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
