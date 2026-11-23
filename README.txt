Library Management System (Java) - CSM 228 Data Structures Project

MODULES (src folder)
  LibraryManagementSystem.java  Console interface: menu, input reading and checking, main()
  Book.java                     Model class: id, title, author, issuedTo
  Member.java                   Model class: id, name, list of borrowed book IDs
  LibraryEngine.java            Core engine (HashMap, Queue, Stack, sorting) and file storage (save/load)

DATA STRUCTURES USED
  HashMap (books, members) | Queue (waiting lists) | Deque as Stack (recent returns)
  ArrayList + Comparator (sorting) | linear search by title

HOW TO RUN (JDK 8 or later)
  1. Open a terminal in this folder.
  2. Compile:  javac -d out src/*.java
  3. Run:      java -cp out LibraryManagementSystem

  In an IDE: open this folder as a project, mark src as the sources folder and run
  the main() method of LibraryManagementSystem.

The program creates books.txt and members.txt in the folder where it is run, so
your data is kept between runs. Delete them to start again with the sample data.
