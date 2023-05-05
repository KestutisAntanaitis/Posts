package lt.codeacademy.projects.chc.Posts.controller;

import lt.codeacademy.projects.chc.Posts.entity.User;
import lt.codeacademy.projects.chc.Posts.service.CommentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static lt.codeacademy.projects.chc.Posts.util.TestsUserUtil.createTestUserUser1;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class CommentControllerTests {
    @Mock
    private CommentService commentService;


    private CommentController commentController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        commentController = new CommentController(commentService);
    }
    @Test
    public void newCommentCalledAndExpectedRedirectReceivedTest() {
        User user = createTestUserUser1();
        Long postId = 1L;
        String body = "test comment body";

        String expectedRedirect = "redirect:/posts";

        String result = commentController.newComment(user, postId, body);

        verify(commentService).createNewComment(postId, body, user);
        assertEquals(expectedRedirect, result);
    }
}
