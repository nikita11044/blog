package practicum.blog.repository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import practicum.blog.entity.Post;
import practicum.blog.jdbc.PostJdbcRepository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class PostRepository {
    private final PostJdbcRepository postJdbcRepository;

    public Post findById(Long id) {
        return postJdbcRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(String.format("Post not found by id: %s", id))
        );
    }

    public List<Post> findByTagName(String tagName, int page, int size) {
        int offset = (page - 1) * size;
        return postJdbcRepository.findByTagName(tagName, size, offset);
    }

    public List<Post> findAll(int page, int size) {
        int offset = (page - 1) * size;
        return postJdbcRepository.findAll(size, offset);
    }

    public Long create(Post post) {
        return postJdbcRepository.create(post).getId();
    }

    public void update(Post post) {
        postJdbcRepository.update(post);
    }

    public void deleteById(Long id) {
        postJdbcRepository.deleteById(id);
    }

    public long countAll() {
        return postJdbcRepository.countAll();
    }

    public long countByTagName(String tagName) {
        return postJdbcRepository.countByTagName(tagName);
    }
}
