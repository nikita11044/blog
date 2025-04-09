package practicum.blog.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import practicum.blog.dto.post.PostDTO;
import practicum.blog.dto.post.PostRequestDTO;
import practicum.blog.entity.Post;
import practicum.blog.mapper.PostMapper;
import practicum.blog.repository.PostRepository;

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

        var tags = tagService.createMultipleFromString(dto.getTagsAsString());
        post.setTags(tags);

        if (dto.getImage() != null) {
            post.setImagePath(fileService.uploadFile(dto.getImage()));
        }

        return postRepository.save(post);
    }

    @Transactional
    public PostDTO getById(long id) {
        var post = postRepository.findById(id);
        return postMapper.toDTO(post);
    }

    public Page<PostDTO> getByTagName(String search, int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize, Sort.by("createdAt").descending());

        Page<Post> postPage;

        if (search.isBlank()) {
            postPage = postRepository.findAll(pageable);
        } else {
            postPage = postRepository.findByTagName(search, pageable);
        }

        List<PostDTO> postDTOs = postPage.getContent().stream()
                .map(postMapper::toDTO)
                .collect(Collectors.toList());

        return new PageImpl<>(postDTOs, pageable, postPage.getTotalElements());
    }

    @Transactional
    public void update(PostRequestDTO dto) {
        var post = postRepository.findById(dto.getId());

        post.setTitle(dto.getTitle());
        post.setText(dto.getText());

        if (dto.getTagsAsString() != null) {
            post.setTags(tagService.createMultipleFromString(dto.getTagsAsString()));
        }

        if (dto.getImage() != null) {
            fileService.deleteFile(post.getImagePath());
            post.setImagePath(fileService.uploadFile(dto.getImage()));
        }

        postRepository.save(post);
    }

    @Transactional
    public void updateLikes(Long postId, boolean like) {
        var post = postRepository.findById(postId);

        if (like) {
            post.setLikesCount(post.getLikesCount() + 1);
        } else if (post.getLikesCount() > 0) {
            post.setLikesCount(post.getLikesCount() - 1);
        }

        postRepository.save(post);
    }


    @Transactional
    public void delete(Long id) {
        var post = postRepository.findById(id);
        fileService.deleteFile(post.getImagePath());
        postRepository.deleteById(id);
    }
}
