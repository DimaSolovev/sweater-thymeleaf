package com.example.sweater.graphql.resolver.mutation;

import com.example.sweater.domain.Message;
import com.example.sweater.domain.input.MessageInput;
import com.example.sweater.repos.MessageRepository;
import com.example.sweater.repos.UserRepo;
import graphql.kickstart.tools.GraphQLMutationResolver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MessageMutationResolver implements GraphQLMutationResolver {

    private final MessageRepository messageRepository;

    private final UserRepo userRepo;

    public Message addMessage(MessageInput messageInput) {
        log.info("Save message with id {}, text {}", messageInput.getId(), messageInput.getText());
        return messageRepository.save(new Message(
                messageInput.getText(),
                messageInput.getTag(),
                userRepo.findById(messageInput.getAuthorId()).orElseThrow(() -> new RuntimeException("user not found"))
        ));
    }

    public Boolean deleteMessage(Long id) {
        log.info("Delete message with id {}", id);
        Message message = messageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Message not found with id" + id));
        messageRepository.delete(message);
        return true;
    }

    public Message updateMessage(Long id, MessageInput messageInput) {
        log.info("Update message with id {}", id);
        Message message = messageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Message not found with id" + id));
        message.setText(messageInput.getText());
        message.setTag(messageInput.getTag());
        return messageRepository.save(message);
    }
}
