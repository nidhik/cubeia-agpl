package com.cubeia.game.poker.bot.ai.simple;

import java.util.Random;

public class NonLinearRng {
	
	static Random r = new Random();
	
	/**
	 * Returns value between 1 (inclusive) and maxSize (inclusive).
	 * @param maxSize
	 * @return
	 */
	public static int nextInt(int maxSize){
	    //Get a linearly multiplied random number
	    int randomMultiplier = maxSize * (maxSize + 1) / 2;
	    
	    int randomInt = r.nextInt(randomMultiplier);

	    //Linearly iterate through the possible values to find the correct one
	    int linearRandomNumber = 0;
	    for(int i=maxSize; randomInt >= 0; i--){
	        randomInt -= i;
	        linearRandomNumber++;
	    }

	    return linearRandomNumber;
	}
	
}
