package com.watoydo.evoGame.graphics;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferStrategy;
import java.io.IOException;
import java.util.Iterator;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import com.watoydo.evoGame.ai.Ai;
import com.watoydo.evoGame.ai.AiCollection;
import com.watoydo.evoGame.world.WorldMap;
import com.watoydo.evoGame.world.WorldStates;
import com.watoydo.utils.Maths;

public class GameWindow extends JFrame {
	
	private static final int FOV_ARC_LENGTH = 20;
	private static final double FOV = 40;
	
	private AiCollection aiCollection;
	private WorldMap map;
	
	private BufferStrategy bufferS;
	private Graphics2D dBuffer;
	
	private double scale;
	
	private double[] panning;
	
	public GameWindow(WorldMap map) {
		
		setIgnoreRepaint(true);
		this.setTitle("Game View");
		this.scale = 1;
		this.panning = new double[]{0,0};
		
		this.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode() == 32) {
				}
			}
			
		});
		
		this.addMouseWheelListener(new MouseWheelListener() {
			
			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				if(e.getWheelRotation() == 1)
					scale-=1;
				else
					scale+=1;
				
				if(scale < 1)
					scale = 1;
				else if(scale > 30)
					scale = 30;
			}
		});
		
		this.map = map;
		this.aiCollection = new AiCollection(map);
		
		final Timer aiTimer = new Timer(10, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				aiCollection.applyRules(map);
				aiCollection.breed();
				repaint();
			}
		});
		
		aiTimer.start();

		for(int i = 0; i < 4; i++) {
			aiCollection.populate();
		}
	}
	
	@Override
	public void paint(Graphics g) {
		dBuffer = (Graphics2D) bufferS.getDrawGraphics();

		dBuffer.setColor(Color.white);
		dBuffer.fillRect(0, 0, this.getWidth(), this.getHeight());
		
		AffineTransform affineTransform = new AffineTransform();
		affineTransform.setToTranslation(panning[0], panning[1]);
		dBuffer.setTransform(affineTransform);
		
		drawMap(dBuffer);

		Iterator<Ai> iterator = aiCollection.getIterator();
		while(iterator.hasNext()) {
			drawAiView(dBuffer, iterator.next());
		}
		
		dBuffer.setColor(Color.BLACK);
		dBuffer.drawString("Population: " + aiCollection.getPopulationCount(), 300, 300);
		
		bufferS.show();

		g.dispose();
		dBuffer.dispose();
	}
	
	private void drawAiView(Graphics2D g, Ai ai) {
		
		double xAi = ai.getX() * scale;
		double yAi = ai.getY() * scale;

		g.setColor(ai.getColour());
		g.fillOval((int)xAi - 5, (int)yAi - 5, 10, 10);

		drawFOV(g, ai);
	}
	
	private void drawMap(Graphics2D g) {
		for (int i = 0; i < this.map.worldBlocks.length; i++) {
			for (int j = 0; j < map.worldBlocks[i].length; j++) {
				WorldStates worldState = map.worldBlocks[i][j];
				
				if(map.hasAi(new int[]{i,j}))
					worldState = WorldStates.AI;
				
				if(map.hasBlob(new int[]{i,j}))
					worldState = map.getBlob(new int[]{i,j});
				
				switch (worldState) {
					case EMPTY:
						g.setColor(Color.gray);
					break;
					case OCCUPIED:
						g.setColor(Color.black);
					break;
					case DEFAULT:
						g.setColor(Color.pink);
					break;
					case IMMORTAL1:
						g.setColor(Color.green.darker().darker());
					break;
					case IMMORTAL2:
						g.setColor(Color.green.darker());
					break;
					case IMMORTAL3:
						g.setColor(Color.green);
					break;
					case AI:
						g.setColor(Color.red);
					break;
					default:
					break;
					}
					g.fillRect((int)(i * scale - scale / 2), (int)(j * scale - scale / 2), (int)scale, (int)scale);
			}
		}
	}
	
	private void drawFOV(Graphics2D g, Ai ai) {
		g.drawLine((int)(ai.getX()*scale), (int)(ai.getY()*scale),
				   (int)(ai.getX()*scale + FOV_ARC_LENGTH * Math.cos(ai.getRotation() + Math.toRadians(FOV) * 0.5)),
				   (int)(ai.getY()*scale + FOV_ARC_LENGTH * Math.sin(ai.getRotation() + Math.toRadians(FOV) * 0.5)));
		
		g.drawLine((int)(ai.getX()*scale), (int)(ai.getY()*scale),
				   (int)(ai.getX()*scale + FOV_ARC_LENGTH * Math.cos(ai.getRotation() - Math.toRadians(FOV) * 0.5)),
				   (int)(ai.getY()*scale + FOV_ARC_LENGTH * Math.sin(ai.getRotation() - Math.toRadians(FOV) * 0.5)));
	}
	
	protected void createBuffers() {
		this.createBufferStrategy(3);
		this.requestFocus();
		bufferS = this.getBufferStrategy();
	}
	
	public static void main(String[] args) throws IOException {
		
		WorldMap map = new WorldMap("C:/Users/Charles/Desktop/Map.txt");
		
		final GameWindow frame = new GameWindow(map);
		
		final Timer timer = new Timer(100, new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				frame.repaint();
				
				Point mousePosition = frame.getMousePosition();
				
				if(mousePosition == null || Maths.getDistance(mousePosition.x, mousePosition.y, frame.getWidth() / 2, frame.getHeight() / 2) < 50)
					return;
				
				frame.panning[0] -= (mousePosition.x - frame.getWidth() / 2) * 0.1;
				frame.panning[1] -= (mousePosition.y - frame.getHeight() / 2) * 0.1;
			}
		});
		
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				frame.setSize(800, 800);
				frame.setVisible(true);
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.createBuffers();
				timer.start();
			}
		});
	}

}
