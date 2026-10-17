package repository.jdbc;

import config.DatabaseConfig;
import model.Ticket;
import model.TicketDetails;
import model.TicketPriority;
import model.TicketStatus;
import model.TicketStatusHistory;
import repository.TicketRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * JDBC-реализация репозитория заявок.
 * Использует PreparedStatement и try-with-resources.
 */
public class JdbcTicketRepository implements TicketRepository {

    private static final String SELECT_BY_ID =
            "SELECT * FROM tickets WHERE id = ?";

    private static final String SELECT_ALL =
            "SELECT * FROM tickets ORDER BY id";

    private static final String SELECT_BY_STATUS = """
            SELECT * FROM tickets
            WHERE status = ?::ticket_status
            ORDER BY created_at DESC
            """;

    private static final String SELECT_BY_CUSTOMER = """
            SELECT * FROM tickets
            WHERE customer_id = ?
            ORDER BY created_at DESC
            """;

    private static final String SELECT_DETAILS = """
            SELECT
                t.id,
                t.title,
                t.status,
                t.priority,
                c.name AS category_name,
                customer.name AS customer_name,
                assignee.name AS assignee_name,
                t.created_at
            FROM tickets t
            JOIN users customer ON customer.id = t.customer_id
            LEFT JOIN users assignee ON assignee.id = t.assignee_id
            LEFT JOIN categories c ON c.id = t.category_id
            """;

    @Override
    public Ticket save(Ticket ticket) {
        String sql = """
                INSERT INTO tickets(
                    title, description, status, priority,
                    customer_id, assignee_id, category_id
                )
                VALUES (?, ?, ?::ticket_status, ?::ticket_priority, ?, ?, ?)
                RETURNING id, created_at, updated_at
                """;

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, ticket.getTitle());
            ps.setString(2, ticket.getDescription());
            ps.setString(3, ticket.getStatus().name());
            ps.setString(4, ticket.getPriority().name());
            ps.setLong(5, ticket.getCustomerId());

            if (ticket.getAssigneeId() == null) {
                ps.setNull(6, Types.BIGINT);
            } else {
                ps.setLong(6, ticket.getAssigneeId());
            }

            if (ticket.getCategoryId() == null) {
                ps.setNull(7, Types.BIGINT);
            } else {
                ps.setLong(7, ticket.getCategoryId());
            }

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    ticket.setId(rs.getLong("id"));
                    ticket.setCreatedAt(
                            rs.getTimestamp("created_at").toLocalDateTime()
                    );
                    ticket.setUpdatedAt(
                            rs.getTimestamp("updated_at").toLocalDateTime()
                    );
                }
            }
            return ticket;

        } catch (SQLException e) {
            throw new RuntimeException("Не удалось сохранить заявку", e);
        }
    }

    @Override
    public Optional<Ticket> findById(long id) {
        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement ps = connection.prepareStatement(SELECT_BY_ID)) {

            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapTicket(rs));
                }
                return Optional.empty();
            }

        } catch (SQLException e) {
            throw new RuntimeException("Ошибка поиска заявки", e);
        }
    }

    @Override
    public List<Ticket> findAll() {
        List<Ticket> result = new ArrayList<>();

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement ps = connection.prepareStatement(SELECT_ALL);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                result.add(mapTicket(rs));
            }
            return result;

        } catch (SQLException e) {
            throw new RuntimeException("Ошибка чтения заявок", e);
        }
    }

    @Override
    public List<Ticket> findByStatus(TicketStatus status) {
        List<Ticket> result = new ArrayList<>();

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement ps =
                     connection.prepareStatement(SELECT_BY_STATUS)) {

            ps.setString(1, status.name());

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(mapTicket(rs));
                }
            }
            return result;

        } catch (SQLException e) {
            throw new RuntimeException("Ошибка фильтрации заявок", e);
        }
    }

    @Override
    public List<Ticket> findByCustomerId(long customerId) {
        List<Ticket> result = new ArrayList<>();

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement ps =
                     connection.prepareStatement(SELECT_BY_CUSTOMER)) {

            ps.setLong(1, customerId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(mapTicket(rs));
                }
            }
            return result;

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Ошибка поиска заявок клиента", e
            );
        }
    }

    @Override
    public void update(Ticket ticket) {
        String sql = """
                UPDATE tickets
                SET title = ?,
                    description = ?,
                    status = ?::ticket_status,
                    priority = ?::ticket_priority,
                    assignee_id = ?,
                    category_id = ?,
                    updated_at = CURRENT_TIMESTAMP
                WHERE id = ?
                """;

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, ticket.getTitle());
            ps.setString(2, ticket.getDescription());
            ps.setString(3, ticket.getStatus().name());
            ps.setString(4, ticket.getPriority().name());

            if (ticket.getAssigneeId() == null) {
                ps.setNull(5, Types.BIGINT);
            } else {
                ps.setLong(5, ticket.getAssigneeId());
            }

            if (ticket.getCategoryId() == null) {
                ps.setNull(6, Types.BIGINT);
            } else {
                ps.setLong(6, ticket.getCategoryId());
            }

            ps.setLong(7, ticket.getId());
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Не удалось обновить заявку", e);
        }
    }

    @Override
    public boolean deleteById(long id) {
        String sql = "DELETE FROM tickets WHERE id = ?";

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setLong(1, id);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Не удалось удалить заявку", e);
        }
    }

    @Override
    public List<TicketDetails> findAllDetails() {
        List<TicketDetails> result = new ArrayList<>();

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement ps =
                     connection.prepareStatement(SELECT_DETAILS + " ORDER BY t.id");
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                result.add(mapDetails(rs));
            }
            return result;

        } catch (SQLException e) {
            throw new RuntimeException("Ошибка JOIN-запроса заявок", e);
        }
    }

    @Override
    public Optional<TicketDetails> findDetailsById(long id) {
        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement ps = connection.prepareStatement(
                     SELECT_DETAILS + " WHERE t.id = ?")) {

            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapDetails(rs));
                }
                return Optional.empty();
            }

        } catch (SQLException e) {
            throw new RuntimeException("Ошибка JOIN-запроса заявки", e);
        }
    }

    @Override
    public void changeStatus(long ticketId, TicketStatus newStatus,
                             long changedByUserId) {
        String selectSql =
                "SELECT status FROM tickets WHERE id = ? FOR UPDATE";
        String updateSql = """
                UPDATE tickets
                SET status = ?::ticket_status,
                    updated_at = CURRENT_TIMESTAMP,
                    closed_at = CASE
                        WHEN ?::ticket_status = 'CLOSED' THEN CURRENT_TIMESTAMP
                        ELSE closed_at
                    END
                WHERE id = ?
                """;
        String historySql = """
                INSERT INTO ticket_status_history(
                    ticket_id, old_status, new_status, changed_by_id
                )
                VALUES (?, ?::ticket_status, ?::ticket_status, ?)
                """;

        try (Connection connection = DatabaseConfig.getConnection()) {
            connection.setAutoCommit(false);

            try {
                TicketStatus oldStatus = lockAndReadStatus(
                        connection, selectSql, ticketId
                );

                try (PreparedStatement ps =
                             connection.prepareStatement(updateSql)) {
                    ps.setString(1, newStatus.name());
                    ps.setString(2, newStatus.name());
                    ps.setLong(3, ticketId);
                    ps.executeUpdate();
                }

                try (PreparedStatement ps =
                             connection.prepareStatement(historySql)) {
                    ps.setLong(1, ticketId);
                    ps.setString(2, oldStatus.name());
                    ps.setString(3, newStatus.name());
                    ps.setLong(4, changedByUserId);
                    ps.executeUpdate();
                }

                connection.commit();

            } catch (Exception e) {
                connection.rollback();
                throw e;
            }

        } catch (SQLException e) {
            throw new RuntimeException("Ошибка изменения статуса", e);
        }
    }

    @Override
    public void assign(long ticketId, long assigneeId) {
        String selectSql =
                "SELECT status FROM tickets WHERE id = ? FOR UPDATE";
        String updateSql = """
                UPDATE tickets
                SET assignee_id = ?,
                    status = 'IN_PROGRESS',
                    updated_at = CURRENT_TIMESTAMP
                WHERE id = ?
                """;
        String historySql = """
                INSERT INTO ticket_status_history(
                    ticket_id, old_status, new_status, changed_by_id
                )
                VALUES (?, ?::ticket_status, 'IN_PROGRESS'::ticket_status, ?)
                """;

        try (Connection connection = DatabaseConfig.getConnection()) {
            connection.setAutoCommit(false);

            try {
                TicketStatus oldStatus = lockAndReadStatus(
                        connection, selectSql, ticketId
                );

                try (PreparedStatement ps =
                             connection.prepareStatement(updateSql)) {
                    ps.setLong(1, assigneeId);
                    ps.setLong(2, ticketId);
                    ps.executeUpdate();
                }

                if (oldStatus != TicketStatus.IN_PROGRESS) {
                    try (PreparedStatement ps =
                                 connection.prepareStatement(historySql)) {
                        ps.setLong(1, ticketId);
                        ps.setString(2, oldStatus.name());
                        ps.setLong(3, assigneeId);
                        ps.executeUpdate();
                    }
                }

                connection.commit();

            } catch (Exception e) {
                connection.rollback();
                throw e;
            }

        } catch (SQLException e) {
            throw new RuntimeException("Ошибка назначения заявки", e);
        }
    }

    @Override
    public List<TicketStatusHistory> findHistoryByTicketId(long ticketId) {
        String sql = """
                SELECT h.id,
                       h.ticket_id,
                       h.old_status,
                       h.new_status,
                       h.changed_by_id,
                       h.changed_at,
                       u.name AS changed_by_name
                FROM ticket_status_history h
                LEFT JOIN users u ON u.id = h.changed_by_id
                WHERE h.ticket_id = ?
                ORDER BY h.changed_at
                """;
        List<TicketStatusHistory> result = new ArrayList<>();

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setLong(1, ticketId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    TicketStatusHistory history = new TicketStatusHistory();
                    history.setId(rs.getLong("id"));
                    history.setTicketId(rs.getLong("ticket_id"));

                    String oldStatus = rs.getString("old_status");
                    history.setOldStatus(
                            oldStatus == null
                                    ? null
                                    : TicketStatus.valueOf(oldStatus)
                    );
                    history.setNewStatus(
                            TicketStatus.valueOf(rs.getString("new_status"))
                    );

                    long changedBy = rs.getLong("changed_by_id");
                    history.setChangedById(
                            rs.wasNull() ? null : changedBy
                    );
                    history.setChangedAt(
                            rs.getTimestamp("changed_at").toLocalDateTime()
                    );
                    history.setChangedByName(
                            rs.getString("changed_by_name")
                    );
                    result.add(history);
                }
            }
            return result;

        } catch (SQLException e) {
            throw new RuntimeException("Ошибка чтения истории статусов", e);
        }
    }

    private TicketStatus lockAndReadStatus(Connection connection,
                                           String selectSql,
                                           long ticketId) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement(selectSql)) {
            ps.setLong(1, ticketId);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    throw new IllegalArgumentException(
                            "Заявка не найдена: id=" + ticketId
                    );
                }
                return TicketStatus.valueOf(rs.getString("status"));
            }
        }
    }

    private Ticket mapTicket(ResultSet rs) throws SQLException {
        Ticket ticket = new Ticket();
        ticket.setId(rs.getLong("id"));
        ticket.setTitle(rs.getString("title"));
        ticket.setDescription(rs.getString("description"));
        ticket.setStatus(TicketStatus.valueOf(rs.getString("status")));
        ticket.setPriority(TicketPriority.valueOf(rs.getString("priority")));
        ticket.setCustomerId(rs.getLong("customer_id"));

        long assigneeId = rs.getLong("assignee_id");
        ticket.setAssigneeId(rs.wasNull() ? null : assigneeId);

        long categoryId = rs.getLong("category_id");
        ticket.setCategoryId(rs.wasNull() ? null : categoryId);

        ticket.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        ticket.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());

        Timestamp closed = rs.getTimestamp("closed_at");
        ticket.setClosedAt(closed == null ? null : closed.toLocalDateTime());

        return ticket;
    }

    private TicketDetails mapDetails(ResultSet rs) throws SQLException {
        return new TicketDetails(
                rs.getLong("id"),
                rs.getString("title"),
                TicketStatus.valueOf(rs.getString("status")),
                TicketPriority.valueOf(rs.getString("priority")),
                rs.getString("category_name"),
                rs.getString("customer_name"),
                rs.getString("assignee_name"),
                rs.getTimestamp("created_at").toLocalDateTime()
        );
    }
}
