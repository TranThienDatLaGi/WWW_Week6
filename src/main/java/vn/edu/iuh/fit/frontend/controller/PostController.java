package vn.edu.iuh.fit.frontend.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import vn.edu.iuh.fit.backend.models.Post;
import vn.edu.iuh.fit.backend.models.PostComment;
import vn.edu.iuh.fit.backend.models.User;
import vn.edu.iuh.fit.backend.repositories.PostCommentRepository;
import vn.edu.iuh.fit.backend.repositories.PostRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
@RequestMapping("/posts")
public class PostController {
    private final PostRepository postRepository;
    private final PostCommentRepository postCommentRepository;
    private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    public PostController(PostRepository postRepository, PostCommentRepository postCommentRepository) {
        this.postRepository = postRepository;
        this.postCommentRepository = postCommentRepository;
    }

    @GetMapping(value = {""})
    public ModelAndView openPostPaging(@RequestParam("page") Optional<Integer> page, @RequestParam("size") Optional<Integer> size, HttpSession session) {
        int pageNum = page.orElse(1);
        int sizeNum = size.orElse(10);
        PageRequest pageable = PageRequest.of(pageNum - 1, sizeNum, Sort.by("publishedAt"));
        Page<Post> posts = postRepository.findAllByPublished(true, pageable);
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("user", session.getAttribute("user"));
        modelAndView.addObject("posts", posts);
        modelAndView.addObject("pages", IntStream.rangeClosed(1, posts.getTotalPages()).boxed().collect(Collectors.toList()));
        modelAndView.setViewName("post/posts");
        return modelAndView;
    }
    @GetMapping("/myPost")
    public ModelAndView openMyPost(@RequestParam("page") Optional<Integer> page, @RequestParam("size") Optional<Integer> size, HttpSession session) {
        int pageNum = page.orElse(1);
        int sizeNum = size.orElse(10);
        PageRequest pageable = PageRequest.of(pageNum - 1, sizeNum, Sort.by("publishedAt"));
        ModelAndView modelAndView = new ModelAndView();
        Object object = session.getAttribute("user");
        if (object == null) {
            modelAndView.setViewName( "redirect:/users/login");
            return modelAndView;
        }
        User user = (User) object;
        Page<Post> posts = postRepository.findAllByAuthorAndPublished(user,true, pageable);

        modelAndView.addObject("user", user);
        modelAndView.addObject("posts", posts);
        modelAndView.addObject("pages", IntStream.rangeClosed(1, posts.getTotalPages()).boxed().collect(Collectors.toList()));
        modelAndView.setViewName("post/myPost");
        return modelAndView;
    }
    @GetMapping("/add")
    public String addPost(HttpSession session, Model model) {
        Object object = session.getAttribute("user");
        if (object == null) {
            return "redirect:/users/login";
        }
        User user = (User) object;
        Post post = new Post();
        model.addAttribute("post", post);
        model.addAttribute("user", user);
        return "post/addPost";
    }

    @PostMapping("/add")
    public String addPost(@ModelAttribute("post") Post post, HttpSession session) {
        Object object = session.getAttribute("user");
        if (session.getAttribute("user") == null) {
            return "redirect:/users/login";
        }
        User user = (User) object;
        post.setAuthor(user);
        post.setPublished(true);
        post.setCreatedAt(Instant.now());
        post.setPublishedAt(Instant.now());
        post.setParent(null);
        postRepository.save(post);

        return "redirect:/posts";
    }
    @GetMapping(value = {"/{id}"})
    public ModelAndView postDetail(@PathVariable("id") String id, @RequestParam("page") Optional<Integer> page, HttpSession session) {
        ModelAndView modelAndView = new ModelAndView();
        Integer pageNum = page.orElse(1);
        try {
            Long idLong = Long.parseLong(id);
            Optional<Post> post = postRepository.findById(idLong);


            if (post.isPresent()) {
                PageRequest pageRequest = PageRequest.of(0, 5 * pageNum, Sort.by("createdAt").descending());

                Page<PostComment> comments = postCommentRepository.findAllByPostId(idLong, pageRequest);
                PostComment postComment = new PostComment();
                PostComment parenPostComment = new PostComment();

                modelAndView.addObject("post", post.get());
                modelAndView.addObject("comments", comments);
                modelAndView.addObject("postComment", postComment);
                modelAndView.addObject("parenPostComment", parenPostComment);
                modelAndView.addObject("user", session.getAttribute("user"));
                modelAndView.addObject("pageNext", comments.getSize() / 5 + 1);
                modelAndView.setViewName("post/detail");
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            modelAndView.setViewName("notFound");
        }

        return modelAndView;
    }
    @PostMapping("/{id}/comment")
    public ModelAndView addComment(@ModelAttribute("postComment") PostComment postComment, @ModelAttribute("parent-comment") String parentCommentId, @PathVariable("id") Long postId, HttpSession session) {
        ModelAndView modelAndView = new ModelAndView();

        Object object = session.getAttribute("user");

        if (object == null) {
            modelAndView.setViewName("redirect:/login");
            return modelAndView;
        }

        postComment.setId(null);
        postComment.setPost(new Post(postId));
        postComment.setPublished(true);
        postComment.setCreatedAt(Instant.now());
        postComment.setUser((User) object);
        if (postComment.getContent() != null && postComment.getContent().isEmpty())
            postComment.setContent(null);

        if (!parentCommentId.isEmpty()) {
            long parentCommentIdLong = Long.parseLong(parentCommentId);

            PostComment parentPostComment = new PostComment(parentCommentIdLong);

            postComment.setParent(parentPostComment);
        }

        postCommentRepository.save(postComment);

        modelAndView.setViewName("redirect:/posts/" + postId);
        return modelAndView;
    }

}
