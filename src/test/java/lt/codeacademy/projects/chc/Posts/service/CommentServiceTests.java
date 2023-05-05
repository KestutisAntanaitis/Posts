package lt.codeacademy.projects.chc.coronahatersclub.service;


import lt.codeacademy.projects.chc.coronahatersclub.entity.Comment;
import lt.codeacademy.projects.chc.coronahatersclub.entity.Post;
import lt.codeacademy.projects.chc.coronahatersclub.entity.User;
import lt.codeacademy.projects.chc.coronahatersclub.exception.CustomValidationException;
import lt.codeacademy.projects.chc.coronahatersclub.repository.CommentRepository;
import lt.codeacademy.projects.chc.coronahatersclub.repository.PostRepository;
import lt.codeacademy.projects.chc.coronahatersclub.repository.UserRepository;
import lt.codeacademy.projects.chc.coronahatersclub.validator.CommentActionValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.firewall.RequestRejectedException;

import javax.transaction.Transactional;
import java.security.InvalidParameterException;
import java.util.List;
import java.util.Optional;

import static lt.codeacademy.projects.chc.coronahatersclub.util.TestsPostUtil.createTestPost;
import static lt.codeacademy.projects.chc.coronahatersclub.util.TestsUserUtil.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class CommentServiceTests {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private CommentActionValidator commentActionValidator;

    @Autowired
    private CommentService commentService;

    @Autowired
    private UserRepository userRepository;

    private User user;

    @BeforeEach
    public void setUp() {
        commentService = new CommentService(commentRepository, postRepository, commentActionValidator);
        user = createTestUserUser1();
    }

    @Test
    public void callsCreateNewCommentWithValidBodySuccessfullySavesToDatabaseTest() {
        String body = "test comment body";

        Post post = postRepository.save(createTestPost(user));

        String result = commentService.createNewComment(post.getId(), body, user);

        assertEquals("new comment generated", result);
        assertEquals(1, post.getComments().size());

        Comment comment = post.getComments().iterator().next();

        assertEquals(post, comment.getPost());
        assertEquals(body, comment.getBody());
        assertEquals(user, comment.getUser());
    }

    @Test
    public void callsCreateNewCommentWithEmptyBodyThrowsExceptionAndDoesNotSaveCommentTest() {
        String body = "";

        Post post = postRepository.save(createTestPost(user));

        assertThrows(CustomValidationException.class, () -> {
            commentService.createNewComment(post.getId(), body, user);
        });

        Optional<Post> optionalPost = postRepository.findById(post.getId());
        assertTrue(optionalPost.isPresent());
        Post persistedPost = optionalPost.get();
        assertTrue(persistedPost.getComments().isEmpty());
    }

    @Test
    public void callsGetAllUserCommentsReturnsListOfCommentsTest() {
        userRepository.save(user);

        Comment comment1 = new Comment();
        comment1.setBody("test comment 1");
        comment1.setUser(user);

        Comment comment2 = new Comment();
        comment2.setBody("test comment 2");
        comment2.setUser(user);

        commentRepository.saveAll(List.of(comment1, comment2));

        List<Comment> comments = commentService.getAllUserComments(user);

        assertEquals(2, comments.size());
        assertTrue(comments.contains(comment1));
        assertTrue(comments.contains(comment2));
    }

    @Test
    public void callsGetAllUserCommentsForUserWithoutCommentsReturnsEmptyListTest() {
        userRepository.save(user);
        List<Comment> comments = commentService.getAllUserComments(user);
        assertTrue(comments.isEmpty());
    }

    @Test
    public void callsDeleteCommentDeletesCommentFromDatabaseTest() {
        User u = userRepository.save(user);

        Post post = postRepository.save(createTestPost(u));

        Comment comment = new Comment();
        comment.setBody("test comment");
        comment.setUser(u);
        comment.setPost(post);

        commentRepository.save(comment);

        String result = commentService.deleteComment(user, comment.getId());

        assertEquals("redirect:/posts", result);
        assertFalse(post.getComments().contains(comment));
        assertNull(commentRepository.findById(comment.getId()).orElse(null));
    }
    @Test
    public void callsDeleteCommentByOtherUserThanAuthorThrowsAccessDeniedExceptionTest() {
        User u1 = userRepository.save(user);
        User u2 = userRepository.save(createTestUserUser2());

        Post post = postRepository.save(createTestPost(u1));
        Comment comment = new Comment(post,"",u1);
        commentRepository.save(comment);

        assertThrows(AccessDeniedException.class, () -> commentService.deleteComment(u2, comment.getId()));
    }

    @Test
    public void callsDeleteCommentByAdminSuccessfullyDeletesCommentTest() {
        User u1 = userRepository.save(user);
        User adminUser = userRepository.save(createTestUserAdmin());

        Post post = postRepository.save(createTestPost(u1));
        Comment comment = new Comment(post,"",u1);
        commentRepository.save(comment);

        commentService.deleteComment(adminUser, comment.getId());

        assertNull(commentRepository.findById(comment.getId()).orElse(null));
    }

    @Test
    public void callsEditCommentWithValidBodyResultsInSuccessfullyEditedCommentTest() {
        User u = userRepository.save(user);

        Post post = postRepository.save(createTestPost(u));

        Comment comment = new Comment();
        comment.setBody("test comment");
        comment.setUser(user);
        comment.setPost(post);
        commentRepository.save(comment);

        String result = commentService.editComment(user, comment.getId(), "new comment body");

        assertEquals("redirect:/posts", result);
        assertEquals("new comment body", commentRepository.findById(comment.getId()).orElse(null).getBody());
    }

    @Test
    public void callsEditCommentByNonAdminNonAuthorThrowsAccessDeniedException() {
        User u = userRepository.save(user);
        User u2 = userRepository.save(createTestUserUser2());

        Post post = postRepository.save(createTestPost(u));

        Comment comment = new Comment();
        comment.setBody("test comment");
        comment.setUser(u);
        comment.setPost(post);
        commentRepository.save(comment);

        assertThrows(AccessDeniedException.class, () -> commentService.editComment(u2, comment.getId(), "new comment body"));
        assertEquals("test comment", commentRepository.findById(comment.getId()).orElse(null).getBody());
    }

    //Admin can only delete comments, does not make sense to allow admin to edit user comments.
    @Test
    public void callsEditCommentByAdminNonAuthorThrowsAccessDeniedException() {
        User u = userRepository.save(user);
        User u2 = userRepository.save(createTestUserAdmin());

        Post post = postRepository.save(createTestPost(u));

        Comment comment = new Comment();
        comment.setBody("test comment");
        comment.setUser(u);
        comment.setPost(post);
        commentRepository.save(comment);

        assertThrows(AccessDeniedException.class, () -> commentService.editComment(u2, comment.getId(), "new comment body"));
        assertEquals("test comment", commentRepository.findById(comment.getId()).orElse(null).getBody());
    }

    @Test
    public void callsEditCommentWithEmptyBodyThrowsRequestRejectedException() {
        User u = userRepository.save(user);

        Post post = postRepository.save(createTestPost(u));

        Comment comment = new Comment();
        comment.setBody("test comment");
        comment.setUser(user);
        comment.setPost(post);
        commentRepository.save(comment);

        assertThrows(RequestRejectedException.class, () -> commentService.editComment(user, comment.getId(), ""));
        assertEquals("test comment", commentRepository.findById(comment.getId()).orElse(null).getBody());
    }

}
