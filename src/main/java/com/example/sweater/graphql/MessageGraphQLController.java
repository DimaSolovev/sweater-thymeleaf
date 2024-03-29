package com.example.sweater.graphql;

import com.example.sweater.domain.Message;
import com.example.sweater.domain.input.MessageInput;
import com.example.sweater.repos.MessageRepository;
import com.example.sweater.repos.UserRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
@RequiredArgsConstructor
public class MessageGraphQLController {
//обрабатывает запросы graphql для message
    private final MessageRepository messageRepository;

    private final UserRepo userRepo;

    @QueryMapping//получить все message
    public Iterable<Message> messages() {
        return messageRepository.findAll();
    }

    @QueryMapping//получить message по id
    public Message messageById(Long id) {
        return messageRepository.findById(id).orElseThrow(() -> new RuntimeException("data not found"));
    }

    @MutationMapping//добавить message
    public Message addMessage(@Argument MessageInput messageInput) {
        log.info("Save message with id {}, text {}", messageInput.getId(), messageInput.getText());
        return messageRepository.save(new Message(
                messageInput.getText(),
                messageInput.getTag(),
                userRepo.findById(messageInput.getAuthorId()).orElseThrow(() -> new RuntimeException("user not found"))
        ));
    }

    @MutationMapping//удалить message
    public Boolean deleteMessage(@Argument Long id) {
        log.info("Delete message with id {}", id);
        Message message = messageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Message not found with id" + id));
        messageRepository.delete(message);
        return true;
    }

    @MutationMapping//изменить message
    public Message updateMessage(@Argument Long id,@Argument MessageInput messageInput) {
        log.info("Update message with id {}", id);
        Message message = messageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Message not found with id" + id));
        message.setText(messageInput.getText());
        message.setTag(messageInput.getTag());
        return messageRepository.save(message);
    }
}
