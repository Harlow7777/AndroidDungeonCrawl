package enemy;

import java.util.ArrayList;
import java.util.List;

import battle.Fighter;
import equipment.Item;

public class Enemy extends Fighter{

	static int healthDie = 6;
	static int LVL = 1;
	static int XP = 0;
	static int VIT = 5;
	static int HP = 7;
	static int STR = 5;
	static int MND = 7;
	static int MP = 9;
	static int  AGI = 10;
	static List<Item> equipment;
	
	public Enemy(String name) {
		super(name, LVL, XP, VIT, HP, STR, healthDie, MND, MP, AGI, equipment);
	}
}
