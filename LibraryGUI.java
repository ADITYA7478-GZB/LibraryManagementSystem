import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

/* ---------- Desktop interface (Swing) ----------
   Uses the same Book, Member and LibraryEngine classes as the console version.
   No engine code is changed: every button calls the same engine method as the matching menu option. */

public class LibraryGUI extends JFrame {

    static final Color BROWN = new Color(0x7D2C10);
    static final Color ORANGE = new Color(0xD9622B);
    static final Color CREAM = new Color(0xFFF8F2);

    private final LibraryEngine lib = new LibraryEngine();
    private final JTextArea output = new JTextArea();
    private final JLabel status = new JLabel("Ready");

    private final JTextField bookIdF = new JTextField(16);
    private final JTextField titleF = new JTextField(16);
    private final JTextField authorF = new JTextField(16);
    private final JTextField memberIdF = new JTextField(16);
    private final JTextField nameF = new JTextField(16);
    private final JTextField issueBookF = new JTextField(16);
    private final JTextField issueMemberF = new JTextField(16);
    private final JTextField searchF = new JTextField(16);

    public LibraryGUI() {
        super("Library Management System");
        lib.load();

        if (!lib.hasBooks()) { // same sample data as the console version
            lib.addBook(101, "Data Structures in Java", "Mark Weiss");
            lib.addBook(102, "Clean Code", "Robert Martin");
            lib.addBook(103, "Introduction to Algorithms", "Thomas Cormen");
            lib.addBook(104, "Effective Java", "Joshua Bloch");
            lib.addMember(1, "Aditya");
            lib.addMember(2, "Riya");
            lib.addMember(3, "Karan");
        }

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                exitApp();
            }
        });

        buildUI();
        setSize(1020, 640);
        setMinimumSize(new Dimension(900, 560));
        setLocationRelativeTo(null);
        log("Welcome", "Data loaded from books.txt and members.txt.\nUse the tabs on the left to manage the library.");
    }

    /* ---------- Layout ---------- */

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(CREAM);

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(BROWN);
        header.setBorder(new EmptyBorder(14, 20, 14, 20));
        JLabel title = new JLabel("Library Management System");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Serif", Font.BOLD, 26));
        JLabel sub = new JLabel("HashMap  |  Queue  |  Stack  |  Sorting  |  File storage");
        sub.setForeground(new Color(0xFFBE86));
        sub.setFont(new Font("SansSerif", Font.PLAIN, 13));
        header.add(title, BorderLayout.CENTER);
        header.add(sub, BorderLayout.SOUTH);
        root.add(header, BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("SansSerif", Font.BOLD, 13));
        tabs.addTab("Books", booksTab());
        tabs.addTab("Members", membersTab());
        tabs.addTab("Issue / Return", issueTab());
        tabs.addTab("Search", searchTab());
        tabs.setPreferredSize(new Dimension(360, 300));

        output.setEditable(false);
        output.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        output.setBackground(Color.WHITE);
        output.setMargin(new Insets(8, 10, 8, 10));
        JScrollPane scroll = new JScrollPane(output);
        TitledBorder tb = BorderFactory.createTitledBorder("Output");
        tb.setTitleFont(new Font("SansSerif", Font.BOLD, 13));
        scroll.setBorder(tb);

        JPanel center = new JPanel(new BorderLayout(12, 0));
        center.setBorder(new EmptyBorder(12, 12, 6, 12));
        center.setOpaque(false);
        center.add(tabs, BorderLayout.WEST);
        center.add(scroll, BorderLayout.CENTER);
        root.add(center, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setBorder(new EmptyBorder(4, 14, 12, 14));
        bottom.setOpaque(false);
        status.setFont(new Font("SansSerif", Font.PLAIN, 13));
        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btns.setOpaque(false);
        btns.add(button("Clear Output", ORANGE, e -> {
            output.setText("");
            status.setText("Output cleared.");
        }));
        btns.add(button("Save and Exit", BROWN, e -> exitApp()));
        bottom.add(status, BorderLayout.CENTER);
        bottom.add(btns, BorderLayout.EAST);
        root.add(bottom, BorderLayout.SOUTH);

        setContentPane(root);
    }

    private JPanel booksTab() {
        JPanel p = form();
        addRow(p, 0, "Book ID", bookIdF);
        addRow(p, 1, "Title", titleF);
        addRow(p, 2, "Author", authorF);
        addButtons(p, 3,
                button("Add Book", BROWN, e -> addBook()),
                button("View All Books (sorted by title)", ORANGE, e -> viewBooks()));
        return p;
    }

    private JPanel membersTab() {
        JPanel p = form();
        addRow(p, 0, "Member ID", memberIdF);
        addRow(p, 1, "Name", nameF);
        addButtons(p, 2,
                button("Add Member", BROWN, e -> addMember()),
                button("View Members", ORANGE, e -> viewMembers()));
        return p;
    }

    private JPanel issueTab() {
        JPanel p = form();
        addRow(p, 0, "Book ID", issueBookF);
        addRow(p, 1, "Member ID", issueMemberF);
        JLabel hint = new JLabel("Return and Waiting List only need the Book ID.");
        hint.setFont(new Font("SansSerif", Font.ITALIC, 12));
        GridBagConstraints g = new GridBagConstraints();
        g.gridx = 0; g.gridy = 2; g.gridwidth = 2; g.anchor = GridBagConstraints.WEST; g.insets = new Insets(2, 0, 0, 0);
        p.add(hint, g);
        addButtons(p, 3,
                button("Issue Book", BROWN, e -> issueBook()),
                button("Return Book", BROWN, e -> returnBook()),
                button("View Waiting List", ORANGE, e -> viewWaiting()),
                button("Recently Returned Books", ORANGE, e -> viewRecent()));
        return p;
    }

    private JPanel searchTab() {
        JPanel p = form();
        addRow(p, 0, "Title keyword", searchF);
        addButtons(p, 1, button("Search", BROWN, e -> search()));
        return p;
    }

    private JPanel form() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(Color.WHITE);
        p.setBorder(new EmptyBorder(14, 14, 14, 14));
        return p;
    }

    private void addRow(JPanel p, int y, String label, JComponent field) {
        GridBagConstraints g = new GridBagConstraints();
        g.gridx = 0; g.gridy = y; g.anchor = GridBagConstraints.WEST; g.insets = new Insets(6, 0, 6, 10);
        JLabel l = new JLabel(label);
        l.setFont(new Font("SansSerif", Font.PLAIN, 13));
        p.add(l, g);
        g.gridx = 1; g.fill = GridBagConstraints.HORIZONTAL; g.weightx = 1; g.insets = new Insets(6, 0, 6, 0);
        p.add(field, g);
    }

    private void addButtons(JPanel p, int y, JButton... buttons) {
        JPanel row = new JPanel(new GridLayout(buttons.length, 1, 0, 8));
        row.setOpaque(false);
        for (JButton b : buttons) row.add(b);
        GridBagConstraints g = new GridBagConstraints();
        g.gridx = 0; g.gridy = y; g.gridwidth = 2; g.fill = GridBagConstraints.HORIZONTAL;
        g.anchor = GridBagConstraints.NORTH; g.weighty = 1; g.insets = new Insets(14, 0, 0, 0);
        p.add(row, g);
    }

    private JButton button(String text, Color bg, ActionListener a) {
        JButton b = new JButton(text);
        b.setBackground(bg);
        b.setForeground(Color.WHITE);
        b.setFont(new Font("SansSerif", Font.BOLD, 13));
        b.setFocusPainted(false);
        b.setOpaque(true);
        b.setBorder(new EmptyBorder(9, 14, 9, 14));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.addActionListener(a);
        return b;
    }

    /* ---------- Helpers ---------- */

    private String capture(Runnable r) { // runs an engine print method and returns what it printed
        PrintStream old = System.out;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        System.setOut(new PrintStream(bos, true));
        try {
            r.run();
        } finally {
            System.out.flush();
            System.setOut(old);
        }
        return bos.toString();
    }

    private void log(String heading, String text) {
        output.append("--- " + heading + " ---\n" + text.replaceAll("\\s+$", "") + "\n\n");
        output.setCaretPosition(output.getDocument().getLength());
        status.setText(heading);
    }

    private void warn(String message) {
        JOptionPane.showMessageDialog(this, message, "Check your input", JOptionPane.WARNING_MESSAGE);
    }

    private Integer number(JTextField f, String label) { // same rule as readInt() in the console version
        try {
            return Integer.parseInt(f.getText().trim());
        } catch (NumberFormatException e) {
            warn(label + ": please enter a valid number.");
            f.requestFocus();
            return null;
        }
    }

    private String text(JTextField f, String label) { // same rule as readText() in the console version
        String t = f.getText().trim();
        if (t.isEmpty()) {
            warn(label + " cannot be empty.");
            f.requestFocus();
            return null;
        }
        return t;
    }

    /* ---------- Actions (one per menu option of the console version) ---------- */

    private void addBook() {
        Integer id = number(bookIdF, "Book ID");
        String title = text(titleF, "Title");
        String author = text(authorF, "Author");
        if (id == null || title == null || author == null) return;
        String msg = lib.addBook(id, title, author);
        log("Add book", msg);
        if (msg.equals("Book added.")) {
            bookIdF.setText(""); titleF.setText(""); authorF.setText("");
        }
        lib.save();
    }

    private void addMember() {
        Integer id = number(memberIdF, "Member ID");
        String name = text(nameF, "Name");
        if (id == null || name == null) return;
        String msg = lib.addMember(id, name);
        log("Add member", msg);
        if (msg.equals("Member added.")) {
            memberIdF.setText(""); nameF.setText("");
        }
        lib.save();
    }

    private void issueBook() {
        Integer bookId = number(issueBookF, "Book ID");
        Integer memberId = number(issueMemberF, "Member ID");
        if (bookId == null || memberId == null) return;
        log("Issue book", lib.issueBook(bookId, memberId));
        lib.save();
    }

    private void returnBook() {
        Integer bookId = number(issueBookF, "Book ID");
        if (bookId == null) return;
        log("Return book", lib.returnBook(bookId));
        lib.save();
    }

    private void search() {
        String keyword = text(searchF, "Title keyword");
        if (keyword == null) return;
        log("Search: " + keyword, capture(() -> lib.printBooks(lib.searchByTitle(keyword))));
        lib.save();
    }

    private void viewBooks() {
        log("All books (sorted by title)", capture(() -> lib.printBooks(lib.allBooksSorted())));
        lib.save();
    }

    private void viewMembers() {
        log("Members", capture(lib::printMembers));
        lib.save();
    }

    private void viewRecent() {
        log("Recently returned books", capture(lib::printRecentReturns));
        lib.save();
    }

    private void viewWaiting() {
        Integer bookId = number(issueBookF, "Book ID");
        if (bookId == null) return;
        log("Waiting list", capture(() -> lib.printWaitingList(bookId)));
        lib.save();
    }

    private void exitApp() {
        lib.save();
        dispose();
        System.exit(0);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LibraryGUI().setVisible(true));
    }
}