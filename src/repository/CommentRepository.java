package repository;

import model.TicketComment;

import java.util.List;

/**
 * Контракт доступа к комментариям заявок.
 */
public interface CommentRepository {

    TicketComment add(TicketComment comment);

    List<TicketComment> findByTicketId(long ticketId);
}
