package com.example.sweater.graphql;

import com.example.sweater.domain.Message;
import com.example.sweater.domain.input.MessageInput;
import com.example.sweater.repos.MessageRepository;
import com.example.sweater.repos.UserRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
@RequiredArgsConstructor
public class MessageGraphQLController {

    private final MessageRepository messageRepository;

    private final UserRepo userRepo;

    @MutationMapping
    public Message addMessage(@Argument MessageInput messageInput) {
        log.info("Save message with id {}, text {}", messageInput.getId(), messageInput.getText());
        return messageRepository.save(new Message(
                messageInput.getText(),
                messageInput.getTag(),
                userRepo.findById(messageInput.getAuthorId()).orElseThrow(() -> new RuntimeException("user not found"))
        ));
    }

    @MutationMapping
    public Boolean deleteMessage(@Argument Long id) {
        log.info("Delete message with id {}", id);
        Message message = messageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Message not found with id" + id));
        messageRepository.delete(message);
        return true;
    }

    @MutationMapping
    public Message updateMessage(@Argument Long id,@Argument MessageInput messageInput) {
        log.info("Update message with id {}", id);
        Message message = messageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Message not found with id" + id));
        message.setText(messageInput.getText());
        message.setTag(messageInput.getTag());
        return messageRepository.save(message);
    }
}
