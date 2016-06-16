import java.util.ArrayList;
import java.io.*;
class Library {
	private ArrayList<String> userData;
	private ArrayList<String> books;
	private ArrayList<String> deptNum;
	/**
	 * Create the database of libary
	 */
	public Library() {
		userData = new ArrayList<String>(200);
		books = new ArrayList<String>(200);
		deptNum = new ArrayList<String>(200);
		String line = null;
		FileInputStream fin = null;
		BufferedReader reader = null;
		try {
			fin = new FileInputStream("DataSet/UID.csv");
			reader = new BufferedReader(new InputStreamReader(fin, "Unicode"));
			reader.readLine();
			line = reader.readLine();
			while(line != null) {
				userData.add(line);
				line = reader.readLine();
			}
			reader.close();

			fin = new FileInputStream("DataSet/booksAndBranches.csv");
			reader = new BufferedReader(new InputStreamReader(fin, "Unicode"));
			reader.readLine();
			line = reader.readLine();
			while(line != null) {
				books.add(line);
				line = reader.readLine();
			}
			reader.close();

			fin = new FileInputStream("DataSet/deptNum.csv");
			reader = new BufferedReader(new InputStreamReader(fin, "Unicode"));
			reader.readLine();
			line = reader.readLine();
			while(line != null) {
				deptNum.add(line);
				line = reader.readLine();
			}
			reader.close();
		} catch (FileNotFoundException e) {
			System.out.println("File not found");
			System.exit(1);
		} catch (IOException e) {
			System.out.println("Something went wrong when reading file");
			System.exit(1);
		}
	}

	/**
	 * Just use to check whether UserData is read correctly
	 */
	public void showUserData() {
		for(int i = 0; i < userData.size(); i++)
			System.out.println(userData.get(i));
		System.out.println(userData.size());
	}

	/**
	 * Just use to check whether UserData is read correctly
	 */
	public void showBooks() {
		for(int i = 0; i < books.size(); i++)
			System.out.println(books.get(i));
		System.out.println(books.size());
	}

	/**
	 * Just use to check whether UserData is read correctly
	 */
	public void showDeptNum() {
		for(int i = 0; i < deptNum.size(); i++)
			System.out.println(deptNum.get(i));
		System.out.println(deptNum.size());
	}

	public static void main(String[] args) {
		Library l = new Library();
		l.showBooks();
	}
}