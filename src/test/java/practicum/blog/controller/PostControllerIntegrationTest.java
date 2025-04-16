package practicum.blog.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import practicum.blog.utils.BaseContextTest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

class PostControllerIntegrationTest extends BaseContextTest {

    @BeforeEach
    void setUpTestData() {
        dbHelper.clearAndResetDatabase();
        dbHelper.createMockPostPlain();
    }

    private MockMultipartFile getMockFile() {
        return new MockMultipartFile("image", "test.jpg", "image/jpeg", "image content".getBytes());
    }

    @Test
    void getPosts_shouldReturnHtmlWithPosts() throws Exception {
        mockMvc.perform(get("/posts"))
                .andExpect(status().isOk())
                .andExpect(view().name("posts"))
                .andExpect(model().attributeExists("posts"))
                .andExpect(model().attributeExists("paging"));
    }

    @Test
    void getPost_shouldReturnPostDetails() throws Exception {
        mockMvc.perform(get("/posts/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("post"))
                .andExpect(model().attributeExists("post"))
                .andExpect(model().attribute("title", "Test title"));
    }

    @Test
    void addPost_shouldCreatePostAndRedirect() throws Exception {
        mockMvc.perform(multipart("/posts")
                        .file(getMockFile())
                        .param("title", "New Post")
                        .param("text", "New post content")
                        .param("tagsAsString", "tag1,tag2"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/posts/2"));
    }

    @Test
    void editPost_shouldUpdatePostAndRedirect() throws Exception {
        mockMvc.perform(multipart("/posts/1")
                        .file(getMockFile())
                        .param("title", "Updated Title")
                        .param("text", "Updated content"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/posts/1"));
    }

    @Test
    void deletePost_shouldRemovePostAndRedirect() throws Exception {
        mockMvc.perform(post("/posts/1/delete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/posts"));
    }

    @Test
    void addComment_shouldCreateCommentAndRedirect() throws Exception {
        mockMvc.perform(post("/posts/1/comments")
                        .param("text", "This is a comment"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/posts/1"));
    }

    @Test
    void deleteComment_shouldRemoveCommentAndRedirect() throws Exception {
        mockMvc.perform(post("/posts/1/comments/1/delete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/posts/1"));
    }
}
