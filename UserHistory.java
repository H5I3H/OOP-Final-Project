
public class UserHistory implements Comparable<UserHistory> {
	byte oriBranch;
	byte borrowBranch;
	byte returnBranch;
	Date borrowDay;
	Date returnDay;
	String bookNum;
	public UserHistory() {
		oriBranch = 0;
		borrowBranch = 0;
		returnBranch = 0;
		borrowDay = new Date(2010, 1, 1);
		returnDay = new Date(2010, 1, 1);
		bookNum = "";
	}
	public UserHistory(byte o, byte b, byte r, Date bD, Date rD, String num) {
		oriBranch = o;
		borrowBranch = b;
		returnBranch = r;
		borrowDay = new Date(bD);
		returnDay = new Date(rD);
		bookNum = num;
	}
	public int compareTo(UserHistory h) {
		if(borrowDay.compare(h.borrowDay) > 0) 
			return 1;
		else if(borrowDay.compare(h.borrowDay) < 0)
			return -1;
		else return 0;
	}
	public String toString() {
		return oriBranch+", "+bookNum+", "+borrowBranch+", "+borrowDay.toString()+", "+returnBranch+", "+returnDay.toString();
	}
}
