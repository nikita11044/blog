package practicum.blog.jpa;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import practicum.blog.entity.Post;

@Repository
public interface PostJpaRepository extends JpaRepository<Post, Long> {
    @Query("SELECT p FROM Post p LEFT JOIN p.tags t WHERE :tagName IS NULL OR LOWER(t.name) LIKE LOWER(CONCAT('%', :tagName, '%'))")
    Page<Post> findByTagName(@Param("tagName") String tagName, Pageable pageable);
}
