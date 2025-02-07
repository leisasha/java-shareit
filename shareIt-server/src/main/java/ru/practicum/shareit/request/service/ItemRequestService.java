package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto createRequest(Long userId, ItemRequestDto requestDto);

    List<ItemRequestDto> getUserRequests(long userId);

    List<ItemRequestDto> getAllRequests(long userId);

    ItemRequestDto getRequestById(long userId, long requestId);
}
