package vgcc.marshmelloween.core;

public class MarshMellow {

	private double x;
	private int y;
	private double width, height;
	
	public MarshMellow(double x, int y)
	{
		this.x = x;
		this.y = y;
		width = 2.0;
		height = 1.0;
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
	
	public double getWidth()
	{
		return width;
	}
	
	public double getHeight()
	{
		return height;
	}
}
