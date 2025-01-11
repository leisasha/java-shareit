package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dal.ItemStorage;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemStorage itemStorage;
    private final UserService userService;

    public ItemDto createItem(ItemDto itemDto, long userId) {
        validationDataEmpty(itemDto, userId);

        if (itemDto.getName().isBlank()) {
            throw new ValidationException("Наименование не должено быть пустым");
        }
        if (itemDto.getDescription().isBlank()) {
            throw new ValidationException("Описание не должено быть пустым");
        }

        UserDto userDto = userService.getUserById(userId);

        return ItemMapper.toItemDto(itemStorage.create(itemDto, userId));
    }

    public ItemDto updateItem(ItemDto itemDto, long itemId, long userId) {
        validationDataEmpty(itemDto, userId);
        UserDto userDto = userService.getUserById(userId);

        Item updatedItem = itemStorage.getItemById(itemId)
                .orElseThrow(() -> new NotFoundException("Item не найден"));

        if (updatedItem.getOwner() != userId) {
            throw new NotFoundException("Редактировать вещь может только её владелец");
        }

        if (itemDto.getAvailable() == null) {
            itemDto.setAvailable(false);
        }

        return ItemMapper.toItemDto(itemStorage.update(updatedItem, itemDto, userId));
    }

    public ItemDto getItemById(long itemId) {
        return itemStorage.getItemById(itemId)
                .map(ItemMapper::toItemDto)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден с ID: " + itemId));
    }

    public List<ItemDto> getItems(long userId) {
        if (userId == 0) {
            throw new ValidationException("userId не должено быть пустым");
        }

        return itemStorage.getAll()
                .stream()
                .filter(x -> x.getOwner() == userId)
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    public Collection<ItemDto> searchItems(String text) {
        Collection<Item> items = itemStorage.getAll();

        if (text == null || text.isEmpty()) {
            return Collections.emptyList();
        }

        return items.stream()
                .filter(Item::isAvailable)
                .filter(x -> (x.getName().toLowerCase().contains(text.toLowerCase())) ||
                        (x.getDescription().toLowerCase().contains(text.toLowerCase())))
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    private void validationDataEmpty(ItemDto itemDto, long userId) {
        if (userId == 0) {
            throw new ValidationException("userId не должено быть пустым");
        }
    }
}
