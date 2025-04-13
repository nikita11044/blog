package practicum.blog.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import practicum.blog.dto.post.PostDTO;
import practicum.blog.dto.post.PostRequestDTO;
import practicum.blog.entity.Post;
import practicum.blog.mapper.PostMapper;
import practicum.blog.repository.PostRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private PostMapper postMapper;

    @Mock
    private FileService fileService;

    @Mock
    private TagService tagService;

    @InjectMocks
    private PostService postService;

    private Post post;
    private PostRequestDTO postRequestDTO;
    private PostDTO postDTO;

    @BeforeEach
    void setUp() {
        post = Post.builder()
                .id(1L)
                .title("Test Title")
                .text("Test Content")
                .likesCount(0)
                .build();

        postRequestDTO = PostRequestDTO.builder()
                .id(1L)
                .title("Updated Title")
                .text("Updated Content")
                .build();

        postDTO = PostDTO.builder()
                .id(1L)
                .title("Test Title")
                .text("Test Content")
                .build();
    }

    @Test
    void create_ShouldSavePost() {
        when(postMapper.toEntity(any(PostRequestDTO.class))).thenReturn(post);
        when(postRepository.save(any(Post.class))).thenReturn(post.getId());

        Long result = postService.create(postRequestDTO);

        assertNotNull(result);
        assertEquals(1L, result);
        verify(tagService).createMultipleFromString(postRequestDTO.getTagsAsString());
        verify(postRepository).save(post);
    }

    @Test
    void getById_ShouldReturnMappedPost() {
        when(postRepository.findById(1L)).thenReturn(post);
        when(postMapper.toDTO(post)).thenReturn(postDTO);

        PostDTO result = postService.getById(1L);

        assertNotNull(result);
        assertEquals("Test Title", result.getTitle());
        verify(postRepository).findById(1L);
        verify(postMapper).toDTO(post);
    }

    @Test
    void getByTagName_ShouldReturnPageOfPosts() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("createdAt").descending());
        Page<Post> page = new PageImpl<>(List.of(post), pageable, 1);

        when(postRepository.findAll(pageable)).thenReturn(page);
        when(postMapper.toDTO(any(Post.class))).thenReturn(postDTO);

        Page<PostDTO> result = postService.getByTagName("", 1, 10);

        assertFalse(result.isEmpty());
        assertEquals(1, result.getTotalElements());
        verify(postRepository).findAll(pageable);
    }

    @Test
    void update_ShouldApplyChangesAndSave() {
        when(postRepository.findById(1L)).thenReturn(post);
        when(postRepository.save(any(Post.class))).thenReturn(1L);

        postService.update(postRequestDTO);

        assertEquals("Updated Title", post.getTitle());
        assertEquals("Updated Content", post.getText());
        verify(postRepository).save(post);
    }

    @Test
    void updateLikes_ShouldIncreaseAndDecreaseCorrectly() {
        when(postRepository.findById(1L)).thenReturn(post);

        postService.updateLikes(1L, true);
        assertEquals(1, post.getLikesCount());

        postService.updateLikes(1L, false);
        assertEquals(0, post.getLikesCount());

        verify(postRepository, times(2)).save(post);
    }

    @Test
    void delete_ShouldDeletePostAndItsImage() {
        when(postRepository.findById(1L)).thenReturn(post);
        doNothing().when(fileService).deleteFile(post.getImagePath());
        doNothing().when(postRepository).deleteById(1L);

        postService.delete(1L);

        verify(fileService).deleteFile(post.getImagePath());
        verify(postRepository).deleteById(1L);
    }
}
