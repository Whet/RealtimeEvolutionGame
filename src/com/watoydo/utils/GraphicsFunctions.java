package com.watoydo.utils;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;

public class GraphicsFunctions {
	
	private static final int LINE_GAP = 40;
	private static final int LINE_LENGTH = 40;
	private static final double ARROW_ANGLE = Math.PI * 1.2;
	private static final int ARROW_LENGTH = 8;
	private static final int ARROW_GAP = 1;
	private static int arrowGapCount = 0;
	private static boolean reached = false;
	private static double angle = 0;
	private static double[] linePos = new double[2];
	
	public static void drawArrowLine(Graphics2D drawShape, double[] start, double[] end, Color colour, Float alpha){
		drawArrowLine(drawShape,start[0],start[1],end[0],end[1],colour,alpha);
	}
	
	public static void drawArrowLine(Graphics2D drawShape, double startX, double startY, double endX, double endY, Color colour, Float alpha){
		
		// set colour
		drawShape.setColor(colour);
		
		// set arrow alpha
		drawShape.setComposite(makeComposite(alpha));
		
		// reset draw vars
		arrowGapCount = 0;
		
		reached = false;
		
		angle = Maths.getRads(startX,startY, endX,endY);
		
		linePos[0] = startX;
		linePos[1] = startY;
		
		while(reached == false){
			
			// If drawing a line won't overshoot the target
			if(Maths.getDistance(linePos[0],linePos[1],endX,endY) > LINE_LENGTH){
				
				// Draw Line to the next position along to target
				drawShape.drawLine((int)(linePos[0]),
								   (int)(linePos[1]),
								   (int)(linePos[0] + Math.cos(angle) * LINE_LENGTH),
								   (int)(linePos[1] + Math.sin(angle) * LINE_LENGTH));
				
				// Update line position
				linePos[0] += Math.cos(angle) * LINE_LENGTH;
				linePos[1] += Math.sin(angle) * LINE_LENGTH;
				
				// Draw arrow if at correct position
				if(arrowGapCount == 0){
					drawArrow(drawShape);
				}
				
			}
			// draw line to reach target exactly
			else{
				
				// Draw Line to the ai target
				drawShape.drawLine((int) (linePos[0]),
								   (int) (linePos[1]),
								   (int) (endX),
								   (int) (endY));

				reached = true;
				
				linePos[0] = endX;
				linePos[1] = endY;
				
				drawArrow(drawShape);
				
				// reset alpha
				drawShape.setComposite(makeComposite(1f));
				return;
			}
			// if a gap can be fit in without overshooting target
			if(Maths.getDistance(linePos[0],linePos[1], endX, endY) > LINE_GAP){
				linePos[0] += Math.cos(angle) * LINE_LENGTH;
				linePos[1] += Math.sin(angle) * LINE_LENGTH;
				
				arrowGapCount++;
				if(arrowGapCount > ARROW_GAP){
					arrowGapCount = 0;
				}
			}
			else{
				reached = true;
				
				linePos[0] = endX;
				linePos[1] = endY;
				
				drawArrow(drawShape);
				
				// reset alpha
				drawShape.setComposite(makeComposite(1f));
				return;
			}
		}
	}
	
	private static void drawArrow(Graphics2D drawShape){
		drawShape.drawLine((int) (linePos[0]),
						   (int) (linePos[1]),
						   (int)(linePos[0] + Math.cos(angle - ARROW_ANGLE) * ARROW_LENGTH), 
						   (int)(linePos[1] + Math.sin(angle - ARROW_ANGLE) * ARROW_LENGTH));
		
		drawShape.drawLine((int) (linePos[0]),
				   		   (int) (linePos[1]),
						   (int)(linePos[0] + Math.cos(angle + ARROW_ANGLE) * ARROW_LENGTH),
						   (int)(linePos[1] + Math.sin(angle + ARROW_ANGLE) * ARROW_LENGTH));
	}
	
	public static AlphaComposite makeComposite(float alpha){
		int type = AlphaComposite.SRC_OVER;
		return(AlphaComposite.getInstance(type, alpha));
	}
	
}
