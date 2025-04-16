package practicum.blog.jdbc;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import practicum.blog.entity.Post;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PostJdbcRepository {

    private final JdbcTemplate jdbcTemplate;

    private static final RowMapper<Post> POST_ROW_MAPPER = (rs, rowNum) ->
            Post.builder()
                    .id(rs.getLong("id"))
                    .title(rs.getString("title"))
                    .imagePath(rs.getString("image_path"))
                    .text(rs.getString("text"))
                    .likesCount(rs.getInt("likes_count"))
                    .createdAt(rs.getTimestamp("created_at").toLocalDateTime())
                    .updatedAt(rs.getTimestamp("updated_at").toLocalDateTime())
                    .build();

    public Optional<Post> findById(Long id) {
        String sql = "SELECT * FROM post WHERE id = ?";
        return jdbcTemplate.query(sql, POST_ROW_MAPPER, id)
                .stream()
                .findFirst();
    }

    public List<Post> findAllByTagName(String tagName, int limit, int offset) {
        String sql = """
            SELECT p.*
            FROM post p
            JOIN post_tag pt ON p.id = pt.post_id
            JOIN tag t ON pt.tag_id = t.id
            WHERE LOWER(t.name) LIKE LOWER(CONCAT('%', ?, '%'))
            ORDER BY p.created_at DESC
            LIMIT ? OFFSET ?
        """;
        return jdbcTemplate.query(sql, POST_ROW_MAPPER, tagName, limit, offset);
    }

    public List<Post> findAll(int limit, int offset) {
        String sql = """
            SELECT id, title, image_path, text, likes_count, created_at, updated_at
            FROM post
            ORDER BY created_at DESC
            LIMIT ? OFFSET ?
    """;
        return jdbcTemplate.query(sql, POST_ROW_MAPPER, limit, offset);
    }

    public long countAll() {
        String sql = "SELECT COUNT(*) FROM post";
        Long count = jdbcTemplate.queryForObject(sql, Long.class);
        return count != null ? count : 0L;
    }

    public long countAllByTagName(String tagName) {
        String sql = """
            SELECT COUNT(DISTINCT p.id)
            FROM post p
            LEFT JOIN post_tag pt ON p.id = pt.post_id
            LEFT JOIN tag t ON pt.tag_id = t.id
            WHERE LOWER(t.name) LIKE LOWER(CONCAT('%', ?, '%'))
        """;

        Long count = jdbcTemplate.queryForObject(sql, Long.class, tagName);
        return count != null ? count : 0L;
    }

    public Post create(Post post) {
        String sql = "INSERT INTO post (title, image_path, text, likes_count) " +
                "VALUES (?, ?, ?, ?) RETURNING id";
        Long id = jdbcTemplate.queryForObject(sql, Long.class,
                post.getTitle(),
                post.getImagePath(),
                post.getText(),
                post.getLikesCount()
        );
        post.setId(id);
        return post;
    }

    public void update(Post post) {
        String sql = "UPDATE post SET title = ?, image_path = ?, text = ?, likes_count = ?, updated_at = ? WHERE id = ?";
        jdbcTemplate.update(sql,
                post.getTitle(),
                post.getImagePath(),
                post.getText(),
                post.getLikesCount(),
                post.getUpdatedAt(),
                post.getId()
        );
    }

    public void deleteById(Long id) {
        String sql = "DELETE FROM post WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }
}

