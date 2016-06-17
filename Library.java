import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;
import java.io.*;
public class Library {
	private ArrayList<UserData> userData;
	private ArrayList<Books> books;
	private ArrayList<DeptNum> depts;
	private Date currentDay;
	private PrintWriter outputStream;

	public Library() {
		currentDay = new Date(2010, 1, 1);
		userData = new ArrayList<UserData>(200);
		books = new ArrayList<Books>(1000);
		depts = new ArrayList<DeptNum>(185);
		String line = null;
		Scanner inputStream;
		try {
			inputStream = new Scanner(new FileInputStream("/Users/True5402/Desktop/JAVA/OOP-Final-Project/DataSet/UID.csv"), "Big5");
			inputStream.nextLine();
			while(inputStream.hasNextLine()) {
				line = inputStream.nextLine();
				userData.add(new UserData(line.split(",")));
			}
			inputStream.close();
			
			inputStream = new Scanner(new FileInputStream("/Users/True5402/Desktop/JAVA/OOP-Final-Project/DataSet/booksAndBranches.csv"), "Unicode");
			inputStream.nextLine();
			while(inputStream.hasNextLine()) {
				line = inputStream.nextLine();
				books.add(new Books(line.split(",")));
			}
			inputStream.close();
			
			inputStream = new Scanner(new FileInputStream("/Users/True5402/Desktop/JAVA/OOP-Final-Project/DataSet/deptNum.csv"), "Big5");
			inputStream.nextLine();
			while(inputStream.hasNextLine()) {
				line = inputStream.nextLine();
				depts.add(new DeptNum(line.split(",")));
			}
			inputStream.close();
			
			outputStream = new PrintWriter(new FileOutputStream("/Users/True5402/Desktop/JAVA/OOP-Final-Project/DataSet/historyLog.csv", true));
		} catch (FileNotFoundException e) {
			System.out.println("File not found");
			System.exit(1);
		}
		Collections.sort(userData);
		Collections.sort(books);
		Collections.sort(depts);
	}
	public void exit() {
		outputStream.close();
	}
	public String today() {
		return currentDay.toString();
	}

	public String borrow(String[] input) {
		currentDay.setDate(input[4].split("/"));
		int UIDIndex = 0;
		int bookIndex = 0;
		if((UIDIndex = getUserInfoByUID(input[0])) != -1) {
			UserData u = userData.get(UIDIndex);
			if(u.bRight == 1) {
				if(u.bQty == 10)
					return input[0]+"已借數量達上限(10本)";
				//This will jump to CHECK_BOOK
			} else if(u.bRight == -1) {
				return "無借書權限";
			} else {
				if(currentDay.compare(new Date(userData.get(UIDIndex).bRightFrom.split("/"))) <= 0) {
					if(u.bQty == 10)
						return input[0]+"已借數量達上限(10本)";
					//This will jump to CHECK_BOOK
				} else
					return "無借書權限";
			}
			//CHECK_BOOK
			if((bookIndex = getBookInfoBySerialNum(input[3])) != -1) {
				Books b = books.get(bookIndex);
				if(b.status == 0) {
					if(b.bBy.equalsIgnoreCase(input[0]))
						return input[0]+"已借同書號的書";
					return "書已被借走";
				}
				else if(b.status != Byte.parseByte(input[2]))
					return "書不在第"+input[2]+"分館,它在第"+books.get(bookIndex).status+"分館";
				else {
					if(b.rUsers == 3)
						return "書已被其他人預約";
					else {
						//終於可以借書了
						int remain = 10 - (++u.bQty);
						b.status = 0;
						b.bBy = input[0];
						b.bDue = currentDay.nDaysAfter(30);
						outputStream.println(input[0]+","+1+","+input[2]+","+input[3]+","+currentDay.toString());
						return input[0]+"借書成功,歸還期限:"+currentDay.nDaysAfter(30)+
								",仍可借數量:"+remain;
					}
				}
			} else
				return "書不存在";
		} else 
			return input[0]+"不存在";
	}
	
	private int getUserInfoByUID(String UID) {
		//Do binary search
		int start = 0;
		int middle = 0;
		int end = userData.size();
		while(end >= start) {
			middle = (start + end) / 2;
			if(userData.get(middle).UID.compareToIgnoreCase(UID) > 0)
				end = middle - 1;
			else if(userData.get(middle).UID.compareToIgnoreCase(UID) < 0)
				start = middle + 1;
			else
				return middle;
		}
		return -1;
	}
	
	private int getBookInfoBySerialNum(String serialNum) {
		//Do binary search
		int start = 0;
		int middle = 0;
		int end = books.size();
		while(end >= start) {
			middle = (start + end) / 2;
			if(books.get(middle).serialNum.compareToIgnoreCase(serialNum) > 0)
				end = middle - 1;
			else if(books.get(middle).serialNum.compareToIgnoreCase(serialNum) < 0)
				start = middle + 1;
			else
				return middle;
		}
		return -1;
	}
	
	private int getDeptInfoByDeptNum(String num) {
		//Do binary search
		int start = 0;
		int middle = 0;
		int end = depts.size();
		while(end >= start) {
			middle = (start + end) / 2;
			if(depts.get(middle).deptNum.compareToIgnoreCase(num) > 0)
				end = middle - 1;
			else if(depts.get(middle).deptNum.compareToIgnoreCase(num) < 0)
				start = middle + 1;
			else
				return middle;
		}
		return -1;
	}
}