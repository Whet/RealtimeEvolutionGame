package com.watoydo.evoGame.ai;

import java.util.Random;

import com.watoydo.evoGame.world.WorldStates;
import com.watoydo.utils.Maths;

public class WorldBlob {

	public static final double SIZE = 10;

	private WorldStates state;
	private int x, y;
	
	public WorldBlob(WorldStates state, int x, int y) {
		this.x = x;
		this.y = y;
		this.state = state;
	}
	
	public void act() {
		
		if(Math.random() < 0.5)
			return;
		
		this.x += Maths.POM() * new Random().nextInt(5);
		
		if(this.x < 0)
			this.x = 0;
		
		if(this.x > 200)
			this.x = 200;
		
		this.y += Maths.POM() * new Random().nextInt(5);
		
		if(this.y < 0)
			this.y = 0;
		
		if(this.y > 200)
			this.y = 200;
	}

	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}

	public WorldStates getType() {
		return this.state;
	}
	
}
