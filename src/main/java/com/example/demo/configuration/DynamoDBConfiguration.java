package com.example.demo.configuration;

import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import static java.util.Optional.ofNullable;

@Slf4j
@Configuration
public class DynamoDBConfiguration {

    @Value("${aws.dynamodb.table.prefix}")
    private String tablePrefix;

    @Value("${aws.dynamodb.local.endpoint}")
    private String dynamoEndpoint;

    @Autowired
    private Environment env;

    @Bean
    public AmazonDynamoDB amazonDynamoDB() {

        final AmazonDynamoDBClientBuilder builder;

        /*
        NB for build and tests, we expect the property dynamodb.endpoint
        to have been set by the maven plugin. Otherwise, we're expecting localstack to be up
        to run locally.
         */
        final String dynamodbEndpointFromEnv =
                env.getProperty("dynamodb.endpoint", dynamoEndpoint);

        final String dynamodbEndpoint =
                dynamodbEndpointFromEnv.endsWith(":") ?
                        dynamodbEndpointFromEnv + "4566" :
                        dynamodbEndpointFromEnv;

        final EndpointConfiguration endpointConfiguration = new EndpointConfiguration(
                dynamodbEndpoint, null); // No region specified for localstack

        builder = AmazonDynamoDBClientBuilder.standard()
                .withEndpointConfiguration(endpointConfiguration);

        log.info("Creating dynamodb client, endpoint={}, region={}",
                ofNullable(builder.getEndpoint())
                        .map(EndpointConfiguration::getServiceEndpoint)
                        .orElse("UNKNOWN"),
                ofNullable(builder.getRegion()));
        return builder.build();
    }

    @Bean
    public DynamoDBMapperConfig dynamoDBMapperConfig() {
        final DynamoDBMapperConfig.TableNameOverride tableNameOverride = DynamoDBMapperConfig.TableNameOverride
                .withTableNamePrefix(tablePrefix);

        DynamoDBMapperConfig.Builder builder = new DynamoDBMapperConfig.Builder();
        builder.setTableNameOverride(tableNameOverride);

        log.info("Creating dynamodb mapper, tableNamePrefix={}", tablePrefix);
        return builder.build();
    }

    @Bean
    public DynamoDBMapper dynamoDBMapper() {
        return new DynamoDBMapper(amazonDynamoDB(), dynamoDBMapperConfig());
    }

}
