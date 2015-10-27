package vgcc.marshmelloween.core;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.JPanel;

class MyPanel extends JPanel
{
	private static final long serialVersionUID = 1L;

	private BufferedImage img;
	
	public void paintComponent(Graphics g)
	{
		g.clearRect(0, 0, this.getWidth(), this.getHeight());
		g.drawImage(img, 0, 0, this);
	}
	
	public void setImage(BufferedImage img)
	{
		this.img = img;
	}
}

public class MarshDisplay {

	private GraphicsDevice device;
	private MarshMain inputReceiver;
	private Dimension frameSize;
	
	private JFrame frame;
	private MyPanel panel;
	private boolean fullscreen;
	private Dimension lastScale, lastOffset;
	
	public MarshDisplay(MarshMain inputReceiver)
	{
		frameSize = new Dimension(800,558);
		device = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices()[0];
		this.inputReceiver = inputReceiver;
		
		panel = new MyPanel();
		
		fullscreen = false;
		
		frame = makeFrame(fullscreen);
		frame.setVisible(true);
		lastScale = new Dimension(1,1);
		lastOffset = new Dimension(1,1);
	}
	
	public Dimension getFrameSize()
	{
		return frameSize;
	}
	
	private JFrame makeFrame(boolean fullscreen)
	{
		JFrame frame = new JFrame("Marshmelloween");
		frame.setLayout(new BorderLayout());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setUndecorated(fullscreen);
		frame.setSize(frameSize);
		frame.setResizable(false);
		frame.add(panel, BorderLayout.CENTER);
		frame.addKeyListener(inputReceiver);
		frame.addMouseListener(inputReceiver);
		frame.addMouseMotionListener(inputReceiver);
		return frame;
	}
	
	public Dimension getDimension()
	{
		return panel.getSize();
	}
	
	public Point convertMSE(Point mse)
	{
		double xx = (mse.x-lastOffset.width/2)*1.0/(panel.getWidth()-lastOffset.width);
		double yy = (mse.y-lastOffset.height/2)*1.0/(panel.getHeight()-lastOffset.height);
		int x = (int)(xx*lastScale.width);
		int y = (int)(yy*lastScale.height);
		return new Point(x,y);
	}
	
	public void update(BufferedImage img)
	{
		BufferedImage img2 = new BufferedImage(panel.getWidth(),panel.getHeight(),img.getType());
		int width = img2.getWidth();
		int height = width*img.getHeight()/img.getWidth();
		if(height>img2.getHeight())
		{
			height = img2.getHeight();
			width = height*img.getWidth()/img.getHeight();
		}
		int xspace = img2.getWidth()-width;
		int yspace = img2.getHeight()-height;
		lastScale.width = img.getWidth();
		lastScale.height = img.getHeight();
		lastOffset.width = xspace;
		lastOffset.height = yspace;
		img2.getGraphics().drawImage(img, xspace/2, yspace/2, width, height, null);
		panel.setImage(img2);
		panel.repaint();
	}
	
	public void setFullScreen(boolean fullscreen)
	{
		if(this.fullscreen == fullscreen)
			return;
		this.fullscreen = fullscreen;
		frame.dispose();
		frame = makeFrame(fullscreen);
		frame.setVisible(true);
		if(fullscreen)
			device.setFullScreenWindow(frame);
		else
			device.setFullScreenWindow(null);
	}
	
	public FontMetrics getFontMetrics(Font font)
	{
		return frame.getFontMetrics(font);
	}
}
