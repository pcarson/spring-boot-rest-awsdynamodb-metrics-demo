package com.example.demo.configuration.dynamodb;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.model.*;
import com.amazonaws.services.dynamodbv2.util.TableUtils;
import com.example.demo.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@Configuration
public class DynamoDBSetup {

    @Autowired
    private AmazonDynamoDB amazonDynamoDB;

    @Autowired
    private DynamoDBMapperConfig dynamoDBMapperConfig;

    @PostConstruct
    public Boolean setupTables() {
        var tableName =
                dynamoDBMapperConfig.getTableNameOverride().getTableNamePrefix() + User.TABLE_NAME;

        var client = new DynamoDB(amazonDynamoDB);
        List<KeySchemaElement> keySchema = Collections
                .singletonList(new KeySchemaElement().withAttributeName(User.ID_NAME).withKeyType(KeyType.HASH));
        List<AttributeDefinition> attributeDefinitions = new ArrayList<>();
        attributeDefinitions
                .add(new AttributeDefinition().withAttributeName(User.ID_NAME)
                        .withAttributeType(ScalarAttributeType.S));
        attributeDefinitions
                .add(new AttributeDefinition().withAttributeName(User.EMAIL_NAME)
                        .withAttributeType(ScalarAttributeType.S));

        List<GlobalSecondaryIndex> globalSecondaryIndexes = new ArrayList<>();
        globalSecondaryIndexes
                .add(new GlobalSecondaryIndex().withIndexName(User.EMAIL_INDEX).withKeySchema(
                                new KeySchemaElement().withAttributeName(User.EMAIL_NAME).withKeyType(KeyType.HASH))
                        .withProjection(new Projection().withProjectionType(ProjectionType.ALL))
                        .withProvisionedThroughput(new ProvisionedThroughput(1L, 1L)));

        var request = new CreateTableRequest().withTableName(tableName)
                .withKeySchema(keySchema)
                .withAttributeDefinitions(attributeDefinitions)
                .withGlobalSecondaryIndexes(globalSecondaryIndexes)
                .withProvisionedThroughput(
                        new ProvisionedThroughput()
                                .withReadCapacityUnits(1L)
                                .withWriteCapacityUnits(1L))
                .withStreamSpecification(
                        new StreamSpecification()
                                .withStreamEnabled(true)
                                .withStreamViewType(StreamViewType.NEW_AND_OLD_IMAGES));

        boolean tablesCreated = TableUtils.createTableIfNotExists(amazonDynamoDB, request);

        if (tablesCreated) {
            var table = client.getTable(tableName);
            log.info("CreateTable request for " + tableName);
            try {
                table.waitForActive();
            } catch (InterruptedException e) {
                log.error("Error during creation of table " + tableName + " - giving up ....", e);
                Thread.currentThread().interrupt();
            }
        }

        return tablesCreated;
    }
}
