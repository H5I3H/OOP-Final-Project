
public class DeptNum implements Comparable<DeptNum> {
	String deptNum;
	String deptName;
	
	public DeptNum(String[] in) {
		deptNum = in[0];
		deptName = in[1];
	}
	public String toString() {
		return deptNum+" "+deptName;
	}
	@Override
	public int compareTo(DeptNum o) {
		return deptNum.compareToIgnoreCase(o.deptNum);
	}

}
