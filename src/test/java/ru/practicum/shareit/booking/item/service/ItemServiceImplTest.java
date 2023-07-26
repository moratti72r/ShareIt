package ru.practicum.shareit.booking.item.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.item.comment.model.Comment;
import ru.practicum.shareit.booking.item.comment.dto.CommentDto;
import ru.practicum.shareit.booking.item.dto.ItemDto;
import ru.practicum.shareit.booking.item.model.Item;
import ru.practicum.shareit.booking.item.repository.ItemRepository;
import ru.practicum.shareit.booking.item.service.ItemService;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.StatusType;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.IncorrectArgumentException;
import ru.practicum.shareit.exception.NotFoundException;
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
public class ItemServiceImplTest {

    private final ItemService itemService;

    private final ItemRepository itemRepository;

    private final EntityManager entityManager;

    private final UserRepository userRepository;

    private final BookingRepository bookingRepository;

    private final ItemRequestRepository itemRequestRepository;

    private User user1;
    private User user2;
    private ItemRequest itemRequest1;
    private ItemRequest itemRequest2;
    private Item item1;
    private Item item2;
    private Booking booking1;
    private Booking booking2;
    private Booking booking3;
    private Booking booking4;

    @BeforeEach
    public void addEntitiesOnDb() {
        user1 = userRepository.save(makeUser("User1", "user1@mail.ru"));
        user2 = userRepository.save(makeUser("User2", "user2@mail.ru"));
        itemRequest1 = itemRequestRepository.save(makeItemRequest("Описание1", user1, LocalDateTime.of(2022, 12, 12, 12, 12)));
        itemRequest2 = itemRequestRepository.save(makeItemRequest("Описание2", user2, LocalDateTime.of(2022, 12, 12, 12, 12)));
        item1 = itemRepository.save(makeItem("Инструмент1", "Описание1", true, user2, itemRequest1));
        item2 = itemRepository.save(makeItem("Инструмент2", "Описание2", true, user1, itemRequest2));
        booking1 = bookingRepository.save(makeBooking(LocalDateTime.now().minusHours(2),
                LocalDateTime.now().minusHours(1),
                item1, user1, StatusType.APPROVED));
        booking2 = bookingRepository.save(makeBooking(LocalDateTime.now().plusHours(2),
                LocalDateTime.now().plusHours(3),
                item2, user2, StatusType.APPROVED));
        booking3 = bookingRepository.save(makeBooking(LocalDateTime.now().plusHours(3),
                LocalDateTime.now().plusHours(4),
                item1, user1, StatusType.APPROVED));
        booking4 = bookingRepository.save(makeBooking(LocalDateTime.now().plusHours(4),
                LocalDateTime.now().plusHours(5),
                item1, user2, StatusType.APPROVED));
    }

    @Test
    void addItemWithoutRequest() {
        ItemDto itemDto = makeItemDto("Инструмент3", "Описание3", true, null);

        itemService.addItem(user1.getId(), itemDto);
        TypedQuery<Item> query = entityManager.createQuery("Select i from Item i where i.name = :name", Item.class);
        Item item = query.setParameter("name", itemDto.getName())
                .getSingleResult();

        assertThat(item.getId(), notNullValue());
        assertThat(item.getName(), equalTo(itemDto.getName()));
        assertThat(item.getAvailable(), equalTo((itemDto.getAvailable())));
        assertThat(item.getOwner().getId(), equalTo(user1.getId()));
        assertThat(item.getRequest(), nullValue());
    }

    @Test
    void addItemWithRequest() {
        ItemDto itemDto = makeItemDto("Инструмент3", "Описание3", true, itemRequest1.getId());

        itemService.addItem(user1.getId(), itemDto);
        TypedQuery<Item> query = entityManager.createQuery("Select i from Item i where i.name = :name", Item.class);
        Item item = query.setParameter("name", itemDto.getName())
                .getSingleResult();

        assertThat(item.getId(), notNullValue());
        assertThat(item.getName(), equalTo(itemDto.getName()));
        assertThat(item.getAvailable(), equalTo((itemDto.getAvailable())));
        assertThat(item.getOwner().getId(), equalTo(user1.getId()));
        assertThat(item.getRequest(), equalTo(itemRequest1));
    }

    @Test
    void catchNotFoundExceptionWhenAddItemWithNotExistUser() {
        ItemDto itemDto = makeItemDto("Инструмент3", "Описание3", true, null);

        Exception exception = assertThrows(NotFoundException.class, () -> {
            itemService.addItem(99, itemDto);
        });
        assertTrue(UserRepository.class.getName().contains(exception.getMessage()));
    }

    @Test
    void catchNotFoundExceptionWhenAddItemWithNotExistRequest() {
        ItemDto itemDto = makeItemDto("Инструмент3", "Описание3", true, 99L);

        Exception exception = assertThrows(NotFoundException.class, () -> {
            itemService.addItem(user1.getId(), itemDto);
        });
        assertTrue(ItemRequestRepository.class.getName().contains(exception.getMessage()));
    }

    @Test
    void addComment() {
        CommentDto commentDto = new CommentDto();
        commentDto.setText("Комментарий");

        itemService.addComment(user1.getId(), item1.getId(), commentDto);

        TypedQuery<Comment> query = entityManager.createQuery("Select c from Comment c where c.text = :text", Comment.class);
        Comment comment = query.setParameter("text", commentDto.getText())
                .getSingleResult();

        assertThat(comment.getId(), notNullValue());
        assertThat(comment.getText(), equalTo(commentDto.getText()));
        assertThat(comment.getAuthor().getId(), equalTo(user1.getId()));
        assertThat(comment.getItem().getId(), equalTo(item1.getId()));
        assertThat(comment.getCreated(), greaterThan(booking1.getEnd()));
    }

    @Test
    void catchNotFoundExceptionWhenAddCommentWithNotExistUser() {
        CommentDto commentDto = new CommentDto();
        commentDto.setText("Комментарий");

        Exception exception = assertThrows(NotFoundException.class, () -> {
            itemService.addComment(999, item1.getId(), commentDto);
        });
        assertTrue(UserRepository.class.getName().contains(exception.getMessage()));
    }

    @Test
    void catchNotFoundExceptionWhenAddCommentWithNotExistItem() {
        CommentDto commentDto = new CommentDto();
        commentDto.setText("Комментарий");

        Exception exception = assertThrows(NotFoundException.class, () -> {
            itemService.addComment(user1.getId(), 999, commentDto);
        });
        assertTrue(ItemRepository.class.getName().contains(exception.getMessage()));
    }

    @Test
    void catchNotFoundExceptionWhenAddCommentWithIncorrectBooking() {
        CommentDto commentDto = new CommentDto();
        commentDto.setText("Комментарий");

        Exception exception = assertThrows(IncorrectArgumentException.class, () -> {
            itemService.addComment(user2.getId(), item2.getId(), commentDto);
        });
        assertTrue(BookingRepository.class.getName().contains(exception.getMessage()));
    }

    @Test
    void updateItem() {
        ItemDto itemDto = makeItemDto(null, "Описание3", false, itemRequest1.getId());

        itemService.updateItem(user2.getId(), item1.getId(), itemDto);

        TypedQuery<Item> query = entityManager.createQuery("Select i from Item i where i.description = :description", Item.class);
        Item item = query.setParameter("description", itemDto.getDescription())
                .getSingleResult();

        assertThat(item.getId(), notNullValue());
        assertThat(item.getName(), equalTo(item1.getName()));
        assertThat(item.getAvailable(), equalTo((itemDto.getAvailable())));
        assertThat(item.getOwner().getId(), equalTo(user2.getId()));
        assertThat(item.getRequest(), equalTo(itemRequest1));
    }

    @Test
    void catchNotFoundExceptionWhenUpdateItemWithNotExistUser() {

        ItemDto itemDto = makeItemDto(null, "Описание3", false, itemRequest1.getId());

        Exception exception = assertThrows(NotFoundException.class, () -> {
            itemService.updateItem(999, item1.getId(), itemDto);
        });
        assertTrue(UserRepository.class.getName().contains(exception.getMessage()));
    }

    @Test
    void catchNotFoundExceptionWhenUpdateItemWithNotExistItem() {

        ItemDto itemDto = makeItemDto(null, "Описание3", false, itemRequest1.getId());

        Exception exception = assertThrows(NotFoundException.class, () -> {
            itemService.updateItem(user2.getId(), 999, itemDto);
        });
        assertTrue(ItemRepository.class.getName().contains(exception.getMessage()));
    }

    @Test
    void catchNotFoundExceptionWhenUpdateItemWithIncorrectOwnerId() {

        ItemDto itemDto = makeItemDto(null, "Описание3", false, itemRequest1.getId());

        Exception exception = assertThrows(NotFoundException.class, () -> {
            itemService.updateItem(user1.getId(), item1.getId(), itemDto);
        });
        assertTrue(UserRepository.class.getName().contains(exception.getMessage()));
    }

    @Test
    void findAllItemByUser() {
        List<Item> itemList = List.of(item2);
        List<ItemDto> itemDtoList = itemService.findAllItemByUser(user1.getId(), 0, 20);

        assertThat(itemDtoList, hasSize(itemList.size()));
        for (Item item : itemList) {
            assertThat(itemDtoList, hasItem(allOf(
                    hasProperty("id", notNullValue()),
                    hasProperty("name", equalTo(item.getName())),
                    hasProperty("description", equalTo(item.getDescription())),
                    hasProperty("available", equalTo(item.getAvailable())),
                    hasProperty("requestId", equalTo(item.getRequest().getId())),
                    hasProperty("lastBooking", nullValue()),
                    hasProperty("nextBooking", equalTo(BookingMapper.toBookingDto(booking2)))
            )));
        }
    }

    @Test
    void findItemByIdWhereUserIsNotOwner() {
        ItemDto itemDto = itemService.findItemById(user1.getId(), item1.getId());

        assertThat(itemDto.getId(), notNullValue());
        assertThat(itemDto.getName(), equalTo(item1.getName()));
        assertThat(itemDto.getDescription(), equalTo(item1.getDescription()));
        assertThat(itemDto.getAvailable(), equalTo(item1.getAvailable()));
        assertThat(itemDto.getRequestId(), equalTo(item1.getRequest().getId()));
        assertThat(itemDto.getLastBooking(), nullValue());
        assertThat(itemDto.getNextBooking(), nullValue());
    }

    @Test
    void findItemByIdWhereUserIsOwner() {
        ItemDto itemDto = itemService.findItemById(user2.getId(), item1.getId());

        assertThat(itemDto.getId(), notNullValue());
        assertThat(itemDto.getName(), equalTo(item1.getName()));
        assertThat(itemDto.getDescription(), equalTo(item1.getDescription()));
        assertThat(itemDto.getAvailable(), equalTo(item1.getAvailable()));
        assertThat(itemDto.getRequestId(), equalTo(item1.getRequest().getId()));
        assertThat(itemDto.getLastBooking(), equalTo(BookingMapper.toBookingDto(booking1)));
        assertThat(itemDto.getNextBooking(), equalTo(BookingMapper.toBookingDto(booking3)));
    }

    @Test
    void catchNotFoundExceptionWhenFindItemByIdWithNotExistUser() {

        Exception exception = assertThrows(NotFoundException.class, () -> {
            itemService.findItemById(999, item1.getId());
        });
        assertTrue(UserRepository.class.getName().contains(exception.getMessage()));
    }

    @Test
    void catchNotFoundExceptionWhenFindItemByIdWithNotExistItem() {

        Exception exception = assertThrows(NotFoundException.class, () -> {
            itemService.findItemById(user1.getId(), 999);
        });
        assertTrue(ItemRepository.class.getName().contains(exception.getMessage()));
    }

    @Test
    void searchItem() {
        List<ItemDto> itemDtoList = itemService.searchItem(user1.getId(), "инструмент", 0, 20);
        List<Item> itemList = List.of(item1, item2);

        assertThat(itemDtoList, hasSize(itemList.size()));
        for (Item item : itemList) {
            assertThat(itemDtoList, hasItem(allOf(
                    hasProperty("id", notNullValue()),
                    hasProperty("name", equalTo(item.getName())),
                    hasProperty("description", equalTo(item.getDescription())),
                    hasProperty("available", equalTo(item.getAvailable())),
                    hasProperty("requestId", equalTo(item.getRequest().getId()))
            )));
        }
    }

    @Test
    void searchItemReturnEmptyListWhenNoResult() {
        List<ItemDto> itemDtoList = itemService.searchItem(user1.getId(), "rewe", 0, 20);

        assertThat(itemDtoList, hasSize(0));
    }

    @Test
    void catchNotFoundExceptionWhenSearchItemWithNotExistUser() {

        Exception exception = assertThrows(NotFoundException.class, () -> {
            itemService.searchItem(999, "инструмент", 0, 20);
        });
        assertTrue(UserRepository.class.getName().contains(exception.getMessage()));
    }

    private ItemDto makeItemDto(String name, String description, Boolean available, Long requestId) {
        ItemDto itemDto = new ItemDto();
        itemDto.setName(name);
        itemDto.setDescription(description);
        itemDto.setAvailable(available);
        itemDto.setRequestId(requestId);

        return itemDto;
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

    private Booking makeBooking(LocalDateTime start, LocalDateTime end, Item item, User booker, StatusType status) {
        Booking booking = new Booking();
        booking.setStart(start);
        booking.setEnd(end);
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(status);

        return booking;
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
