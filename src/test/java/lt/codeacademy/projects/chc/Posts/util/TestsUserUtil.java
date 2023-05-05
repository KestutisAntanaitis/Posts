package lt.codeacademy.projects.chc.Posts.util;

import lt.codeacademy.projects.chc.Posts.entity.User;
import lt.codeacademy.projects.chc.Posts.enums.UserRole;

public class TestsUserUtil {

    public static User createTestUserUser1() {
        User user = new User("username123", "password123", "user1@user.com", UserRole.USER);
        return user;
    }

    public static User createTestUserUser2() {
        User user = new User("username321", "password321", "user2@user.com", UserRole.USER);
        return user;
    }

    public static User createTestUserAdmin() {
        User user = new User("username456", "password456", "admin123@admin.com", UserRole.ADMIN);
        return user;
    }
}
