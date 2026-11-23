import java.util.ArrayList;

/* ---------- Model class: Member ---------- */

class Member {
    int id;
    String name;
    ArrayList<Integer> borrowed = new ArrayList<>(); // IDs of books this member holds

    Member(int id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public String toString() {
        return String.format("%-5d %-20s Books borrowed: %s", id, name, borrowed);
    }
}
