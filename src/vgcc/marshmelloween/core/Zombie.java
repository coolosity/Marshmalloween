package vgcc.marshmelloween.core;

public class Zombie {

	private double x;
	private int y;
	private int health;
	private double width, height;
	
	public Zombie(double x, int y, int health)
	{
		this.x = x;
		this.y = y;
		this.health = health;
		width = 4.0;
		height = 8.0;
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
		return width;
	}
	
	public double getHeight()
	{
		return height;
	}
}
