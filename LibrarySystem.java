import java.sql.*;
import java.util.Scanner;

public class LibrarySystem {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        try (Connection connection = DatabaseConnection.getConnection()) {
            System.out.println("Welcome to the Library System!");
            int flag = 1;
            int userId = -1;

            while (flag == 1) {
                flag = 0;
                System.out.println("Choose an option:");
                System.out.println("1. Log in to an existing account");
                System.out.println("2. Register a new account");
                System.out.println("3. Exit");
                System.out.print("Enter choice: ");
                int loginChoice = scanner.nextInt();
                scanner.nextLine();

                String name;

                switch (loginChoice) {
                    case 1:
                        System.out.print("Enter your name: ");
                        name = scanner.nextLine();
                        userId = User.getUserId(connection, name);
                        if (userId == -1) {
                            System.err.println("Error: User not found. Please register first.");
                            flag = 1;
                        }
                        break;
                    case 2:
                        System.out.print("Enter your name: ");
                        name = scanner.nextLine();
                        if (User.getUserId(connection, name) != -1) {
                            System.err.println("Error: Username already exists. Please choose a different name.");
                            flag = 1;
                        }
                        else{
                        userId = User.registerUser(connection, name);
                        System.out.println("Registration successful! Your user ID is: " + userId);
                        break;
                        }
                    case 3:
                        System.out.println("Goodbye!");
                        return;
                    default:
                        System.err.println("Invalid choice. Exiting...");
                        flag = 1;
                }
            }
            while (true) {
                System.out.println("Choose an option:");
                System.out.println("1. Add a new interest");
                System.out.println("2. Add a book to an existing interest");
                System.out.println("3. Display your books");
                System.out.println("4. View books by topic");
                System.out.println("5. Exit");
                System.out.print("Enter choice: ");
                int choice = scanner.nextInt();
                scanner.nextLine();

                switch (choice) {
                    case 1:
                        System.out.print("Enter your new interest: ");
                        String newInterest = scanner.nextLine();
                        Interest.addInterest(connection, userId, newInterest);
                        break;
                    case 2:
                        System.out.println("Choose an interest:");
                        Interest.displayInterests(connection, userId);
                        System.out.print("Enter interest ID: ");
                        int interestId = scanner.nextInt();
                        scanner.nextLine();
                        System.out.print("Enter book title: ");
                        String bookTitle = scanner.nextLine();
                        Book.addBook(connection, interestId, bookTitle);
                        break;
                    case 3:
                        Book.displayBooks(connection, userId);
                        break;
                    case 4:
                        System.out.print("Enter the topic to view books: ");
                        String topic = scanner.nextLine();
                        StringBuilder booksList = new StringBuilder();
                        Book.displayBooksByTopic(connection, topic, booksList);
                        
                        if (booksList.length() == 0) { 
                            booksList.append("No books found for the topic: ").append(topic);
                        } else {
                            System.out.println("\nBooks read by your friends:\n");
                            System.out.println(booksList.toString());
                            String[] books = booksList.toString().split("\n"); 
                            for (String book : books) {
                            booksList.append(book).append("\n");
                            }
                        }
                        System.out.println("Other helpful books you can read:\n");
                        
                        String prompt = "Please write a recommendation  books on the topic '" + topic ;
                        String response = OllamaQueryRunner.runOllamaQuery(prompt); 
                        System.out.println(response); 
                        
                        break;
                    case 5:
                        System.out.println("Goodbye!");
                        return;
                    default:
                        System.out.println("Invalid choice. Try again.");
                }
            }
        } catch (SQLException e) {
            System.err.println("An error occurred: " + e.getMessage());
            e.printStackTrace();
        } finally {
            scanner.close();
        }
    }
}
