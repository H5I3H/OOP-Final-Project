import java.util.Scanner;
public class LibraryDemo {
	public static void main(String[] args) {
		Scanner keyboard = new Scanner(System.in);
		String userInput = null;
		
		System.out.println("Loading data into library...");
		Library l = new Library();
		System.out.println("Done");
		while(true){
			System.out.println("Today is " + l.today());
			System.out.print("Input your command: ");
			userInput = keyboard.nextLine();
			switch(parseInput(userInput)) {
			case "borrow":
				//TODO
				System.out.println(l.borrow(userInput.split(",")));
				System.out.println();
				break;
			case "return":
				//TODO 寫在Library.java然後在這邊呼叫它
				//像是 l.return(userInput) 之類的。
				//要傳什麼參數跟方法要叫什麼名字都看個人
				System.out.println(l.returnn(userInput.split(",")));
				System.out.println();
				break;
			case "renew":
				//TODO 同上
				System.out.println(l.renew(userInput.split(",")));
				System.out.println();
				break;
			case "reserve":
				//TODO 同上
				System.out.println(l.reserve(userInput.split(",")));
				System.out.println();
				break;
			case "inquire":
				//TODO 同上
				System.out.println(l.inquire(userInput.split(",")));
				System.out.println();
				break;
			case "addUser":
				System.out.println(l.AddUser(userInput.split(",")));
				System.out.println();
				break;
			case "deleteUser":
				System.out.println(l.deleteUser(userInput.split(",")));
				System.out.println();
				break;
			case "transfer":
				System.out.println(l.transfer(userInput.split(",")));
				System.out.println();
				break;
			case "exit":
				/* Close the output stream to make sure
				 * all the data is correctly written to file */
				l.exit();
				keyboard.close();
				return;
			default:
				System.out.println("Unknown operation");
			}
		}
	}
	/**
	 * Get user input and determine the operation
	 * @param input user input
	 * @return name of operation. It can be "borrow", "return", "renew", 
	 * "reserve", "inquire", and "exit". If return "unknown" then its an unknown operation
	 */
	public static String parseInput(String input) {
		if(input.equalsIgnoreCase("exit"))
			return "exit";
		String[] t = input.split(",");
		if(t[1].equalsIgnoreCase("borrow"))
			return "borrow";
		else if(t[1].equalsIgnoreCase("return"))
			return "return";
		else if(t[1].equalsIgnoreCase("renew"))
			return "renew";
		else if(t[1].equalsIgnoreCase("reserve"))
			return "reserve";
		else if(t[1].equalsIgnoreCase("addUser"))
			return "addUser";
		else if(t[1].equalsIgnoreCase("deleteUser"))
			return "deleteUser";
		else if(t[1].equalsIgnoreCase("transfer"))
			return "transfer";
		else {
			if(t[0].equalsIgnoreCase("inquire"))
				return "inquire";
			else
				return "unknown";
		}
	}
}
