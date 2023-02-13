package com.example.sweater.graphql.resolver.query;

import com.graphql.spring.boot.test.GraphQLResponse;
import com.graphql.spring.boot.test.GraphQLTestTemplate;
import io.micrometer.core.instrument.util.IOUtils;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;
import static org.skyscreamer.jsonassert.JSONAssert.assertEquals;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = SpringApplication.class)
class MessageResolverTest {

    private static final String GRAPHQL_QUERY_REQUEST_PATH = "graphql/request/%s.graphql";
    private static final String GRAPHQL_QUERY_RESPONSE_PATH = "graphql/response/%s.json";

    @Autowired
    GraphQLTestTemplate graphQLTestTemplate;

    @Test
    void usersById_successful() throws IOException, JSONException {
        String testName = "user";
        GraphQLResponse graphQLResponse =
                graphQLTestTemplate.postForResource(format(GRAPHQL_QUERY_REQUEST_PATH, testName));

        String expectedResponseBody = read(format(GRAPHQL_QUERY_RESPONSE_PATH, testName));

        assertThat(graphQLResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertEquals(expectedResponseBody, graphQLResponse.getRawResponse().getBody(),true);
    }

    private String read(String location) throws IOException {
        return IOUtils.toString(new ClassPathResource(location).getInputStream(), StandardCharsets.UTF_8);

    }
}