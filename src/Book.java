/* ---------- Model class: Book ---------- */

class Book {
    int id;
    String title;
    String author;
    int issuedTo; // 0 means the book is available

    Book(int id, String title, String author, int issuedTo) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.issuedTo = issuedTo;
    }

    boolean isAvailable() {
        return issuedTo == 0;
    }

    @Override
    public String toString() {
        return String.format("%-5d %-30s %-20s %s", id, title, author,
                isAvailable() ? "Available" : "Issued to member " + issuedTo);
    }
}
