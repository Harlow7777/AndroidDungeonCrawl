package equipment;

import android.os.Parcel;
import android.os.Parcelable;

public class Club extends Weapon implements Parcelable{
	
	public Club(String type){
		super("club", type);
		init();
	}
	
	public void init(){
		switch(String.valueOf(this.type)){
			case "blunt":
				this.dmgDie = 6;
				break;
			case "mace":
				this.dmgDie = 8;
				break;
		}
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
		public Club createFromParcel(Parcel in) {
			return new Club(in);
		}

		public Club[] newArray(int size) {
			return new Club[size];
		}
	};

	public Club(Parcel in){
		super("club", in.readString());
	}
}
