package practicum.blog.dto.post;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;
import practicum.blog.entity.Comment;
import practicum.blog.entity.Tag;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Validated
public class PostDTO {
    @NotNull
    private Long id;

    @NotEmpty
    @Size(max = 255, message = "Title must be less than 255 characters")
    private String title;

    @Size(max = 500, message = "Image path must be less than 500 characters")
    private String imagePath;

    private String text;

    private String textPreview;

    private Set<Tag> tags;

    private String tagsAsString;

    private int likesCount;

    private Set<Comment> comments;
}
