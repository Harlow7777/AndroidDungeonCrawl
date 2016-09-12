package equipment;

import android.os.Parcel;
import android.os.Parcelable;

public class Item extends Inventory implements Parcelable{
	
	public String group = "";
	public String type = "";
	public int amount = 0;

	public Item(String group, String type, int amount){
		this.group = group;
		this.type = type;
		this.amount = amount;
	}

	public int getAmount() {
		return this.amount;
	}

	public void setAmount(int newAmount){
		this.amount = newAmount;
	}
	
	public String getGroup(){
		return this.group;
	}
	
	public String getType(){
		return this.type;
	}

	public void setType(String newType){
		this.type = newType;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(this.group);
		dest.writeString(this.type);
		dest.writeInt(this.amount);
	}
	public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
		public Item createFromParcel(Parcel in) {
			return new Item(in);
		}

		public Item[] newArray(int size) {
			return new Item[size];
		}
	};

	public Item(Parcel in){
		this.group = in.readString();
		this.type = in.readString();
		this.amount = in.readInt();
	}
}
