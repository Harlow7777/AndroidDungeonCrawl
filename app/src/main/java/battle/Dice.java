package battle;
import java.util.Random;

//
//call rollDice, you tell it how many sides, and the number of times to roll it.
//rollNum = number of times you've rolled it.
//numDice = how many dice you need to roll.
//while number of times rolled is less than the amount that you need to roll, roll the dice, 
//add the amount from the roll to the rollTotal
//increase number of rolls made
//return the total number
//am i catching that right?


public  class Dice {
	
public static int rollDice(int sides, int numDice) {
		int rollNum = 0;
		int rollTotal = 0;
		while ( rollNum < numDice ) {
			int roll = new Random().nextInt(sides)+1;
			rollTotal += roll;
			rollNum++;
		}
		return rollTotal;
	}
}
