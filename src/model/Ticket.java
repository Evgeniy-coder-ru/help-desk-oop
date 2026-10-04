package model;

import java.time.LocalDateTime;


public class Ticket {

    private long id;
    private String title;
    private String description;
    private TicketStatus status;
    private TicketPriority priority;
    private LocalDateTime createdAt;


    public Ticket(
            long id,
            String title,
            String description,
            TicketPriority priority
    ) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException(
                    "Название заявки не может быть пустым"
            );
        }

        this.id = id;
        this.title = title;
        this.description = description;
        this.status = TicketStatus.NEW;
        this.priority = priority;
        this.createdAt = LocalDateTime.now();
    }

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public TicketStatus getStatus() {
        return status;
    }

    public TicketPriority getPriority() {
        return priority;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }


    public void startProcessing() {
        if (status != TicketStatus.NEW &&
                status != TicketStatus.REOPENED) {

            System.out.println(
                    "В работу можно взять только новую или переоткрытую заявку"
            );
            return;
        }

        status = TicketStatus.IN_PROGRESS;
    }


    public void resolve() {
        if (status != TicketStatus.IN_PROGRESS) {
            System.out.println(
                    "Ошибка: решить можно только заявку в работе"
            );
            return;
        }

        status = TicketStatus.RESOLVED;
    }


    public void close() {
        if (status != TicketStatus.RESOLVED) {
            System.out.println(
                    "Ошибка: закрыть можно только решённую заявку"
            );
            return;
        }

        status = TicketStatus.CLOSED;
    }


    public void reopen() {
        if (status != TicketStatus.CLOSED) {
            System.out.println(
                    "Ошибка: переоткрыть можно только закрытую заявку"
            );
            return;
        }

        status = TicketStatus.REOPENED;
    }


    public void cancel() {
        if (status == TicketStatus.CLOSED) {
            System.out.println(
                    "Ошибка: закрытую заявку нельзя отменить"
            );
            return;
        }

        status = TicketStatus.CANCELLED;
    }
}