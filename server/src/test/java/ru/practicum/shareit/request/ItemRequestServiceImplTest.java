package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dal.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dal.ItemRequestRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class ItemRequestServiceImplTest {
    private ItemRequestService itemRequestService;
    private ItemRequestRepository itemRequestRepository;
    private ItemRepository itemRepository;

    private ItemRequest itemRequest;
    private ItemRequestDto itemRequestDto;
    private Item item;
    private ItemDto itemDto;

    @BeforeEach
    void setUp() {
        itemRequestRepository = mock(ItemRequestRepository.class);
        itemRepository = mock(ItemRepository.class);
        itemRequestService = new ItemRequestServiceImpl(itemRequestRepository, itemRepository);

        itemRequest = new ItemRequest(1L, "itemRequest description", 1L, LocalDateTime.now());
        itemRequestDto = ItemRequestMapper.toItemRequestDto(itemRequest);
        item = new Item(1L, "Item", "item description", true, 2L, itemRequest);
        itemDto = new ItemDto(1L, "itemDtoName", "", true);
    }

    @Test
    void createRequestShouldSaveRequest() {
        when(itemRequestRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        ItemRequestDto result = itemRequestService.createRequest(1L, itemRequestDto);

        assertNotNull(result);
        assertEquals(itemRequestDto.getDescription(), result.getDescription());
        verify(itemRequestRepository, times(1)).save(any());
    }

    @Test
    void getUserRequestsShouldReturnRequests() {
        when(itemRequestRepository.findByRequestorOrderByCreatedDesc(anyLong())).thenReturn(Collections.singletonList(itemRequest));
        when(itemRepository.findByRequestIdIn(anyList())).thenReturn(Collections.singletonList(item));

        List<ItemRequestDto> result = itemRequestService.getUserRequests(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(itemRequest.getDescription(), result.get(0).getDescription());
        assertEquals(1, result.get(0).getItems().size());
        verify(itemRequestRepository, times(1)).findByRequestorOrderByCreatedDesc(anyLong());
    }

    @Test
    void getAllRequestsShouldReturnRequests() {
        when(itemRequestRepository.findAllByRequestorNotOrderByCreatedDesc(anyLong())).thenReturn(Collections.singletonList(itemRequest));
        when(itemRepository.findByRequestIdIn(anyList())).thenReturn(Collections.singletonList(item));

        List<ItemRequestDto> result = itemRequestService.getAllRequests(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(itemRequest.getDescription(), result.get(0).getDescription());
        assertEquals(1, result.get(0).getItems().size());
        verify(itemRequestRepository, times(1)).findAllByRequestorNotOrderByCreatedDesc(anyLong());
    }

    @Test
    void getRequestByIdShouldReturnRequest() {
        when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.of(itemRequest));
        when(itemRepository.findByRequest(any())).thenReturn(Collections.singletonList(item));

        ItemRequestDto result = itemRequestService.getRequestById(1L, 1L);

        assertNotNull(result);
        assertEquals(itemRequest.getDescription(), result.getDescription());
        assertEquals(1, result.getItems().size());
        verify(itemRequestRepository, times(1)).findById(anyLong());
    }

    @Test
    void getRequestByIdShouldThrowNotFoundExceptionWhenRequestNotFound() {
        when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemRequestService.getRequestById(1L, 999L));
        verify(itemRequestRepository, times(1)).findById(anyLong());
    }
}
