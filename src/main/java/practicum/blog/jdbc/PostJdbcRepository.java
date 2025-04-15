package practicum.blog.jdbc;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import practicum.blog.entity.Comment;
import practicum.blog.entity.Post;
import practicum.blog.entity.Tag;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PostJdbcRepository {

    private final JdbcTemplate jdbcTemplate;

    private static final RowMapper<Post> BASE_POST_ROW_MAPPER = (rs, rowNum)  ->
            Post.builder()
                    .id(rs.getLong("post_id"))
                    .title(rs.getString("title"))
                    .imagePath(rs.getString("image_path"))
                    .text(rs.getString("text"))
                    .likesCount(rs.getInt("likes_count"))
                    .createdAt(rs.getTimestamp("post_created_at").toLocalDateTime())
                    .comments(new HashSet<>())
                    .tags(new HashSet<>())
                    .build();

    private List<Post> extractPostsWithNestedData(String sql, Object... params) {
        return jdbcTemplate.query(sql, rs -> {
            Map<Long, Post> postMap = new LinkedHashMap<>();

            while (rs.next()) {
                long postId = rs.getLong("post_id");

                Post post = postMap.get(postId);
                if (post == null) {
                    post = BASE_POST_ROW_MAPPER.mapRow(rs, rs.getRow());
                    postMap.put(postId, post);
                }

                long tagId = rs.getLong("tag_id");
                if (!rs.wasNull()) {
                    Tag tag = Tag.builder()
                            .id(tagId)
                            .name(rs.getString("tag_name"))
                            .createdAt(rs.getTimestamp("tag_created_at").toLocalDateTime())
                            .updatedAt(rs.getTimestamp("tag_updated_at").toLocalDateTime())
                            .build();

                    if (post.getTags().stream().noneMatch(t -> t.getId().equals(tagId))) {
                        post.getTags().add(tag);
                    }
                }

                long commentId = rs.getLong("comment_id");
                if (!rs.wasNull()) {
                    Comment comment = Comment.builder()
                            .id(commentId)
                            .postId(postId)
                            .text(rs.getString("comment_text"))
                            .createdAt(rs.getTimestamp("comment_created_at").toLocalDateTime())
                            .updatedAt(rs.getTimestamp("comment_updated_at").toLocalDateTime())
                            .build();

                    if (post.getComments().stream().noneMatch(c -> c.getId().equals(commentId))) {
                        post.getComments().add(comment);
                    }
                }
            }

            return new ArrayList<>(postMap.values());
        }, params);
    }



    public Optional<Post> findById(Long id) {
        String sql = """
            SELECT
                p.id AS post_id, p.title, p.image_path, p.text, p.likes_count,
                p.created_at AS post_created_at, p.updated_at AS post_updated_at,
                t.id AS tag_id, t.name AS tag_name, t.created_at AS tag_created_at, t.updated_at AS tag_updated_at,
                c.id AS comment_id, c.text AS comment_text, c.created_at AS comment_created_at, c.updated_at AS comment_updated_at
            FROM post p
            LEFT JOIN post_tag pt ON p.id = pt.post_id
            LEFT JOIN tag t ON pt.tag_id = t.id
            LEFT JOIN comment c ON p.id = c.post_id
            WHERE p.id = ?
        """;

        List<Post> posts = extractPostsWithNestedData(sql, id);
        return posts.stream().findFirst();
    }

    public List<Post> findByTagName(String tagName, int limit, int offset) {
        String sql = """
            SELECT
                p.id AS post_id, p.title, p.image_path, p.text, p.likes_count,
                p.created_at AS post_created_at, p.updated_at AS post_updated_at,
                t.id AS tag_id, t.name AS tag_name, t.created_at AS tag_created_at, t.updated_at AS tag_updated_at,
                c.id AS comment_id, c.text AS comment_text, c.created_at AS comment_created_at, c.updated_at AS comment_updated_at
            FROM post p
            LEFT JOIN post_tag pt ON p.id = pt.post_id
            LEFT JOIN tag t ON pt.tag_id = t.id
            LEFT JOIN comment c ON p.id = c.post_id
            WHERE ? IS NULL OR LOWER(t.name) LIKE LOWER(CONCAT('%', ?, '%'))
            LIMIT ? OFFSET ?
        """;

        return extractPostsWithNestedData(sql, tagName, tagName, limit, offset);
    }

    public List<Post> findAll(int limit, int offset) {
        String sql = """
            SELECT
                p.id AS post_id, p.title, p.image_path, p.text, p.likes_count,
                p.created_at AS post_created_at, p.updated_at AS post_updated_at,
                t.id AS tag_id, t.name AS tag_name, t.created_at AS tag_created_at, t.updated_at AS tag_updated_at,
                c.id AS comment_id, c.text AS comment_text, c.created_at AS comment_created_at, c.updated_at AS comment_updated_at
            FROM post p
            LEFT JOIN post_tag pt ON p.id = pt.post_id
            LEFT JOIN tag t ON pt.tag_id = t.id
            LEFT JOIN comment c ON p.id = c.post_id
            ORDER BY p.created_at DESC
            LIMIT ? OFFSET ?
        """;

        return extractPostsWithNestedData(sql, limit, offset);
    }

    public long countAll() {
        String sql = "SELECT COUNT(*) FROM post";
        Long count = jdbcTemplate.queryForObject(sql, Long.class);
        return count != null ? count : 0L;
    }

    public long countByTagName(String tagName) {
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

