package practicum.blog.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import practicum.blog.entity.Post;
import practicum.blog.jpa.PostJpaRepository;
import practicum.blog.utils.BaseContextTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PostRepositoryIntegrationTest extends BaseContextTest {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private PostJpaRepository postJpaRepository;

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
    void testSave_shouldSavePost() {
        Post post = createPost("New Post", "This is a new post.");

        Long savedPostId = postRepository.save(post);
        Optional<Post> savedPost = postJpaRepository.findById(savedPostId);

        assertTrue(savedPost.isPresent());
        assertEquals("New Post", savedPost.get().getTitle());
        assertEquals("This is a new post.", savedPost.get().getText());
    }

    @Test
    void testFindById_shouldReturnPost() {
        Post post = createPost("Test Post", "Test content for the post.");
        Long postId = postRepository.save(post);

        Post foundPost = postRepository.findById(postId);

        assertNotNull(foundPost);
        assertEquals("Test Post", foundPost.getTitle());
        assertEquals("Test content for the post.", foundPost.getText());
    }

    @Test
    void testFindAll_shouldReturnPagedPosts() {
        Page<Post> page = postRepository.findAll(PageRequest.of(0, 10));

        assertNotNull(page);
        assertTrue(page.hasContent());
        assertTrue(page.getTotalElements() > 0);
    }

    @Test
    void testFindByTagName_shouldReturnPagedPostsByTag() {
        Page<Post> page = postRepository.findByTagName("java", PageRequest.of(0, 10));

        assertNotNull(page);
        assertTrue(page.hasContent());
    }

    @Test
    void testDeleteById_shouldDeletePost() {
        Post post = createPost("Post to delete", "Text of the post to be deleted.");
        Long postId = postRepository.save(post);

        Optional<Post> savedPost = postJpaRepository.findById(postId);
        assertTrue(savedPost.isPresent());

        postRepository.deleteById(postId);

        Optional<Post> deletedPost = postJpaRepository.findById(postId);
        assertFalse(deletedPost.isPresent());
    }
}
