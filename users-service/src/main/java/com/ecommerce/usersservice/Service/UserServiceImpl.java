package com.ecommerce.usersservice.Service;

import com.ecommerce.usersservice.Dto.UserPatchRequest;
import com.ecommerce.usersservice.Dto.UserRequest;
import com.ecommerce.usersservice.Dto.UsersDto;
import com.ecommerce.usersservice.ExceptionHandlers.EmailAlreadyExistsException;
import com.ecommerce.usersservice.ExceptionHandlers.UserDoesNotExist;
import com.ecommerce.usersservice.GrpcClients.NotificationServiceGrpcClient;
import com.ecommerce.usersservice.Kafka.KafkaProducer;
import com.ecommerce.usersservice.Mappers.UserMapper;
import com.ecommerce.usersservice.Repository.UserRepository;
import com.ecommerce.usersservice.model.Users;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    private static Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    private UserRepository userRepository;
    private KafkaProducer kafkaProducer;
    private NotificationServiceGrpcClient notificationServiceGrpcClient;
    public UserServiceImpl(UserRepository userRepository, NotificationServiceGrpcClient notificationServiceGrpcClient,KafkaProducer kafkaProducer) {
        this.userRepository = userRepository;
        this.notificationServiceGrpcClient = notificationServiceGrpcClient;
        this.kafkaProducer=kafkaProducer;
    }

    @Override
    public List<UsersDto> getAllUsers() {
        return userRepository.findAll().stream().map(UserMapper::mapToDto).collect(Collectors.toList());
    }

    public UsersDto addUser(UserRequest userRequest) {
        if(userRepository.existsByEmail(userRequest.getEmail())) {
            throw new EmailAlreadyExistsException("Email already exists");
        }
        Users users=UserMapper.mapToEntity(userRequest);
        this.userRepository.save(users);
        this.notificationServiceGrpcClient.createNotification(userRequest);
        this.kafkaProducer.sendKafkaMessage(users);
        return UserMapper.mapToDto(users);
    }

    public UsersDto updateUser(UserRequest userRequest) {
        if(userRepository.existsByEmail(userRequest.getEmail())) {
            Users users=userRepository.findByEmail(userRequest.getEmail());
            users.setName(userRequest.getName());
            users.setEmail(userRequest.getEmail());
            users.setAddress(userRequest.getAddress());
            users.setDate_of_birth(userRequest.getDate_of_birth());
            users.setRegistered_date(userRequest.getRegistered_date());
            userRepository.save(users);
            return UserMapper.mapToDto(users);
        }
        else{
            throw new UserDoesNotExist("User does not exist");
        }
    }

    @Override
    public UsersDto patchUsers(UserPatchRequest userRequest) {
        if(userRepository.existsByEmail(userRequest.getEmail())) {
            Users users=userRepository.findByEmail(userRequest.getEmail());
            if(userRequest.getName() != null) {
                users.setName(userRequest.getName());
            }
            if(userRequest.getAddress() != null) {
                users.setAddress(userRequest.getAddress());
            }
            if(userRequest.getDate_of_birth() != null) {
                users.setDate_of_birth(userRequest.getDate_of_birth());
            }
            if(userRequest.getEmail() != null) {
                users.setEmail(userRequest.getEmail());
            }
            userRepository.save(users);
            return UserMapper.mapToDto(users);
        }
        else{
            throw new UserDoesNotExist("User does not exist");
        }
    }

    @Override
    public UsersDto getUserByEmail(String email) {
        if(userRepository.existsByEmail(email)) {
            return UserMapper.mapToDto(userRepository.findByEmail(email));
        }
        throw new UserDoesNotExist("User does not exist");
    }

    @Override
    public List<UsersDto> getUserByUsername(String username) {
       return this.userRepository.findByName(username).stream().map(UserMapper::mapToDto).collect(Collectors.toList());
    }

    @Override
    public void deleteUserByEmail(String email) {
        if(userRepository.existsByEmail(email)) {
            Users users=userRepository.findByEmail(email);
            this.userRepository.delete(users);
        }
        else{
            throw new UserDoesNotExist("User does not exist");
        }
    }
}
