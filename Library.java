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
	/**
	 * Before shutdown the program, we must close the file
	 * to make sure data is actually written in the disk.
	 */
	public void exit() {
		outputStream.close();
	}
	/**
	 * Today
	 * @return date of today, its a string in this format "yyyy/mm/dd"
	 */
	public String today() {
		return currentDay.toString();
	}

	/**
	 * Borrow book operation
	 * @param input user input, which has this format [UID, "borrow", bookSerialNum, date]
	 * @return response of the library system
	 */
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
	/**
	 * Register a new user to the library system
	 * @param input user input in this format [UID, "addUser", date]
	 * @return response of library system
	 */
	public String AddUser(String[] input) {
		currentDay.setDate(input[2].split("/"));
		if(input[0].length() != 9)
			return input[0]+"學號不符合規則";
		if(getUserInfoByUID(input[0]) != -1)
			return input[0]+"已存在";
		else {
			String first = input[0].substring(0, 1);
			String second = input[0].substring(1, 3);
			String third = input[0].substring(3, 6);
			String forth = input[0].substring(6, 9);
			if(!first.matches("^[BRDT]$"))
				return input[0]+"學號不符合規則";
			try {
				int t = Integer.parseInt(second);
				if(t >= 5 && t <= 33)
					return input[0]+"學號不符合規則";

				if(getDeptInfoByDeptNum(third) == -1)
					return input[0]+"學號不符合規則";

				t = Integer.parseInt(forth);
				if(t >= 300)
					return input[0]+"學號不符合規則";
			} catch(NumberFormatException e) {
				return input[0]+"學號不符合規則";
			}

			if(first.equals("B") || first.equals("T")) {
				if(third.charAt(1) != '0' && third.charAt(1) != '1')
					return input[0]+"學號不符合規則";
			}

			if(first.equals("R") || first.equals("D")) {
				if(third.charAt(1) != '2' && third.charAt(1) != '3')
					return input[0]+"學號不符合規則";
			}
			String[] in = new String[7];
			in[0] = input[0];
			in[1] = "1";
			in[2] = "1900/1/1";
			in[3] = "0";
			in[4] = "0";
			in[5] = "1900/1/1";
			in[6] = "0";
			userData.add(new UserData(in));
			Collections.sort(userData);
			outputStream.println(input[0]+","+0+", , ,"+currentDay.toString());
			return input[0]+"註冊成功";
		}
	}
	
	/**
	 * Delete a user from library system
	 * @param input user input in this format [UID, "deleteUser", date]
	 * @return response of library system
	 */
	public String deleteUser(String[] input) {
		currentDay.setDate(input[2].split("/"));
		int userIndex = 0;
		if((userIndex = getUserInfoByUID(input[0])) != -1) {
			if(userData.get(userIndex).bQty != 0)
				return input[0]+"尚有書未還";
			else {
				userData.remove(userIndex);
				Collections.sort(userData);
				outputStream.println(input[0]+","+(-1)+", , ,"+currentDay.toString());
				return input[0]+"註銷成功";
			}
		} else
			return input[0]+"不存在";
	}
	private int getUserInfoByUID(String UID) {
		//Do binary search
		int start = 0;
		int middle = 0;
		int end = userData.size() - 1;
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
		int end = books.size() - 1;
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
		int end = depts.size() - 1;
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