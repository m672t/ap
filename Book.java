
import java.sql.*;
import java.util.HashSet;

public class Book {
    public static void addBook(Connection connection, int interestId, String bookTitle) throws SQLException {
        String query = "INSERT INTO Books (interest_id, book_title) VALUES (?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, interestId);
            pstmt.setString(2, bookTitle);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Book added successfully.");
            } else {
                System.out.println("Failed to add book.");
            }
        }
    }

    public static void displayBooks(Connection connection, int userId) throws SQLException {
        String query = "SELECT b.book_title FROM Books b " +
                       "JOIN Interests i ON b.interest_id = i.id " +
                       "WHERE i.user_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            if (!rs.isBeforeFirst()) {
                System.out.println("No books found for this user.");
            } else {
                System.out.println("Books you have read:");
                while (rs.next()) {
                    String title = rs.getString("book_title");
                    try {
                        String language = LanguageDetector.detectLanguage(title);
                        System.out.println("- " + title + " in " + language + " language");
                    } catch (Exception e) {
                        System.err.println("Error detecting language for book: " + title);
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public static void displayBooksByTopic(Connection connection, String topic) throws SQLException {
        String query = "SELECT DISTINCT b.book_title FROM Books b " +
                       "JOIN Interests i ON b.interest_id = i.id " +
                       "WHERE i.interest = ?";
        HashSet<String> uniqueBooks = new HashSet<>();
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, topic);
            ResultSet rs = pstmt.executeQuery();
            if (!rs.isBeforeFirst()) {
                System.out.println("No books found for the topic: " + topic);
            } else {
                System.out.println("Books available in the topic '" + topic + "':");
                while (rs.next()) {
                    String title = rs.getString("book_title");
                    try {
                        String language = LanguageDetector.detectLanguage(title);
                        uniqueBooks.add(title+ " in " + language + " language");
                    } catch (Exception e) {
                        uniqueBooks.add(title);
                    }
                }
                for (String title : uniqueBooks) {
                    System.out.println("- " + title);
                }
            }
        }
    }
}
