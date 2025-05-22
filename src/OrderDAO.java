import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class OrderDAO {

    public static void saveOrder(int itemId, int quantity) throws SQLException {
        String sql = "INSERT INTO orders (item_id, quantity) VALUES (?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, itemId);
            pstmt.setInt(2, quantity);
            pstmt.executeUpdate();
        }
    }
}
