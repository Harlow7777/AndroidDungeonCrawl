package battle;

import java.util.HashMap;
import battle.Fighter;

public class PlayerStats {
	
	/*
	 *	baseSTR = player.characterSTR(); 
	 * 	baseVIT = player.characterVIT();
	 * 	baseAGI = player.characterAGI();
	 * 	baseMND = player.characterMND();
	 * 
	 * Status calculation per level =
	 * 
	 * STR = player.level *((baseSTR/99));
	 * VIT = player.level *((baseVIT/99));
	 * AGI = player.level *((baseAGI/99));
	 * MND = player.level *((baseAGI/99));
	 * 
	 * HPLvl_1 = player.healthDie(max)+ VIT;
	 * MPLvl_1 = (player.healthDie(max)+ MND)*2;
	 * 
	 * HPperLevel = ((VIT/10)+(STR/10))+ player.healthDie;
	 * MPperLevel = ((VIT/10)+(MND/10))+ player.healthDie;
	 * 
	 * PAttack = ((this.getFighterSTR()/2) + ((this.getFighterLVL()/2) + (this.getFighterAGI()/10)));
	 * PDefense = ((fighter.getFighterAGI()/10)+ (fighter.getFighterSTR()/5) + (fighter.getFighterVIT()/10));
	 * 
	 * 
	 */
	
	public static void initializePlayer(Fighter fighter){
		fighter.setFighterHP(fighter.healthDie + fighter.getFighterVIT());
		fighter.setFighterMP((fighter.healthDie + fighter.getFighterMND()) * 2);
	}
	
	public static void levelUp(Fighter fighter){
//		int baseSTR = fighter.getBaseSTR();
//		fighter.setFighterSTR(fighter.getFighterLVL() * ((baseSTR)/99));
//		int baseVIT = fighter.getBaseVIT();
//		fighter.setFighterVIT(fighter.getFighterLVL() * ((baseVIT)/99));
//		int baseAGI = fighter.getBaseAGI();
//		fighter.setFighterAGI(fighter.getFighterLVL() * ((baseAGI)/99));
//		int baseMND = fighter.getBaseMND();
//		fighter.setFighterSTR(fighter.getFighterLVL() * ((baseMND)/99));
		
		fighter.setFighterHP((fighter.getFighterVIT()/10) + (fighter.getFighterSTR()/10) + fighter.WeaponSlot.dmgDie); 
		fighter.setFighterMP((fighter.getFighterVIT()/10) + (fighter.getFighterMND()/10) + fighter.WeaponSlot.dmgDie);
	}
}
