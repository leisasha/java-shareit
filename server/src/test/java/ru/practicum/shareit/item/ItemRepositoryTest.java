package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.item.dal.ItemRepository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@DataJpaTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRepositoryTest {
    private final ItemRepository itemRepository;

    private Item item;

    @BeforeEach
    void setUp() {
        item = new Item();
        item.setId(1L);
        item.setName("Name");
        item.setDescription("description");
        item.setAvailable(true);
        item.setOwner(1L);
        itemRepository.save(item);
    }

    @Test
    void findByOwnerIdShouldReturnItemsOwnedByUser() {
        List<Item> items = itemRepository.findByOwnerId(1L);
        Assertions.assertEquals(1, items.size());
        Assertions.assertEquals("Name", items.get(0).getName());
    }

    @Test
    void searchShouldReturnAvailableItemsByName() {
        List<Item> items = itemRepository.search("name");
        Assertions.assertEquals(1, items.size());
        Assertions.assertEquals("name", items.get(0).getName().toLowerCase());
    }

    @Test
    void searchShouldReturnAvailableItemsByDescription() {
        List<Item> items = itemRepository.search("descript");
        Assertions.assertEquals(1, items.size());
        Assertions.assertEquals("description", items.get(0).getDescription().toLowerCase());
    }

    @Test
    void search_ShouldReturnEmptyList_WhenNoAvailableItems() {
        List<Item> items = itemRepository.search("qwerty");
        Assertions.assertTrue(items.isEmpty());
    }

    @AfterEach
    void tearDown() {
        itemRepository.deleteAll();
    }
}
