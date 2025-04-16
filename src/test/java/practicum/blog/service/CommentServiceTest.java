package practicum.blog.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import practicum.blog.dto.comment.CommentDTO;
import practicum.blog.entity.Comment;
import practicum.blog.mapper.CommentMapper;
import practicum.blog.repository.CommentRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private CommentMapper commentMapper;

    @InjectMocks
    private CommentService commentService;

    private CommentDTO commentDTO;
    private Comment comment;

    @BeforeEach
    void setUp() {
        comment = Comment.builder()
                .id(1L)
                .text("This is a comment")
                .postId(1L)
                .build();

        commentDTO = CommentDTO.builder()
                .id(1L)
                .text("This is a new comment text")
                .postId(1L)
                .build();
    }

    @Test
    void create_ShouldSaveComment() {
        when(commentMapper.toEntity(any(CommentDTO.class))).thenReturn(comment);
        when(commentRepository.create(any(Comment.class))).thenReturn(comment.getId());

        Long result = commentService.create(commentDTO);

        assertNotNull(result);
        assertEquals(1L, result);
        verify(commentRepository).create(comment);
    }

    @Test
    void update_ShouldModifyExistingComment() {
        when(commentRepository.findById(commentDTO.getId())).thenReturn(comment);

        commentService.update(commentDTO);

        assertEquals("This is a new comment text", comment.getText());
        verify(commentRepository).update(comment);
    }

    @Test
    void delete_ShouldRemoveComment() {
        doNothing().when(commentRepository).deleteByIdAndPostId(commentDTO.getId(), commentDTO.getPostId());

        commentService.delete(commentDTO.getId(), commentDTO.getPostId());

        verify(commentRepository).deleteByIdAndPostId(commentDTO.getId(), commentDTO.getPostId());
    }
}
