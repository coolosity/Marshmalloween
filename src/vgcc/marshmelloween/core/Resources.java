package vgcc.marshmelloween.core;

import java.awt.image.BufferedImage;
import java.io.File;
import java.lang.reflect.Field;
import java.util.HashMap;

import javax.imageio.ImageIO;

public class Resources {

	private static HashMap<String,BufferedImage> images;
	
	public static final String IMAGE_BACKGROUND = "background";
	public static final String IMAGE_PLAYER = "player";
	public static final String IMAGE_ZOMBIE = "zombie";
	public static final String IMAGE_MARSHMELLOW = "marshmellow";
	public static final String IMAGE_AMMO = "ammo";
	public static final String IMAGE_CANDY1 = "candy_1";
	public static final String IMAGE_CANDY2 = "candy_2";
	public static final String IMAGE_CANDY3 = "candy_3";
	public static final String IMAGE_SPLASH = "splash";
	public static final String IMAGE_PLAY0 = "play_0";
	public static final String IMAGE_PLAY1 = "play_1";
	public static final String IMAGE_LEADER0 = "leader_0";
	public static final String IMAGE_LEADER1 = "leader_1";
	public static final String IMAGE_LEADERSPLASH = "leadersplash";
	public static final String IMAGE_BACK0 = "back_0";
	public static final String IMAGE_BACK1 = "back_1";
	public static final String IMAGE_WINSPLASH = "winsplash";
	public static final String IMAGE_SUBMIT0 = "submit_0";
	public static final String IMAGE_SUBMIT1 = "submit_1";
	
	public static void loadResources()
	{
		images = new HashMap<String,BufferedImage>();
		for(Field f : Resources.class.getFields())
		{
			if(f.getName().startsWith("IMAGE_"))
			{
				try {
					loadImage((String) f.get(null));
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private static void loadImage(String shortName)
	{
		BufferedImage img;
		try
		{
			File file = new File("res"+File.separator+shortName+".png");
			img = ImageIO.read(file);
		}
		catch(Exception e)
		{
			System.err.println("Could not load image res/"+shortName+".png");
			img = new BufferedImage(100,100,BufferedImage.TYPE_INT_ARGB);
		}
		images.put(shortName, img);
	}
	
	public static BufferedImage getImage(String image)
	{
		return images.get(image);
	}
}
