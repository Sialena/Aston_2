package com.example.demo.service;

import com.example.demo.dto.UserDto;
import com.example.demo.kafka.KafkaProducerService;
import com.example.demo.repository.User;
import com.example.demo.repository.UserRepository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final KafkaProducerService kafkaProducerService;

    public UserService(UserRepository userRepository, KafkaProducerService kafkaProducerService) {
        this.userRepository = userRepository;
        this.kafkaProducerService = kafkaProducerService;
    }

    public List<UserDto> findAll() {
        return userRepository.findAll()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public UserDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("User с id " + id + " не найден"));
        return toDto(user);
    }

    @Transactional
    public UserDto create(UserDto userDto) {
        if (userRepository.findByEmail(userDto.getEmail()).isPresent()) {
            throw new IllegalStateException("User с таким email уже существует");
        }
        User user = new User(userDto.getName(), userDto.getEmail(), userDto.getAge());
        User saved = userRepository.save(user);

        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                kafkaProducerService.sendMessage("CREATE", saved.getEmail());
            }
        });
        return toDto(saved);
    }

    @Transactional
    public UserDto update(Long id, UserDto userDto) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("User с id " + id + " не найден"));

        String newEmail = userDto.getEmail();
        if (newEmail != null && !newEmail.equals(existingUser.getEmail())) {
            Optional<User> optionalUser = userRepository.findByEmail(newEmail);
            if (optionalUser.isPresent()) {
                throw new IllegalStateException("User с таким email уже существует");
            }
            existingUser.setEmail(newEmail);
        }

        if (userDto.getName() != null) {
            existingUser.setName(userDto.getName());
        }
        if (userDto.getAge() != null) {
            existingUser.setAge(userDto.getAge());
        }

        User updated = userRepository.save(existingUser);
        return toDto(updated);
    }

    @Transactional
    public void delete(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("User с id " + id + " не существует"));
        userRepository.delete(user);

        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                kafkaProducerService.sendMessage("DELETE", user.getEmail());
            }
        });
    }

    private UserDto toDto(User user) {
        return new UserDto(user.getId(), user.getName(), user.getEmail(), user.getAge());
    }
}