package ru.practicum.shareit.booking.item.comment.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.shareit.booking.item.comment.model.Comment;
import ru.practicum.shareit.booking.item.comment.repository.CommentRepository;
import ru.practicum.shareit.booking.item.model.Item;
import ru.practicum.shareit.booking.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
public class CommentRepositoryTest {

    Comment comment1;
    Comment comment2;
    @Autowired
    private TestEntityManager em;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CommentRepository commentRepository;
    private User user1;
    private User user2;
    private Item item1;
    private Item item2;

    @BeforeEach
    void addEntityOnDb() {
        user1 = userRepository.save(makeUser("Viktor", "viktor@mail.ru"));
        user2 = userRepository.save(makeUser("Michail", "misha@mail.ru"));

        item1 = itemRepository.save(makeItem("Инструмент1", "Описание1", true, user1));
        item2 = itemRepository.save(makeItem("Инструмент2", "Описание2", false, user2));

        comment1 = commentRepository.save(makeComment("Текст1", item1, user2, LocalDateTime.now()));
        comment2 = commentRepository.save(makeComment("Текст2", item1, user2, LocalDateTime.now()));
    }

    @Test
    void verifyFindCommentByItemId() {
        List<Comment> comments = commentRepository.findCommentByItem_Id(item1.getId());

        assertEquals(2, comments.size());
        assertEquals(comment1.getText(), comments.get(0).getText());
        assertEquals(comment2.getText(), comments.get(1).getText());
    }

    private User makeUser(String name, String email) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);

        return user;
    }

    private Item makeItem(String name, String description, Boolean available, User owner) {
        Item item = new Item();
        item.setName(name);
        item.setDescription(description);
        item.setAvailable(available);
        item.setOwner(owner);

        return item;
    }

    private Comment makeComment(String text, Item item, User author, LocalDateTime lcd) {
        Comment comment = new Comment();
        comment.setText(text);
        comment.setItem(item);
        comment.setAuthor(author);
        comment.setCreated(lcd);

        return comment;
    }
}
