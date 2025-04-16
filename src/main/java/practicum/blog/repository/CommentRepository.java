package practicum.blog.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import practicum.blog.entity.Comment;
import practicum.blog.jdbc.CommentJdbcRepository;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class CommentRepository {
    private final CommentJdbcRepository commentJdbcRepository;

    public Comment findById(Long id) {
        return commentJdbcRepository.findById(id).orElseThrow(
                () -> new RuntimeException(String.format("Comment not found by id: %s", id))
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

    public Map<Long, Set<Comment>> findAllByPostIds(List<Long> postIds) {
        return commentJdbcRepository.findAllByPostIdsIn(postIds);
    }
}
