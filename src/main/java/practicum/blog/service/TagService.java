package practicum.blog.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import practicum.blog.entity.Tag;
import practicum.blog.repository.TagRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class TagService {
    private final TagRepository tagRepository;

    public Set<Tag> createMultipleFromString(String tagsAsString) {
        Set<String> tagNames = parseTagNames(tagsAsString);
        return findOrCreateTags(tagNames);
    }

    public void linkTagsToPost(Long postId, Set<Tag> tags) {
        tags.forEach(tag -> tagRepository.attachTagToPost(postId, tag.getId()));
    }

    @Transactional
    public void syncTagsWithPost(Long postId, String tagsAsString) {
        Set<String> requestedTagNames = parseTagNames(tagsAsString);

        if (requestedTagNames.isEmpty()) {
            tagRepository.unlinkAllTagsFromPost(postId);
            return;
        }

        Set<Tag> tagsToLink = findOrCreateTags(requestedTagNames);

        tagRepository.unlinkTagsNotInList(postId, requestedTagNames);
        tagRepository.linkTagsToPost(postId, tagsToLink);
    }

    public Map<Long, Set<Tag>> findTagsByPostIds(List<Long> postIds) {
        return tagRepository.findAllByPostIds(postIds);
    }

    private Set<String> parseTagNames(String tagsAsString) {
        return Stream.of(tagsAsString.split(","))
                .map(String::trim)
                .filter(name -> !name.isEmpty())
                .collect(Collectors.toSet());
    }

    private Set<Tag> findOrCreateTags(Set<String> tagNames) {
        Set<Tag> existingTags = tagRepository.findMultipleByNames(tagNames);
        Set<String> existingTagNames = existingTags.stream()
                .map(Tag::getName)
                .collect(Collectors.toSet());

        Set<String> missingTagNames = new HashSet<>(tagNames);
        missingTagNames.removeAll(existingTagNames);

        Set<Tag> newTags = missingTagNames.stream()
                .map(name -> Tag.builder().name(name).build())
                .collect(Collectors.toSet());

        tagRepository.saveAll(newTags);

        Set<Tag> allTags = new HashSet<>(existingTags);
        allTags.addAll(newTags);

        return allTags;
    }
}
