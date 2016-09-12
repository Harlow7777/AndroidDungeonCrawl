package character;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import equipment.Blade;
import equipment.Item;
import equipment.Utility;
import battle.Fighter;

public class Knight extends Fighter implements Serializable {
	/*
	 * HUZZAH!
	 */
	
	static int healthDie = 6;
	static int LVL = 1;
	static int XP = 0;
	static int VIT = 5;
	static int HP = 7;
	static int STR = 5;
	static int MND = 7;
	static int MP = 9;
	static int  AGI = 10;
	static List<Item> equipment = new ArrayList<Item>();
	
	public Knight(String name){
		super(name, LVL, XP, VIT, HP, STR, healthDie, MND, MP, AGI, equipment);
		this.WeaponSlot = new Blade("broadsword");
		equipment.add(new Utility("potion", 3));
		this.setEquipment(equipment);
	}
	
	public void moralBoost(){
		System.out.println("HUZZAH!");
		
	}
}
