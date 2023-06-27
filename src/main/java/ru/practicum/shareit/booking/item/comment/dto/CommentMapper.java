package ru.practicum.shareit.booking.item.comment.dto;

import ru.practicum.shareit.booking.item.comment.Comment;

public class CommentMapper {

    public static CommentDto toCommentDto(Comment comment) {

        CommentDto commentDto = new CommentDto();
        commentDto.setId(comment.getId());
        commentDto.setText(comment.getText());
        commentDto.setAuthorName(comment.getAuthor().getName());
        commentDto.setCreated(comment.getCreated());

        return commentDto;
    }

    public static Comment fromCommentDto(Comment comment, CommentDto commentDto) {
        comment.setText(commentDto.getText());
        return comment;
    }

}
