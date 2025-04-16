package practicum.blog.jdbc;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import practicum.blog.entity.Comment;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class CommentJdbcRepository {

    private final JdbcTemplate jdbcTemplate;

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private static final RowMapper<Comment> COMMENT_ROW_MAPPER = (rs, rowNum) ->
            Comment.builder()
                    .id(rs.getLong("id"))
                    .createdAt(rs.getObject("created_at", LocalDateTime.class))
                    .updatedAt(rs.getObject("updated_at", LocalDateTime.class))
                    .text(rs.getString("text"))
                    .postId(rs.getLong("post_id"))
                    .build();

    public Map<Long, Set<Comment>> findAllByPostIdsIn(List<Long> postIds) {
        if (postIds == null || postIds.isEmpty()) {
            return Collections.emptyMap();
        }

        String sql = """
            SELECT c.id, c.post_id, c.text, c.created_at, c.updated_at
            FROM comment c
            WHERE c.post_id IN (:postIds)
        """;

        MapSqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("postIds", postIds);

        return namedParameterJdbcTemplate.query(sql, parameters, rs -> {
            Map<Long, Set<Comment>> result = new HashMap<>();
            while (rs.next()) {
                long postId = rs.getLong("post_id");
                Comment comment = COMMENT_ROW_MAPPER.mapRow(rs, rs.getRow());
                result.computeIfAbsent(postId, k -> new HashSet<>()).add(comment);
            }
            return result;
        });
    }

    public Optional<Comment> findById(Long id) {
        String sql = "SELECT * FROM comment WHERE id = ?";
        return jdbcTemplate.query(sql, COMMENT_ROW_MAPPER, id)
                .stream()
                .findFirst();
    }

    public Long create(Comment comment) {
        String sql = "INSERT INTO comment (text, post_id) VALUES (?, ?) RETURNING id";
        return jdbcTemplate.queryForObject(sql, Long.class,
                comment.getText(),
                comment.getPostId());
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
