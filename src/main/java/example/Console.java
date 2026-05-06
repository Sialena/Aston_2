package com.example;

import java.util.List;
import java.util.Scanner;
   
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.bd.HibernateUtil;
import com.example.bd.User;
import com.example.dao.UserDAO;

public class Console {

    private static final Logger logger = LoggerFactory.getLogger(Console.class);
    private static final UserDAO userDAO = new UserDAO();
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        logger.info("Запуск User Service приложения");

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Завершение работы, закрытие SessionFactory");
            HibernateUtil.shutdown();
        }));

        boolean running = true;
        while (running) {
            showMenu();
            int choice = getIntInput("Выберите операцию: ");

            switch (choice) {
                case 1:
                    createUser();
                    break;
                case 2:
                    showAllUsers();
                    break;
                case 3:
                    findUserById();
                    break;
                case 4:
                    updateUser();
                    break;
                case 5:
                    deleteUser();
                    break;
                case 0:
                    running = false;
                    System.out.println("До свидания!");
                    break;
                default:
                    System.out.println("Неверный выбор. Попробуйте снова.");
            }
        }
        scanner.close();
        HibernateUtil.shutdown();
    }

    private static void showMenu() {
        System.out.println("\nВыберите опцию");
        System.out.println("1. Создать пользователя");
        System.out.println("2. Показать всех пользователей");
        System.out.println("3. Найти пользователя по ID");
        System.out.println("4. Обновить пользователя");
        System.out.println("5. Удалить пользователя");
        System.out.println("0. Выход");
    }

    private static int getIntInput(String prompt) {
        System.out.print(prompt);
        while (!scanner.hasNextInt()) {
            System.out.print("Ошибка: введите число. " + prompt);
            scanner.next();
        }
        int result = scanner.nextInt();
        scanner.nextLine(); // очистка буфера
        return result;
    }

    private static void createUser() {
        System.out.println("\nСоздание пользователя");
        System.out.print("Имя: ");
        String name = scanner.nextLine();
        System.out.print("Email: ");
        String email = scanner.nextLine();
        System.out.print("Возраст: ");
        int age = getIntInput("");

        User user = new User(name, email, age);
        Long id = userDAO.saveUser(user);
        if (id != null) {
            System.out.println("Пользователь создан с ID: " + id);
            logger.info("Создан пользователь: {}", email);
        } else {
            System.out.println("Ошибка при создании пользователя (возможно, email уже существует).");
        }
    }

    private static void showAllUsers() {
        System.out.println("\n--- Список всех пользователей ---");
        List<User> users = userDAO.getAllUsers();
        if (users == null || users.isEmpty()) {
            System.out.println("Пользователей нет.");
        } else {
            users.forEach(System.out::println);
        }
    }

    private static void findUserById() {
        System.out.println("\n--- Поиск пользователя по ID ---");
        Long id = (long) getIntInput("Введите ID: ");
        User user = userDAO.getUserById(id);
        if (user == null) {
            System.out.println("Пользователь с ID " + id + " не найден.");
        } else {
            System.out.println(user);
        }
    }

    private static void updateUser() {
        System.out.println("\nОбновление пользователя");
        Long id = (long) getIntInput("Введите ID пользователя для обновления: ");
        User user = userDAO.getUserById(id);
        if (user == null) {
            System.out.println("Пользователь не найден.");
            return;
        }

        System.out.print("Новое имя (оставьте пустым, чтобы не менять): ");
        String name = scanner.nextLine();
        if (!name.isBlank()) user.setName(name);

        System.out.print("Новый email (оставьте пустым, чтобы не менять): ");
        String email = scanner.nextLine();
        if (!email.isBlank()) user.setEmail(email);

        System.out.print("Новый возраст (0 - чтобы не менять): ");
        int age = getIntInput("");
        if (age > 0) user.setAge(age);

        boolean updated = userDAO.updateUser(user);
        if (updated) {
            System.out.println("Пользователь обновлён.");
        } else {
            System.out.println("Ошибка обновления (возможно, email уже занят).");
        }
    }

    private static void deleteUser() {
        System.out.println("\n Удаление пользователя");
        Long id = (long) getIntInput("Введите ID пользователя для удаления: ");
        boolean deleted = userDAO.deleteUserById(id);
        if (deleted) {
            System.out.println("Пользователь удалён.");
        } else {
            System.out.println("Пользователь с ID " + id + " не найден.");
        }
    }
}