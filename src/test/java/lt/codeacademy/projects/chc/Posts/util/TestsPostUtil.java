package lt.codeacademy.projects.chc.Posts.util;

import lt.codeacademy.projects.chc.Posts.entity.Post;
import lt.codeacademy.projects.chc.Posts.entity.User;

public class TestsPostUtil {

    public static Post createTestPost(User user) {
        return new Post(user, "post123", "test post body");
    }
}
