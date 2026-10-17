package model;

import java.time.LocalDateTime;

/**
 * Результат JOIN-запроса: заявка вместе с именами клиента,
 * исполнителя и названием категории.
 */
public class TicketDetails {

    private long id;
    private String title;
    private TicketStatus status;
    private TicketPriority priority;
    private String categoryName;
    private String customerName;
    private String assigneeName;
    private LocalDateTime createdAt;

    public TicketDetails(long id, String title, TicketStatus status,
                         TicketPriority priority, String categoryName,
                         String customerName, String assigneeName,
                         LocalDateTime createdAt) {
        this.id = id;
        this.title = title;
        this.status = status;
        this.priority = priority;
        this.categoryName = categoryName;
        this.customerName = customerName;
        this.assigneeName = assigneeName;
        this.createdAt = createdAt;
    }

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public TicketStatus getStatus() {
        return status;
    }

    public TicketPriority getPriority() {
        return priority;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getAssigneeName() {
        return assigneeName;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    @Override
    public String toString() {
        return "#" + id + " " + title
                + " | " + status
                + " | " + priority
                + " | категория: " + categoryName
                + " | клиент: " + customerName
                + " | исполнитель: "
                + (assigneeName == null ? "не назначен" : assigneeName);
    }
}
