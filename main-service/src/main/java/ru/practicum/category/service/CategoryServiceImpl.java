package ru.practicum.category.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.dto.CategoryRequestDto;
import ru.practicum.category.entity.Category;
import ru.practicum.category.mapper.CategoryMapper;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.ValidationException;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    @Transactional
    public CategoryDto create(CategoryRequestDto categoryRequestDto) {
        log.info("добавление новой категории");
        if (categoryRepository.existsByName(categoryRequestDto.getName())) {
            throw new ConflictException("категория с таким именем уже существует");
        }
        Category categoryCreate = categoryMapper.toCategory(categoryRequestDto);

        log.info("сохранение категории");
        categoryRepository.save(categoryCreate);

        return categoryMapper.toCategoryDto(categoryCreate);
    }

    @Override
    @Transactional
    public CategoryDto update(Long id, CategoryRequestDto categoryRequestDto) {
        log.info("обновление категории с id = {}", id);
        if (id == null) {
            throw new ValidationException("обновление category: id не может быть равен null");
        }

        Category categoryUpdate = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("категория с id = " + id + " не найдена")
        );

        if (!categoryUpdate.getName().equals(categoryRequestDto.getName()) &&
        categoryRepository.existsByName(categoryRequestDto.getName())) {
            throw new ConflictException("категория с именем " + categoryRequestDto.getName() + " уже существует");
        }
        categoryUpdate.setName(categoryRequestDto.getName());
        categoryRepository.save(categoryUpdate);

        return categoryMapper.toCategoryDto(categoryUpdate);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        log.info("удаление категории с id = {}", id);
        if (!categoryRepository.existsById(id)) {
            throw new NotFoundException("категория с id = " + id + " не найдена");
        }

        categoryRepository.deleteById(id);
        log.info("категория с id = {} удалена", id);
    }
}
