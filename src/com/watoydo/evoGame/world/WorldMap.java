package com.watoydo.evoGame.world;

import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.watoydo.evoGame.ai.Ai;
import com.watoydo.evoGame.ai.WorldBlob;
import com.watoydo.utils.Maths;

public class WorldMap {

	public static final int VIEW_SIZE = 20;
	public WorldStates[][] worldBlocks;
	private Map<int[], Ai> aiLocations;
	private Map<int[], WorldBlob> blobLocations;

	public WorldMap(String mapFile) throws IOException {
		this.hardCodedBlocks(mapFile);
		this.aiLocations = new HashMap<int[], Ai>();
		this.blobLocations = new HashMap<int[], WorldBlob>();
	}
	
	public int[][][] getGridView(Ai ai) {
		return getGridView(ai.getX(), ai.getY(), ai);
	}
	
	public int[][][] getGridView(double aiX, double aiY, Ai ai) {

		int[][][] worldView = new int[VIEW_SIZE][VIEW_SIZE][WorldStates.getDimensions()];

		for (int i = 0; i < worldView.length; i++) {
			for (int j = 0; j < worldView[i].length; j++) {
				for(int k = 0; k < worldView[i][j].length; k++) {
					if(k == 0)
						worldView[j][j][k] = WorldStates.EMPTY.getCode();
					else
						worldView[j][j][k] = WorldStates.OCCUPIED.getCode();
				}
			}
		}

		int x, y;

		int xOffset = WorldMap.VIEW_SIZE / 2;

		for (int i = 0; i < WorldMap.VIEW_SIZE; i++) {
			for (int j = 0; j < WorldMap.VIEW_SIZE; j++) {

				x = (int) Math.round(i - xOffset);
				y = (int) Math.round(j);

				x += aiX;
				y += aiY;

				Point result = new Point();
				AffineTransform rotation = new AffineTransform();
				double angleInRadians = ai.getRotation() - Math.PI / 2;
				rotation.rotate(angleInRadians, aiX, aiY);
				rotation.transform(new Point(x, y), result);

				if (result.x >= 0 && result.x < worldBlocks.length && result.y >= 0 && result.y < worldBlocks[result.x].length) {
					
					List<WorldStates> states = new ArrayList<WorldStates>();
					
					states.add(worldBlocks[result.x][result.y]);
					
					if(hasAi(ai, new int[]{result.x, result.y}))
						states.add(WorldStates.AI);
					
					if(hasBlob(new int[]{result.x, result.y}))
						states.add(getBlob(new int[]{result.x, result.y}));
					
					worldView[i][j] = worldToArray(states.toArray(new WorldStates[states.size()]));
				}
			}
		}

		return worldView;
	}

	private int[] worldToArray(WorldStates... worldBlock) {
		int[] dimension = new int[WorldStates.getDimensions()];
		
		for(int i = 0; i < dimension.length; i++) {
			dimension[i] = WorldStates.DEFAULT.getCode();
		}
		
		for(int i = 0; i < worldBlock.length; i++) {
			switch(worldBlock[i]) {
				case EMPTY:
					dimension[0] = WorldStates.EMPTY.getCode();
				break;
				case OCCUPIED:
					dimension[0] = WorldStates.OCCUPIED.getCode();
				break;
				case IMMORTAL1:
					dimension[1] = WorldStates.IMMORTAL1.getCode();
				break;
				case IMMORTAL2:
					dimension[1] = WorldStates.IMMORTAL2.getCode();
				break;
				case IMMORTAL3:
					dimension[1] = WorldStates.IMMORTAL3.getCode();
				break;
				case AI:
					dimension[2] = WorldStates.AI.getCode();
				break;
				default:
				break;
			}
		}
		
		return dimension;
	}

	public boolean isValid(double x, double y) {

		int xLoc = (int)Math.floor(x);
		int yLoc = (int)Math.floor(y);
		
		if(xLoc < 0 || xLoc >= this.worldBlocks.length || yLoc < 0 || yLoc >= this.worldBlocks[0].length ||
		   this.worldBlocks[xLoc][yLoc] == WorldStates.OCCUPIED)
			return false;
		
		return true;
	}
	
	public void setAiLocations(List<Ai> aiList) {
		this.aiLocations.clear();
		for(Ai ai:aiList) {
			this.aiLocations.put(new int[]{(int) ai.getX(), (int) ai.getY()}, ai);
		}
	}
	
	public void setBlobLocations(List<WorldBlob> blobs) {
		this.blobLocations.clear();
		for(WorldBlob ai:blobs) {
			this.blobLocations.put(new int[]{(int) ai.getX(), (int) ai.getY()}, ai);
		}
	}
	
	private void hardCodedBlocks(String mapFile) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(new File(mapFile)));
		
		String readLine = reader.readLine();
		
		int x = Integer.parseInt(readLine.split(",")[0]);
		int y = Integer.parseInt(readLine.split(",")[1]);
		
		this.worldBlocks = new WorldStates[x][y];
		
		for(int i = 0; i < this.worldBlocks.length; i++) {
			for(int j = 0; j < this.worldBlocks[i].length; j++) {
				this.worldBlocks[i][j] = WorldStates.EMPTY;
			}
		}
		
		readLine = reader.readLine();
		
		while(readLine != null) {
			x = Integer.parseInt(readLine.split(",")[0]);
			y = Integer.parseInt(readLine.split(",")[1]);
			worldBlocks[x][y] = WorldStates.parseState(readLine.split(",")[2]);
			readLine = reader.readLine();
		}
		
		reader.close();
	}

	public WorldStates getGridValue(double x, double y) {
		return this.worldBlocks[(int)x][(int)y];
	}

	public boolean hasAi(int[] ai) {
		for(int[] key:this.aiLocations.keySet()) {
			if(ai[0] == key[0] && ai[1] == key[1])
				return true;
		}
		return false;
	}
	
	public boolean hasAi(Ai ai, int[] location) {
		for(Entry<int[], Ai> entry:this.aiLocations.entrySet()) {
			if(entry.getValue() != ai && location[0] == entry.getKey()[0] && location[1] == entry.getKey()[1])
				return true;
		}
		return false;
	}
	
	public boolean hasBlob(int[] location) {
		for(Entry<int[], WorldBlob> entry:this.blobLocations.entrySet()) {
			if(Maths.getDistance(location, entry.getKey()) < WorldBlob.SIZE)
				return true;
		}
		return false;
	}
	
	public WorldStates getBlob(int[] location) {
		for(Entry<int[], WorldBlob> entry:this.blobLocations.entrySet()) {
			if(Maths.getDistance(location, entry.getKey()) < WorldBlob.SIZE)
				return entry.getValue().getType();
		}
		return null;
	}

	public WorldStates[] getBlobs(int[] location) {
		List<WorldStates> states = new ArrayList<WorldStates>();
		for(Entry<int[], WorldBlob> entry:this.blobLocations.entrySet()) {
			if(Maths.getDistance(location, entry.getKey()) < WorldBlob.SIZE)
				states.add(entry.getValue().getType());
		}
		return states.toArray(new WorldStates[states.size()]);
	}


}
