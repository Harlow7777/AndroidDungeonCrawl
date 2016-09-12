package character;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import equipment.Club;
import equipment.Item;
import equipment.Utility;
import battle.Fighter;

public class Wizard extends Fighter implements Serializable {

	static int LVL = 0;
	static int XP = 0;
	static int VIT = 0;
	static int HP = 0;
	static int STR = 0;
	static int healthDie = 0;
	static int MND = 0;
	static int MP = 0;
	static int AGI = 0;
	static List<Item> equipment = new ArrayList<Item>();
	
	public Wizard(String name) {
		super(name, LVL, XP, VIT, HP, STR, healthDie, MND, MP, AGI, equipment);
		this.WeaponSlot = new Club("staff");
		equipment.add(new Utility("potion", 3));
		this.setEquipment(equipment);
	}
}
