package practicum.blog.repository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import practicum.blog.entity.Comment;
import practicum.blog.jdbc.CommentJdbcRepository;

@Repository
@RequiredArgsConstructor
public class CommentRepository {
    private final CommentJdbcRepository commentJdbcRepository;

    public Comment findById(Long id) {
        return commentJdbcRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(String.format("Comment not found by id: %s", id))
        );
    }

    public Long create(Comment comment) {
        return commentJdbcRepository.create(comment);
    }

    public void update(Comment comment) {
        commentJdbcRepository.update(comment);
    }

    public void deleteByIdAndPostId(Long id, Long postId) {
        commentJdbcRepository.deleteByIdAndPostId(id, postId);
    }
}
