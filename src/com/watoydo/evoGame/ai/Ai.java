package com.watoydo.evoGame.ai;


import java.awt.Color;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import com.watoydo.evoGame.ai.emotion.DesireSatisfaction;
import com.watoydo.evoGame.ai.rules.MovementRule;
import com.watoydo.evoGame.ai.rules.RotationRule;
import com.watoydo.evoGame.ai.rules.Rule;
import com.watoydo.evoGame.world.WorldMap;
import com.watoydo.evoGame.world.WorldStates;

public class Ai {

	private static final int NUMBER_OF_RULES = 100;
	public static final double BREED = 10;
	
	private Set<Rule> rules;
	
	private double x, y;
	private double rotation;
	private int move;
	
	public WorldMap map;
	private Color colour;
	private int life;
	
	private DesireSatisfaction desire;
	
	public Ai(WorldMap map) {
		
		this.map = map;
		
		this.life = 0;
		this.x = 0;
		this.y = 0;
		this.rotation = 0;
		this.move = 0;
		
		this.rules = new HashSet<>();
		
		this.colour = Color.red;
		
		this.desire = new DesireSatisfaction();
	}
	
	public Ai(WorldMap map, List<Rule> rules) {
		
		this.map = map;
		
		this.x = 0;
		this.y = 0;
		this.rotation = 0;
		this.move = 0;
		
		this.rules = new HashSet<>();
		
		for(Rule rule:rules) {
			this.rules.add(rule);
		}
		
		this.colour = Color.red;
		
		this.desire = new DesireSatisfaction();
	}
	
	public Set<Rule> getRules() {
		return rules;
	}

	public void setRules(Set<Rule> rules) {
		this.rules = rules;
	}

	public double getX() {
		return this.x;
	}

	public double getY() {
		return this.y;
	}
	
	public void setX(double x) {
		this.x = x;
	}

	public void setY(double y) {
		this.y = y;
	}

	public double getRotation() {
		return this.rotation;
	}
	
	public void applyRules(int[][][] gridView) {
		
		Rule closestRule = getClosestRule(gridView);
		closestRule.apply(this);
		act();
	}
	
	public void act() {
		handleSatisfaction(this.getWorldState());
		move();
		this.life++;
	}

	private WorldStates[] getWorldState() {
		
		List<WorldStates> states = new ArrayList<WorldStates>();
		
		states.add(this.getWorldLocation());
		
		for(WorldStates state:this.map.getBlobs(new int[]{(int) this.getX(), (int) this.getY()})) {
			states.add(state);
		}
		
		return states.toArray(new WorldStates[states.size()]);
	}

	private void handleSatisfaction(WorldStates[] currentWorldState) {
		
		if(Math.random() < DesireSatisfaction.DESIRE_DECREASE_RATE)
			this.desire.modSatisfaction(0, -1);
		if(Math.random() < DesireSatisfaction.DESIRE_DECREASE_RATE)
			this.desire.modSatisfaction(1, -1);
		if(Math.random() < DesireSatisfaction.DESIRE_DECREASE_RATE)
			this.desire.modSatisfaction(2, -1);
		
		for(WorldStates state:currentWorldState) {
			switch(state) {
				case IMMORTAL1:
					this.desire.modSatisfaction(0, 1);
				break;
				case IMMORTAL2:
					this.desire.modSatisfaction(1, 1);
				break;
				case IMMORTAL3:
					this.desire.modSatisfaction(2, 1);
				break;
				default:
				break;
			}
		}
		
		this.setColour(new Color(this.desire.getSatisfaction()[0], this.desire.getSatisfaction()[1], this.desire.getSatisfaction()[2]));
		
	}

	private void move() {
		
		double x, y;
		
		x = this.x + Math.cos(rotation) * move;
		y = this.y + Math.sin(rotation) * move;
		
		if(map.isValid(x, y)) {
			this.x = x;
			this.y = y;
		}
		
	}

	private Rule getClosestRule(int[][][] gridView) {
		
		Rule closestRule = null;
		double closestRuleDist = 0;
		
		for(Rule rule:this.rules) {
			double dist = rule.difference(gridView);
			if(closestRule == null || dist < closestRuleDist) {
				closestRule = rule;
				closestRuleDist = dist;
			}
		}
		
		return closestRule;
	}

	public void rotate(RotationRule r, double phi) {
		switch(r) {
			case NO_TURN:
				this.rotation += r.getCode() * phi;
			break;
			case TURN_LEFT:
				this.rotation += r.getCode() * phi;
			break;
			case TURN_RIGHT:
				this.rotation -= r.getCode() * phi;
			break;
		}
	}

	public void moveDirection(MovementRule f) {
		switch(f) {
			case MOVE_BACKWARD:
				this.move = f.getCode();
			break;
			case MOVE_FORWARD:
				this.move = f.getCode();
			break;
			case STAND_STILL:
				this.move = f.getCode();
			break;
		}
	}

	public static Set<Rule> randomRules(Random random) {
		
		Set<Rule> rules = new HashSet<>();
		
		for(int i = 0; i < NUMBER_OF_RULES; i++) {
			Rule rule = new Rule();
			
			rule.initialMutation(random);
			
			rules.add(rule);
		}
		
		return rules;
	}
	
	public static Set<Rule> crossover(Ai ai1, Ai ai2) {
		
		Random random = new Random();
		
		List<Rule> rules1 = new ArrayList<>(ai1.getRules());
		List<Rule> rules2 = new ArrayList<>(ai2.getRules());
		
		Set<Rule> crossoverRules = new HashSet<>();
		
		for(int i = 0; i < rules1.size(); i++) {
			Rule rule = null;
			if(random.nextBoolean())
				rule = rules1.get(random.nextInt(rules1.size())).copy();
			else
				rule = rules2.get(random.nextInt(rules2.size())).copy();
			
				rule.mutate(random);
			
				crossoverRules.add(rule);
		}
		return crossoverRules;
	}

	public void setRotation(double phi) {
		this.rotation = phi;
	}

	public WorldStates getWorldLocation() {
		return this.map.getGridValue(this.getX(), this.getY());
	}

	public void setColour(Color colour) {
		this.colour = colour;
	}

	public Color getColour() {
		return this.colour;
	}
	
	public int getLifetime() {
		return this.life;
	}

	public boolean dying() {
		return this.desire.dying();
	}

	public boolean wantsToBreed() {
		return this.desire.wantsToBreed();
	}
	
}
