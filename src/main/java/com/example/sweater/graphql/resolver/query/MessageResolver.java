package com.example.sweater.graphql.resolver.query;

import com.example.sweater.domain.Message;
import com.example.sweater.repos.MessageRepository;
import graphql.kickstart.tools.GraphQLQueryResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MessageResolver implements GraphQLQueryResolver {

    private final MessageRepository messageRepository;

    public Iterable<Message> messages() {
        return messageRepository.findAll();
    }

    public Message messageById(Long id) {
        return messageRepository.findById(id).orElseThrow(() -> new RuntimeException("data not found"));
    }
}
