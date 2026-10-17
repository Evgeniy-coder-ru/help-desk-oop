package service;

import model.Ticket;
import model.TicketComment;
import model.TicketDetails;
import model.TicketPriority;
import model.TicketStatus;
import model.TicketStatusHistory;
import model.User;
import model.UserRole;
import repository.CommentRepository;
import repository.TicketRepository;
import repository.UserRepository;

import java.util.List;
import java.util.Optional;

/**
 * Бизнес-логика Help Desk. Сервис работает только с контрактами
 * репозиториев и не знает, что данные хранятся в PostgreSQL.
 */
public class TicketService {

    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;

    public TicketService(TicketRepository ticketRepository,
                         UserRepository userRepository,
                         CommentRepository commentRepository) {
        this.ticketRepository = ticketRepository;
        this.userRepository = userRepository;
        this.commentRepository = commentRepository;
    }

    public Ticket createTicket(String title,
                               String description,
                               TicketPriority priority,
                               long customerId,
                               Long categoryId) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Пустая тема заявки");
        }

        Ticket ticket = new Ticket();
        ticket.setTitle(title.trim());
        ticket.setDescription(description);
        ticket.setStatus(TicketStatus.NEW);
        ticket.setPriority(priority);
        ticket.setCustomerId(customerId);
        ticket.setCategoryId(categoryId);

        return ticketRepository.save(ticket);
    }

    public Optional<Ticket> findTicket(long ticketId) {
        return ticketRepository.findById(ticketId);
    }

    public List<Ticket> findAllTickets() {
        return ticketRepository.findAll();
    }

    public List<Ticket> findByStatus(TicketStatus status) {
        return ticketRepository.findByStatus(status);
    }

    public List<Ticket> findByCustomerId(long customerId) {
        return ticketRepository.findByCustomerId(customerId);
    }

    public Optional<TicketDetails> findDetails(long ticketId) {
        return ticketRepository.findDetailsById(ticketId);
    }

    public List<TicketDetails> findAllDetails() {
        return ticketRepository.findAllDetails();
    }

    public void assignTicket(long ticketId, long supportAgentId) {
        User agent = userRepository.findById(supportAgentId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Пользователь не найден: id=" + supportAgentId
                ));

        if (agent.getRole() != UserRole.SUPPORT_AGENT) {
            throw new IllegalStateException(
                    "Назначить заявку можно только пользователю с ролью "
                            + "SUPPORT_AGENT, а у пользователя роль "
                            + agent.getRole()
            );
        }

        ticketRepository.assign(ticketId, supportAgentId);
    }

    public void changeStatus(long ticketId,
                             TicketStatus newStatus,
                             long changedByUserId) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Заявка не найдена: id=" + ticketId
                ));

        validateTransition(ticket.getStatus(), newStatus);
        ticketRepository.changeStatus(ticketId, newStatus, changedByUserId);
    }

    public TicketComment addComment(long ticketId,
                                    long authorId,
                                    String text,
                                    boolean internal) {
        if (text == null || text.isBlank()) {
            throw new IllegalArgumentException("Пустой комментарий");
        }

        TicketComment comment =
                new TicketComment(ticketId, authorId, text.trim(), internal);
        return commentRepository.add(comment);
    }

    public List<TicketComment> getComments(long ticketId) {
        return commentRepository.findByTicketId(ticketId);
    }

    public List<TicketStatusHistory> getHistory(long ticketId) {
        return ticketRepository.findHistoryByTicketId(ticketId);
    }

    private void validateTransition(TicketStatus oldStatus,
                                    TicketStatus newStatus) {
        boolean allowed = switch (oldStatus) {
            case NEW -> newStatus == TicketStatus.IN_PROGRESS
                    || newStatus == TicketStatus.CANCELLED;
            case IN_PROGRESS -> newStatus == TicketStatus.RESOLVED
                    || newStatus == TicketStatus.CANCELLED;
            case RESOLVED -> newStatus == TicketStatus.CLOSED
                    || newStatus == TicketStatus.IN_PROGRESS;
            case CLOSED, CANCELLED, REOPENED -> false;
        };

        if (!allowed) {
            throw new IllegalStateException(
                    "Недопустимый переход: " + oldStatus + " -> " + newStatus
            );
        }
    }
}
