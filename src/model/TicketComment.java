package model;

import java.time.LocalDateTime;

/**
 * Комментарий к заявке.
 */
public class TicketComment {

    private Long id;
    private long ticketId;
    private long authorId;
    private String text;
    private boolean internal;
    private LocalDateTime createdAt;

    private String authorName;
    private UserRole authorRole;

    public TicketComment() {
    }

    public TicketComment(long ticketId, long authorId, String text,
                         boolean internal) {
        this.ticketId = ticketId;
        this.authorId = authorId;
        this.text = text;
        this.internal = internal;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public long getTicketId() {
        return ticketId;
    }

    public void setTicketId(long ticketId) {
        this.ticketId = ticketId;
    }

    public long getAuthorId() {
        return authorId;
    }

    public void setAuthorId(long authorId) {
        this.authorId = authorId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isInternal() {
        return internal;
    }

    public void setInternal(boolean internal) {
        this.internal = internal;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public UserRole getAuthorRole() {
        return authorRole;
    }

    public void setAuthorRole(UserRole authorRole) {
        this.authorRole = authorRole;
    }

    @Override
    public String toString() {
        String type = internal ? "внутренний" : "клиентский";
        return "[" + type + "] " + authorName + ": " + text;
    }
}
