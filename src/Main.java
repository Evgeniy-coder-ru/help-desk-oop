

import model.Customer;
import model.SupportAgent;
import model.Ticket;
import repository.TicketRepository;
import service.ConsoleNotificationService;
import service.NotificationService;
import service.TicketService;


public class Main {

    public static void main(String[] args) {

        System.out.println("=== HELP DESK ===");


        Customer customer = new Customer(
                1,
                "Анна Петрова",
                "anna@mail.ru"
        );


        SupportAgent supportAgent = new SupportAgent(
                2,
                "Сергей Иванов",
                "sergey@helpdesk.ru"
        );


        System.out.println("Клиент: " + customer.getName());


        Ticket ticket = new Ticket(
                1,
                "Не работает Wi-Fi",
                "После подключения к сети Интернет отсутствует"
        );


        System.out.println(
                "Заявка #" + ticket.getId()
                        + ": " + ticket.getTitle()
                        + " | " + ticket.getStatus()
        );


        NotificationService notificationService =
                new ConsoleNotificationService();


        TicketService ticketService =
                new TicketService(notificationService);


        ticketService.startTicket(ticket);
        System.out.println("Статус: " + ticket.getStatus());


        ticketService.resolveTicket(ticket);
        System.out.println("Статус: " + ticket.getStatus());


        ticketService.closeTicket(ticket);
        System.out.println("Статус: " + ticket.getStatus());


        TicketRepository ticketRepository = new TicketRepository();
        ticketRepository.add(ticket);


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