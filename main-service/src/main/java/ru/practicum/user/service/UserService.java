package ru.practicum.user.service;

import ru.practicum.user.dto.UserDto;
import ru.practicum.user.dto.UserRequestDto;

import java.util.List;

public interface UserService {

    UserDto create(UserRequestDto userRequestDto);

    List<UserDto> getAllUsers();

    UserDto getUserById(Long id);

    void deleteUser(Long id);
}
