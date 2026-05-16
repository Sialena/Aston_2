package example.service;

import example.bd.User;
import example.dao.UserDAO;
import example.exception.UserNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceDefaultTest {

    @Mock
    private UserDAO userDAO;

    @InjectMocks
    private UserServiceDefault userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User("Test", "test@example.com", 25);
        testUser.setId(1L);
    }

    @Test
    void getUserById_ShouldReturnUser_WhenExists() {
        when(userDAO.getUserById(1L)).thenReturn(testUser);
        User found = userService.getUserById(1L);
        assertNotNull(found);
        assertEquals("Test", found.getName());
        verify(userDAO).getUserById(1L);
    }

    @Test
    void getUserById_ShouldThrowException_WhenNotFound() {
        when(userDAO.getUserById(99L)).thenReturn(null);
        assertThrows(UserNotFoundException.class, () -> userService.getUserById(99L));
    }

    @Test
    void createUser_ShouldReturnId_WhenValid() {
        when(userDAO.saveUser(any(User.class))).thenReturn(1L);
        Long id = userService.createUser(testUser);
        assertEquals(1L, id);
        verify(userDAO).saveUser(testUser);
    }

    @Test
    void createUser_ShouldReturnNull_WhenDaoFails() {
        when(userDAO.saveUser(any(User.class))).thenReturn(null);
        Long id = userService.createUser(testUser);
        assertNull(id);
    }

    @Test
    void getAllUsers_ShouldReturnList() {
        List<User> users = List.of(testUser, new User("Jane", "jane@example.com", 30));
        when(userDAO.getAllUsers()).thenReturn(users);
        List<User> result = userService.getAllUsers();
        assertEquals(2, result.size());
        verify(userDAO).getAllUsers();
    }

    @Test
    void updateUser_ShouldReturnTrue_WhenUserExistsAndUpdated() {
        when(userDAO.updateUser(any(User.class))).thenReturn(true);
        boolean updated = userService.updateUser(testUser);
        assertTrue(updated);
    }

    @Test
    void updateUser_ShouldReturnFalse_WhenIdIsNull() {
        testUser.setId(null);
        boolean updated = userService.updateUser(testUser);
        assertFalse(updated);
        verify(userDAO, never()).updateUser(any());
    }

    @Test
    void deleteUser_ShouldReturnTrue_WhenDeleted() {
        when(userDAO.deleteUserById(1L)).thenReturn(true);
        boolean deleted = userService.deleteUser(1L);
        assertTrue(deleted);
    }

    @Test
    void findUsersByName_ShouldReturnList() {
        List<User> users = List.of(testUser);
        when(userDAO.findUsersByName("Test")).thenReturn(users);
        List<User> result = userService.findUsersByName("Test");
        assertEquals(1, result.size());
    }
}