package ru.practicum.shareit.item.dal;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.Optional;

public interface ItemStorage {
    Item create(ItemDto itemDto, long userId);

    Item update(Item item, ItemDto itemDto, long userId);

    Collection<Item> getAll();

    Optional<Item> getItemById(long itemId);
}
