package ru.practicum.shareit.item.dal;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
@Slf4j
public class InMemoryItemStorage implements ItemStorage {
    private final Map<Long, Item> itemMap = new HashMap<>();

    public Item create(ItemDto itemDto, long userId) {
        Item item = ItemMapper.toItem(itemDto, getNextId(), userId);
        itemMap.put(item.getId(), item);
        log.trace("Экземпляр {} создан", item);
        return item;
    }

    public Item update(Item item, ItemDto itemDto, long userId) {
        ItemMapper.updateItemFields(item, itemDto);
        log.trace("Экземпляр {} изменен", item);
        return item;
    }

    public Collection<Item> getAll() {
        return itemMap.values();
    }

    public Optional<Item> getItemById(long id) {
        if (itemMap.containsKey(id))
            return Optional.ofNullable(itemMap.get(id));
        else
            return Optional.empty();
    }

    private long getNextId() {
        long currentMaxId = itemMap.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
