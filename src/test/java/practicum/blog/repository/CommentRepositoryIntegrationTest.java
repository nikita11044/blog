package practicum.blog.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import practicum.blog.entity.Comment;
import practicum.blog.jpa.CommentJpaRepository;
import practicum.blog.jpa.PostJpaRepository;
import practicum.blog.utils.BaseContextTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CommentRepositoryIntegrationTest extends BaseContextTest {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private CommentJpaRepository commentJpaRepository;

    @Autowired
    private PostJpaRepository postJpaRepository;

    @BeforeEach
    void setUp() {
        dbHelper.clearAndResetDatabase();
        dbHelper.createMockPostPlain();
    }

    private Comment createComment(String text) {
        return postJpaRepository.findById(1L)
                .map(post -> Comment.builder()
                        .post(post)
                        .text(text)
                        .build())
                .orElseThrow(() -> new IllegalStateException("Mock post not found"));
    }

    @Test
    void testSave_shouldSaveComment() {
        Comment comment = createComment("This is a comment");
        commentRepository.save(comment);

        Optional<Comment> savedComment = commentJpaRepository.findById(comment.getId());
        assertTrue(savedComment.isPresent());
        assertEquals("This is a comment", savedComment.get().getText());
    }

    @Test
    void testFindById_shouldReturnComment() {
        Comment comment = createComment("This is a comment");
        commentRepository.save(comment);

        Comment foundComment = commentRepository.findById(comment.getId());
        assertNotNull(foundComment);
        assertEquals("This is a comment", foundComment.getText());
    }

    @Test
    void testDeleteById_shouldDeleteComment() {
        Comment comment = createComment("This is a comment to be deleted");
        commentRepository.save(comment);

        commentRepository.deleteByIdAndPostId(comment.getId(), 1L);

        Optional<Comment> deletedComment = commentJpaRepository.findById(comment.getId());
        assertFalse(deletedComment.isPresent(), "Comment should be deleted from the database.");
    }
}
