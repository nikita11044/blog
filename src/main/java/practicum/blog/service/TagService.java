package practicum.blog.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import practicum.blog.entity.Tag;
import practicum.blog.repository.TagRepository;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class TagService {
    private final TagRepository tagRepository;

    public Set<Tag> createMultipleFromString(String tagsAsString) {
        Set<String> tagNames = Stream.of(tagsAsString.split(","))
                .map(String::trim)
                .filter(name -> !name.isEmpty())
                .collect(Collectors.toSet());

        Set<Tag> existingTags = tagRepository.findMultipleByNames(tagNames);

        Set<String> nonExistingTagNames = tagNames.stream()
                .filter(tagName -> existingTags.stream().noneMatch(tag -> tag.getName().equals(tagName)))
                .collect(Collectors.toSet());

        Set<Tag> newTags = nonExistingTagNames.stream()
                .map(tagName -> Tag.builder().name(tagName).build())
                .collect(Collectors.toSet());

        tagRepository.saveAll(newTags);

        existingTags.addAll(newTags);

        return existingTags;
    }

    public void linkTagsToPost(Long postId, Set<Tag> tags) {
        for (Tag tag : tags) {
            tagRepository.attachTagToPost(postId, tag.getId());
        }
    }

    @Transactional
    public void syncTagsWithPost(Long postId, String tagsAsString) {
        Set<String> requestedTagNames = Stream.of(tagsAsString.split(","))
                .map(String::trim)
                .filter(name -> !name.isEmpty())
                .collect(Collectors.toSet());

        if (requestedTagNames.isEmpty()) {
            tagRepository.unlinkAllTagsFromPost(postId);
            return;
        }

        Set<Tag> existingTags = tagRepository.findMultipleByNames(requestedTagNames);

        Set<String> existingTagNames = existingTags.stream()
                .map(Tag::getName)
                .collect(Collectors.toSet());

        Set<String> missingTagNames = new HashSet<>(requestedTagNames);
        missingTagNames.removeAll(existingTagNames);

        Set<Tag> newTags = missingTagNames.stream()
                .map(name -> Tag.builder().name(name).build())
                .collect(Collectors.toSet());

        tagRepository.saveAll(newTags);

        Set<Tag> allTagsToLink = new HashSet<>(existingTags);
        allTagsToLink.addAll(newTags);

        tagRepository.unlinkTagsNotInList(postId, requestedTagNames);
        tagRepository.linkTagsToPost(postId, allTagsToLink);
    }

}
