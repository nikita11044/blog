package practicum.blog.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import practicum.blog.entity.Tag;
import practicum.blog.jdbc.TagJdbcRepository;

import java.util.Set;

@Repository
@RequiredArgsConstructor
public class TagRepository {
    private final TagJdbcRepository tagJdbcRepository;

    public Set<Tag> findMultipleByNames(Set<String> names) {
        return tagJdbcRepository.findAllByNameIn(names);
    }

    public void saveAll(Set<Tag> tags) {
        tagJdbcRepository.saveAll(tags);
    }

    public void attachTagToPost(Long postId, Long tagId) {
        tagJdbcRepository.attachTagToPost(postId, tagId);
    }

    public void unlinkTagsNotInList(Long postId, Set<String> tagNamesToKeep) {
        tagJdbcRepository.unlinkTagsNotInList(postId, tagNamesToKeep);
    }

    public void unlinkAllTagsFromPost(Long postId) {
        tagJdbcRepository.unlinkAllTagsFromPost(postId);
    }

    public void linkTagsToPost(Long postId, Set<Tag> tags) {
        tagJdbcRepository.linkTagsToPost(postId, tags);
    }
}
