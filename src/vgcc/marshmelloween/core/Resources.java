package vgcc.marshmelloween.core;

import java.awt.image.BufferedImage;
import java.io.File;
import java.lang.reflect.Field;
import java.util.HashMap;

import javax.imageio.ImageIO;

public class Resources {

	private static HashMap<String,Sprite> images;
	
	public static final String IMAGE_BACKGROUND = "background";
	public static final String IMAGE_PLAYER = "player";
	public static final String IMAGE_ZOMBIE = "zombie";
		public static final int STATES_ZOMBIE = 2;
	public static final String IMAGE_MARSHMELLOW = "marshmellow";
	public static final String IMAGE_AMMO = "ammo";
	public static final String IMAGE_CANDY = "candy";
		public static final int STATES_CANDY = 3;
	public static final String IMAGE_SPLASH = "splash";
	public static final String IMAGE_PLAY = "play";
		public static final int STATES_PLAY = 2;
	public static final String IMAGE_LEADER = "leader";
		public static final int STATES_LEADER = 2;
	public static final String IMAGE_LEADERSPLASH = "leadersplash";
	public static final String IMAGE_BACK = "back";
		public static final int STATES_BACK = 2;
	public static final String IMAGE_WINSPLASH = "winsplash";
	public static final String IMAGE_SUBMIT = "submit";
		public static final int STATES_SUBMIT = 2;
	
	public static void loadResources()
	{
		images = new HashMap<String,Sprite>();
		for(Field f : Resources.class.getFields())
		{
			String name = f.getName();
			if(name.startsWith("IMAGE_"))
			{
				String[] spl = name.split("_");
				try {
					int states = 0;
					Field sta = null;
					try {
						sta = Resources.class.getField("STATES_"+spl[1]);
						states = sta.getInt(null);
					} catch (NoSuchFieldException | SecurityException e) {}
					MarshMain.log("Loading "+name+" with "+states+" state"+(states!=1?"s":""));
					loadImage((String) f.get(null),states);
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private static void loadImage(String shortName, int states)
	{
		if(states==0)
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
			images.put(shortName, new Sprite(new BufferedImage[]{img}));
		}
		else
		{
			BufferedImage[] imgs = new BufferedImage[states];
			for(int i=0;i<states;i++)
			{
				BufferedImage img;
				try
				{
					File file = new File("res"+File.separator+shortName+"_"+i+".png");
					img = ImageIO.read(file);
				}
				catch(Exception e)
				{
					System.err.println("Could not load image res/"+shortName+"_"+i+".png");
					img = new BufferedImage(100,100,BufferedImage.TYPE_INT_ARGB);
				}
				imgs[i] = img;
			}
			images.put(shortName, new Sprite(imgs));
		}
	}
	
	public static Sprite getSprite(String sprite)
	{
		return images.get(sprite);
	}
	
	public static BufferedImage getImage(String image)
	{
		Sprite spr = getSprite(image);
		return spr.getImage(0);
	}
}
