package practicum.blog.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import practicum.blog.dto.comment.CommentDTO;
import practicum.blog.entity.Comment;
import practicum.blog.mapper.CommentMapper;
import practicum.blog.repository.CommentRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;

    @Transactional
    public Long create(CommentDTO dto) {
        var comment = commentMapper.toEntity(dto);
        return commentRepository.create(comment);
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

    public Map<Long, Set<Comment>> findCommentsByPostIds(List<Long> postIds) {
        return commentRepository.findAllByPostIds(postIds);
    }
}
