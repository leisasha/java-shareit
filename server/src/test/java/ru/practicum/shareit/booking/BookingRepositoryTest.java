package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.booking.dal.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.dal.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dal.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingRepositoryTest {
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    private User user;
    private Item item;
    private Booking pastBooking;
    private Booking futureBooking;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setName("User");
        user.setEmail("test@yandex.ru");
        user = userRepository.save(user);

        item = new Item();
        item.setName("Item");
        item.setDescription("description");
        item.setAvailable(true);
        item.setOwner(user.getId());
        item = itemRepository.save(item);

        pastBooking = new Booking();
        pastBooking.setItem(item);
        pastBooking.setBooker(user);
        pastBooking.setStart(LocalDateTime.now().minusDays(10));
        pastBooking.setEnd(LocalDateTime.now().minusDays(5));
        pastBooking.setStatus(Status.APPROVED);
        pastBooking = bookingRepository.save(pastBooking);

        futureBooking = new Booking();
        futureBooking.setItem(item);
        futureBooking.setBooker(user);
        futureBooking.setStart(LocalDateTime.now().plusDays(5));
        futureBooking.setEnd(LocalDateTime.now().plusDays(10));
        futureBooking.setStatus(Status.APPROVED);
        futureBooking = bookingRepository.save(futureBooking);
    }

    @Test
    void findLastBookings_ShouldReturnPastBookings() {
        List<Booking> lastBookings = bookingRepository.findLastBookings(List.of(item));
        assertEquals(1, lastBookings.size());
        assertEquals(pastBooking.getId(), lastBookings.get(0).getId());
    }

    @Test
    void findNextBookings_ShouldReturnFutureBookings() {
        List<Booking> nextBookings = bookingRepository.findNextBookings(List.of(item));
        assertEquals(1, nextBookings.size());
        assertEquals(futureBooking.getId(), nextBookings.get(0).getId());
    }

    @Test
    void findLastBookings_ShouldReturnEmptyList_WhenNoPastBookings() {
        bookingRepository.delete(pastBooking);
        List<Booking> lastBookings = bookingRepository.findLastBookings(List.of(item));
        assertTrue(lastBookings.isEmpty());
    }

    @Test
    void findNextBookings_ShouldReturnEmptyList_WhenNoFutureBookings() {
        bookingRepository.delete(futureBooking);
        List<Booking> nextBookings = bookingRepository.findNextBookings(List.of(item));
        assertTrue(nextBookings.isEmpty());
    }

    @AfterEach
    void tearDown() {
        bookingRepository.deleteAll();
        itemRepository.deleteAll();
        userRepository.deleteAll();
    }
}
