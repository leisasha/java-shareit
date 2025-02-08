package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dal.BookingRepository;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dal.CommentRepository;
import ru.practicum.shareit.item.dal.ItemRepository;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.request.dal.ItemRequestRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dal.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class ItemServiceImplTest {
    private ItemService itemService;
    private ItemRepository itemRepository;
    private UserRepository userRepository;
    private BookingRepository bookingRepository;
    private CommentRepository commentRepository;
    private ItemRequestRepository itemRequestRepository;

    private User user;
    private Item item;
    private ItemDto itemDto;
    private ItemRequest itemRequest;

    @BeforeEach
    void setUp() {
        itemRepository = mock(ItemRepository.class);
        userRepository = mock(UserRepository.class);
        bookingRepository = mock(BookingRepository.class);
        commentRepository = mock(CommentRepository.class);
        itemRequestRepository = mock(ItemRequestRepository.class);
        itemService = new ItemServiceImpl(itemRepository, userRepository, bookingRepository, commentRepository, itemRequestRepository);

        user = new User(1L, "User", "test@google.com");
        itemRequest = new ItemRequest(1L, "ItemRequest description", user.getId(), LocalDateTime.now());
        item = new Item(1L, "Item", "item description", true, user.getId(), itemRequest);
        itemDto = ItemMapper.toItemDto(item);
        itemDto.setRequestId(itemRequest.getId());
    }

    @Test
    void createItemShouldSaveItem() {
        when(userRepository.existsById(user.getId())).thenReturn(true);
        when(itemRequestRepository.findById(itemDto.getRequestId())).thenReturn(Optional.of(itemRequest));
        when(itemRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        ItemDto result = itemService.createItem(itemDto, user.getId());

        assertNotNull(result);
        assertEquals(itemDto.getName(), result.getName());
        verify(itemRepository, times(1)).save(any());
    }

    @Test
    void createItemShouldThrowNotFoundExceptionWhenUserDoesNotExist() {
        when(userRepository.existsById(anyLong())).thenReturn(false);
        assertThrows(NotFoundException.class, () -> itemService.createItem(itemDto, 999L));
    }

    @Test
    void updateItemShouldUpdateExistingItem() {
        when(userRepository.existsById(user.getId())).thenReturn(true);
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(itemRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        ItemDto updateDto = new ItemDto(item.getId(), "new item name", "new item description", true);
        ItemDto result = itemService.updateItem(updateDto, item.getId(), user.getId());

        assertNotNull(result);
        assertEquals("new item name", result.getName());
        verify(itemRepository, times(1)).save(any());
    }

    @Test
    void updateItemShouldThrowNotFoundExceptionWhenItemNotFound() {
        when(userRepository.existsById(user.getId())).thenReturn(true);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        item.setOwner(user.getId() + 1);

        assertThrows(NotFoundException.class, () -> itemService.updateItem(itemDto, 999L, user.getId()));
    }

    @Test
    void updateItemShouldThrowNotFoundExceptionWhenOwnerNotSame() {
        when(userRepository.existsById(user.getId())).thenReturn(true);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.updateItem(itemDto, 999L, user.getId()));
    }

    @Test
    void getItemByIdShouldReturnItem() {
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(commentRepository.findByItemIdOrderByCreatedDesc(item.getId())).thenReturn(Collections.emptyList());

        ItemDto result = itemService.getItemById(item.getId());

        assertNotNull(result);
        assertEquals(item.getId(), result.getId());
        verify(itemRepository, times(1)).findById(item.getId());
    }

    @Test
    void getItemByIdShouldThrowNotFoundExceptionWhenItemNotFound() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.getItemById(999L));
    }

    @Test
    void getItemsShouldReturnListOfItems() {
        when(userRepository.existsById(user.getId())).thenReturn(true);
        when(itemRepository.findByOwnerId(user.getId())).thenReturn(Collections.singletonList(item));
        when(bookingRepository.findLastBookings(any())).thenReturn(Collections.emptyList());
        when(bookingRepository.findNextBookings(any())).thenReturn(Collections.emptyList());
        when(commentRepository.findByItemIdOrderByCreatedDesc(anyLong())).thenReturn(Collections.emptyList());

        List<ItemDto> result = itemService.getItems(user.getId());

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(itemRepository, times(1)).findByOwnerId(user.getId());
    }

    @Test
    void searchItemsShouldReturnMatchingItems() {
        when(itemRepository.search(anyString())).thenReturn(Collections.singletonList(item));

        Collection<ItemDto> result = itemService.searchItems("Item");

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(itemRepository, times(1)).search("Item");
    }

    @Test
    void addCommentShouldAddComment() {
        when(userRepository.existsById(user.getId())).thenReturn(true);
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(bookingRepository.existsByItemIdAndBookerIdAndStatusAndEndBefore(eq(item.getId()), eq(user.getId()), eq(Status.APPROVED), any()))
                .thenReturn(true);
        when(commentRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        CommentDto commentDto = new CommentDto(null, "description", "authorName", LocalDateTime.now());
        CommentDto result = itemService.addComment(user.getId(), item.getId(), commentDto);

        assertNotNull(result);
        assertEquals("description", result.getText());
        verify(commentRepository, times(1)).save(any());
    }

    @Test
    void addCommentShouldThrowValidationExceptionWhenNoCompletedBooking() {
        when(userRepository.existsById(user.getId())).thenReturn(true);
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(bookingRepository.existsByItemIdAndBookerIdAndStatusAndEndBefore(eq(item.getId()), eq(user.getId()), eq(Status.APPROVED), any()))
                .thenReturn(false);

        CommentDto commentDto = new CommentDto(null, "description", "authorName", LocalDateTime.now());

        assertThrows(ValidationException.class, () -> itemService.addComment(user.getId(), item.getId(), commentDto));
    }
}
