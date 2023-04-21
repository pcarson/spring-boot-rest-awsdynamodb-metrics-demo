package com.example.demo.repository;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.example.demo.entity.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserRepositoryTest {

    @Mock
    DynamoDBMapper mapper;

    @InjectMocks
    UserRepository userRepository;

    @Test
    void testCreateHappyDays() {
        var pd = new User();
        userRepository.save(pd);
        verify(mapper, times(1)).save(any());
    }

    @Test
    void testGetReturnsAnItemHappyDays() {

        when(mapper.load(eq(User.class), any(String.class))).thenReturn(new User());
        assertNotNull(userRepository.findOne(UUID.randomUUID().toString()));
    }

    @Test
    void testGetReturnsNothing() {

        when(mapper.load(eq(User.class), any(String.class))).thenReturn(null);
        assertEquals(Optional.empty(), userRepository.findOne(UUID.randomUUID().toString()));
    }

}