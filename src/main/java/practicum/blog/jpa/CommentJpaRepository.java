package practicum.blog.jpa;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import practicum.blog.entity.Comment;

@Repository
public interface CommentJpaRepository extends JpaRepository<Comment, Long> {
    @Modifying
    @Transactional
    @Query("DELETE FROM Comment c WHERE c.id = :id AND c.post.id = :postId")
    void deleteByIdAndPostId(@Param("id") Long id, @Param("postId") Long postId);
}
