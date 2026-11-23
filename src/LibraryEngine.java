import java.io.*;
import java.util.*;

/* ---------- Core engine ---------- */

class LibraryEngine {
    static final int MAX_BOOKS = 3;
    static final String BOOK_FILE = "books.txt";
    static final String MEMBER_FILE = "members.txt";

    private HashMap<Integer, Book> books = new HashMap<>();                 // bookId -> Book
    private HashMap<Integer, Member> members = new HashMap<>();             // memberId -> Member
    private HashMap<Integer, Queue<Integer>> waitingLists = new HashMap<>(); // bookId -> queue of memberIds
    private Deque<String> recentReturns = new ArrayDeque<>();               // used as a Stack (last 5 returns)

    boolean hasBooks() {
        return !books.isEmpty();
    }

    String addBook(int id, String title, String author) {
        if (books.containsKey(id)) return "A book with this ID already exists.";
        books.put(id, new Book(id, title.replace("|", "-"), author.replace("|", "-"), 0));
        return "Book added.";
    }

    String addMember(int id, String name) {
        if (members.containsKey(id)) return "A member with this ID already exists.";
        members.put(id, new Member(id, name.replace("|", "-")));
        return "Member added.";
    }

    String issueBook(int bookId, int memberId) {
        Book b = books.get(bookId);
        Member m = members.get(memberId);
        if (b == null) return "No book with that ID.";
        if (m == null) return "No member with that ID.";
        if (b.issuedTo == memberId) return m.name + " already has this book.";

        if (b.isAvailable()) {
            if (m.borrowed.size() >= MAX_BOOKS) return "Borrow limit reached (" + MAX_BOOKS + " books).";
            b.issuedTo = memberId;
            m.borrowed.add(bookId);
            return "Book issued to " + m.name + ".";
        }

        // Book is taken: add the member to the waiting list (Queue = first come, first served)
        Queue<Integer> q = waitingLists.computeIfAbsent(bookId, k -> new LinkedList<>());
        if (q.contains(memberId)) return m.name + " is already on the waiting list.";
        q.add(memberId);
        return "Book is currently issued. " + m.name + " added to the waiting list (position " + q.size() + ").";
    }

    String returnBook(int bookId) {
        Book b = books.get(bookId);
        if (b == null) return "No book with that ID.";
        if (b.isAvailable()) return "This book is not issued.";

        Member holder = members.get(b.issuedTo);
        if (holder != null) holder.borrowed.remove(Integer.valueOf(bookId));

        recentReturns.push(b.title + " (returned by " + (holder != null ? holder.name : "member " + b.issuedTo) + ")");
        if (recentReturns.size() > 5) recentReturns.removeLast();
        b.issuedTo = 0;

        StringBuilder msg = new StringBuilder("Book returned.");
        Queue<Integer> q = waitingLists.get(bookId);
        while (q != null && !q.isEmpty()) {
            Member next = members.get(q.poll());
            if (next != null && next.borrowed.size() < MAX_BOOKS) {
                b.issuedTo = next.id;
                next.borrowed.add(bookId);
                msg.append(" Automatically issued to ").append(next.name).append(" from the waiting list.");
                break;
            }
        }
        return msg.toString();
    }

    ArrayList<Book> searchByTitle(String keyword) {
        ArrayList<Book> result = new ArrayList<>();
        for (Book b : books.values()) {
            if (b.title.toLowerCase().contains(keyword.toLowerCase())) result.add(b);
        }
        return result;
    }

    ArrayList<Book> allBooksSorted() {
        ArrayList<Book> list = new ArrayList<>(books.values());
        list.sort(Comparator.comparing((Book x) -> x.title.toLowerCase()));
        return list;
    }

    void printBooks(List<Book> list) {
        if (list.isEmpty()) {
            System.out.println("No books found.");
            return;
        }
        System.out.println(String.format("%-5s %-30s %-20s %s", "ID", "Title", "Author", "Status"));
        System.out.println("---------------------------------------------------------------------------");
        for (Book b : list) System.out.println(b);
    }

    void printMembers() {
        if (members.isEmpty()) {
            System.out.println("No members yet.");
            return;
        }
        ArrayList<Member> list = new ArrayList<>(members.values());
        list.sort(Comparator.comparingInt((Member x) -> x.id));
        for (Member m : list) System.out.println(m);
    }

    void printRecentReturns() {
        if (recentReturns.isEmpty()) {
            System.out.println("No returns yet.");
            return;
        }
        int i = 1;
        for (String s : recentReturns) System.out.println(i++ + ". " + s); // most recent first
    }

    void printWaitingList(int bookId) {
        Book b = books.get(bookId);
        if (b == null) {
            System.out.println("No book with that ID.");
            return;
        }
        Queue<Integer> q = waitingLists.get(bookId);
        if (q == null || q.isEmpty()) {
            System.out.println("Nobody is waiting for \"" + b.title + "\".");
            return;
        }
        System.out.println("Waiting list for \"" + b.title + "\":");
        int pos = 1;
        for (int memberId : q) {
            Member m = members.get(memberId);
            System.out.println(pos++ + ". " + (m != null ? m.name : "member " + memberId));
        }
    }

    /* ---------- File storage ---------- */

    void save() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(BOOK_FILE))) {
            for (Book b : books.values())
                pw.println(b.id + "|" + b.title + "|" + b.author + "|" + b.issuedTo);
        } catch (IOException e) {
            System.out.println("Could not save books: " + e.getMessage());
        }
        try (PrintWriter pw = new PrintWriter(new FileWriter(MEMBER_FILE))) {
            for (Member m : members.values())
                pw.println(m.id + "|" + m.name);
        } catch (IOException e) {
            System.out.println("Could not save members: " + e.getMessage());
        }
    }

    void load() {
        File mf = new File(MEMBER_FILE);
        File bf = new File(BOOK_FILE);
        try {
            if (mf.exists()) {
                try (BufferedReader br = new BufferedReader(new FileReader(mf))) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        String[] p = line.split("\\|");
                        if (p.length == 2) members.put(Integer.parseInt(p[0]), new Member(Integer.parseInt(p[0]), p[1]));
                    }
                }
            }
            if (bf.exists()) {
                try (BufferedReader br = new BufferedReader(new FileReader(bf))) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        String[] p = line.split("\\|");
                        if (p.length == 4) {
                            Book b = new Book(Integer.parseInt(p[0]), p[1], p[2], Integer.parseInt(p[3]));
                            books.put(b.id, b);
                            Member holder = members.get(b.issuedTo);
                            if (holder != null) holder.borrowed.add(b.id);
                        }
                    }
                }
            }
        } catch (IOException | NumberFormatException e) {
            System.out.println("Problem reading saved data: " + e.getMessage());
        }
    }
}
