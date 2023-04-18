package com.example.demo;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.DeleteItemRequest;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

public class BaseDynamoDbTest {

    @Autowired
    private AmazonDynamoDB dynamoDB;

    @BeforeEach
    public void truncateDb() {
        dynamoDB.listTables().getTableNames().forEach(
                tableName -> dynamoDB.scan(new ScanRequest(tableName))
                        .getItems()
                        .stream()
                        .map(i -> i.get("id").getS())
                        .forEach(id -> dynamoDB.deleteItem(
                                new DeleteItemRequest(tableName,
                                        Map.of("id", new AttributeValue(id))))));
    }
}
