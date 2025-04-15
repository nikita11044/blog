package practicum.blog.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Builder
public class Post {
    private Long id;
    private String title;
    private String imagePath;
    private String text;
    private int likesCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Set<Tag> tags = new HashSet<>();
    private Set<Comment> comments = new HashSet<>();
}
