package equipment;

public class Armor extends Item {
	
	public int defense = 0;
	public int vitality = 0;
	public int agility = 0;
	public int MaxHP = 0;
	public int MaxMP = 0;
	public int pDefenseBonus = 0;
	public int mDefenseBonus = 0;
	
	public Armor(String group, String type) {
		super(group, type, 1);
	}

}
