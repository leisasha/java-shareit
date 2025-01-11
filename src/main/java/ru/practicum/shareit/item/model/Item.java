package ru.practicum.shareit.item.model;

import lombok.Data;
import ru.practicum.shareit.request.model.ItemRequest;

@Data
public class Item {
    private Long id;
    private String name;
    private String description;
    private boolean available;
    private long owner;
    private ItemRequest request;

    public Item(long id, String name, String description, boolean available, long owner) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.available = available;
        this.owner = owner;
    }
}
