package lt.codeacademy.projects.chc.coronahatersclub.util;

import lt.codeacademy.projects.chc.coronahatersclub.entity.Post;
import lt.codeacademy.projects.chc.coronahatersclub.entity.User;

public class TestsPostUtil {

    public static Post createTestPost(User user) {
        return new Post(user, "post123", "test post body");
    }
}
