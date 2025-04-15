package practicum.blog.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import practicum.blog.entity.Tag;
import practicum.blog.repository.TagRepository;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TagServiceTest {

    @InjectMocks
    private TagService tagService;

    @Mock
    private TagRepository tagRepository;

    private Tag existingTag;

    @BeforeEach
    void setUp() {
        existingTag = Tag.builder().id(1L).name("java").build();
    }

    @Test
    void createMultipleFromString_ShouldCreateAll_WhenNoneExist() {
        String tagInput = "java,spring,hibernate";
        Set<String> tagNames = Set.of("java", "spring", "hibernate");

        when(tagRepository.findMultipleByNames(tagNames)).thenReturn(new HashSet<>());

        Set<Tag> result = tagService.createMultipleFromString(tagInput);

        assertEquals(3, result.size());
        assertTrue(result.stream().anyMatch(tag -> tag.getName().equals("java")));

        verify(tagRepository).findMultipleByNames(tagNames);
        verify(tagRepository).saveAll(argThat(tags ->
                tags.size() == 3 &&
                        tags.stream().map(Tag::getName).collect(Collectors.toSet()).containsAll(tagNames)
        ));
    }

    @Test
    void createMultipleFromString_ShouldOnlyCreateMissing_WhenSomeExist() {
        String tagInput = "java,spring,hibernate";
        Set<String> tagNames = Set.of("java", "spring", "hibernate");

        Tag existing = Tag.builder().id(1L).name("java").build();
        when(tagRepository.findMultipleByNames(tagNames)).thenReturn(new HashSet<>(Set.of(existing)));

        Set<Tag> result = tagService.createMultipleFromString(tagInput);

        assertEquals(3, result.size());
        assertTrue(result.contains(existing));

        verify(tagRepository).saveAll(argThat(tags ->
                tags.size() == 2 &&
                        tags.stream().noneMatch(tag -> tag.getName().equals("java"))
        ));
    }

    @Test
    void createMultipleFromString_ShouldReturnExisting_WhenAllExist() {
        String tagInput = "java,spring";
        Set<String> tagNames = Set.of("java", "spring");

        Tag java = Tag.builder().id(1L).name("java").build();
        Tag spring = Tag.builder().id(2L).name("spring").build();

        when(tagRepository.findMultipleByNames(tagNames)).thenReturn(new HashSet<>(Set.of(java, spring)));

        Set<Tag> result = tagService.createMultipleFromString(tagInput);

        assertEquals(2, result.size());
        assertTrue(result.contains(java));
        assertTrue(result.contains(spring));

        verify(tagRepository).findMultipleByNames(tagNames);
        verify(tagRepository).saveAll(Collections.emptySet());
    }

    @Test
    void createMultipleFromString_ShouldReturnEmpty_WhenInputIsEmpty() {
        Set<Tag> result = tagService.createMultipleFromString("   , , , ");

        assertTrue(result.isEmpty());

        verify(tagRepository).findMultipleByNames(Collections.emptySet());
        verify(tagRepository).saveAll(Collections.emptySet());
    }

    @Test
    void createMultipleFromString_ShouldTrimWhitespace() {
        String input = " spring ,  java, ,spring ";

        Set<Tag> existingTags = new HashSet<>(Set.of(existingTag));
        when(tagRepository.findMultipleByNames(anySet())).thenReturn(existingTags);

        doAnswer(invocation -> {
            Set<Tag> tags = invocation.getArgument(0);
            existingTags.addAll(tags);
            return null;
        }).when(tagRepository).saveAll(anySet());

        Set<Tag> result = tagService.createMultipleFromString(input);

        assertEquals(2, result.size());
        verify(tagRepository).findMultipleByNames(Set.of("spring", "java"));
        verify(tagRepository).saveAll(anySet());
    }
}
