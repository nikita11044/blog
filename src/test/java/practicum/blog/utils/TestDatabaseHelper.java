package practicum.blog.utils;


import org.springframework.jdbc.core.JdbcTemplate;

public class TestDatabaseHelper {
    private final JdbcTemplate jdbcTemplate;

    public TestDatabaseHelper(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void clearAndResetDatabase() {
        jdbcTemplate.execute("DELETE FROM post_tag");
        jdbcTemplate.execute("DELETE FROM tag");
        jdbcTemplate.execute("DELETE FROM comment");
        jdbcTemplate.execute("DELETE FROM post");

        jdbcTemplate.execute("ALTER SEQUENCE post_id_seq RESTART WITH 1");
        jdbcTemplate.execute("ALTER SEQUENCE tag_id_seq RESTART WITH 1");
        jdbcTemplate.execute("ALTER SEQUENCE comment_id_seq RESTART WITH 1");
    }

    public void createMockPostPlain() {
        jdbcTemplate.execute("INSERT INTO post (title, text, likes_count) VALUES ('Test title', 'Test text', 0)");
    }

    public void createMockPostWithTag() {
        createMockPostPlain();
        jdbcTemplate.execute("INSERT INTO tag (name) VALUES ('java')");
        jdbcTemplate.execute("INSERT INTO post_tag (post_id, tag_id) VALUES (1, 1)");
    }

    public void createMockPostWithComment() {
        createMockPostPlain();
        jdbcTemplate.execute("INSERT INTO comment (post_id, text) VALUES (1, 'Test comment text')");
    }
}
