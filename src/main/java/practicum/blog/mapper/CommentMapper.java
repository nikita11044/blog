package practicum.blog.mapper;

import org.mapstruct.Mapper;
import practicum.blog.dto.comment.CommentDTO;
import practicum.blog.entity.Comment;

@Mapper(componentModel = "spring")
public interface CommentMapper {
    Comment toEntity(CommentDTO dto);
}
