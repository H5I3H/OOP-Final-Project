import java.util.ArrayList;
import java.util.Scanner;
import java.io.*;
class Library {
	private ArrayList<String[]> userData;
	private ArrayList<String[]> books;
	private ArrayList<String[]> deptNum;
	/**
	 * Create the database of libary
	 */
	public Library() {
		userData = new ArrayList<String[]>(200);
		books = new ArrayList<String[]>(200);
		deptNum = new ArrayList<String[]>(200);
		String line = null;
		Scanner inputStream = null;
		try {
			inputStream = new Scanner(new FileInputStream("DataSet/UID.csv"), "Big5");
			line = inputStream.nextLine();
			while(inputStream.hasNextLine()) {
				line = inputStream.nextLine();
				userData.add(line.split(","));
			}
			inputStream.close();

			inputStream = new Scanner(new FileInputStream("DataSet/booksAndBranches.csv"), "Unicode");
			inputStream.nextLine();
			while(inputStream.hasNextLine()) {
				line = inputStream.nextLine();
				books.add(line.split(","));
			}
			inputStream.close();

			inputStream = new Scanner(new FileInputStream("DataSet/deptNum.csv"), "Big5");
			inputStream.nextLine();
			while(inputStream.hasNextLine()) {
				line = inputStream.nextLine();
				deptNum.add(line.split(","));
			}
			inputStream.close();
		} catch (FileNotFoundException e) {
			System.out.println("File not found");
			System.exit(1);
		}
	}

	/**
	 * Just use to check whether UserData is read correctly
	 */
	public void showUserData() {
		for(int i = 0; i < userData.size(); i++) {
			for(String e : userData.get(i))
				System.out.print(e + " ");
			System.out.println("");
		}
	}

	/**
	 * Just use to check whether UserData is read correctly
	 */
	public void showBooks() {
		for(int i = 0; i < books.size(); i++) {
			for(String e : books.get(i))
				System.out.print(e + " ");
			System.out.println("");
		}
	}

	/**
	 * Just use to check whether UserData is read correctly
	 */
	public void showDeptNum() {
		for(int i = 0; i < deptNum.size(); i++) {
			for(String e : deptNum.get(i))
				System.out.print(e + " ");
			System.out.println("");
		}
	}

	public static void main(String[] args) {
		Library l = new Library();
		//l.showBooks();
		//l.showDeptNum();
		//l.showUserData();
	}
}