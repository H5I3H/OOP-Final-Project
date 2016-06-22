import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;
import java.util.Arrays;
import java.util.Queue;
import java.util.LinkedList;
import java.io.*;
public class Library {
	private ArrayList<UserData> userData;
	private ArrayList<Books> books;
	private ArrayList<DeptNum> depts;
	private Date currentDay;
	private PrintWriter outputStream;
	private Queue<Books> queue;
	private Queue<Books> borrowQueue;

	public Library() {
		currentDay = new Date(2010, 1, 1);
		userData = new ArrayList<UserData>(200);
		books = new ArrayList<Books>(1000);
		depts = new ArrayList<DeptNum>(185);
		queue = new LinkedList<Books>();
		borrowQueue = new LinkedList<Books>();
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
	 * @param input user input, which has this format [UID, "borrow", userBranch , bookSerialNum, date]
	 * @return response of the library system
	 */
	public String borrow(String[] input) {
		currentDay.setDate(input[4].split("/"));
		routine(); 
		int UIDIndex = 0;
		int bookIndex = 0;
		if((UIDIndex = getUserInfoByUID(input[0])) != -1) {
			UserData u = userData.get(UIDIndex);
			if(u.bRight == 1) {
				if(u.bQty == 10)
					return input[0]+"已屆數量達上限(10本)";
				//This will jump to CHECK_BOOK
			} else if(u.bRight == -1) {
				return "無借書權限";
			} else {
				if(currentDay.compare(new Date(userData.get(UIDIndex).bRightFrom.split("/"))) <= 0) {
					if(u.bQty == 10)
						return input[0]+"已屆數量達上限(10本)";
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
					//在這邊應該先更新這本書預約的使用者的資訊
					updateReserveInfo(b);
					if(b.rUsers == 0) { //沒有人預約所以可以借書
						int remain = 10 - (++u.bQty);
						b.status = 0;
						b.bBy = input[0];
						b.bDue = currentDay.nDaysAfter(30);
						borrowQueue.offer(b);
						outputStream.println(input[0]+","+1+","+input[2]+","+input[3]+","+currentDay.toString());
						return input[0]+"借書成功,歸還期限:"+currentDay.nDaysAfter(30) + ",仍可借數量:"+remain;
					} else { //有人預約則去檢查第一順位是不是這位使用者
						if(b.rUser1.equalsIgnoreCase(input[0])) { //預約者第一順位就是你
							int remain = 10 - (++u.bQty);
							b.status = 0;
							b.bBy = input[0];
							b.bDue = currentDay.nDaysAfter(30);
							b.rUsers--;
							borrowQueue.offer(b);
							outputStream.println(input[0]+","+1+","+input[2]+","+input[3]+","+currentDay.toString());
							return input[0]+"借書成功,歸還期限:"+currentDay.nDaysAfter(30) + ",仍可借數量:"+remain;
						} else {
							return "書已被其他人預約";
						}
					}
				}
			} else
				return "書不存在";
		} else 
			return input[0]+"不存在";
	}
	
	/**
	 * return book operation
	 * @param input user input, which has this format [UID, "return", userBranch , bookSerialNum, date]
	 * @return response of the library system
	 */
	public String returnn(String[] input){
		currentDay.setDate(input[4].split("/"));
		routine();
		int UIDIndex = 0;
		int bookIndex = 0;
		byte userBranch = Byte.parseByte(input[2]);
		if((UIDIndex = getUserInfoByUID(input[0])) != -1) {
			UserData u = userData.get(UIDIndex);
			if((bookIndex = getBookInfoBySerialNum(input[3])) != -1) {
				Books b = books.get(bookIndex);
				if (b.bBy.equals(input[0])) {
					int remain = 10 - (--u.bQty);
					if (userBranch != b.oriBranch) {
						b.changeBranch = b.oriBranch; //紀錄隔天這本書將會被移到哪一館
						b.returnDate = new Date(currentDay); //紀錄還書的日期
						queue.offer(b);
					}
					b.status= userBranch;			
					b.bBy="B00000000";
					b.bTimes++;
					u.bTimes++;
					b.renewTimes = 0;
					b.rDue = currentDay.nDaysAfter(5);
					if(currentDay.compare(new Date(b.bDue.split("/"))) >= 0){
						outputStream.println(input[0]+","+2+","+input[2]+","+input[3]+","+currentDay.toString());
						return input[0]+"還書成功,仍可借數量:"+remain;
					}
					else {
						Date compareday = new Date(b.bDue.split("/"));  //用來計算逾期幾天
						int i = 0;
						while(true){
							compareday.nextDay();
							i++;
							if (compareday.equals(currentDay)||i==30) break;
						}
						u.bRight = 0;
						u.bRightFrom=currentDay.nDaysAfter(2*i+1);
						outputStream.println(input[0]+","+2+","+input[2]+","+input[3]+","+currentDay.toString());
						return input[0]+"還書成功,暫停借書"+2*i+"天,仍可借數量:"+remain;
					}
				}else 
					return input[0]+"未借該書";
			}else
				return input[0]+"未借該書";
		}
		else return input[0]+"不存在";		
	}

	/**
	 * renew book operation
	 * @param input user input, which has this format [UID, "renew", bookSerialNum, date]
	 * @return response of the library system
	 */
	public String renew(String[] input){
		currentDay.setDate(input[3].split("/"));
		routine();
		int UIDIndex = 0;
		int bookIndex = 0;
		if((UIDIndex = getUserInfoByUID(input[0])) != -1) {
			UserData u = userData.get(UIDIndex);
			if(u.bRight == -1) {
				return input[0] + "無借書權限";
			} 
			else {
				if(currentDay.compare(new Date(userData.get(UIDIndex).bRightFrom.split("/"))) > 0) 
					return input[0] + "無借書權限";
			}
			//CHECK_BOOK
			bookIndex = getBookInfoBySerialNum(input[2]);
				Books b = books.get(bookIndex);
				if(b.bBy.equalsIgnoreCase(input[0])) {
					if (b.rUsers == 0){
						if (b.renewTimes != 2){
							int remain = 10 - u.rQty;
							b.status = 0;
							b.bDue = currentDay.nDaysAfter(30);
							b.renewTimes++;
							outputStream.println(input[0]+","+3+", ,"+input[2]+","+currentDay.toString());
							return input[0] + "續借成功, 歸還期限  :" + currentDay.nDaysAfter(30) + ",仍可借數量:" + remain;
						}
						else
							return input[0] + "已續借此書兩次";	
					}
					else
						return input[0] + "書已被其他人預約";					
				}
				else
					return input[0] + "未借此書";
		}
		else 
			return input[0] + "無借書權限";
					
					
	}
	
	/**
	 * reserve book operation
	 * @param input user input, which has this format [UID, "reserve" , bookSerialNum, date]
	 * @return response of the library system
	 */
	public String reserve(String[] input){
		currentDay.setDate(input[3].split("/"));
		routine();
		int UIDIndex = 0;
		int bookIndex = 0;
		if((UIDIndex = getUserInfoByUID(input[0])) != -1) {
			UserData u = userData.get(UIDIndex);
			if(currentDay.compare(new Date(userData.get(UIDIndex).rRightFrom.split("/"))) <= 0) {
				if(u.bQty == 5)
					return input[0]+"預約量已達上限(5本)";
				//This will jump to CHECK_BOOK
			} else
				return "無預約權限";
			//CHECK_BOOK
			if((bookIndex = getBookInfoBySerialNum(input[2])) != -1) {
				Books b = books.get(bookIndex);
				if(b.bBy.equals(input[0]))
					return input[0] + "已借相同書號的書";
				if(b.rUsers == 3) 
					return "該書已經被3位讀者預約";
				else {
					//終於可以預約了
					switch(b.rUsers){
					case 0:
						b.rUser1 = input[0];
						b.rDue = currentDay.nDaysAfter(5);
						break;
					case 1:
						b.rUser2 = input[0];
						break;
					case 2:
						b.rUser3 = input[0];
						break;
					default:
						break;
					}
					outputStream.println(input[0]+","+4+", ,"+input[2]+","+currentDay.toString());
					return input[0]+"預約成功,前面有" + b.rUsers++ +"人預約";
					}
			}
			else
				return "書不存在";
		} 
		else 
			return input[0]+"不存在";
	}

	
	/**
	 * Inquire informations
	 * @param input user input in this format ["inquire", command]
	 * @return response of library system
	 */
	public String inquire(String[] input){
		routine();
		if (input.length == 2){
			switch(input[1]){
			case "bookBorrowed" :
				int bookBorrowed = 0;
				for(int i = 0;i<books.size();i++){
					if (books.get(i).status == 0 )
						bookBorrowed++;
				}return "當前所有圖書館總共借出" + bookBorrowed + "本書。";
		
			case "peopleBorrowing" :
				int peopleBorrowing = 0;
				for(int i = 0;i < userData.size();i++){
					if(userData.get(i).bQty != 0 )
						peopleBorrowing++;
				}return "當前所有圖書館總借書人數為" + peopleBorrowing;
				
			case "userBorrowingBookRank" :
				String userBorrowingBookRank = "";
				byte booksBorrowing = 10;
				int rankCount = 0;
				int nameListNum = 0;
				int rank= 1;
				while(true){
					rankCount = 0;
					for(int i = 0;i<userData.size();i++){
						if (userData.get(i).bQty == booksBorrowing){
							nameListNum++;
							rankCount++;
							userBorrowingBookRank = userBorrowingBookRank + "第" + rank +  "名, ID:" + userData.get(i).UID + ", 當前借了" + booksBorrowing + "本書。\n";
						}
					}
					rank = rank + rankCount;
					booksBorrowing--;
					if (nameListNum >=10)
						break;
				}return userBorrowingBookRank ;
			
			case "userHasBorrowedBookRank" :
				String userHasBorrowedBookRank = "";
				nameListNum = 0;
				rank= 1;
				Integer userHasBorrowedBook[] = new Integer[userData.size()];
				for(int i = 0;i<userData.size();i++){
					userHasBorrowedBook[i] = userData.get(i).bTimes;
				}
				int booksBorrowed = (int) Collections.max(Arrays.asList(userHasBorrowedBook));
				while(true){
					rankCount = 0;
					for(int i = 0;i<userData.size();i++){
						if (userData.get(i).bTimes == booksBorrowed){
							nameListNum++;
							rankCount++;
							userHasBorrowedBookRank = userHasBorrowedBookRank + "第" + rank +  "名, ID:" + userData.get(i).UID + ", 已經借了" + booksBorrowed + "本書。\n";
						}
					}
					rank = rank + rankCount;
					booksBorrowed--;
					if (nameListNum >=10)
						break;
				}return userHasBorrowedBookRank ;
			
			case "userNoRightToBorrow":
				int userNoRightToBorrow = 0;
				for(int i = 0;i < userData.size();i++){
					if(userData.get(i).bRight != 1 )
						userNoRightToBorrow++;
				}return "當前停止借書權力人數共有" + userNoRightToBorrow + "人。";
			
			case "userNoRightToBorrowRank" :
				String userNoRightToBorrowRank = "";
				for (int i = 0 ; i<userData.size();i++){
					Date From = new Date(userData.get(i).bRightFrom.split("/"));
					if(userData.get(i).bRight == 0 )
						userNoRightToBorrowRank = userNoRightToBorrowRank + "第"+i+"位, " + userData.get(i).UID + ", "+ currentDay.difference(From) +"日後始可借書";
				}return userNoRightToBorrowRank;
			
			case "userNoRightToReserve":
				int userNoRightToReserve = 0;
				for(int i = 0;i < userData.size();i++){
					if(currentDay.compare(new Date(userData.get(i).bRightFrom.split("/"))) <= 0)
						userNoRightToReserve++;
				}return "當前停止預約權力人數共有" + userNoRightToReserve + "人。";
			
			case "userNoRightToReserveRank" :
				String userNoRightToReserveRank = "";
				for (int i = 0 ; i<userData.size();i++){
					Date From = new Date(userData.get(i).rRightFrom.split("/"));
					if(userData.get(i).bRight == 0 )
						userNoRightToReserveRank = userNoRightToReserveRank + "第"+i+"位, " + userData.get(i).UID + ", "+ currentDay.difference(From) +"日後始可預約";
				}return userNoRightToReserveRank;
			
			case "bookBorrowedTimesRank":
				String bookBorrowedTimesRank = "";
				int bookListNum = 0;
				rank= 1;
				Integer bookBorrowedTimes[] = new Integer[books.size()];
				for(int i = 0;i<books.size();i++){
					bookBorrowedTimes[i] = books.get(i).bTimes;
				}
				int BorrowedTimes = (int) Collections.max(Arrays.asList(bookBorrowedTimes));
				while(true){
					rankCount = 0;
					for(int i = 0;i<books.size();i++){
						if (books.get(i).bTimes == BorrowedTimes){
							bookListNum++;
							rankCount++;
							bookBorrowedTimesRank = bookBorrowedTimesRank + "第" + rank +  "名, 原館:" + books.get(i).oriBranch + ", 累計借出次數:" + BorrowedTimes + "次。\n";
						}
					}
					rank = rank + rankCount;
					BorrowedTimes--;
					if (bookListNum >=10)
						break;
				}return bookBorrowedTimesRank ;
			case "libraryBookBorrowedTimesRank":
				String libraryBookBorrowedTimesRank = "";
				int Q = 0;
				int temp[] = new int[6] ;
				int libraryBookBorrowedTimes[] = {0,0,0,0,0,0};
				for(int i = 0;i<books.size();i++){
					switch (books.get(i).oriBranch){
					case 0:
			
					case 1:
						libraryBookBorrowedTimes[1]++;
					
					case 2:
						libraryBookBorrowedTimes[2]++;
						
					case 3:
						libraryBookBorrowedTimes[3]++;
						
					case 4:
						libraryBookBorrowedTimes[4]++;
						
					case 5:
						libraryBookBorrowedTimes[5]++;
					}
				}
				for(int i = 1 ; i <= 5 ; i++){
					temp[i] = libraryBookBorrowedTimes[i];
				}
				Arrays.sort(temp);
				Q = temp[5];
				rank = 1;
				while(true){
					rankCount = 0;
					for(int i = 1 ; i <= 5 ; i++){
						if (libraryBookBorrowedTimes[i] == Q){
							rankCount++;
							libraryBookBorrowedTimesRank = libraryBookBorrowedTimesRank + "第" + rank +  "名, 圖書館名:" + i + ", 累計借出次數:" + libraryBookBorrowedTimes[i] + "次。\n";
						}
					}
					Q--;
					rank = rank + rankCount;
					if(Q == 0)
						break;
				}return libraryBookBorrowedTimesRank;
			default : return "input error";		
			}
		}
		else if(input[2].equals("userBorrowHistory")){
			//TODO "inquire", "userBorrowHistory", UID
			return "";
		}
		else{
			int bookIndex = 0;
			bookIndex = getBookInfoBySerialNum(input[2]);
				Books b = books.get(bookIndex);
			if (b.status == 0)
				return "該書籍已被借走。";
			else if (b.status == b.oriBranch)
				return "該書在原館內";
			else
				return "該書在第" + b.status + "分館內。";
	}
}
		
	
	/**
	 * Register a new user to the library system
	 * @param input user input in this format [UID, "addUser", date]
	 * @return response of library system
	 */
	public String AddUser(String[] input) {
		currentDay.setDate(input[2].split("/"));
		routine();
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
		routine();
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
	/**
	 * 每個query的最一開始在設完今天的日期以後，應該都要立即invoke此method
	 * 它做的事就是處理異館還書後書本在各館間移動的問題，以及使用者逾期未還書卻又想借書的權限問題
	 */
	private void routine() {
		//處理異館還書
		while(!queue.isEmpty()) {
			Books temp = queue.peek();
			if(currentDay.compare(temp.returnDate) != 0) {
				temp.status = temp.changeBranch;
				queue.poll();
			} else {
				break;
			}
		}
		while(!borrowQueue.isEmpty()) {
			Books temp = borrowQueue.peek();
			if(temp.status == 0) { //書處於借出的狀態
				if(currentDay.compare(new Date(temp.bDue.split("/"))) < 0) { //已逾期還書
					int uIndex = getUserInfoByUID(temp.bBy);
					userData.get(uIndex).bRight = -1; //把該user借書權限拿掉
					borrowQueue.poll();
				} else //queue最前面的書還沒逾期，代表queue裡面剩下所有的書都一定還沒逾期，所以直接break
					break;
			} else
				borrowQueue.poll();
		}
	}
	private void updateReserveInfo(Books b) {
		switch(b.rUsers) {
		case 0:
			return;
		case 1:
			if(currentDay.compare(new Date(b.rDue.split("/"))) < 0) { //第一位預約者已超過取書時間
				/* 取消第一位預約者的預約權限90天 */
				int uIndex = getUserInfoByUID(b.rUser1);
				UserData u = userData.get(uIndex);
				u.rRightFrom = new Date(b.rDue.split("/")).nDaysAfter(91);
				u.rQty--;
				b.rUsers--;
			}
			break;
		case 2:
			for(int i = 0; i < 2; i++) {
				if(currentDay.compare(new Date(b.rDue.split("/"))) < 0) { //第一位預約者已超過取書時間
					/* 取消第一位預約者的預約權限90天 */
					int uIndex = getUserInfoByUID(b.rUser1);
					UserData u = userData.get(uIndex);
					u.rRightFrom = new Date(b.rDue.split("/")).nDaysAfter(91);
					u.rQty--;
					b.rUsers--;
					if(i == 0) {
						b.rUser1 = b.rUser2;
						b.rDue = b.returnDate.nDaysAfter(10);
					}
				}
			}
			break;
		case 3:
			for(int i = 0; i < 3; i++) {
				if(currentDay.compare(new Date(b.rDue.split("/"))) < 0) { //第一位預約者已超過取書時間
					/* 取消第一位預約者的預約權限90天 */
					int uIndex = getUserInfoByUID(b.rUser1);
					UserData u = userData.get(uIndex);
					u.rRightFrom = new Date(b.rDue.split("/")).nDaysAfter(91);
					u.rQty--;
					b.rUsers--;
					if(i == 0) {
						b.rUser1 = b.rUser2;
						b.rUser2 = b.rUser3;
						b.rDue = b.returnDate.nDaysAfter(10);
					} else if(i == 1) {
						b.rUser1 = b.rUser2;
						b.rDue = b.returnDate.nDaysAfter(15);
					}
				}
			}
			break;
		default:
			break;
		}
	}
}