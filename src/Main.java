import model.Customer;
import model.SupportAgent;
import model.Ticket;
import model.TicketPriority;
import model.TicketStatus;
import model.User;
import repository.TicketRepository;
import service.ConsoleNotificationService;
import service.NotificationService;
import service.TicketService;

import java.util.ArrayList;
import java.util.List;

/**
 * Точка входа в программу.
 * Выполняет сценарий Help Desk и демонстрирует
 * возможности самостоятельной работы.
 */
public class Main {

    public static void main(String[] args) {

        System.out.println("=== HELP DESK ===");

        // Создаём клиента Анну Петрову.
        Customer customer = new Customer(
                1,
                "Анна Петрова",
                "anna@mail.ru"
        );

        // Создаём сп   ециалиста Сергея Иванова.
        SupportAgent supportAgent = new SupportAgent(
                2,
                "Сергей Иванов",
                "sergey@helpdesk.ru"
        );

        System.out.println("Клиент: " + customer.getName());

        // Создаём заявку с приоритетом HIGH.
        Ticket ticket = new Ticket(
                1,
                "Не работает Wi-Fi",
                "После подключения к сети Интернет отсутствует",
                TicketPriority.HIGH
        );

        System.out.println(
                "Заявка #" + ticket.getId()
                        + ": " + ticket.getTitle()
                        + " | " + ticket.getStatus()
        );

        // Показываем приоритет и время создания заявки.
        System.out.println("Приоритет: " + ticket.getPriority());
        System.out.println("Создана: " + ticket.getCreatedAt());

        // Создаём сервис уведомлений.
        NotificationService notificationService =
                new ConsoleNotificationService();

        // Создаём сервис работы с заявками.
        TicketService ticketService =
                new TicketService(notificationService);

        // NEW -> IN_PROGRESS.
        ticketService.startTicket(ticket);
        System.out.println("Статус: " + ticket.getStatus());

        // IN_PROGRESS -> RESOLVED.
        ticketService.resolveTicket(ticket);
        System.out.println("Статус: " + ticket.getStatus());

        // RESOLVED -> CLOSED.
        ticketService.closeTicket(ticket);
        System.out.println("Статус: " + ticket.getStatus());
        // CLOSED -> REOPENED.
        ticketService.reopenTicket(ticket);
        System.out.println("Статус: " + ticket.getStatus());

// REOPENED -> IN_PROGRESS.
        ticketService.startTicket(ticket);
        System.out.println("Статус: " + ticket.getStatus());

// IN_PROGRESS -> RESOLVED.
        ticketService.resolveTicket(ticket);
        System.out.println("Статус: " + ticket.getStatus());

// RESOLVED -> CLOSED.
        ticketService.closeTicket(ticket);
        System.out.println("Статус: " + ticket.getStatus());

        // Добавляем заявку в репозиторий.
        TicketRepository ticketRepository = new TicketRepository();
        ticketRepository.add(ticket);

        // Выводим полный список заявок.
        System.out.println("Список заявок:");

        for (Ticket storedTicket : ticketRepository.findAll()) {
            System.out.println(
                    "#" + storedTicket.getId()
                            + " " + storedTicket.getTitle()
                            + " | " + storedTicket.getStatus()
            );
        }
    }
}