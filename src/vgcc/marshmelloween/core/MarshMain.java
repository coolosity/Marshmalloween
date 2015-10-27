package vgcc.marshmelloween.core;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;

public class MarshMain implements KeyListener, Runnable, MouseListener, MouseMotionListener {
	
	private static final double[] PLAY = {0.75,0.88,0.4,0.36};
	private static final double[] LEADER = {0.8,0.23,0.264,0.357};
	private static final double[] BACK = {0.15/2,0.156/2,0.242/2,0.272/2};
	private static final double[] SUBMIT = {0.1,0.9,0.4,0.36};
	
	private static final Font leaderboardFont = new Font("Papyrus",Font.PLAIN,25);
	private static final Color leaderboardColor = Color.WHITE;
	private static final double LEADERBOARD_Y = 0.3;
	private static final int LEADERBOARD_COLUMNS = 3;
	private static final double LEADERBOARD_BORDER = 0.05;
	private static final double LEADERBOARD_YSPACE = 0;
	private static final double LEADERBOARD_COLSPACE = 0.05;
	
	private static final Font winFont = new Font("Arial",Font.PLAIN,32);
	private static final Color winColor = Color.WHITE;
	private static final double WIN_Y = 0.5;
	private static final int WIN_MAXLEN = 16;
	private static final String valid = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ!@#$%^&*()_-=+. ,1234567890<>:;[]{}`~/|\\";
	
	private static Dimension screenSize;
	
	public static void main(String[] args)
	{
		Resources.loadResources();
		Leaderboard.loadScores();
		screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
		new MarshMain();
	}
	
	private boolean fullscreen;
	
	private MarshDisplay display;
	private MarshGame current;
	private HashMap<Integer,Boolean> keysPressed;
	private Point mse;
	private boolean hoverPlay, startGame;
	private boolean hoverLeader, hoverBack, goLeader;
	private boolean hoverSubmit, goSubmit;
	private int score;
	private long nextTick;
	private String currentName;
	
	public MarshMain()
	{
		mse = new Point(0,0);
		keysPressed = new HashMap<Integer,Boolean>();
		display = new MarshDisplay(this);
		display.setFullScreen(fullscreen);
		score = -1;
		(new Thread(this)).start();
	}

	@Override
	public void keyPressed(KeyEvent e) {
		int code = e.getKeyCode();
		if(score != -1)
		{
			if(code==8)
			{
				if(currentName.length()>0)
					currentName = currentName.substring(0,currentName.length()-1);
				return;
			}
			else if(code==10)
			{
				goSubmit = true;
				return;
			}
			char key = e.getKeyChar();
			if(valid.indexOf(String.valueOf(key))>=0 && currentName.length()<WIN_MAXLEN)
				currentName += key;
			return;
		}
		keysPressed.put(code, true);
		System.out.println(code);
		switch(code)
		{
		//ESC
		case 27:
			System.exit(0);
			break;
		//F11
		case 122:
			fullscreen = !fullscreen;
			display.setFullScreen(fullscreen);
			break;
		//UP ARROW
		case 38:
			if(current!=null)
				current.playerMove(-1);
			break;
		//DOWN ARROW
		case 40:
			if(current!=null)
				current.playerMove(1);
			break;
		//D
		case 68:
			if(current!=null)
				current.dev();
		//R
		case 82:
			if(current!=null)
				current.reload();
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		int code = e.getKeyCode();
		keysPressed.put(code, false);
	}

	@Override
	public void keyTyped(KeyEvent e) {
		
	}

	@Override
	public void run() {
		while(true)
		{
			if(startGame)
			{
				startGame = false;
				log("Starting New Game");
				current = new MarshGame(this);
			}
			if(goSubmit)
			{
				if(currentName.length()==0)
					currentName = "[Unknown]";
				if(score>=0)
					Leaderboard.addScore(currentName, score);
				currentName = "";
				score = -1;
				goSubmit = false;
			}
			//Leaderboard
			if(goLeader)
			{
				display.update(getLeader());
			}
			//Win screen
			else if(score != -1)
			{
				display.update(getWinScreen());
			}
			//SPLASH SCREEN
			else if(current==null)
			{
				display.update(getSplash());
			}
			else
			{
				if(System.currentTimeMillis()>=nextTick)
				{
					nextTick = System.currentTimeMillis()+(1000/MarshGame.TPS);
					current.tick();
					if(keysPressed.containsKey(32))
					{
						if(keysPressed.get(32))
						{
							current.playerShoot();
						}
					}
				}
				BufferedImage image = new BufferedImage(screenSize.width,screenSize.height,BufferedImage.TYPE_INT_ARGB);
				current.draw(image);
				display.update(image);
				if(current.isFinished())
				{
					currentName = "";
					score = current.getScore();
					current = null;
				}
			}
		}
	}
	
	private BufferedImage getSplash()
	{
		Dimension screenSize = display.getDimension();
		Point mse = display.convertMSE(this.mse);
		if(!fullscreen)
		{
			mse.x -= 3;
			mse.y -= 25;
		}
		BufferedImage splash = Resources.getImage(Resources.IMAGE_SPLASH);
		int width = screenSize.width;
		int height = width*splash.getHeight()/splash.getWidth();
		if(height>screenSize.height)
		{
			height = screenSize.height;
			width = height*splash.getWidth()/splash.getHeight();
		}
		BufferedImage img = new BufferedImage(width,height,BufferedImage.TYPE_INT_ARGB);
		Graphics g = img.getGraphics();
		g.drawImage(splash, 0, 0, img.getWidth(), img.getHeight(), null);
		
		//Play button
		{
			int pwidth = (int)(PLAY[2]*img.getWidth());
			int pheight = (int)(PLAY[3]*img.getHeight());
			int px = (int)(PLAY[0]*img.getWidth())-pwidth/2;
			int py = (int)(PLAY[1]*img.getHeight())-pheight/2;
			int state = 0;
			hoverPlay = false;
			if(mse.x >= px && mse.y >= py && mse.x <= px+pwidth && mse.y <= py+pheight)
			{
				state = 1;
				hoverPlay = true;
			}
			g.drawImage(Resources.getSprite(Resources.IMAGE_PLAY).getImage(state), px, py, pwidth, pheight, null);
		}
		
		//Leader button
		{
			int pwidth = (int)(LEADER[2]*img.getWidth());
			int pheight = (int)(LEADER[3]*img.getHeight());
			int px = (int)(LEADER[0]*img.getWidth())-pwidth/2;
			int py = (int)(LEADER[1]*img.getHeight())-pheight/2;
			int state = 0;
			hoverLeader = false;
			if(mse.x >= px && mse.y >= py && mse.x <= px+pwidth && mse.y <= py+pheight)
			{
				state = 1;
				hoverLeader = true;
			}
			g.drawImage(Resources.getSprite(Resources.IMAGE_LEADER).getImage(state), px, py, pwidth, pheight, null);
		}
		return img;
	}
	
	private BufferedImage getLeader()
	{
		Dimension screenSize = display.getFrameSize();
		Point mse = display.convertMSE(this.mse);
		if(!fullscreen)
		{
			mse.x -= 3;
			mse.y -= 25;
		}
		BufferedImage splash = Resources.getImage(Resources.IMAGE_LEADERSPLASH);
		int width = screenSize.width;
		int height = width*splash.getHeight()/splash.getWidth();
		if(height>screenSize.height)
		{
			height = screenSize.height;
			width = height*splash.getWidth()/splash.getHeight();
		}
		BufferedImage img = new BufferedImage(width,height,BufferedImage.TYPE_INT_ARGB);
		Graphics g = img.getGraphics();
		g.drawImage(splash, 0, 0, img.getWidth(), img.getHeight(), null);
		
		//Back button
		{
			int pwidth = (int)(BACK[2]*img.getWidth());
			int pheight = (int)(BACK[3]*img.getHeight());
			int px = (int)(BACK[0]*img.getWidth())-pwidth/2;
			int py = (int)(BACK[1]*img.getHeight())-pheight/2;
			int state = 0;
			hoverBack = false;
			if(mse.x >= px && mse.y >= py && mse.x <= px+pwidth && mse.y <= py+pheight)
			{
				state = 1;
				hoverBack = true;
			}
			g.drawImage(Resources.getSprite(Resources.IMAGE_BACK).getImage(state), px, py, pwidth, pheight, null);
		}
		
		//Scores
		{
			ArrayList<Score> scores = Leaderboard.getScores();
			FontMetrics fm = display.getFontMetrics(leaderboardFont);
			g.setFont(leaderboardFont);
			g.setColor(leaderboardColor);
			int starty = (int) (img.getHeight()*LEADERBOARD_Y);
			int cury = starty;
			int colwidth = (int) ((img.getWidth()-img.getWidth()*LEADERBOARD_BORDER*2-img.getWidth()*LEADERBOARD_COLSPACE*(LEADERBOARD_COLUMNS-1))/LEADERBOARD_COLUMNS);
			int curx = (int)(img.getWidth()*LEADERBOARD_BORDER);
			int curscore = 0;
			while(curscore < scores.size() && curx < img.getWidth())
			{
				Score s = scores.get(curscore++);
				String str = (curscore)+") "+s.getName();
				String end = ":";
				String sscore = ""+s.getScore();
				int endx = curx+colwidth-fm.stringWidth(sscore);
				while(curx+fm.stringWidth(str+end)>endx)
				{
					str = str.substring(0,str.length()-1);
				}
				g.drawString(str+end, curx, cury);
				g.drawString(sscore, endx, cury);
				cury += fm.getHeight()+(int)(img.getHeight()*LEADERBOARD_YSPACE);
				if(cury>img.getHeight())
				{
					cury = starty;
					curx += colwidth+img.getWidth()*LEADERBOARD_COLSPACE;
				}
			}
		}
		return img;
	}
	
	private BufferedImage getWinScreen()
	{
		Dimension screenSize = display.getDimension();
		Point mse = display.convertMSE(this.mse);
		if(!fullscreen)
		{
			mse.x -= 3;
			mse.y -= 25;
		}
		BufferedImage splash = Resources.getImage(Resources.IMAGE_WINSPLASH);
		int width = screenSize.width;
		int height = width*splash.getHeight()/splash.getWidth();
		if(height>screenSize.height)
		{
			height = screenSize.height;
			width = height*splash.getWidth()/splash.getHeight();
		}
		BufferedImage img = new BufferedImage(width,height,BufferedImage.TYPE_INT_ARGB);
		Graphics g = img.getGraphics();
		g.drawImage(splash, 0, 0, img.getWidth(), img.getHeight(), null);
		
		//Submit button
		{
			int pwidth = (int)(SUBMIT[2]*img.getWidth());
			int pheight = (int)(SUBMIT[3]*img.getHeight());
			int px = (int)(SUBMIT[0]*img.getWidth())-pwidth/2;
			int py = (int)(SUBMIT[1]*img.getHeight())-pheight/2;
			int state = 0;
			hoverSubmit = false;
			if(mse.x >= px && mse.y >= py && mse.x <= px+pwidth && mse.y <= py+pheight)
			{
				state = 1;
				hoverSubmit = true;
			}
			g.drawImage(Resources.getSprite(Resources.IMAGE_SUBMIT).getImage(state), px, py, pwidth, pheight, null);
		}
		
		//Name
		{
			FontMetrics fm = display.getFontMetrics(winFont);
			g.setFont(winFont);
			g.setColor(winColor);
			int yloc = (int)(img.getHeight()*WIN_Y);
			int x = img.getWidth()/2 - fm.stringWidth(currentName)/2;
			g.drawString(currentName, x, yloc);
		}
		return img;
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		mse = e.getPoint();
	}
	
	@Override
	public void mousePressed(MouseEvent e) {
		if(current != null || score != -1)
			hoverPlay = false;
		if(hoverPlay)
		{
			log("Click: Play");
			hoverPlay = false;
			startGame = true;
		}
		if(hoverLeader)
		{
			log("Click: Leaderboard");
			hoverLeader = false;
			goLeader = true;
		}
		if(hoverBack)
		{
			log("Click: Back");
			hoverBack = false;
			goLeader = false;
		}
		if(hoverSubmit)
		{
			log("Click: Submit");
			hoverSubmit = false;
			goSubmit = true;
		}
	}
	
	public FontMetrics getFontMetrics(Font font)
	{
		return display.getFontMetrics(font);
	}
	
	@Override
	public void mouseDragged(MouseEvent e) {}

	@Override
	public void mouseClicked(MouseEvent e) {}

	@Override
	public void mouseEntered(MouseEvent e) {}

	@Override
	public void mouseExited(MouseEvent e) {}

	@Override
	public void mouseReleased(MouseEvent e) {}

	public static void log(String text)
	{
		System.out.println(text);
	}
}
