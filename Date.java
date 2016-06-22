
public class Date {
	private int year;
	private int month;
	private int day;
	public Date() {
		year = 2010;
		month = 1;
		day = 1;
	}
	public Date(int y, int m, int d) {
		year = y;
		month = m;
		day = d;
	}
	public Date(String y, String m, String d) {
		year = Integer.parseInt(y);
		month = Integer.parseInt(m);
		day = Integer.parseInt(d);
	}
	public Date(String[] date) {
		year = Integer.parseInt(date[0]);
		month = Integer.parseInt(date[1]);
		day = Integer.parseInt(date[2]);
	}
	public Date(Date d){
		year = d.year;
		month = d.month;
		day = d.day;
	}
	/**
	 * 日期加一天
	 */
	public void nextDay() {
		switch (month) {
		case 2:
			if(isLeapYear()) {
				if (++day == 30) {
					day = 1;
					month++;
				}
			} else {
				if (++day == 29) {
					day = 1;
					month++;
				}
			}
			break;
		case 1:
		case 3:
		case 5:
		case 7:
		case 8:
		case 10:
		case 12:
			if (++day == 32) {
				day = 1;
				if (++month == 13) {
					month = 1;
					year++;
				}
			}
			break;
		case 4:
		case 6:
		case 9:
		case 11:
			if (++day == 31) {
				day = 1;
				month++;
			}
			break;
		}
	}
	/**
	 * 從今天開始算n天以後的日期
	 * @param n 幾天後
	 * @return 日期
	 */
	public String nDaysAfter(int n) {
		Date t = new Date(this.year, this.month, this.day);
		for(int i = 0; i < n; i++)
			t.nextDay();
		return t.toString();
	}
	public void setDate(String[] d) {
		year = Integer.parseInt(d[0]);
		month = Integer.parseInt(d[1]);
		day = Integer.parseInt(d[2]);
	}
	public boolean equals(Date d) {
		return (year == d.year) && (month == d.month) && (day == d.day);
	}
	/**
	 * 檢查呼叫這個方法的物件日期是不是比被比較的日較早，像是 1993/12/2 比 1993/12/24 早
	 * @param d 用來跟呼叫這個方法的物件做比較的物件
	 * @return 回傳1則呼叫此方法的物件比較早，回傳0則相同，回傳-1則呼叫此方法的物件比較晚
	 */
	public int compare(Date d) {
		if(year < d.year)
			return 1;
		else if(year > d.year)
			return -1;
		else {
			if(month < d.month)
				return 1; 
			else if(month > d.month)
				return -1;
			else {
				if(day < d.day)
					return 1;
				else if(day > d.day)
					return -1;
				else 
					return 0;
			}
		}
	}
	public int difference(Date d) {
		int count = 0;
		Date temp = new Date(d);
		Date temp2 = new Date(this);
		if(compare(d) > 0) {
			while(!temp2.equals(temp)) {
				temp2.nextDay();
				count++;
			}
		} else if(compare(d) < 0) {
			while(!temp.equals(temp2)) {
				temp.nextDay();
				count++;
			}
		} else
			count = 0;
		return count;
	}
	public String toString() {
		return year + "/" + month + "/" + day;
	}
	/**
	 * 檢查是不是閏年
	 * @return 今年是閏年則回傳true，否則回傳false
	 */
	private boolean isLeapYear() {
		if (((year % 4 == 0) && (year % 100 != 0)) || (year % 400 == 0)) {
			return true;
		} else {
			return false;
		}
	}
}
