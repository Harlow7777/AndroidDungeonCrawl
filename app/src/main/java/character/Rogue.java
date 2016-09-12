package character;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import equipment.Blade;
import equipment.Item;
import equipment.Utility;
import battle.Dice;
import battle.Fighter;

public class Rogue extends Fighter {
	
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
	
	public Rogue(String name) {
		super(name, LVL, XP, VIT, HP, STR, healthDie, MND, MP, AGI, equipment);
		this.WeaponSlot = new Blade("rapier");
		equipment.add(new Utility("potion", 3));
		equipment.add(new Blade("dagger"));
		this.setEquipment(equipment);
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(this.fighterName);
		dest.writeInt(this.fighterLVL);
		dest.writeInt(this.fighterXP);
		dest.writeInt(this.fighterVIT);
		dest.writeInt(this.fighterHP);
		dest.writeInt(this.healthDie);
		dest.writeInt(this.fighterSTR);
		dest.writeInt(this.fighterMND);
		dest.writeInt(this.fighterMP);
		dest.writeInt(this.fighterAGI);
//		dest.writeTypedArray(this.characterInventory, flags);
		dest.writeList(equipment);
	}

	public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
		public Rogue createFromParcel(Parcel in) {
			return new Rogue(in);
		}

		public Rogue[] newArray(int size) {
			return new Rogue[size];
		}
	};

	public Rogue(Parcel in){
		super(in.readString(), in.readInt(), in.readInt(), in.readInt(), in.readInt(), in.readInt(), in.readInt(), in.readInt(), in.readInt(), in.readInt(), in.readArrayList(Item.class.getClassLoader()));
//		in.readArrayList(Item.class.getClassLoader());
//		this.setEquipment(equipment);
	}

//	public void sneakAttack(Fighter fighter){
//		int roll = Dice.rollDice(20, 1);
//		if (roll > fighter.getFighterPDefense()){
//		    System.out.println(this.fighterName + "'s attack hit " + fighter.getFighterName() + "!");
//		    int damage = (Dice.rollDice(this.fighterDmgDice, 1)+this.fighterSTR)*2;
//		    int newHealth = fighter.getFighterHP() - damage;	
//		    if(newHealth < 0){
//		    	fighter.setFighterHP(0);
//		    } else {
//		    	fighter.setFighterHP(newHealth);
//		    }
//			System.out.println(fighter.getFighterName() + " took " + damage + " damage!");
//			System.out.println(fighter.getFighterName() + " has " + fighter.getFighterHP() + " health left!");
//		}else{
//		   System.out.println(this.fighterName + "'s attack missed!");
//		}
//		this.evaluate();
//	}
	
	public void poisonStab(Fighter enemy){
		enemy.setPoison(true);
		enemy.setPoisonCounter(3);
		System.out.println(enemy.getFighterName() + " was poisoned!");
	}
}
