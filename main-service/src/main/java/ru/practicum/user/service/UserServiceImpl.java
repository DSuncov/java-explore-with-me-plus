package ru.practicum.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.dto.UserRequestDto;
import ru.practicum.user.entity.User;
import ru.practicum.user.mapper.UserMapper;
import ru.practicum.user.repository.UserRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public UserDto create(UserRequestDto userRequestDto) {
        log.info("ЗАПРОС НА СОЗДАНИЕ ПОЛЬЗОВАТЕЛЯ");
        log.info("проверка на существования такого email");
        if (userRepository.existsByEmail(userRequestDto.getEmail())) {
            throw new ConflictException("пользователь с таким Email уже существует");
        }

        User userCreate = userMapper.toUser(userRequestDto);

        log.info("сохранение пользователя в БД");
        userRepository.save(userCreate);

        log.info("возвращение созданного пользователя");
        return userMapper.toUserDto(userCreate);
    }

    @Override
    public List<UserDto> getAllUsers() {
        log.info("ЗАПРОС НА ВЫВОД ВСЕХ ПОЛЬЗОВАТЕЛЕЙ");
        return userMapper.toUserDtoList(userRepository.findAll());
    }

    @Override
    public UserDto getUserById(Long id) {
        log.info("запрос на вывод пользователя по id = {}", id);
        User userGetById = userRepository.findById(id).orElseThrow(
                () -> new NotFoundException("пользователь с id = " + id + " не существует")
        );
        return userMapper.toUserDto(userGetById);
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        log.info("запрос на удаление пользователя с id = {}", id);
        if(userRepository.existsById(id)) {
            throw new NotFoundException("пользователя с id = " + id + " не существует");
        }
        userRepository.deleteById(id);
        log.info("пользователь с id = {} удален", id);
    }
}
