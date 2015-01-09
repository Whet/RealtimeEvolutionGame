package com.watoydo.evoGame.world;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;

import javax.imageio.ImageIO;

public class MapLoader {

	public static void main(String[] args) throws FileNotFoundException, IOException {
		imageToMap(ImageIO.read(new FileInputStream(new File(args[0]))), new File(args[1]));
	}
	
	public static void imageToMap(BufferedImage image, File file) throws FileNotFoundException {
		
		PrintWriter writer = new PrintWriter(file);
		
		writer.println(image.getWidth()+","+image.getHeight());
		
		for(int i = 0; i < image.getWidth(); i++) {
			for(int j = 0; j < image.getHeight(); j++) {
				
				String worldState = "";
				
				Color colour = new Color(image.getRGB(i, j));
				int red = colour.getRed();
				int green = colour.getGreen();
				int blue = colour.getBlue();
				
				if(red < 10 && green < 10 && blue < 10) {
					worldState = "WorldStates.OCCUPIED";
				}
				else if(green > 100 && blue < 120 && red < 120) {
					worldState = "WorldStates.IMMORTAL";
				}
				
				if(!worldState.isEmpty())
					writer.println(i+","+j+","+worldState);
			}
		}
		writer.close();
	}
	
}
