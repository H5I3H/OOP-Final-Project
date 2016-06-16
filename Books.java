
public class Books implements Comparable<Books> {
	String oriBranch;
	String title;
	String serialNum;
	int bTimes;
	byte status;
	String bBy;
	String bDue;
	byte renewTimes;
	byte rUsers;
	String rUser1;
	String rUser2;
	String rUser3;
	String rDue;
	byte transferTo1;
	byte transferTo2;
	byte transferTo3;
	public Books(String[] in) {
		oriBranch = in[0];
		title = in[1];
		serialNum = in[2];
		bTimes = Integer.parseInt(in[3]);
		status = Byte.parseByte(in[4]);
		bBy = in[5];
		bDue = in[6];
		renewTimes = Byte.parseByte(in[7]);
		rUsers = Byte.parseByte(in[8]);
		rUser1 = in[9];
		rUser2 = in[10];
		rUser3 = in[11];
		rDue = in[12];
		transferTo1 = Byte.parseByte(in[13]);
		transferTo2 = Byte.parseByte(in[14]);
		transferTo3 = Byte.parseByte(in[15]);	
	}
	public String toString() {
		return oriBranch+" "+title+" "+serialNum+" "+bTimes+" "+status+
				" "+bBy+" "+bDue+" "+renewTimes+" "+rUsers+" "+rUser1+
				" "+rUser2+" "+rUser3+" "+rDue+" "+transferTo1+" "+transferTo2+" "+transferTo3;
	}
	@Override
	public int compareTo(Books o) {
		return serialNum.compareToIgnoreCase(o.serialNum);
	}

}
