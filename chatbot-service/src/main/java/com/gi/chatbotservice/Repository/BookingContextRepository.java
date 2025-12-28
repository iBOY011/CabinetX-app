package com.gi.chatbotservice.Repository;

import com.gi.chatbotservice.Model.Entity.BookingContext;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BookingContextRepository extends JpaRepository<BookingContext, Long> {

    BookingContext save(BookingContext ctx);

    Optional<BookingContext> findBySessionId(Long sessionId);
}
