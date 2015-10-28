package vgcc.marshmelloween.core;

import java.awt.image.BufferedImage;

public class Sprite {

	private BufferedImage[] images;
	private int cur;
	
	public Sprite(BufferedImage[] images)
	{
		this.images = images;
		this.cur = 0;
	}
	
	public BufferedImage getImage(int state)
	{
		return images[state];
	}
	
	public BufferedImage getNext()
	{
		BufferedImage next = images[cur];
		cur++;
		if(cur>=images.length)
		{
			cur -= images.length;
		}
		return next;
	}
}
