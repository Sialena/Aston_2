package example.dao;

import example.bd.User;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
class UserDAOTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    private static SessionFactory sessionFactory;
    private UserDAO userDAO;

    @BeforeAll
    static void createSessionFactory() {
        Configuration cfg = new Configuration();
        cfg.setProperty("hibernate.connection.driver_class", "org.postgresql.Driver");
        cfg.setProperty("hibernate.connection.url", postgres.getJdbcUrl());
        cfg.setProperty("hibernate.connection.username", postgres.getUsername());
        cfg.setProperty("hibernate.connection.password", postgres.getPassword());
        cfg.setProperty("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
        cfg.setProperty("hibernate.hbm2ddl.auto", "create-drop");
        cfg.addAnnotatedClass(User.class);
        sessionFactory = cfg.buildSessionFactory();
    }

    @AfterAll
    static void closeSessionFactory() {
        if (sessionFactory != null) sessionFactory.close();
    }

    @BeforeEach
    void setUp() {
        userDAO = new UserDAO(sessionFactory);
    }

    @Test
    void testSaveAndGetUser() {
        User user = new User("Alice", "alice@example.com", 30);
        Long id = userDAO.saveUser(user);
        assertNotNull(id);

        User found = userDAO.getUserById(id);
        assertNotNull(found);
        assertEquals("Alice", found.getName());
        assertEquals("alice@example.com", found.getEmail());
        assertEquals(30, found.getAge());
        assertNotNull(found.getCreatedAt());
    }

    @Test
    void testGetAllUsers() {
        userDAO.saveUser(new User("Bob", "bob@example.com", 25));
        userDAO.saveUser(new User("Charlie", "charlie@example.com", 35));
        List<User> users = userDAO.getAllUsers();
        assertEquals(2, users.size());
    }

    @Test
    void testUpdateUser() {
        User user = new User("David", "david@example.com", 40);
        Long id = userDAO.saveUser(user);
        user.setName("David Updated");
        user.setAge(41);
        boolean updated = userDAO.updateUser(user);
        assertTrue(updated);
        User updatedUser = userDAO.getUserById(id);
        assertEquals("David Updated", updatedUser.getName());
        assertEquals(41, updatedUser.getAge());
    }

    @Test
    void testDeleteUser() {
        User user = new User("Eve", "eve@example.com", 28);
        Long id = userDAO.saveUser(user);
        boolean deleted = userDAO.deleteUserById(id);
        assertTrue(deleted);
        assertNull(userDAO.getUserById(id));
    }

    @Test
    void testFindUsersByName() {
        userDAO.saveUser(new User("John Smith", "john@example.com", 20));
        userDAO.saveUser(new User("John Doe", "doe@example.com", 22));
        List<User> found = userDAO.findUsersByName("John");
        assertEquals(2, found.size());
    }
}