package example.service;

import example.bd.User;
import example.dao.UserDAO;
import example.exception.UserNotFoundException;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserServiceDefault implements UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserServiceDefault.class);
    private final UserDAO userDAO;

    public UserServiceDefault() {
        this.userDAO = new UserDAO();
    }

    @Override
    public User getUserById(Long id) {
        User user = userDAO.getUserById(id);
        if (user == null) {
            throw new UserNotFoundException("User not found: id = " + id);
        }
        return user;
    }

    @Override
    public List<User> getAllUsers() {
        return userDAO.getAllUsers();
    }

    @Override
    public Long createUser(User user) {
        return userDAO.saveUser(user);
    }

    @Override
    public boolean updateUser(User user) {
        if (user.getId() == null) {
            return false;
        }
        return userDAO.updateUser(user);
    }

    @Override
    public boolean deleteUser(Long id) {
        return userDAO.deleteUserById(id);
    }

    @Override
    public List<User> findUsersByName(String name) {
        return userDAO.findUsersByName(name);
    }
}