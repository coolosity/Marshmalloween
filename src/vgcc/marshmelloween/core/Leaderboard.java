package vgcc.marshmelloween.core;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

class Score implements Comparable<Score>
{
	private String name;
	private int score;
	
	public Score(String name, int score)
	{
		this.name = name;
		this.score = score;
	}
	
	public int getScore()
	{
		return score;
	}
	
	public String getName()
	{
		return name;
	}

	@Override
	public int compareTo(Score s) {
		return s.score-score;
	}
}

public class Leaderboard {

	private static final String fileName = "scores.txt";
	
	private static ArrayList<Score> scores;
	
	public static void loadScores()
	{
		scores = new ArrayList<Score>();
		File file = new File(fileName);
		if(file.exists())
		{
			try
			{
				BufferedReader reader = new BufferedReader(new FileReader(file));
				String line;
				while((line=reader.readLine())!=null)
				{
					String[] sp = line.split(" ");
					String name = "";
					for(int i=0;i<sp.length-1;i++)
						name += sp[i];
					int score = 0;
					try
					{
						score = Integer.parseInt(sp[sp.length-1]);
					}
					catch(Exception e)
					{
						MarshMain.log("Could not load score for entry "+name);
					}
					scores.add(new Score(name,score));
				}
				reader.close();
			}
			catch(IOException e)
			{
				MarshMain.log("Could not load scores");
			}
		}
		Collections.sort(scores);
	}
	
	public static void addScore(String name, int score)
	{
		scores.add(new Score(name,score));
		Collections.sort(scores);
		try
		{
			BufferedWriter writer = new BufferedWriter(new FileWriter(new File(fileName)));
			for(Score s : scores)
			{
				writer.write(s.getName()+" "+s.getScore());
				writer.newLine();
			}
			writer.close();
		}
		catch(IOException e)
		{
			MarshMain.log("Could not save scores");
		}
	}
	
	public static ArrayList<Score> getScores()
	{
		return scores;
	}
}
