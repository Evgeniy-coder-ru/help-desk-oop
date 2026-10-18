package repository;

import model.Ticket;
import model.TicketDetails;
import model.TicketStatus;
import model.TicketStatusHistory;

import java.util.List;
import java.util.Optional;

/**
 * Контракт доступа к заявкам.
 */
public interface TicketRepository {

    Ticket save(Ticket ticket);

    Optional<Ticket> findById(long id);

    List<Ticket> findAll();

    List<Ticket> findByStatus(TicketStatus status);

    List<Ticket> findByCustomerId(long customerId);

    void update(Ticket ticket);

    boolean deleteById(long id);

    List<TicketDetails> findAllDetails();

    Optional<TicketDetails> findDetailsById(long id);

    void changeStatus(long ticketId, TicketStatus newStatus, long changedByUserId);

    void assign(long ticketId, long assigneeId);

    List<TicketStatusHistory> findHistoryByTicketId(long ticketId);
}
