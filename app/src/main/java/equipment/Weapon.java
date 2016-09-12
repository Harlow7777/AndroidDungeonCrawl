package equipment;

import android.os.Parcel;
import android.os.Parcelable;

public class Weapon extends Item implements Parcelable{
	
	public int timesRolled = 0;
	public int dmgDie = 0;
	
	public Weapon(String group, String type){
		super(group, type, 1);
	}
	
	public int getDmgDie(){
		return this.dmgDie;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(this.group);
		dest.writeValue(this.type);
		dest.writeInt(this.amount);
	}
	public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
		public Weapon createFromParcel(Parcel in) {
			return new Weapon(in);
		}

		public Weapon[] newArray(int size) {
			return new Weapon[size];
		}
	};

	public Weapon(Parcel in){
		super(in.readString(), in.readString(), in.readInt());
	}
}
