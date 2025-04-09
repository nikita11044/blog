package practicum.blog.repository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import practicum.blog.entity.Post;
import practicum.blog.jpa.PostJpaRepository;

@Repository
@RequiredArgsConstructor
public class PostRepository {
    private final PostJpaRepository postJpaRepository;

    public Post findById(Long id) {
        return postJpaRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(String.format("Post not found by id: %s", id))
        );
    }

    public Page<Post> findByTagName(String tagName, Pageable pageable) {
        return postJpaRepository.findByTagName(tagName, pageable);
    }

    public Page<Post> findAll(Pageable pageable) {
        return postJpaRepository.findAll(pageable);
    }

    public Long save(Post post) {
        return postJpaRepository.save(post).getId();
    }

    public void deleteById(Long id) {
        postJpaRepository.deleteById(id);
    }
}
