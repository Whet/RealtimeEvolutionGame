package com.watoydo.ruleIO;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import com.watoydo.evoGame.ai.Ai;
import com.watoydo.evoGame.ai.rules.Rule;
import com.watoydo.evoGame.world.WorldMap;

public class LoadSave {

	public static void saveAi(File file, Ai ai) {
		try {
			List<Rule> rules = new ArrayList<Rule>();
			for(Rule rule:ai.getRules()) {
				rules.add(rule.copy());
			}
		    ObjectOutput output = new ObjectOutputStream(new FileOutputStream(file));
		    output.writeObject(rules);
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static Ai loadAi(File file, WorldMap map) {
		try {
			InputStream buffer = new BufferedInputStream(new FileInputStream(file));
			ObjectInput input = new ObjectInputStream (buffer);
			List<Rule> rules = (List<Rule>)input.readObject();
			return new Ai(map, rules);
		}
		catch(IOException e) {
			e.printStackTrace();
		}
		catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
	
}
