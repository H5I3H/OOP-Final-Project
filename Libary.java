import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;
import java.io.*;
public class Libary {
	private ArrayList<UserData> userData;
	private ArrayList<Books> books;
	private ArrayList<DeptNum> depts;

	public Libary() {
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
		} catch (FileNotFoundException e) {
			System.out.println("File not found");
			System.exit(1);
		}
		Collections.sort(userData);
		Collections.sort(books);
		Collections.sort(depts);
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

	public static void main(String[] args) {
		Libary l = new Libary();
	}
}
