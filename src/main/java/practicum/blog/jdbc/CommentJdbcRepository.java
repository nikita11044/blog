package practicum.blog.jdbc;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import practicum.blog.entity.Comment;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CommentJdbcRepository {

    private final JdbcTemplate jdbcTemplate;

    private static final RowMapper<Comment> COMMENT_ROW_MAPPER = (rs, rowNum) ->
            Comment.builder()
                    .id(rs.getLong("id"))
                    .createdAt(rs.getObject("created_at", LocalDateTime.class))
                    .updatedAt(rs.getObject("updated_at", LocalDateTime.class))
                    .text(rs.getString("text"))
                    .postId(rs.getLong("post_id"))
                    .build();

    public Optional<Comment> findById(Long id) {
        String sql = "SELECT * FROM comment WHERE id = ?";
        return jdbcTemplate.query(sql, COMMENT_ROW_MAPPER, id)
                .stream()
                .findFirst();
    }

    public void create(Comment comment) {
        String sql = "INSERT INTO comment (text, post_id) VALUES (?, ?)";
        jdbcTemplate.update(sql,
                comment.getText(),
                comment.getPostId()
        );
    }

    public void update(Comment comment) {
        String sql = "UPDATE comment SET text = ?, updated_at = ? WHERE id = ?";
        jdbcTemplate.update(sql,
                comment.getText(),
                comment.getUpdatedAt(),
                comment.getId()
        );
    }

    public void deleteByIdAndPostId(Long id, Long postId) {
        String sql = "DELETE FROM comment WHERE id = ? AND post_id = ?";
        jdbcTemplate.update(sql, id, postId);
    }
}
