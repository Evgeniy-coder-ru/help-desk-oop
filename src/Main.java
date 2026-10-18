import config.DatabaseConfig;
import model.Category;
import model.Ticket;
import model.TicketComment;
import model.TicketDetails;
import model.TicketPriority;
import model.TicketStatus;
import model.TicketStatusHistory;
import model.User;
import repository.CategoryRepository;
import repository.CommentRepository;
import repository.TicketRepository;
import repository.UserRepository;
import repository.jdbc.JdbcCategoryRepository;
import repository.jdbc.JdbcCommentRepository;
import repository.jdbc.JdbcTicketRepository;
import repository.jdbc.JdbcUserRepository;
import service.TicketService;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;


public class Main {

    private static final String CUSTOMER_EMAIL = "anna@example.org";
    private static final String AGENT_EMAIL = "ivan.support@example.org";

    public static void main(String[] args) {
        System.out.println("=== HELP DESK (PostgreSQL + JDBC) ===");

        TicketRepository ticketRepository = new JdbcTicketRepository();
        UserRepository userRepository = new JdbcUserRepository();
        CommentRepository commentRepository = new JdbcCommentRepository();
        CategoryRepository categoryRepository = new JdbcCategoryRepository();

        TicketService ticketService = new TicketService(
                ticketRepository, userRepository, commentRepository
        );

        // 1. Подключение к PostgreSQL.
        try (Connection connection = DatabaseConfig.getConnection()) {
            System.out.println("Подключение к PostgreSQL успешно");
            System.out.println("AutoCommit = " + connection.getAutoCommit());
        } catch (SQLException e) {
            System.out.println("Не удалось подключиться к БД: " + e.getMessage());
            return;
        }

        // 2. Поиск клиента по email.
        User customer = userRepository.findByEmail(CUSTOMER_EMAIL)
                .orElseThrow(() -> new IllegalStateException(
                        "Клиент не найден: " + CUSTOMER_EMAIL
                ));
        System.out.println("\nКлиент: " + customer.getName()
                + " (" + customer.getRole() + ")");

        // 3. Список категорий.
        System.out.println("\nКатегории обращений:");
        List<Category> categories =
                categoryRepository.findAllCategories();
        for (Category category : categories) {
            System.out.println("  " + category);
        }
        Long categoryId = categories.stream()
                .filter(c -> c.getName().equals("Сеть"))
                .map(Category::getId)
                .findFirst()
                .orElse(null);

        // 4. Создание новой заявки.
        Ticket ticket = ticketService.createTicket(
                "Не работает Wi-Fi в офисе",
                "Ноутбук подключается к сети, но Интернет недоступен",
                TicketPriority.HIGH,
                customer.getId(),
                categoryId
        );

        // 5. Идентификатор, сгенерированный PostgreSQL.
        System.out.println("\nСоздана заявка, id из PostgreSQL = "
                + ticket.getId());

        // 6. Чтение заявки через findById.
        Ticket loaded = ticketService.findTicket(ticket.getId())
                .orElseThrow();
        System.out.println("findById: " + loaded);

        // 7. Назначение сотрудника поддержки.
        User agent = userRepository.findByEmail(AGENT_EMAIL)
                .orElseThrow(() -> new IllegalStateException(
                        "Сотрудник не найден: " + AGENT_EMAIL
                ));
        ticketService.assignTicket(ticket.getId(), agent.getId());
        System.out.println("\nЗаявка назначена: " + agent.getName()
                + " -> статус IN_PROGRESS");

        // 8. Клиентский комментарий.
        TicketComment clientComment = ticketService.addComment(
                ticket.getId(),
                customer.getId(),
                "Проблема сохраняется после перезагрузки роутера",
                false
        );
        System.out.println("Клиентский комментарий добавлен: "
                + clientComment.getText());

        // 9. Смена статуса IN_PROGRESS -> RESOLVED.
        ticketService.changeStatus(
                ticket.getId(), TicketStatus.RESOLVED, agent.getId()
        );
        System.out.println("Статус изменён на RESOLVED");

        // 10. Внутренний комментарий сотрудника.
        ticketService.addComment(
                ticket.getId(),
                agent.getId(),
                "Заменил настройки DNS, интернет восстановлен",
                true
        );
        System.out.println("Внутренний комментарий сотрудника добавлен");

        // 11. Смена статуса RESOLVED -> CLOSED.
        ticketService.changeStatus(
                ticket.getId(), TicketStatus.CLOSED, agent.getId()
        );
        System.out.println("Статус изменён на CLOSED");

        // 12. История статусов заявки.
        System.out.println("\nИстория статусов заявки #" + ticket.getId() + ":");
        List<TicketStatusHistory> history =
                ticketService.getHistory(ticket.getId());
        for (TicketStatusHistory record : history) {
            System.out.println("  " + record);
        }

        // Комментарии заявки.
        System.out.println("\nКомментарии заявки #" + ticket.getId() + ":");
        for (TicketComment comment : ticketService.getComments(ticket.getId())) {
            System.out.println("  " + comment);
        }

        // 13. JOIN-запрос: заявка + клиент + исполнитель + категория.
        System.out.println("\nJOIN-запрос по заявке:");
        TicketDetails details = ticketService.findDetails(ticket.getId())
                .orElseThrow();
        System.out.println("  " + details);

        // 14. Данные из БД (доказывает сохранение между запусками).
        System.out.println("\nВсе заявки из PostgreSQL:");
        for (Ticket stored : ticketService.findAllTickets()) {
            System.out.println("  " + stored);
        }

        // Дополнительно (задание п.44): заявки конкретного клиента.
        System.out.println("\nЗаявки клиента #" + customer.getId()
                + " (findByCustomerId):");
        for (Ticket clientTicket
                : ticketService.findByCustomerId(customer.getId())) {
            System.out.println("  " + clientTicket);
        }

        System.out.println("\n=== ГОТОВО ===");
    }
}
