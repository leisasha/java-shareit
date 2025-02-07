package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dal.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dal.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dal.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;


    @Override
    public BookingDto createBooking(BookingDto bookingDto, Long userId) {
        User booker = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        Item item = itemRepository.findById(bookingDto.getItemId())
                .orElseThrow(() -> new NotFoundException("Предмет не найден"));

        if (!item.isAvailable()) {
            throw new ValidationException("Предмет недоступен для бронирования");
        }

        Booking booking = BookingMapper.toBooking(bookingDto, booker, item, Status.WAITING);
        Booking savedBooking = bookingRepository.save(booking);
        log.error("Booking создан в БД. bookingId = " + savedBooking.getId() + ", status = " + savedBooking.getStatus());
        log.error("start = " + savedBooking.getStart() + ", end = " + savedBooking.getEnd());
        log.error("itemId = " + savedBooking.getItem().getId());
        log.error("userId = " + savedBooking.getItem().getOwner());
        return BookingMapper.toBookingDto(savedBooking);
    }

    @Override
    public BookingDto updateBookingStatus(Long userId, Long bookingId, Boolean approved) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование не найдено"));

        if (booking.getItem().getOwner() != userId) {
            throw new ValidationException("Только владелец вещи может подтверждать или отклонять бронирование");
        }

        if (!booking.getStatus().equals(Status.WAITING)) {
            throw new ValidationException("Бронирование уже обработано");
        }

        booking.setStatus(approved ? Status.APPROVED : Status.REJECTED);
        log.error("Booking APPROVED = " + approved);
        log.error("bookingId = " + booking.getId() + ", status = " + booking.getStatus());
        log.error("start = " + booking.getStart() + ", end = " + booking.getEnd());
        log.error("itemId = " + booking.getItem().getId());
        log.error("userId = " + booking.getItem().getOwner());

        bookingRepository.save(booking);

        return BookingMapper.toBookingDto(booking);
    }

    @Override
    public BookingDto getBookingById(Long userId, Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование не найдено"));

        if (booking.getBooker().getId() != userId && booking.getItem().getOwner() != userId) {
            throw new NotFoundException("Доступ запрещен: Только автор бронирования или владелец вещи могут просматривать бронирование");
        }

        return BookingMapper.toBookingDto(booking);
    }

    @Override
    public List<BookingDto> getUserBookings(Long userId, String state) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь с ID " + userId + " не найден");
        }

        List<Booking> bookings;

        switch (State.fromString(state)) {
            case CURRENT:
                bookings = bookingRepository.findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(
                        userId, LocalDateTime.now(), LocalDateTime.now());
                break;
            case PAST:
                bookings = bookingRepository.findByBookerIdAndEndBeforeOrderByStartDesc(
                        userId, LocalDateTime.now());
                break;
            case FUTURE:
                bookings = bookingRepository.findByBookerIdAndStartAfterOrderByStartDesc(
                        userId, LocalDateTime.now());
                break;
            case WAITING:
                bookings = bookingRepository.findByBookerIdAndStatusOrderByStartDesc(
                        userId, Status.WAITING);
                break;
            case REJECTED:
                bookings = bookingRepository.findByBookerIdAndStatusOrderByStartDesc(
                        userId, Status.REJECTED);
                break;
            default:
                bookings = bookingRepository.findByBookerIdOrderByStartDesc(userId);
                break;
        }

        return bookings.stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingDto> getOwnerBookings(Long ownerId, String state) {
        if (!userRepository.existsById(ownerId)) {
            throw new NotFoundException("Пользователь с ID " + ownerId + " не найден");
        }

        List<Booking> bookings;

        switch (State.fromString(state)) {
            case CURRENT:
                bookings = bookingRepository.findByItemOwnerAndStartBeforeAndEndAfterOrderByStartDesc(
                        ownerId, LocalDateTime.now(), LocalDateTime.now());
                break;
            case PAST:
                bookings = bookingRepository.findByItemOwnerAndEndBeforeOrderByStartDesc(
                        ownerId, LocalDateTime.now());
                break;
            case FUTURE:
                bookings = bookingRepository.findByItemOwnerAndStartAfterOrderByStartDesc(
                        ownerId, LocalDateTime.now());
                break;
            case WAITING:
                bookings = bookingRepository.findByItemOwnerAndStatusOrderByStartDesc(
                        ownerId, Status.WAITING);
                break;
            case REJECTED:
                bookings = bookingRepository.findByItemOwnerAndStatusOrderByStartDesc(
                        ownerId, Status.REJECTED);
                break;
            default:
                bookings = bookingRepository.findByItemOwnerOrderByStartDesc(ownerId);
                break;
        }

        return bookings.stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }
}
