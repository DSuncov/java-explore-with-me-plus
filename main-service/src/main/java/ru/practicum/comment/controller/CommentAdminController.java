package ru.practicum.comment.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.comment.service.CommentService;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "admin/comments/{commentId}")
public class CommentAdminController {
    private final CommentService commentService;

    @DeleteMapping
    public ResponseEntity<Void> delete(@PathVariable("commentId") Long commentId) {
        commentService.deleteByAdmin(commentId);
        return ResponseEntity.noContent().build();
    }
}