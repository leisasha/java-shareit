package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.request.dal.ItemRequestRepository;
import ru.practicum.shareit.request.model.ItemRequest;

import java.time.LocalDateTime;
import java.util.List;

@DataJpaTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRequestRepositoryTest {
    private final ItemRequestRepository itemRequestRepository;

    private ItemRequest itemRequest;

    @BeforeEach
    void setUp() {
        itemRequest = new ItemRequest();
        itemRequest.setId(1L);
        itemRequest.setDescription("description");
        itemRequest.setRequestor(1L);
        itemRequest.setCreated(LocalDateTime.now());

        itemRequestRepository.save(itemRequest);
    }

    @Test
    void findAllByRequestorNotOrderByCreatedDesc() {
        List<ItemRequest> items = itemRequestRepository.findAllByRequestorNotOrderByCreatedDesc(2L);
        Assertions.assertEquals(1, items.size());
        Assertions.assertEquals(1L, items.get(0).getId());
    }

    @AfterEach
    void tearDown() {
        itemRequestRepository.deleteAll();
    }
}
