package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import ru.practicum.shareit.booking.item.model.Item;
import ru.practicum.shareit.booking.item.repository.ItemRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Transactional
@TestPropertySource(locations = "classpath:test.properties")
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRequestServiceImplTest {

    private final ItemRequestService itemRequestService;

    private final ItemRequestRepository itemRequestRepository;

    private final UserRepository userRepository;

    private final ItemRepository itemRepository;

    private final EntityManager entityManager;

    private User user1;
    private User user2;

    private ItemRequest request1OnUser1;
    private ItemRequest request2OnUser2;

    private Item item1OnRequest1;
    private Item item2OnRequest1;

    @BeforeEach
    void addEntityOnDb() {
        user1 = userRepository.save(makeUser("Viktor", "viktor@mail.ru"));
        user2 = userRepository.save(makeUser("Michail", "misha@mail.ru"));

        request1OnUser1 = itemRequestRepository.save(makeRequest("Описание1", user1, LocalDateTime.now().plusHours(1)));
        request2OnUser2 = itemRequestRepository.save(makeRequest("Описание1", user2, LocalDateTime.now().plusHours(1)));

        item1OnRequest1 = itemRepository.save(makeItem("Инструмент1", "Описание1", true, user2, request1OnUser1));
        item2OnRequest1 = itemRepository.save(makeItem("Инструмент2", "Описание2", true, user2, request1OnUser1));
    }

    @Test
    void addItemRequest() {
        ItemRequestDto requestDto = new ItemRequestDto();
        requestDto.setDescription("Описание запроса");

        itemRequestService.addItemRequest(user2.getId(), requestDto);

        TypedQuery<ItemRequest> query = entityManager.createQuery("Select ir from ItemRequest ir " +
                "where ir.description = :description", ItemRequest.class);
        ItemRequest request = query.setParameter("description", requestDto.getDescription())
                .getSingleResult();

        assertThat(request.getId(), notNullValue());
        assertThat(request.getCreated(), notNullValue());
        assertThat(request.getDescription(), equalTo(requestDto.getDescription()));
        assertThat(request.getRequestor(), equalTo(user2));
    }

    @Test
    void catchNotFoundExceptionWhenAddItemRequestWithNotExistUser() {

        Exception exception = assertThrows(NotFoundException.class, () -> {
            itemRequestService.addItemRequest(99, new ItemRequestDto());
        });
        assertTrue(UserRepository.class.getName().contains(exception.getMessage()));
    }

    @Test
    void findAllRequestsByRequestor() {

        List<ItemRequestDto> requestDtoList = itemRequestService.findAllRequestsByRequestor(user1.getId());

        assertThat(requestDtoList, hasSize(1));
        assertThat(requestDtoList.get(0).getDescription(), equalTo(request1OnUser1.getDescription()));
        assertThat(requestDtoList.get(0).getItems(), hasSize(2));
    }

    @Test
    void catchNotFoundExceptionWhenFindAllRequestsByRequestorWithNotExistUser() {

        Exception exception = assertThrows(NotFoundException.class, () -> {
            itemRequestService.findAllRequestsByRequestor(99);
        });
        assertTrue(UserRepository.class.getName().contains(exception.getMessage()));
    }

    @Test
    void findRequestById() {

        ItemRequestDto requestDto = itemRequestService.findRequestById(user1.getId(), request1OnUser1.getId());

        assertThat(requestDto.getId(), equalTo(request1OnUser1.getId()));
        assertThat(requestDto.getCreated(), equalTo(request1OnUser1.getCreated()));
        assertThat(requestDto.getItems(), hasSize(2));
    }

    @Test
    void catchNotFoundExceptionWhenFindRequestByIdWithNotExistUser() {

        Exception exception = assertThrows(NotFoundException.class, () -> {
            itemRequestService.findRequestById(99, request1OnUser1.getId());
        });
        assertTrue(UserRepository.class.getName().contains(exception.getMessage()));
    }

    @Test
    void catchNotFoundExceptionWhenFindRequestByIdWithNotExistRequest() {

        Exception exception = assertThrows(NotFoundException.class, () -> {
            itemRequestService.findRequestById(user2.getId(), 99);
        });
        assertTrue(ItemRequestRepository.class.getName().contains(exception.getMessage()));
    }

    @Test
    void findAllRequests() {

        List<ItemRequestDto> requestDtoList = itemRequestService.findAllRequests(user1.getId(), 0, 20);

        assertThat(requestDtoList, hasSize(1));
        assertThat(requestDtoList.get(0).getDescription(), equalTo(request2OnUser2.getDescription()));
        assertThat(requestDtoList.get(0).getItems(), hasSize(0));
    }

    @Test
    void catchNotFoundExceptionWhenFindAllRequestsWithNotExistUser() {

        Exception exception = assertThrows(NotFoundException.class, () -> {
            itemRequestService.findAllRequests(99, 0, 20);
        });
        assertTrue(UserRepository.class.getName().contains(exception.getMessage()));
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

    private Item makeItem(String name, String description, Boolean available, User owner, ItemRequest request) {
        Item item = new Item();
        item.setName(name);
        item.setDescription(description);
        item.setAvailable(available);
        item.setOwner(owner);
        item.setRequest(request);

        return item;
    }
}
