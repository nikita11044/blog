package practicum.blog.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import practicum.blog.entity.Post;
import practicum.blog.jdbc.PostJdbcRepository;
import practicum.blog.utils.BaseContextTest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PostRepositoryIntegrationTest extends BaseContextTest {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private PostJdbcRepository postJdbcRepository;

    @BeforeEach
    void setUp() {
        dbHelper.clearAndResetDatabase();
        dbHelper.createMockPostWithTag();
    }

    private Post createPost(String title, String text) {
        return Post.builder()
                .title(title)
                .text(text)
                .likesCount(0)
                .build();
    }

    @Test
    void testCreate_shouldCreatePost() {
        Post post = createPost("New Post", "This is a new post.");

        Long savedPostId = postRepository.create(post);
        Optional<Post> savedPost = postJdbcRepository.findById(savedPostId);

        assertTrue(savedPost.isPresent());
        assertEquals("New Post", savedPost.get().getTitle());
        assertEquals("This is a new post.", savedPost.get().getText());
    }

    @Test
    void testFindAll_shouldReturnPagedPosts() {
        List<Post> posts = postRepository.findAll(1, 10);
        long count = postRepository.countAll();

        assertNotNull(posts);
        assertFalse(posts.isEmpty());
        assertTrue(count > 0);
    }

    @Test
    void testFindByTagName_shouldReturnPagedPostsByTag() {
        List<Post> posts = postRepository.findByTagName("java", 1, 10);
        long count = postRepository.countByTagName("java");

        assertNotNull(posts);
        assertFalse(posts.isEmpty());
        assertTrue(count > 0);
    }

    @Test
    void testUpdate_shouldUpdatePost() {
        Post post = createPost("Original Post", "Original content of the post.");
        Long savedPostId = postRepository.create(post);

        post.setId(savedPostId);
        post.setText("Updated content of the post.");
        post.setUpdatedAt(LocalDateTime.now());
        postRepository.update(post);

        Optional<Post> updatedPost = postJdbcRepository.findById(post.getId());
        assertTrue(updatedPost.isPresent(), "Updated post should exist.");
        assertEquals("Updated content of the post.", updatedPost.get().getText());
    }

    @Test
    void testDeleteById_shouldDeletePost() {
        Post post = createPost("Post to delete", "Text of the post to be deleted.");
        Long savedPostId = postRepository.create(post);

        Optional<Post> savedPostOptional = postJdbcRepository.findById(savedPostId);
        assertTrue(savedPostOptional.isPresent());

        postRepository.deleteById(savedPostId);

        Optional<Post> deletedPost = postJdbcRepository.findById(savedPostId);
        assertFalse(deletedPost.isPresent());
    }
}
