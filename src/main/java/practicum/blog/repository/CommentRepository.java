package practicum.blog.repository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import practicum.blog.entity.Comment;
import practicum.blog.jpa.CommentJpaRepository;

@Repository
@RequiredArgsConstructor
public class CommentRepository {
    private final CommentJpaRepository commentJpaRepository;

    public Comment findById(Long id) {
        return commentJpaRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(String.format("Comment not found by id: %s", id))
        );
    }

    public void save(Comment comment) {
        commentJpaRepository.save(comment);
    }

    public void deleteByIdAndPostId(Long id, Long postId) {
        commentJpaRepository.deleteByIdAndPostId(id, postId);
    }
}
