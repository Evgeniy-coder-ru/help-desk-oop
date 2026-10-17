package repository.jdbc;

import config.DatabaseConfig;
import model.Category;
import model.TicketPriority;
import repository.CategoryRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * JDBC-реализация репозитория категорий.
 */
public class JdbcCategoryRepository implements CategoryRepository {

    @Override
    public List<Category> findAllCategories() {
        String sql = "SELECT * FROM categories ORDER BY id";
        List<Category> result = new ArrayList<>();

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                result.add(mapCategory(rs));
            }
            return result;

        } catch (SQLException e) {
            throw new RuntimeException("Ошибка чтения категорий", e);
        }
    }

    @Override
    public Optional<Category> findCategoryById(long id) {
        String sql = "SELECT * FROM categories WHERE id = ?";

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapCategory(rs));
                }
                return Optional.empty();
            }

        } catch (SQLException e) {
            throw new RuntimeException("Ошибка поиска категории", e);
        }
    }

    private Category mapCategory(ResultSet rs) throws SQLException {
        return new Category(
                rs.getLong("id"),
                rs.getString("name"),
                rs.getString("description"),
                TicketPriority.valueOf(rs.getString("default_priority")),
                rs.getBoolean("active")
        );
    }
}
