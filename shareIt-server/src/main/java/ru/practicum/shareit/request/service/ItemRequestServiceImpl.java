package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dal.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dal.ItemRequestRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final ItemRepository itemRepository;

    public ItemRequestDto createRequest(Long userId, ItemRequestDto requestDto) {
        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(requestDto, 0, userId, LocalDateTime.now());
        return ItemRequestMapper.toItemRequestDto(itemRequestRepository.save(itemRequest));
    }

    public List<ItemRequestDto> getUserRequests(long userId) {
        List<ItemRequest> itemRequests = itemRequestRepository.findByRequestorOrderByCreatedDesc(userId);
        return putItemDtoListInToItemRequestDtoList(itemRequests);
    }

    public List<ItemRequestDto> getAllRequests(long userId) {
        List<ItemRequest> itemRequests = itemRequestRepository.findAllByRequestorNotOrderByCreatedDesc(userId);
        return putItemDtoListInToItemRequestDtoList(itemRequests);
    }

    public ItemRequestDto getRequestById(long userId, long requestId) {
        ItemRequest itemRequest = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Request not found"));
        List<ItemDto> items = itemRepository.findByRequest(itemRequest)
                .stream()
                .map(ItemMapper::toItemDto)
                .toList();
        ItemRequestDto itemRequestDto = ItemRequestMapper.toItemRequestDto(itemRequest);
        itemRequestDto.setItems(items);
        return itemRequestDto;
    }

    private List<ItemRequestDto> putItemDtoListInToItemRequestDtoList(List<ItemRequest> itemRequests) {
        List<Long> requestIds = itemRequests.stream().map(ItemRequest::getId).toList();
        List<Item> items = itemRepository.findByRequestIdIn(requestIds);

        Map<Long, List<ItemDto>> itemsByRequest = items.stream()
                .collect(Collectors.groupingBy(item -> item.getRequest().getId(),
                        Collectors.mapping(ItemMapper::toItemDto, Collectors.toList())));

        return itemRequests.stream()
                .map(request -> {
                    ItemRequestDto dto = ItemRequestMapper.toItemRequestDto(request);
                    dto.setItems(itemsByRequest.getOrDefault(request.getId(), List.of()));
                    return dto;
                })
                .toList();
    }
}
