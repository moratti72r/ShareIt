package ru.practicum.shareit.booking.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.model.StatusType;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.item.comment.model.Comment;
import ru.practicum.shareit.booking.item.comment.repository.CommentRepository;
import ru.practicum.shareit.booking.item.comment.dto.CommentDto;
import ru.practicum.shareit.booking.item.comment.dto.CommentMapper;
import ru.practicum.shareit.booking.item.dto.ItemDto;
import ru.practicum.shareit.booking.item.dto.ItemMapper;
import ru.practicum.shareit.booking.item.model.Item;
import ru.practicum.shareit.booking.item.repository.ItemRepository;
import ru.practicum.shareit.exception.IncorrectArgumentException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;

    private final UserRepository userRepository;

    private final BookingRepository bookingRepository;

    private final CommentRepository commentRepository;

    private final ItemRequestRepository itemRequestRepository;

    @Override
    public ItemDto addItem(long idUser, ItemDto itemDto) {
        Optional<User> userOptional = userRepository.findById(idUser);
        if (userOptional.isPresent()) {
            Item item = ItemMapper.fromItemDto(new Item(), itemDto);
            item.setOwner(userOptional.get());
            if (itemDto.getRequestId() != null) {
                Optional<ItemRequest> iro = itemRequestRepository.findById(itemDto.getRequestId());
                if (iro.isPresent()) {
                    item.setRequest(iro.get());
                } else throw new NotFoundException(ItemRequestRepository.class);
            }
            Item result = itemRepository.save(item);
            log.info("Продукт c id={} от Пользователя с id={} добавлен", result.getId(), idUser);

            return ItemMapper.toItemDto(result);

        } else throw new NotFoundException(UserRepository.class);
    }

    @Override
    public CommentDto addComment(long userId, long itemId, CommentDto commentDto) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(UserRepository.class);
        }
        if (!itemRepository.existsById(itemId)) {
            throw new NotFoundException(ItemRepository.class);
        }
        if (bookingRepository.existsByItemIdAndBookerIdAndStatusAndEndIsBefore(itemId, userId, StatusType.APPROVED, LocalDateTime.now())) {
            Comment comment = new Comment();
            Item item = itemRepository.findById(itemId).get();
            User user = userRepository.findById(userId).get();
            comment.setText(commentDto.getText());
            comment.setItem(item);
            comment.setAuthor(user);
            comment.setCreated(LocalDateTime.now());

            CommentDto result = CommentMapper.toCommentDto(commentRepository.save(comment));
            log.info("Комментарий c id={} от Пользователя с id={} на Продукт с id={} добавлен", result.getId(), userId, itemId);
            return result;
        } else throw new IncorrectArgumentException(BookingRepository.class);
    }

    @Override
    public ItemDto updateItem(long idUser, long idItem, ItemDto itemDto) {

        if (!userRepository.existsById(idUser)) {
            throw new NotFoundException(UserRepository.class);
        }
        Optional<Item> itemOptional = itemRepository.findById(idItem);
        if (itemOptional.isPresent()) {
            Item item = itemOptional.get();

            if (item.getOwner().getId() != idUser) {
                throw new NotFoundException(UserRepository.class);
            }

            Item updateItem = ItemMapper.fromItemDto(item, itemDto);
            itemRepository.save(updateItem);
            log.info("Продукт c id={} успешно изменен Пользователем с id={}", idItem, idUser);

            return ItemMapper.toItemDto(updateItem);
        } else throw new NotFoundException(ItemRepository.class);
    }

    @Override
    public ItemDto findItemById(long idUser, long idItem) {

        if (!userRepository.existsById(idUser)) {
            throw new NotFoundException(UserRepository.class);
        }
        Optional<Item> itemOptional = itemRepository.findById(idItem);
        if (itemOptional.isPresent()) {
            Item item = itemOptional.get();
            ItemDto itemDto = ItemMapper.toItemDto(item);
            if (item.getOwner().getId() == idUser) {

                Optional<Booking> lastBookingOpt = bookingRepository.findLastBookingByItemId(idItem);
                lastBookingOpt.ifPresent(booking -> itemDto.setLastBooking(BookingMapper.toBookingDto(booking)));

                Optional<Booking> nextBookingOpt = bookingRepository.findNextBookingByItemId(idItem);
                nextBookingOpt.ifPresent(booking -> itemDto.setNextBooking(BookingMapper.toBookingDto(booking)));
            }

            List<CommentDto> commentsDto = commentRepository.findCommentByItem_Id(itemDto.getId())
                    .stream()
                    .map(CommentMapper::toCommentDto)
                    .collect(Collectors.toList());

            itemDto.setComments(commentsDto);

            log.info("Продукт c id={} получен Пользователем с id={}", idItem, idUser);
            return itemDto;
        } else throw new NotFoundException(ItemRepository.class);
    }

    @Override
    public List<ItemDto> findAllItemByUser(long idUser, int from, int size) {
        List<ItemDto> result = itemRepository
                .findAllItemByUser(idUser, PageRequest.of(from / size, size, Sort.by("id")))
                .stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());

        for (ItemDto itemDto : result) {
            Optional<Booking> lastBookingOpt = bookingRepository.findLastBookingByItemId(itemDto.getId());
            lastBookingOpt.ifPresent(booking -> itemDto.setLastBooking(BookingMapper.toBookingDto(booking)));

            Optional<Booking> nextBookingOpt = bookingRepository.findNextBookingByItemId(itemDto.getId());
            nextBookingOpt.ifPresent(booking -> itemDto.setNextBooking(BookingMapper.toBookingDto(booking)));

            List<CommentDto> commentsDto = commentRepository.findCommentByItem_Id(itemDto.getId())
                    .stream()
                    .map(CommentMapper::toCommentDto)
                    .collect(Collectors.toList());

            itemDto.setComments(commentsDto);
        }
        log.info("Список продуктов от Пользователя c id={} получен", idUser);
        return result;
    }

    @Override
    public List<ItemDto> searchItem(long idUser, String text, int from, int size) {
        if (!userRepository.existsUserById(idUser)) {
            throw new NotFoundException(UserRepository.class);
        }
        if (text.isEmpty() || text.isBlank()) {
            return new ArrayList<>();
        }
        List<ItemDto> result = itemRepository
                .searchItem(text, PageRequest.of(from / size, size))
                .stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());

        log.info("Получен список доступных продуктов в поиске по тексту \"{}\" " +
                "для Пользователя с id={}", text, idUser);

        return result;

    }
}
