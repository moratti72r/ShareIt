package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findAllByBookerIdOrderByStartDesc(long userId);

    List<Booking> findAllByBookerIdAndStatusEqualsOrderByStartDesc(long userId, StatusType statusType);

    List<Booking> findAllByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(long userId, LocalDateTime ldt1, LocalDateTime ldt2);

    List<Booking> findAllByBookerIdAndEndIsBeforeOrderByStartDesc(long userId, LocalDateTime ldt);

    List<Booking> findAllByBookerIdAndStartIsAfterOrderByStartDesc(long userId, LocalDateTime ldt);


    @Query(value = "SELECT b.id, b.start_date, b.end_date, b.item_id, b.booker_id, b.status " +
            "FROM bookings b LEFT JOIN items i ON i.id=b.item_id " +
            "WHERE i.owner_id=?1 ORDER BY b.start_date DESC", nativeQuery = true)
    List<Booking> findAllBookingsByItemOwner(long userId);

    @Query(value = "SELECT b.id, b.start_date, b.end_date, b.item_id, b.booker_id, b.status " +
            "FROM bookings b LEFT JOIN items i ON i.id=b.item_id " +
            "WHERE i.owner_id=?1 AND b.status='WAITING' ORDER BY b.start_date DESC", nativeQuery = true)
    List<Booking> findAllBookingsByItemOwnerWithStatusWaiting(long userId);

    @Query(value = "SELECT b.id, b.start_date, b.end_date, b.item_id, b.booker_id, b.status " +
            "FROM bookings b LEFT JOIN items i ON i.id=b.item_id " +
            "WHERE i.owner_id=?1 AND b.status='REJECTED' ORDER BY b.start_date DESC", nativeQuery = true)
    List<Booking> findAllBookingsByItemOwnerWithStatusCanceled(long userId);

    @Query(value = "SELECT b.id, b.start_date, b.end_date, b.item_id, b.booker_id, b.status " +
            "FROM bookings b LEFT JOIN items i ON i.id=b.item_id " +
            "WHERE i.owner_id=?1 AND b.start_date<CAST(CURRENT_TIMESTAMP AS TIMESTAMP WITHOUT TIME ZONE)" +
            "AND b.end_date>CAST(CURRENT_TIMESTAMP AS TIMESTAMP WITHOUT TIME ZONE) " +
            "ORDER BY b.start_date DESC", nativeQuery = true)
    List<Booking> findAllBookingsByItemOwnerCurrent(long userId);

    @Query(value = "SELECT b.id, b.start_date, b.end_date, b.item_id, b.booker_id, b.status " +
            "FROM bookings b LEFT JOIN items i ON i.id=b.item_id " +
            "WHERE i.owner_id=?1 AND b.end_date<CAST(CURRENT_TIMESTAMP AS TIMESTAMP WITHOUT TIME ZONE) ORDER BY b.start_date DESC", nativeQuery = true)
    List<Booking> findAllBookingsByItemOwnerPast(long userId);

    @Query(value = "SELECT b.id, b.start_date, b.end_date, b.item_id, b.booker_id, b.status " +
            "FROM bookings b LEFT JOIN items i ON i.id=b.item_id " +
            "WHERE i.owner_id=?1 AND b.start_date>CAST(CURRENT_TIMESTAMP AS TIMESTAMP WITHOUT TIME ZONE) ORDER BY b.start_date DESC", nativeQuery = true)
    List<Booking> findAllBookingsByItemOwnerFuture(long userId);


    @Query(value = "SELECT * FROM bookings b WHERE b.item_id=?1 " +
            "AND b.start_date<CAST(CURRENT_TIMESTAMP AS TIMESTAMP WITHOUT TIME ZONE) " +
            "AND b.status NOT IN ('REJECTED','CANCELED')" +
            " ORDER BY b.start_date DESC LIMIT 1", nativeQuery = true)
    Optional<Booking> findLastBookingByItemId(long id);

    @Query(value = "SELECT * FROM bookings b WHERE b.item_id=?1 " +
            "AND b.start_date>CAST(CURRENT_TIMESTAMP AS TIMESTAMP WITHOUT TIME ZONE) " +
            "AND b.status NOT IN ('REJECTED','CANCELED')" +
            "ORDER BY b.start_date LIMIT 1", nativeQuery = true)
    Optional<Booking> findNextBookingByItemId(long id);

    boolean existsByItemIdAndBookerIdAndStatusAndEndIsBefore(long itemId, long bookerId, StatusType st, LocalDateTime ldt);

}
