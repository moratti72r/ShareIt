package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.DuplicateValuesException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UserDto addUser(UserDto userDto) {
        User user = UserMapper.fromUserDto(new User(), userDto);
        User result = userRepository.save(user);
        log.info("Пользователь с id={} добавлен", result.getId());

        return UserMapper.toUserDto(result);
    }

    public UserDto updateUser(long id, UserDto userDto) {
        if (userDto.getEmail() != null && userRepository.existsUserByEmailAndIdNot(userDto.getEmail(), id)) {
            throw new DuplicateValuesException(UserRepository.class);
        }
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            User updateUser = UserMapper.fromUserDto(user, userDto);
            updateUser.setId(id);
            userRepository.save(updateUser);
            log.info("Пользователь c id={} успешно изменен", id);
            return UserMapper.toUserDto(updateUser);
        } else throw new NotFoundException(UserRepository.class);
    }

    public List<UserDto> findAllUser() {
        List<UserDto> result = userRepository.findAll().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
        log.info("Список пользователей получен");
        return result;
    }

    public UserDto findUserById(long id) {
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            UserDto result = UserMapper.toUserDto(user);
            log.info("Пользователь с id={} получен", id);
            return result;
        } else throw new NotFoundException(UserRepository.class);
    }

    public void deleteUserById(long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
        } else throw new NotFoundException(UserRepository.class);
    }
}
