package example.service;

import example.bd.User;
import java.util.List;

public interface UserService {
    User getUserById(Long id);

    List<User> getAllUsers();

    Long createUser(User user);

    boolean updateUser(User user);

    boolean deleteUser(Long id);

    List<User> findUsersByName(String name);
}