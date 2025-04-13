package practicum.blog.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import practicum.blog.entity.Tag;
import practicum.blog.jpa.TagJpaRepository;
import practicum.blog.utils.BaseContextTest;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TagRepositoryIntegrationTest extends BaseContextTest {

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private TagJpaRepository tagJpaRepository;

    @BeforeEach
    void setUp() {
        dbHelper.clearAndResetDatabase();
    }

    private Tag createTag(String name) {
        return Tag.builder()
                .name(name)
                .build();
    }

    @Test
    void testSaveAll_shouldSaveTags() {
        Tag tag1 = createTag("Java");
        Tag tag2 = createTag("Spring");

        tagRepository.saveAll(Arrays.asList(tag1, tag2));

        List<Tag> tags = tagJpaRepository.findAll();
        assertEquals(2, tags.size());
        assertTrue(tags.stream().anyMatch(tag -> tag.getName().equals("Java")));
        assertTrue(tags.stream().anyMatch(tag -> tag.getName().equals("Spring")));
    }

    @Test
    void testFindMultipleByNames_shouldReturnTags() {
        Tag tag1 = createTag("Java");
        Tag tag2 = createTag("Spring");
        tagRepository.saveAll(Arrays.asList(tag1, tag2));

        Set<String> tagNames = new HashSet<>(Arrays.asList("Java", "Spring"));
        Set<Tag> foundTags = tagRepository.findMultipleByNames(tagNames);

        assertEquals(2, foundTags.size());
        assertTrue(foundTags.stream().anyMatch(tag -> tag.getName().equals("Java")));
        assertTrue(foundTags.stream().anyMatch(tag -> tag.getName().equals("Spring")));
    }

    @Test
    void testFindMultipleByNames_shouldReturnEmptyIfNoTagsFound() {
        Set<String> tagNames = new HashSet<>(List.of("NonExistentTag"));
        Set<Tag> foundTags = tagRepository.findMultipleByNames(tagNames);

        assertTrue(foundTags.isEmpty());
    }
}
