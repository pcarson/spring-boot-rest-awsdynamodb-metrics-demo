package com.example.demo.repository;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.example.demo.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
public class UserRepository {

    private final DynamoDBMapper mapper;

    public UserRepository(final DynamoDBMapper mapper) {
        this.mapper = mapper;
    }

    public User save(final User user) {

        if (!StringUtils.isEmpty(user.getEmail())) {
            user.setEmail(user.getEmail().toLowerCase());
        }

        var now = LocalDateTime.now();
        user.setLastModified(now);
        if (user.getId() != null) {
            log.info("Updated user with id {} and email {}", user.getId(), user.getEmail());
        } else {
            // new
            user.setCreated(now);
        }

        mapper.save(user);

        return user;
    }

    public List<User> findAll() {
        return mapper.scan(User.class, new DynamoDBScanExpression());
    }

    public Optional<User> findOne(final String id) {
        return Optional.ofNullable(mapper.load(User.class, id));
    }

    /**
     * As we can't enforce unique indexes via dynamo, this returns
     * a list.
     *
     * @param email
     * @return a list of matches
     */
    public List<User> findUserByEmailAddress(String email) {

        final DynamoDBQueryExpression<User> queryExpression = new DynamoDBQueryExpression<User>()
                .withIndexName(User.EMAIL_INDEX)
                .withConsistentRead(false)
                .withKeyConditionExpression("email = :v1")
                .withExpressionAttributeValues(Map.of(
                        ":v1", new AttributeValue().withS(email)
                ));
        return mapper.query(User.class, queryExpression);

    }

    public void deleteOne(final User user) {
        mapper.delete(user);
    }
}
