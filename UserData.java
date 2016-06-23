import java.util.ArrayList;

public class UserData implements Comparable<UserData> {
	String UID;
	byte bRight;
	String bRightFrom;
	byte bQty;
	int bTimes;
	String rRightFrom;
	byte rQty;
	ArrayList<UserHistory> history;

	public UserData(String[] in) {
		UID = in[0];
		bRight = Byte.parseByte(in[1]);
		bRightFrom = in[2];
		bQty = Byte.parseByte(in[3]);
		bTimes = Integer.parseInt(in[4]);
		rRightFrom = in[5];
		rQty = Byte.parseByte(in[6]);
		history = new ArrayList<UserHistory>(10);
	}
	public String toString() {
		return UID+" "+bRight+" "+bRightFrom+" "+bQty+" "+bTimes+" "+rRightFrom+" "+rQty;
	}	
	public int compareTo(UserData t) {
		return UID.compareToIgnoreCase(t.UID);
	}
}