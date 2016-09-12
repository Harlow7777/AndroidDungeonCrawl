package equipment;

import android.os.Parcel;
import android.os.Parcelable;

public class Blade extends Weapon implements Parcelable{
	
	public Blade(String type){
		super("blade", type);
		init();
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeValue(this.type);
	}
	public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
		public Blade createFromParcel(Parcel in) {
			return new Blade(in);
		}

		public Blade[] newArray(int size) {
			return new Blade[size];
		}
	};

	public Blade(Parcel in){
		super("blade", in.readString());
	}

	public void init(){
		switch(String.valueOf(this.type)){
		case "longsword": 
			this.dmgDie = 8;
			break;
		case "shortsword":
			this.dmgDie = 6;
			break;
		case "broadsword":
			this.dmgDie = 8;
			break;
		case "rapier":
			this.dmgDie = 10;
			break;
		case "axe":
			this.dmgDie = 10;
			break;
		case "sickle":
			this.dmgDie = 4;
			break;
		case "kukri":
			this.dmgDie = 4;
			break;
		case "ranseur":
			this.dmgDie = 6;
			break;
		case "sai":
			this.dmgDie = 4;
			break;
		case "dagger":
			this.dmgDie = 6;
			break;
		case "katar":
			this.dmgDie = 8;
			break;
		case "battleaxe":
			this.dmgDie = 8;
			break;
		case "scimitar":
			this.dmgDie = 8;
			break;
		case "waraxe":
			this.dmgDie = 12;
			break;
		case "falchion":
			this.dmgDie = 8;
			break;
		case "scythe":
			this.dmgDie = 8;
			break;
		case "bastardsword":
			this.dmgDie =10;
			break;
		case "urgrosh":
			this.dmgDie =10;
			break;
		case "halberd":
			this.dmgDie = 6;
			break;
		case "giserne":
			this.dmgDie = 8;
			break;
		case "greatsword":
			this.dmgDie = 10;
			break;
		case "greataxe":
			this.dmgDie = 10;
			break;
		case "glaive":
			this.dmgDie = 8;
			break;
		case "zweihander":
			this.dmgDie = 12;
			//this.description = "make them eat dirt"
			break;
		}
	}
}
