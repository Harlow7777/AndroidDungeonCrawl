package equipment;

import android.os.Parcel;
import android.os.Parcelable;
import android.widget.TextView;

import battle.Fighter;
import battle.R;

public class Utility extends Item{

	public Utility(String type, int amount){
		super("Utility", type, amount);
	}

	public Utility(Parcel in){
		super("Utility", in.readString(), in.readInt());
	}

	public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
		public Utility createFromParcel(Parcel in) {
			return new Utility(in);
		}

		public Utility[] newArray(int size) {
			return new Utility[size];
		}
	};

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(this.type);
		dest.writeInt(this.amount);
	}


		
}
