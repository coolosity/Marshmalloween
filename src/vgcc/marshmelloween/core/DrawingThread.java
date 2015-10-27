package vgcc.marshmelloween.core;

import java.awt.Dimension;
import java.awt.image.BufferedImage;

public class DrawingThread implements Runnable {

	private static final int FPS = 60;
	
	private MarshGame game;
	private MarshDisplay display;
	private Dimension screenSize;
	
	public DrawingThread(MarshGame game, MarshDisplay display)
	{
		this.game = game;
		this.display = display;
		screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
		(new Thread(this)).start();
	}
	
	public void run()
	{
		long nextFrame = 0;
		while(!game.isFinished())
		{
			if(System.currentTimeMillis()>=nextFrame)
			{
				nextFrame = System.currentTimeMillis()+(1000/FPS);
				BufferedImage image = new BufferedImage(screenSize.width,screenSize.height,BufferedImage.TYPE_INT_ARGB);
				game.draw(image);
				display.update(image);
			}
		}
	}
}
