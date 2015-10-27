package vgcc.marshmelloween.core;

public class Zombie {

	private double x;
	private int y;
	private int health;
	
	public Zombie(double x, int y, int health)
	{
		this.x = x;
		this.y = y;
		this.health = health;
	}
	
	public double getX()
	{
		return x;
	}
	
	public void setX(double x)
	{
		this.x = x;
	}
	
	public int getY()
	{
		return y;
	}
	
	public int getHealth()
	{
		return health;
	}
	
	public void setHealth(int health)
	{
		this.health = health;
	}
	
	public double getWidth()
	{
		return 5.0;
	}
	
	public double getHeight()
	{
		double wid = getWidth()/MarshGame.BOARD_WIDTH*MarshGame.lastImgSize.width;
		double hei = 29.0/14*wid;
		return hei/MarshGame.lastImgSize.height*MarshGame.BOARD_HEIGHT;
	}
}
