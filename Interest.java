
import java.sql.*;
import java.util.HashSet;

public class Interest {
    public static int addInterest(Connection connection, int userId, String interest) throws SQLException {
        String query = "INSERT INTO Interests (user_id, interest) VALUES (?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, userId);
            pstmt.setString(2, interest);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        System.out.println("Interest added successfully with ID: " + generatedKeys.getInt(1));
                        return generatedKeys.getInt(1);
                    }
                }
            }
            throw new SQLException("Creating interest failed, no ID obtained.");
        }
    }

    public static void displayInterests(Connection connection, int userId) throws SQLException {
        String query = "SELECT id, interest FROM Interests WHERE user_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, userId);
            try (ResultSet resultSet = pstmt.executeQuery()) {
                System.out.println("Your interests:");
                while (resultSet.next()) {
                    int interestId = resultSet.getInt("id");
                    String interest = resultSet.getString("interest");
                    System.out.println("ID: " + interestId + ", Interest: " + interest);
                }
            }
        }
    }
}
