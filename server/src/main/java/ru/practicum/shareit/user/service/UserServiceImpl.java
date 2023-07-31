package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.DuplicateValuesException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserDto addUser(UserDto userDto) {
        User user = UserMapper.fromUserDto(userDto);
        User result = userRepository.save(user);
        log.info("Пользователь с id={} добавлен", result.getId());

        return UserMapper.toUserDto(result);
    }

    @Override
    @Transactional
    public UserDto updateUser(long id, UserDto userDto) {
        if (userDto.getEmail() != null && userRepository.existsUserByEmailAndIdIsNot(userDto.getEmail(), id)) {
            throw new DuplicateValuesException(UserRepository.class);
        }
        User updateUser = patchUser(id, userDto);
        User result = userRepository.save(updateUser);
        log.info("Пользователь c id={} успешно изменен", id);
        return UserMapper.toUserDto(result);
    }

    @Override
    @Transactional
    public List<UserDto> findAllUser() {
        List<UserDto> result = userRepository.findAll().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
        log.info("Список пользователей получен");
        return result;
    }

    @Override
    @Transactional
    public UserDto findUserById(long id) {
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            UserDto result = UserMapper.toUserDto(user);
            log.info("Пользователь с id={} получен", id);
            return result;
        } else throw new NotFoundException(UserRepository.class);
    }

    @Override
    @Transactional
    public void deleteUserById(long id) {
        userRepository.deleteById(id);
    }

    private User patchUser(long id, UserDto userDto) {
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (userDto.getName() != null) {
                user.setName(userDto.getName());
            }
            if (userDto.getEmail() != null) {
                user.setEmail(userDto.getEmail());
            }
            return user;
        } else {
            throw new NotFoundException(UserRepository.class);
        }
    }
}
