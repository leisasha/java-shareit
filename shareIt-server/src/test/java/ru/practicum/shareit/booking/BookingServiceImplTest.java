package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dal.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dal.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dal.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class BookingServiceImplTest {
    private BookingService bookingService;
    private BookingRepository bookingRepository;
    private UserRepository userRepository;
    private ItemRepository itemRepository;

    private User user;
    private Item item;
    private Booking booking;
    private BookingDto bookingDto;

    @BeforeEach
    void setUp() {
        bookingRepository = mock(BookingRepository.class);
        userRepository = mock(UserRepository.class);
        itemRepository = mock(ItemRepository.class);
        bookingService = new BookingServiceImpl(bookingRepository, userRepository, itemRepository);

        user = new User(1L, "User", "user@yandex.ru");
        item = new Item(1L, "Item", "description", true, user.getId(), null);
        booking = new Booking(1L, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), item, user, Status.WAITING);
        bookingDto = BookingMapper.toBookingDto(booking);
        bookingDto.setItemId(item.getId());
    }

    @Test
    void createBookingShouldSaveBooking() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(bookingRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        BookingDto result = bookingService.createBooking(bookingDto, user.getId());

        assertNotNull(result);
        assertEquals(bookingDto.getId(), result.getId());
        assertEquals(bookingDto.getItem(), result.getItem());
        verify(bookingRepository, times(1)).save(any());
    }

    @Test
    void createBookingShouldThrowNotFoundExceptionWhenUserNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookingService.createBooking(bookingDto, 999L));
    }

    @Test
    void createBookingShouldThrowNotFoundExceptionWhenItemNotFound() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookingService.createBooking(bookingDto, user.getId()));
    }

    @Test
    void createBookingShouldThrowValidationExceptionWhenItemNotAvailable() {
        item.setAvailable(false);
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));

        assertThrows(ValidationException.class, () -> bookingService.createBooking(bookingDto, user.getId()));
    }

    @Test
    void updateBookingStatusShouldUpdateStatus() {
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));

        BookingDto result = bookingService.updateBookingStatus(user.getId(), booking.getId(), true);

        assertEquals(Status.APPROVED, booking.getStatus());
        verify(bookingRepository, times(1)).save(any());
    }

    @Test
    void updateBookingStatusShouldThrowNotFoundExceptionWhenBookingNotFound() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookingService.updateBookingStatus(user.getId(), 999L, true));
    }

    @Test
    void updateBookingStatusShouldThrowValidationExceptionWhenUserNotOwner() {
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));

        assertThrows(ValidationException.class, () -> bookingService.updateBookingStatus(999L, booking.getId(), true));
    }

    @Test
    void getBookingByIdShouldReturnBooking() {
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));

        BookingDto result = bookingService.getBookingById(user.getId(), booking.getId());

        assertNotNull(result);
        assertEquals(bookingDto.getId(), result.getId());
        assertEquals(bookingDto.getItem(), result.getItem());
        verify(bookingRepository, times(1)).findById(booking.getId());
    }

    @Test
    void getBookingByIdShouldThrowNotFoundExceptionWhenBookingNotFound() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookingService.getBookingById(user.getId(), 999L));
    }

    @Test
    void getUserBookingsAllShouldReturnBookings() {
        when(userRepository.existsById(user.getId())).thenReturn(true);
        when(bookingRepository.findByBookerIdOrderByStartDesc(user.getId())).thenReturn(List.of(booking));

        List<BookingDto> result = bookingService.getUserBookings(user.getId(), "ALL");

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(bookingRepository, times(1)).findByBookerIdOrderByStartDesc(user.getId());
    }

    @Test
    void getUserBookingsREJECTEDShouldReturnBookings() {
        when(userRepository.existsById(user.getId())).thenReturn(true);
        when(bookingRepository.findByBookerIdOrderByStartDesc(user.getId())).thenReturn(List.of(booking));

        List<BookingDto> result = bookingService.getUserBookings(user.getId(), "REJECTED");

        assertNotNull(result);
        assertEquals(0, result.size());
        verify(bookingRepository, times(1)).findByBookerIdAndStatusOrderByStartDesc(user.getId(), Status.REJECTED);
    }

    @Test
    void getUserBookingsShouldThrowNotFoundExceptionWhenUserNotFound() {
        when(userRepository.existsById(anyLong())).thenReturn(false);

        assertThrows(NotFoundException.class, () -> bookingService.getUserBookings(999L, "ALL"));
    }

    @Test
    void getOwnerBookingsShouldReturnBookings() {
        when(userRepository.existsById(user.getId())).thenReturn(true);
        when(bookingRepository.findByItemOwnerOrderByStartDesc(user.getId())).thenReturn(List.of(booking));

        List<BookingDto> result = bookingService.getOwnerBookings(user.getId(), "ALL");

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(bookingRepository, times(1)).findByItemOwnerOrderByStartDesc(user.getId());
    }

    @Test
    void getOwnerBookingsShouldThrowNotFoundExceptionWhenUserNotFound() {
        when(userRepository.existsById(anyLong())).thenReturn(false);

        assertThrows(NotFoundException.class, () -> bookingService.getOwnerBookings(999L, "ALL"));
    }
}
