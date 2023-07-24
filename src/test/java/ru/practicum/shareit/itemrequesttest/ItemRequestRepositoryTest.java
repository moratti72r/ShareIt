package ru.practicum.shareit.itemrequesttest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class ItemRequestRepositoryTest {

    private final PageRequest pr = PageRequest.of(0, 20);
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRequestRepository itemRequestRepository;
    private User user1;
    private User user2;

    private ItemRequest request1;
    private ItemRequest request2;
    private ItemRequest request3;

    @BeforeEach
    void addEntityOnDb() {
        user1 = userRepository.save(makeUser("Viktor", "viktor@mail.ru"));
        user2 = userRepository.save(makeUser("Michail", "misha@mail.ru"));

        request1 = itemRequestRepository.save(makeRequest("Описание1", user1, LocalDateTime.now().plusHours(1)));
        request2 = itemRequestRepository.save(makeRequest("Описание2", user1, LocalDateTime.now().plusHours(2)));
        request3 = itemRequestRepository.save(makeRequest("Описание3", user2, LocalDateTime.now().plusHours(3)));
    }

    @Test
    void verifyFindAllByRequestorId() {
        List<ItemRequest> requests = itemRequestRepository.findAllByRequestorId(user1.getId(), pr);

        assertEquals(2, requests.size());
        assertEquals(requests.get(0), request1);
        assertEquals(requests.get(1), request2);
    }

    @Test
    void verifyFindAllByRequestorIdIsNot() {
        List<ItemRequest> requests = itemRequestRepository.findAllByRequestorIdIsNot(user1.getId(), pr);

        assertEquals(1, requests.size());
        assertEquals(requests.get(0), request3);
    }

    @Test
    void verifyExistsByRequestorId() {
        boolean result1 = itemRequestRepository.existsByRequestorId(user1.getId());
        boolean result2 = itemRequestRepository.existsByRequestorId(user2.getId());
        boolean result3 = itemRequestRepository.existsByRequestorId(99);

        assertTrue(result1);
        assertTrue(result2);
        assertFalse(result3);
    }

    private User makeUser(String name, String email) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);

        return user;
    }

    private ItemRequest makeRequest(String description, User requestor, LocalDateTime created) {
        ItemRequest request = new ItemRequest();
        request.setDescription(description);
        request.setRequestor(requestor);
        request.setCreated(created);

        return request;
    }
}
