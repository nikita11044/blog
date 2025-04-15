package practicum.blog.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import practicum.blog.entity.Comment;
import practicum.blog.jdbc.CommentJdbcRepository;
import practicum.blog.jdbc.PostJdbcRepository;
import practicum.blog.utils.BaseContextTest;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class CommentRepositoryIntegrationTest extends BaseContextTest {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private CommentJdbcRepository commentJdbcRepository;

    @Autowired
    private PostJdbcRepository postJdbcRepository;

    @BeforeEach
    void setUp() {
        dbHelper.clearAndResetDatabase();
        dbHelper.createMockPostPlain();
    }

    private Comment createComment(String text) {
        return postJdbcRepository.findById(1L)
                .map(post -> Comment.builder()
                        .postId(1L)
                        .text(text)
                        .build())
                .orElseThrow(() -> new IllegalStateException("Mock post not found"));
    }

    @Test
    void testCreate_shouldCreateComment() {
        Comment comment = createComment("This is a comment");
        var savedCommentId = commentRepository.create(comment);

        Optional<Comment> savedComment = commentJdbcRepository.findById(savedCommentId);
        assertTrue(savedComment.isPresent());
        assertEquals("This is a comment", savedComment.get().getText());
    }

    @Test
    void testUpdate_shouldUpdateCommentText() {
        Comment comment = createComment("This is a comment");
        var savedCommentId = commentRepository.create(comment);

        comment.setId(savedCommentId);
        comment.setText("This is an updated comment");
        comment.setUpdatedAt(LocalDateTime.now());
        commentRepository.update(comment);

        Optional<Comment> updatedComment = commentJdbcRepository.findById(comment.getId());
        assertTrue(updatedComment.isPresent(), "Updated comment should exist.");
        assertEquals("This is an updated comment", updatedComment.get().getText());
    }

    @Test
    void testDeleteById_shouldDeleteComment() {
        Comment comment = createComment("This is a comment to be deleted");
        commentRepository.create(comment);

        commentRepository.deleteByIdAndPostId(comment.getId(), 1L);

        Optional<Comment> deletedComment = commentJdbcRepository.findById(comment.getId());
        assertFalse(deletedComment.isPresent(), "Comment should be deleted from the database.");
    }
}
