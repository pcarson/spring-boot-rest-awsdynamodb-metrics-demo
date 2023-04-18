package com.example.demo.service.impl;

import com.example.demo.entity.User;
import com.example.demo.exception.UserExistsException;
import com.example.demo.exception.UserNotFoundException;
import com.example.demo.exception.UserServiceException;
import com.example.demo.mapper.UserMapper;
import com.example.demo.mapper.UserMapperImpl;
import com.example.demo.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    UserRepository userRepository;

    @InjectMocks
    UserServiceImpl service;

    private final UserMapper mapper = new UserMapperImpl();

    @Test
    void testGetUsers() {
        var howMany = 5;
        when(userRepository.findAll()).thenReturn(getNUsers(howMany));

        var dtos = service.getUsers();
        assertNotNull(dtos);
        assertEquals(howMany, dtos.size());
    }

    @Test
    void testGetUserNotFound() {
        when(userRepository.findOne(any())).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> service.getUser(UUID.randomUUID().toString()));
    }

    @Test
    void testGetUserFound() throws UserNotFoundException {
        when(userRepository.findOne(any())).thenReturn(Optional.of(getDummyUser()));
        var dto = service.getUser(UUID.randomUUID().toString());
        assertNotNull(dto);
    }

    @Test
    void testFindUserByEmailFound() throws UserNotFoundException, UserServiceException {
        when(userRepository.findUserByEmailAddress(any())).thenReturn(Arrays.asList(getDummyUser()));
        var dtos = service.getUserByEmailAddress("x@y.com");
        assertNotNull(dtos);
    }

    @Test
    void testFindUserByEmailNotFound() {
        when(userRepository.findUserByEmailAddress(any())).thenReturn(Collections.emptyList());
        assertThrows(UserNotFoundException.class, () -> service.getUserByEmailAddress("x@y.com"));
    }

    @Test
    void testCreateUsers() throws UserExistsException, UserServiceException {

        when(userRepository.findUserByEmailAddress(any())).thenReturn(Collections.emptyList());
        var user = service.createUser(mapper.mapToUserDto(getDummyUser()));
        assertNotNull(user);
        assertNotNull(user.getId());
        verify(userRepository, times(1)).save(any());
    }

    @Test
    void testCreateUserEmailNotProvided() {

        var user = getDummyUser();
        user.setEmail(null);
        assertThrows(UserServiceException.class, () -> service.createUser(mapper.mapToUserDto(user)));
        verify(userRepository, times(0)).save(any());
    }

    @Test
    void testCreateUserEmailAlreadyExists() {

        when(userRepository.findUserByEmailAddress(any())).thenReturn(Arrays.asList(getDummyUser()));
        assertThrows(UserExistsException.class, () -> service.createUser(mapper.mapToUserDto(getDummyUser())));
        verify(userRepository, times(0)).save(any());
    }

    @Test
    void testDeleteUserNotFound() {
        when(userRepository.findOne(any())).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> service.deleteUser(UUID.randomUUID().toString()));
        verify(userRepository, times(0)).deleteOne(any());
    }

    @Test
    void testDeleteUserFound() throws UserNotFoundException {
        when(userRepository.findOne(any())).thenReturn(Optional.of(getDummyUser()));
        service.deleteUser(UUID.randomUUID().toString());
        verify(userRepository, times(1)).deleteOne(any());
    }

    private List<User> getNUsers(int howMany) {
        var listu = new ArrayList<User>();
        for (int i = 0; i < howMany; i++) {
            listu.add(getDummyUser());
        }
        return listu;
    }

    private User getDummyUser() {
        var user = new User();
        user.setId(UUID.randomUUID().toString());
        user.setEmail("x@y.com");
        user.setPassword("ssshhhh");
        var now = LocalDateTime.now();
        user.setCreated(now);
        user.setLastModified(now);
        return user;
    }
}