package practicum.blog.jdbc;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import practicum.blog.entity.Tag;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class TagJdbcRepository {

    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private static final RowMapper<Tag> TAG_ROW_MAPPER = (rs, rowNum) ->
            Tag.builder()
                    .id(rs.getLong("id"))
                    .name(rs.getString("name"))
                    .createdAt(rs.getObject("created_at", LocalDateTime.class))
                    .updatedAt(rs.getObject("updated_at", LocalDateTime.class))
                    .build();

    public Set<Tag> findAll() {
        String sql = "SELECT * FROM tag";

        return new HashSet<>(jdbcTemplate.query(sql, TAG_ROW_MAPPER));
    }

    public Set<Tag> findAllByNameIn(Set<String> names) {
        if (names == null || names.isEmpty()) {
            return Collections.emptySet();
        }

        String placeholders = String.join(",", Collections.nCopies(names.size(), "?"));
        String sql = "SELECT * FROM tag WHERE name IN (" + placeholders + ")";

        return new HashSet<>(jdbcTemplate.query(sql, TAG_ROW_MAPPER, names.toArray()));
    }


    public void saveAll(Set<Tag> tags) {
        String sql = "INSERT INTO tag (name) VALUES (?) RETURNING id";

        for (Tag tag : tags) {
            Long id = jdbcTemplate.queryForObject(sql, Long.class, tag.getName());
            tag.setId(id);
        }
    }

    public void attachTagToPost(Long postId, Long tagId) {
        String sql = "INSERT INTO post_tag (post_id, tag_id) VALUES (?, ?) ON CONFLICT DO NOTHING";
        jdbcTemplate.update(sql, postId, tagId);
    }

    public void unlinkTagsNotInList(Long postId, Set<String> tagNamesToKeep) {
        if (tagNamesToKeep.isEmpty()) {
            return;
        }

        String sql = """
        DELETE FROM post_tag
        WHERE post_id = :postId
        AND tag_id IN (
            SELECT pt.tag_id FROM post_tag pt
            JOIN tag t ON pt.tag_id = t.id
            WHERE pt.post_id = :postId
            AND t.name NOT IN (:tagNames)
        )
    """;

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("postId", postId)
                .addValue("tagNames", tagNamesToKeep);

        namedParameterJdbcTemplate.update(sql, params);
    }

    public void unlinkAllTagsFromPost(Long postId) {
        String sql = "DELETE FROM post_tag WHERE post_id = ?";
        jdbcTemplate.update(sql, postId);
    }

    public void linkTagsToPost(Long postId, Set<Tag> tags) {
        String sql = "INSERT INTO post_tag (post_id, tag_id) VALUES (?, ?) ON CONFLICT DO NOTHING";
        for (Tag tag : tags) {
            jdbcTemplate.update(sql, postId, tag.getId());
        }
    }
}
