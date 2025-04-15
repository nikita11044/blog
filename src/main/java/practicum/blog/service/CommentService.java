package practicum.blog.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import practicum.blog.dto.comment.CommentDTO;
import practicum.blog.entity.Comment;
import practicum.blog.repository.CommentRepository;
import practicum.blog.repository.PostRepository;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;

    @Transactional
    public void create(CommentDTO dto) {
        var post = postRepository.findById(dto.getPostId());

        var comment = Comment.builder()
                .postId(post.getId())
                .text(dto.getText())
                .build();

        commentRepository.create(comment);
    }

    @Transactional
    public void update(CommentDTO dto) {
        var comment = commentRepository.findById(dto.getId());

        comment.setText(dto.getText());
        comment.setUpdatedAt(LocalDateTime.now());

        commentRepository.update(comment);
    }

    public void delete(Long id, Long postId) {
        commentRepository.deleteByIdAndPostId(id, postId);
    }
}
