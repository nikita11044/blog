package practicum.blog.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import practicum.blog.entity.Tag;

import java.util.Set;

@Repository
public interface TagJpaRepository extends JpaRepository<Tag, Long> {
    Set<Tag> findAllByNameIn(Set<String> names);
}
