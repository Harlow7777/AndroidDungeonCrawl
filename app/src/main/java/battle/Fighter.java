package battle;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Base64;
import android.widget.TextView;

import com.dungeoncrawl.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import equipment.Armor;
import equipment.Item;
import equipment.Utility;
import equipment.Weapon;
import battle.Dice;

public class Fighter extends GameSetup implements Parcelable {
	/*
	 * Default values for maxhealth, health and damage These values are
	 * initialized when you create a new Hero in your SetupPlayer class
	 */

	public String fighterName = "";
	public int fighterLVL = 0, fighterMaxLVL = 99;
	public int fighterXP = 0, fighterMaxXP = 0;
	public int fighterVIT = 0, fighterMaxVIT = 255;
	public int fighterHP = 0, fighterMaxHP = 10;
	public int healthDie = 0;
	public int fighterSTR = 0, fighterMaxSTR = 255;
	public int fighterMND = 0, fighterMaxMND = 255;
	public int fighterMP = 0, fighterMaxMP = 0;
	public int fighterAGI = 0, fighterMaxAGI = 255;
	public boolean poison = false;
	public boolean hidden = false;
	public int poisonCounter = 0;
//TODO: Add more equipment slots eventually?
	public Weapon WeaponSlot = new Weapon("","");
	public Armor ArmorSlot = new Armor("","");
	public List<Item> characterInventory;

	public Fighter(String name, int LVL, int XP, int VIT, int HP, int STR, 
			int healthDie, int MND, int MP, int AGI, List<Item> items) {
		this.fighterName = name;
		this.fighterLVL = LVL;
		this.fighterXP = XP;
		//TODO: Need to find out how we're going to do level up XP. this.fighterLVLUP?
		this.fighterVIT = VIT;
		this.fighterHP = HP;
		this.healthDie = healthDie;
		this.fighterSTR = STR;
		this.fighterMND = MND;
		this.fighterMP = MP;
		this.fighterAGI = AGI;
		this.characterInventory = items;
		//TODO: create formulas for MaxHP and MaxMP.
	}

	public int getFighterLVL() {
		return this.fighterLVL;
	}

	public void setFighterLVL(int newFighterLVL) {
		this.fighterLVL = newFighterLVL;
	}

	public int getFighterXP() {
		return this.fighterXP;
	}

	public void setFighterXP(int newFighterXP) {
		this.fighterXP = newFighterXP;
	}

	public int getFighterVIT() {
		return this.fighterVIT;
	}

	public void setFighterVIT(int newFighterVIT) {
		this.fighterVIT = newFighterVIT;
	}

	public int getFighterHP() {
		return this.fighterHP;
	}

	public void setFighterHP(int newFighterHP) {
		this.fighterHP = newFighterHP;
	}

	public int getFighterMaxHP() {
		return this.fighterMaxHP;
	}

	public void setFighterMaxHP(int newFighterMaxHP) {
		this.fighterMaxHP = newFighterMaxHP;
	}

	public int getFighterSTR() {
		return this.fighterSTR;
	}

	public void setFighterSTR(int newFighterSTR) {
		this.fighterSTR = newFighterSTR;
	}

	public int getFighterMND() {
		return this.fighterMND;
	}

	public void setFighterMND(int newFighterMND) {
		this.fighterMND = newFighterMND;
	}

	public int getFighterMP() {
		return this.fighterMP;
	}

	public void setFighterMP(int newFighterMP) {
		this.fighterMP = newFighterMP;
	}

	public int getFighterMaxMP() {
		return this.fighterMaxMP;
	}

	public void setFighterMaxMP(int newFighterMaxMP) {
		this.fighterMaxMP = newFighterMaxMP;
	}

	public int getFighterAGI() {
		return this.fighterAGI;
	}

	public void setFighterAGI(int newFighterAGI) {
		this.fighterAGI = newFighterAGI;
	}

	public void setEquipment(List<Item> items) {
		this.characterInventory = items;
	}

	public String getFighterName() {
		return fighterName;
	}

	public void setFighterName(String newName) {
		this.fighterName = newName;
	}

	public void setPoison(boolean newPoison) {
		this.poison = newPoison;
	}

	public void setPoisonCounter(int newPoisonCounter) {
		this.poisonCounter = newPoisonCounter;
	}
	
	public void setHidden(boolean newHidden){
		this.hidden = newHidden;
	}
	
	public boolean THEO(Fighter enemy){
		if(enemy.getFighterAGI() == 0){
			return true;
		}else if((this.fighterAGI/enemy.getFighterAGI()) >= (Dice.rollDice(100,1)/100)){
			return true;
		}else{
			System.out.println("Attack Misses!");
			return false;
		}
	}
	
//TODO: create armorBonus percentages and defense values of different armors.

	public void PAttacks(Fighter fighter) {
		int fighterPAttack = 0, enemyPDefense = 0;
		if (THEO(fighter) == true) {
			fighterPAttack = ((this.getFighterSTR() / 2) + ((this.getFighterLVL() / 2) + (this.getFighterAGI() / 10)) + WeaponSlot.dmgDie);

			enemyPDefense = ((fighter.getFighterAGI() / 10) + (fighter.getFighterSTR() / 5) + (fighter.getFighterVIT() / 10));

			int pDamage = ((fighterPAttack * (255 - enemyPDefense)) / 256) + 1;
			int newHealth = fighter.getFighterHP() - pDamage;

			// prevent negative health
			if (newHealth < 0) {
				fighter.setFighterHP(0);
			} else {
				fighter.setFighterHP(newHealth);
			}
			System.out.println(fighter.getFighterName() + " took " + pDamage
					+ " damage!");
			System.out.println(fighter.getFighterName() + " has "
					+ fighter.getFighterHP() + " health left!");
			if (player.equals(fighter)) {
				TextView t = (TextView) findViewById(R.id.enemyHP);
				t.setText(fighter.getFighterHP());
			} else {
				TextView t = (TextView) findViewById(R.id.playerHP);
				t.setText(fighter.getFighterHP());
			}
		}
		this.evaluate();
	}

	public boolean equip(String itemType){
		boolean weaponFound = false;
		List<Item> inv = new CopyOnWriteArrayList<Item>(this.characterInventory);
		Iterator<Item> it = inv.iterator();
		while(it.hasNext()){
			Item item = it.next();
			if(item != null && !item.group.equals("Utility") && item.type.equals(itemType)){
				//get previously equipped weapon
				Item w = this.WeaponSlot;
				if(w != null){
					//put previously equipped weapon in array in place of newly equipped weapon
					inv.set(inv.indexOf(item), w);
				}
				//equip item
				this.WeaponSlot = (Weapon) item;
				this.characterInventory = inv;
				weaponFound = true;
			}
		}
		return weaponFound;
	}
	
	public void newMaxFighterHealth() {
		int hp = Dice.rollDice(healthDie, 1);
		this.fighterMaxHP += hp;
		System.out.println(this.fighterName + " leveled up! Max Health is now "
				+ this.fighterMaxHP + "!");
		this.fighterHP = this.fighterMaxHP;
		System.out.println(this.fighterName + " currently has " + this.fighterHP + " HP");
	}

	public void evaluate() {
		if (this.poisonCounter == 0) {
			this.poison = false;
		}
		if (poison == true) {
			this.fighterHP--;
			this.poisonCounter--;
			System.out.println(this.fighterName + " took poison damage!");
			System.out.println(this.fighterName + "'s health is now at "
					+ this.fighterHP);
		}
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
		dest.writeList(this.characterInventory);
	}
	public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
		public Fighter createFromParcel(Parcel in) {
			return new Fighter(in);
		}

		public Fighter[] newArray(int size) {
			return new Fighter[size];
		}
	};

	public Fighter(Parcel in){
		this.fighterName = in.readString();
		this.fighterLVL = in.readInt();
		this.fighterXP = in.readInt();
		//TODO: Need to find out how we're going to do level up XP. this.fighterLVLUP?
		this.fighterVIT = in.readInt();
		this.fighterHP = in.readInt();
		this.healthDie = in.readInt();
		this.fighterSTR = in.readInt();
		this.fighterMND = in.readInt();
		this.fighterMP = in.readInt();
		this.fighterAGI = in.readInt();
//		this.characterInventory = (Item[]) in.createTypedArray(Item.CREATOR);
		in.readList(this.characterInventory, Item.class.getClassLoader());
	}
}




