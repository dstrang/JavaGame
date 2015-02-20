package utilities;

import java.awt.Color;
import java.util.Random;

public class Util {
	
	public int randomInteger(int min, int max){
		Random rand = new Random();
	    int randomNum = rand.nextInt((max - min) + 1) + min;
	    return randomNum;	
	}
	
	public Color randomColor(){
		Random rand = new Random();
		int r = rand.nextInt(255);
		int g = rand.nextInt(255);
		int b = rand.nextInt(255);
		return new Color(r,g,b);
	}
}
