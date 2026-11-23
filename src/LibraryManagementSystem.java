import java.util.Scanner;

/* ---------- Console interface ---------- */

public class LibraryManagementSystem {

    static Scanner sc = new Scanner(System.in);

    static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = sc.nextLine().trim();
            try {
                return Integer.parseInt(line);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    static String readText(String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = sc.nextLine().trim();
            if (!line.isEmpty()) return line;
            System.out.println("This cannot be empty.");
        }
    }

    static void printMenu() {
        System.out.println("\n===== LIBRARY MANAGEMENT SYSTEM =====");
        System.out.println("1. Add book");
        System.out.println("2. Add member");
        System.out.println("3. Issue book");
        System.out.println("4. Return book");
        System.out.println("5. Search book by title");
        System.out.println("6. View all books (sorted by title)");
        System.out.println("7. View members");
        System.out.println("8. Recently returned books");
        System.out.println("9. View waiting list of a book");
        System.out.println("0. Save and exit");
    }

    public static void main(String[] args) {
        LibraryEngine lib = new LibraryEngine();
        lib.load();

        if (!lib.hasBooks()) { // sample data so the program has something to show on first run
            lib.addBook(101, "Data Structures in Java", "Mark Weiss");
            lib.addBook(102, "Clean Code", "Robert Martin");
            lib.addBook(103, "Introduction to Algorithms", "Thomas Cormen");
            lib.addBook(104, "Effective Java", "Joshua Bloch");
            lib.addMember(1, "Aditya");
            lib.addMember(2, "Riya");
            lib.addMember(3, "Karan");
        }

        while (true) {
            printMenu();
            int choice = readInt("Enter choice: ");
            switch (choice) {
                case 1:
                    System.out.println(lib.addBook(readInt("Book ID: "), readText("Title: "), readText("Author: ")));
                    break;
                case 2:
                    System.out.println(lib.addMember(readInt("Member ID: "), readText("Name: ")));
                    break;
                case 3:
                    System.out.println(lib.issueBook(readInt("Book ID: "), readInt("Member ID: ")));
                    break;
                case 4:
                    System.out.println(lib.returnBook(readInt("Book ID: ")));
                    break;
                case 5:
                    lib.printBooks(lib.searchByTitle(readText("Search keyword: ")));
                    break;
                case 6:
                    lib.printBooks(lib.allBooksSorted());
                    break;
                case 7:
                    lib.printMembers();
                    break;
                case 8:
                    lib.printRecentReturns();
                    break;
                case 9:
                    lib.printWaitingList(readInt("Book ID: "));
                    break;
                case 0:
                    lib.save();
                    System.out.println("Data saved. Goodbye!");
                    return;
                default:
                    System.out.println("Invalid choice, try again.");
            }
            lib.save(); // autosave after every action
        }
    }
}
