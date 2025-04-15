package practicum.blog.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import practicum.blog.dto.post.PostDTO;
import practicum.blog.dto.post.PostRequestDTO;
import practicum.blog.entity.Post;
import practicum.blog.mapper.PostMapper;
import practicum.blog.repository.PostRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final PostMapper postMapper;
    private final FileService fileService;
    private final TagService tagService;

    @Transactional
    public Long create(PostRequestDTO dto) {
        var post = postMapper.toEntity(dto);

        if (dto.getImage() != null) {
            post.setImagePath(fileService.uploadFile(dto.getImage()));
        }

        Long postId = postRepository.create(post);

        var tags = tagService.createMultipleFromString(dto.getTagsAsString());

        tagService.linkTagsToPost(postId, tags);

        return postId;
    }


    @Transactional
    public PostDTO getById(long id) {
        var post = postRepository.findById(id);
        return postMapper.toDTO(post);
    }

    public List<PostDTO> getByTagName(String search, int pageNumber, int pageSize) {
        List<Post> posts;

        if (search.isBlank()) {
            posts = postRepository.findAll(pageNumber, pageSize);
        } else {
            posts = postRepository.findByTagName(search, pageNumber, pageSize);
        }

        return posts.stream()
                .map(postMapper::toDTO)
                .collect(Collectors.toList());
    }

    public long countPostsByTag(String tagName) {
        if (tagName.isBlank()) {
            return postRepository.countAll();
        } else {
            return postRepository.countByTagName(tagName);
        }
    }

    @Transactional
    public void update(PostRequestDTO dto) {
        var post = postRepository.findById(dto.getId());

        post.setTitle(dto.getTitle());
        post.setText(dto.getText());
        post.setUpdatedAt(LocalDateTime.now());

        if (dto.getTagsAsString() != null) {
            tagService.syncTagsWithPost(post.getId(), dto.getTagsAsString());
        }

        if (dto.getImage() != null) {
            fileService.deleteFile(post.getImagePath());
            post.setImagePath(fileService.uploadFile(dto.getImage()));
        }

        postRepository.update(post);
    }


    @Transactional
    public void updateLikes(Long postId, boolean like) {
        var post = postRepository.findById(postId);

        if (like) {
            post.setLikesCount(post.getLikesCount() + 1);
        } else if (post.getLikesCount() > 0) {
            post.setLikesCount(post.getLikesCount() - 1);
        }

        postRepository.update(post);
    }

    @Transactional
    public void delete(Long id) {
        var post = postRepository.findById(id);
        fileService.deleteFile(post.getImagePath());
        postRepository.deleteById(id);
    }
}
