package service;

import model.Ticket;
import model.TicketStatus;


public class TicketService {

    private final NotificationService notificationService;


    public TicketService(NotificationService notificationService) {
        this.notificationService = notificationService;
    }


    public void startTicket(Ticket ticket) {
        TicketStatus oldStatus = ticket.getStatus();

        ticket.startProcessing();

        if (oldStatus != ticket.getStatus()) {
            notificationService.send(
                    "Заявка №" + ticket.getId() + " принята в работу"
            );
        }
    }


    public void resolveTicket(Ticket ticket) {
        TicketStatus oldStatus = ticket.getStatus();

        ticket.resolve();

        if (oldStatus != ticket.getStatus()) {
            notificationService.send(
                    "По заявке №" + ticket.getId() + " найдено решение"
            );
        }
    }


    public void closeTicket(Ticket ticket) {
        TicketStatus oldStatus = ticket.getStatus();

        ticket.close();

        if (oldStatus != ticket.getStatus()) {
            notificationService.send(
                    "Заявка №" + ticket.getId() + " закрыта"
            );
        }
    }


    public void reopenTicket(Ticket ticket) {
        TicketStatus oldStatus = ticket.getStatus();

        ticket.reopen();

        if (oldStatus != ticket.getStatus()) {
            notificationService.send(
                    "Заявка №" + ticket.getId() + " переоткрыта"
            );
        }
    }
}