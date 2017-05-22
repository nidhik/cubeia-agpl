package com.cubeia.game.poker.bot.ai.simple;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;

public class NonLinearRngTest {

	@Test
	public void testRng() {
		int[] count = new int[11];
		
		for (int i = 0; i < 1000; i++) {
			int number = NonLinearRng.nextInt(10);
			count[number]++;
		}
				
		for (int i = 0; i < count.length; i++) {
		//	System.out.println(i+" -> "+count[i]);
		}
		
		
	}
	
	@Test
	public void testZeroInput() {
		int number = NonLinearRng.nextInt(1);
		Assert.assertThat(number, CoreMatchers.is(1));
	}

}
