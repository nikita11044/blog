package practicum.blog.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import practicum.blog.dto.post.PostDTO;
import practicum.blog.dto.post.PostRequestDTO;
import practicum.blog.entity.Comment;
import practicum.blog.entity.Post;
import practicum.blog.entity.Tag;
import practicum.blog.mapper.PostMapper;
import practicum.blog.repository.PostRepository;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final PostMapper postMapper;
    private final FileService fileService;
    private final TagService tagService;
    private final CommentService commentService;

    @Transactional
    public Long create(PostRequestDTO dto) {
        Post post = postMapper.toEntity(dto);
        handleImageUpload(dto, post);
        Long postId = postRepository.create(post);

        Set<Tag> tags = tagService.createMultipleFromString(dto.getTagsAsString());
        tagService.linkTagsToPost(postId, tags);

        return postId;
    }

    @Transactional
    public PostDTO getById(long id) {
        Post post = postRepository.findById(id);
        enrichPostsWithTagsAndComments(List.of(post));
        return postMapper.toDTO(post);
    }

    @Transactional
    public List<PostDTO> getByTagName(String search, int pageNumber, int pageSize) {
        List<Post> posts = search.isBlank()
                ? postRepository.findAll(pageNumber, pageSize)
                : postRepository.findByTagName(search, pageNumber, pageSize);

        enrichPostsWithTagsAndComments(posts);

        return posts.stream()
                .map(postMapper::toDTO)
                .collect(Collectors.toList());
    }

    public long countPostsByTag(String tagName) {
        return tagName.isBlank()
                ? postRepository.countAll()
                : postRepository.countByTagName(tagName);
    }

    @Transactional
    public void update(PostRequestDTO dto) {
        Post post = postRepository.findById(dto.getId());

        post.setTitle(dto.getTitle());
        post.setText(dto.getText());
        post.setUpdatedAt(LocalDateTime.now());

        if (dto.getTagsAsString() != null) {
            tagService.syncTagsWithPost(post.getId(), dto.getTagsAsString());
        }

        if (dto.getImage() != null) {
            fileService.deleteFile(post.getImagePath());
            handleImageUpload(dto, post);
        }

        postRepository.update(post);
    }

    @Transactional
    public void updateLikes(Long postId, boolean like) {
        Post post = postRepository.findById(postId);

        int likes = post.getLikesCount();
        post.setLikesCount(like ? likes + 1 : Math.max(0, likes - 1));

        postRepository.update(post);
    }

    @Transactional
    public void delete(Long id) {
        Post post = postRepository.findById(id);
        fileService.deleteFile(post.getImagePath());
        postRepository.deleteById(id);
    }

    private void enrichPostsWithTagsAndComments(List<Post> posts) {
        if (posts.isEmpty()) return;

        List<Long> postIds = posts.stream()
                .map(Post::getId)
                .collect(Collectors.toList());

        Map<Long, Set<Tag>> tags = tagService.findTagsByPostIds(postIds);
        Map<Long, Set<Comment>> comments = commentService.findCommentsByPostIds(postIds);

        for (Post post : posts) {
            post.setTags(tags.getOrDefault(post.getId(), new HashSet<>()));
            post.setComments(comments.getOrDefault(post.getId(), new HashSet<>()));
        }
    }

    private void handleImageUpload(PostRequestDTO dto, Post post) {
        post.setImagePath(fileService.uploadFile(dto.getImage()));
    }
}
