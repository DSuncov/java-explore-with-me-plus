package ru.practicum.user.mapper;

import org.mapstruct.Mapper;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.dto.UserShortDto;
import ru.practicum.user.entity.User;

import java.util.List;

@Mapper(componentModel = "string")
public interface UserMapper {

    User toUSer(UserShortDto userShortDto);

    UserDto toUserDto(User user);

    List<UserDto> toUserDtoList(List<User> users);
}
