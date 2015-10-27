package vgcc.marshmelloween.core;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;

public class MarshGame {

	//Game Constants
	public static final int TPS = 40;//ticks per second
	private static final double ZDPT = 0.1875;//zombie distance per tick
	private static final double MDPT = 2.5;//marshmellow distance per tick
	private static final int TPZ_MIN = 20;//ticks per zombie
	private static final int TPZ_DEC = 4;//ticks per zombie decrease per kill
	private static final int TPZ_INIT = 80;//ticks per zombie initial
	private static final double ZHI = .067;//zombie health incriment per kill
	private static final int KILL_POINTS = 10;
	private static final long SHOT_DELAY = 250;
	
	//Display Constants
	private static final int BOARD_WIDTH = 100;
	private static final int BOARD_HEIGHT = 80;
	private static final int AMMO_SPACE = 30;
	private static final int AMMO_WIDTH = 30;
	private static final int AMMO_HEIGHT = 60;
	private static final int AMMO_YBORDER = 20;
	private static final double CANDY_X = 0;
	private static final double CANDY_Y = 0;
	private static final double CANDY_WIDTH = 0.174;
	private static final double CANDY_HEIGHT = 0.755;
	private static final double PLAYER_WIDTH = 4.0;
	private static final double PLAYER_HEIGHT = 12.0;
	private static final double[] rows = {0.12, 0.387, 0.655};
	private static final double playerLoc = 0.181;
	private static final Font fontMain = new Font("Arial",Font.PLAIN,64);
	private static final Color fontColor = Color.RED;
	
	//private MarshMain plugin;
	private Random random;
	private ArrayList<Zombie> zombies;
	private ArrayList<MarshMellow> mellows;

	//Changing values
	private double startingZombieHealth;
	private int tpz;//Ticks per zombie
	private int score;
	private int lives;
	
	private int playerY;
	private int zombieTick;
	private int maxAmmo;
	private int ammo;
	private long nextShot;
	private boolean isReloading;
	private long nextReload;
	private long reloadDelay;
	
	public MarshGame(MarshMain instance)
	{
		//plugin = instance;
		random = new Random();
		zombies = new ArrayList<Zombie>();
		mellows = new ArrayList<MarshMellow>();
		
		startingZombieHealth = 1d;
		tpz = TPZ_INIT;
		score = 0;
		
		playerY = 0;
		lives = 3;
		zombieTick = tpz;
		maxAmmo = 6;
		ammo = maxAmmo;
		reloadDelay = 750;
	}
	
	public void tick()
	{
		if(isReloading)
		{
			if(System.currentTimeMillis()>=nextReload)
			{
				ammo++;
				if(ammo>=maxAmmo)
				{
					isReloading = false;
				}
				nextReload = System.currentTimeMillis()+reloadDelay;
			}
		}
		if(ammo>maxAmmo)
		{
			ammo = maxAmmo;
		}
		zombieTick--;
		if(zombieTick <= 0)
		{
			zombieTick = tpz;
			zombies.add(new Zombie(BOARD_WIDTH,random.nextInt(3),(int)startingZombieHealth));
		}
		//Move marshmellows
		for(int i=mellows.size()-1;i>=0;i--)
		{
			mellows.get(i).setX(mellows.get(i).getX()+MDPT);
			if(mellows.get(i).getX()>100)
			{
				mellows.remove(i);
				d("Mellow removed");
			}
			else
			{
				Zombie coll = null;
				for(Zombie z : zombies)
				{
					if(z.getY()==mellows.get(i).getY())
					{
						if(collides(mellows.get(i).getX(), mellows.get(i).getWidth(), z.getX(), z.getWidth()))
						{
							coll = z;
						}
					}
				}
				if(coll != null)
				{
					startingZombieHealth += ZHI;
					mellows.remove(i);
					zombies.remove(coll);
					tpz -= TPZ_DEC;
					if(tpz < TPZ_MIN)tpz = TPZ_MIN;
					score += KILL_POINTS;
					d("Mellow collide");
				}
			}
		}
		
		//Move zombies
		int lane = -1;
		for(int i=zombies.size()-1;i>=0;i--)
		{
			zombies.get(i).setX(zombies.get(i).getX()-ZDPT);
			if(zombies.get(i).getX()+zombies.get(i).getWidth()<0)
			{
				lane = zombies.get(i).getY();
				zombies.remove(i);
				loseLife();
				d("Zombie Remove");
			}
			else if(playerY == zombies.get(i).getY())
			{
				if(collides(0,PLAYER_WIDTH,zombies.get(i).getX(),zombies.get(i).getWidth()))
				{
					lane = zombies.get(i).getY();
					zombies.remove(i);
					loseLife();
					d("Zombie Collide");
				}
			}
		}
		for(int i=zombies.size()-1;i>=0;i--)
		{
			if(zombies.get(i).getY()==lane)
				zombies.remove(i);
		}
	}
	
	public void playerShoot()
	{
		if(System.currentTimeMillis()>=nextShot)
		{
			if(ammo>0)
			{
				isReloading = false;
				nextShot = System.currentTimeMillis()+SHOT_DELAY;
				mellows.add(new MarshMellow(PLAYER_WIDTH,playerY));
				ammo--;
				if(ammo==0)
				{
					nextReload = System.currentTimeMillis()+reloadDelay;
					isReloading = true;
				}
			}
		}
	}
	
	private boolean collides(double x1, double w1, double x2, double w2)
	{
		return (x1 >= x2 && x1 <= x2+w2) || (x2 >= x1 && x2 <= x1+w1);
	}
	
	private void loseLife()
	{
		lives--;
	}
	
	public void draw(BufferedImage img)
	{
		int boardX = (int) (playerLoc*img.getWidth());
		Graphics g = img.getGraphics();
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, img.getWidth(), img.getHeight());
		g.drawImage(Resources.getImage(Resources.IMAGE_BACKGROUND), 0, 0, img.getWidth(), img.getHeight(), null);
		
		//Draw player
		int pHeight = (int)(img.getHeight()*PLAYER_HEIGHT/BOARD_HEIGHT);
		g.drawImage(Resources.getImage(Resources.IMAGE_PLAYER), boardX, (int)(img.getHeight()*rows[playerY])-pHeight/2, (int)(img.getWidth()*PLAYER_WIDTH/BOARD_WIDTH), pHeight, null);
	
		//Draw zombies
		@SuppressWarnings("unchecked")
		ArrayList<Zombie> zombies = (ArrayList<Zombie>) this.zombies.clone();
		for(Zombie z : zombies)
		{
			int zHeight = (int)(img.getHeight()*z.getHeight()/BOARD_HEIGHT);
			g.drawImage(Resources.getImage(Resources.IMAGE_ZOMBIE), boardX+(int)((img.getWidth()-boardX)*z.getX()/BOARD_WIDTH), (int)(img.getHeight()*rows[z.getY()])-zHeight/2, (int)(img.getWidth()*z.getWidth()/BOARD_WIDTH), zHeight, null);
		}
		
		//Draw mellows
		@SuppressWarnings("unchecked")
		ArrayList<MarshMellow> mellows = (ArrayList<MarshMellow>) this.mellows.clone();
		for(MarshMellow z : mellows)
		{
			int zHeight = (int)(img.getHeight()*z.getHeight()/BOARD_HEIGHT);
			g.drawImage(Resources.getImage(Resources.IMAGE_MARSHMELLOW), boardX+(int)((img.getWidth()-boardX)*z.getX()/BOARD_WIDTH), (int)(img.getHeight()*rows[z.getY()])-zHeight/2, (int)(img.getWidth()*z.getWidth()/BOARD_WIDTH), zHeight, null);
		}
		
		//Draw ammo
		{
			int ammox = img.getWidth()-AMMO_SPACE-AMMO_WIDTH;
			for(int i=0;i<ammo;i++)
			{
				g.drawImage(Resources.getImage(Resources.IMAGE_AMMO), ammox, img.getHeight()-AMMO_HEIGHT-AMMO_YBORDER, AMMO_WIDTH, AMMO_HEIGHT, null);
				ammox -= (AMMO_SPACE+AMMO_WIDTH);
			}
		}
		
		//Draw candy lives
		if(lives>0)
		{
			String imgg = "candy_"+lives;
			g.drawImage(Resources.getImage(imgg), (int)(img.getWidth()*CANDY_X), (int)(img.getHeight()*CANDY_Y), (int)(img.getWidth()*CANDY_WIDTH), (int)(img.getHeight()*CANDY_HEIGHT), null);
		}
		
		//Draw score
		{
			String text = "Score: "+score;
			g.setFont(fontMain);
			g.setColor(fontColor);
			g.drawString(text, 10, img.getHeight()-10);
		}
	}
	
	public boolean isFinished()
	{
		return lives==0;
	}
	
	public int getScore()
	{
		return score;
	}
	
	public void reload()
	{
		nextReload = System.currentTimeMillis()+reloadDelay;
		isReloading = true;
	}
	
	public void playerMove(int dir)
	{
		playerY += dir;
		if(playerY<0)playerY = 0;
		if(playerY>2)playerY = 2;
	}
	
	private void d(String text)
	{
		System.out.println(text);
	}
	
	public void dev()
	{
		lives--;
	}
}
