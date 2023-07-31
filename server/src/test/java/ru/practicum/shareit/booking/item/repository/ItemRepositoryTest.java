package ru.practicum.shareit.booking.item.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
public class ItemRepositoryTest {

    private final PageRequest pr = PageRequest.of(0, 20, Sort.by("id"));
    @Autowired
    private TestEntityManager em;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRequestRepository itemRequestRepository;
    private User user1;
    private User user2;
    private ItemRequest ir1;
    private ItemRequest ir2;

    private Item item1;
    private Item item2;

    @BeforeEach
    void addEntityOnDb() {
        user1 = userRepository.save(makeUser("Viktor", "viktor@mail.ru"));
        user2 = userRepository.save(makeUser("Michail", "misha@mail.ru"));

        ir1 = itemRequestRepository.save(makeItemRequest("Описание1", user1, LocalDateTime.of(2022, 12, 12, 12, 12)));
        ir2 = itemRequestRepository.save(makeItemRequest("Описание2", user2, LocalDateTime.of(2022, 12, 12, 12, 12)));

        item1 = itemRepository.save(makeItem("Инструмент1", "Описание1", true, user1, ir1));
        item2 = itemRepository.save(makeItem("Инструмент2", "Описание2", false, user2, ir1));
    }

    @Test
    void verifyFindAllItemByUser() {
        long id = (long) em.getId(user2);
        List<Item> items = itemRepository.findAllItemByUser(id, pr);

        assertEquals(1, items.size());
        assertEquals("Описание2", items.get(0).getDescription());
    }

    @Test
    void verifySearchItem() {
        List<Item> items = itemRepository.searchItem("Инструмент1", pr);
        List<Item> items2 = itemRepository.searchItem("Инструмент2", pr);

        assertEquals(1, items.size());
        assertTrue(items.get(0).getAvailable());
        assertTrue(items2.isEmpty());
    }

    @Test
    void verifyFindAllByRequestId() {
        long id1 = (long) em.getId(ir1);
        long id2 = (long) em.getId(ir2);
        List<Item> items1 = itemRepository.findAllByRequestId(id1);
        List<Item> items2 = itemRepository.findAllByRequestId(id2);

        assertEquals(2, items1.size());
        assertTrue(items2.isEmpty());
    }

    private Item makeItem(String name, String description, Boolean available, User owner, ItemRequest request) {
        Item item = new Item();
        item.setName(name);
        item.setDescription(description);
        item.setAvailable(available);
        item.setOwner(owner);
        item.setRequest(request);

        return item;
    }

    private User makeUser(String name, String email) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);

        return user;
    }

    private ItemRequest makeItemRequest(String description, User user, LocalDateTime ldt) {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription(description);
        itemRequest.setRequestor(user);
        itemRequest.setCreated(ldt);

        return itemRequest;
    }

}
