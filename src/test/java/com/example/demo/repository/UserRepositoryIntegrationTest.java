package com.example.demo.repository;

import com.example.demo.BaseDynamoDbTest;
import com.example.demo.entity.User;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@SpringBootTest
/**
 * NB If this test is run as part of a maven build then local dynamo will be provided by maven.
 * To run this test standalone, a running localstack instance is required.
 */
public class UserRepositoryIntegrationTest extends BaseDynamoDbTest {

    private static final LocalDateTime NOW = LocalDateTime.now();

    @Autowired
    private UserRepository repository;

    @Test
    public void createUserTest() {
        var user = createUser();

        var savedUser = repository.save(user);

        assertTrue(savedUser.getCreated() != null);
        assertTrue(savedUser.getLastModified() != null);
        assertThat(repository.findOne(user.getId()).get(), is(user));
    }

    @Test
    public void findByEmailHappyDays() {
        var user = createUser();
        user.setEmail("x@y.com");

        repository.save(user);

        var userList = repository.findUserByEmailAddress("x@y.com");

        assertEquals(1, userList.size());
    }

    @Test
    public void findByEmailNotFound() {
        assertEquals(Collections.emptyList(), repository.findUserByEmailAddress("y@x.com"));
    }

    @Test
    public void findAll() {
        var userOne = repository.save(createUser());
        var userTwo = repository.save(createUser());

        assertThat(repository.findAll(), Matchers.containsInAnyOrder(userOne, userTwo));
    }

    private User createUser() {
        var user = new User();
        user.setEmail("x@y.com");
        return user;
    }
}
