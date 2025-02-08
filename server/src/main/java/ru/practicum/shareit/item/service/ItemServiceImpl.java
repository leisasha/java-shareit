package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dal.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dal.CommentRepository;
import ru.practicum.shareit.item.dal.ItemRepository;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dal.ItemRequestRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dal.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemRequestRepository itemRequestRepository;

    public ItemDto createItem(ItemDto itemDto, long userId) {
        validateUserExists(userId);

        Item item = ItemMapper.toItem(itemDto, 0, userId);
        item.setOwner(userId);

        if (itemDto.getRequestId() != null) {
            ItemRequest request = itemRequestRepository.findById(itemDto.getRequestId())
                    .orElseThrow(() -> new NotFoundException("Запрос не найден"));
            item.setRequest(request);
        }

        item = itemRepository.save(item);
        return ItemMapper.toItemDto(item);
    }

    public ItemDto updateItem(ItemDto itemDto, long itemId, long userId) {
        validateUserExists(userId);

        Item existingItem = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item не найден"));

        if (existingItem.getOwner() != userId) {
            throw new NotFoundException("Редактировать вещь может только её владелец");
        }

        ItemMapper.updateItemFields(existingItem, itemDto);
        return ItemMapper.toItemDto(itemRepository.save(existingItem));
    }

    public ItemDto getItemById(long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item не найден с ID: " + itemId));

        List<CommentDto> comments = commentRepository.findByItemIdOrderByCreatedDesc(itemId)
                .stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());

        ItemDto itemDto = ItemMapper.toItemDto(item);
        itemDto.setComments(comments);

        return itemDto;
    }

    public List<ItemDto> getItems(long ownerId) {
        validateUserExists(ownerId);

        List<Item> items = itemRepository.findByOwnerId(ownerId);

        if (items.isEmpty()) {
            return Collections.emptyList();
        }

        List<Booking> lastBookingsList = bookingRepository.findLastBookings(items);
        List<Booking> nextBookingsList = bookingRepository.findNextBookings(items);

        Map<Long, Booking> lastBookings = lastBookingsList.stream()
                .collect(Collectors.toMap(b -> b.getItem().getId(), b -> b, (b1, b2) -> b1));

        Map<Long, Booking> nextBookings = nextBookingsList.stream()
                .collect(Collectors.toMap(b -> b.getItem().getId(), b -> b, (b1, b2) -> b1));

        return items.stream()
                .map(item -> {
                    ItemDto itemDto = ItemMapper.toItemDto(item);

                    itemDto.setLastBooking(
                            Optional.ofNullable(lastBookings.get(item.getId()))
                                    .map(Booking::getEnd)
                                    .orElse(null)
                    );

                    itemDto.setNextBooking(
                            Optional.ofNullable(nextBookings.get(item.getId()))
                                    .map(Booking::getStart)
                                    .orElse(null)
                    );

                    List<CommentDto> comments = commentRepository.findByItemIdOrderByCreatedDesc(item.getId())
                            .stream()
                            .map(CommentMapper::toCommentDto)
                            .collect(Collectors.toList());
                    itemDto.setComments(comments);

                    return itemDto;
                })
                .collect(Collectors.toList());
    }

    public Collection<ItemDto> searchItems(String text) {
        if (text == null || text.isEmpty()) {
            return Collections.emptyList();
        }

        return itemRepository.search(text).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    public CommentDto addComment(long userId, long itemId, CommentDto commentDto) {
        validateUserExists(userId);

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item не найден"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User не найден"));

        boolean hasCompletedBooking = bookingRepository.existsByItemIdAndBookerIdAndStatusAndEndBefore(
                itemId, userId, Status.APPROVED, LocalDateTime.now());

        if (!hasCompletedBooking) {
            throw new ValidationException("Пользователь не может оставить комментарий без завершенного бронирования");
        }

        Comment comment = new Comment();
        comment.setText(commentDto.getText());
        comment.setItem(item);
        comment.setAuthor(user);
        comment.setCreated(LocalDateTime.now());

        Comment savedComment = commentRepository.save(comment);

        return CommentMapper.toCommentDto(savedComment);
    }

    private void validateUserExists(long userId) {
        if (userId == 0) {
            throw new ValidationException("userId не должно быть пустым");
        }

        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь с ID " + userId + " не найден");
        }
    }
}
