package com.example.sweater.controller;

import com.example.sweater.domain.Message;
import com.example.sweater.domain.User;
import com.example.sweater.repos.MessageRepository;
import com.example.sweater.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.IntStream;

@Controller
@RequestMapping
public class MessageController {

    @Autowired
    private MessageRepository messageRepository;
    @Autowired
    private MessageService messageService;

    @Value("${upload.path}")
    private String uploadPath;

    @ModelAttribute(name = "message")
    public Message message() {
        return new Message();
    }

    @GetMapping("/")
    public String greeting() {
        return "greeting";
    }

    @GetMapping("/main")
    public String main(
            @RequestParam(required = false, defaultValue = "") String filter,
            Model model,//сортируем по id, показываем сообщения созданные последними
            @PageableDefault(sort = {"id"}, direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<Message> page = messageService.messageList(pageable, filter);
        model.addAttribute("pagination", computePagination(page));
        model.addAttribute("elements", new int[]{2, 5, 10, 25, 50});
        model.addAttribute("url", "/main");
        model.addAttribute("page", page);
        model.addAttribute("filter", filter);
        return "main";
    }

    @PostMapping("/main")
    public String addMessage(
            @AuthenticationPrincipal User user,
            @Valid Message message,
            BindingResult bindingResult,
            Model model,
            @RequestParam(value = "file", required = false) MultipartFile file,
            @PageableDefault(sort = {"id"}, direction = Sort.Direction.DESC) Pageable pageable
    ) throws IOException {
        message.setAuthor(user);
        if (bindingResult.hasErrors()) {
            model.addAttribute("message", message);
        } else {//сохраняем если не будет ошибок валидации
            saveFile(message, file);
            model.addAttribute("message", new Message());
            messageRepository.save(message);
        }
        Page<Message> page = messageService.messageList(pageable, null);
//        Iterable<Message> messages = messageRepository.findAll();
//        model.addAttribute("messages", messages);
        model.addAttribute("page", page);
        model.addAttribute("pagination", computePagination(page));
        model.addAttribute("elements", new int[]{2, 5, 10, 25, 50});
        model.addAttribute("url", "/main");
        return "redirect:/main";
    }

    private void saveFile(@Valid Message message, @RequestParam(value = "file", required = false) MultipartFile file) throws IOException {
        if (file != null && !file.getOriginalFilename().isEmpty()) {
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) {
                uploadDir.mkdir();
            }
            String uuidFile = UUID.randomUUID().toString();
            String resultFileName = uuidFile + "." + file.getOriginalFilename();

            file.transferTo(new File(uploadPath + "/" + resultFileName));

            message.setFilename(resultFileName);
        }
    }

    //<a class="nav-link" th:href="@{/user-messages/{id} (id = ${#authentication.principal.id})}">My messages</a>
    @GetMapping("/user-messages/{user}")//spring получит из id юзера
    public String userMessages(
            @AuthenticationPrincipal User currentUser,
            @PathVariable User user,//если имена отличаются то @PathVariable(name = "user") User user
            Model model,
            @RequestParam(required = false) Message message,
            @PageableDefault(sort = {"id"}, direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Set<Message> messages = user.getMessages();
        model.addAttribute("userChannel", user);
        model.addAttribute("subscriptionsCount", user.getSubscriptions().size());
        model.addAttribute("subscribersCount", user.getSubscribers().size());
        model.addAttribute("messages", messages);
        model.addAttribute("isSubscriber", user.getSubscribers().contains(currentUser));
        model.addAttribute("isCurrentUser", currentUser.equals(user));
        model.addAttribute("elements", new int[]{2, 5, 10, 25, 50});
        if (message == null) {
            model.addAttribute("message", new Message());
        } else {
            model.addAttribute("message", message);
        }
        return "userMessages";
    }

    @PostMapping("/user-messages/{user}")//spring получит из id юзера
    public String updateMessage(
            @AuthenticationPrincipal User currentUser,
            @PathVariable User user,//если имена отличаются то @PathVariable(name = "user") User user
            @RequestParam("id") Message message,
            @RequestParam("text") String text,
            @RequestParam("tag") String tag,
            @RequestParam(value = "file", required = false) MultipartFile file
    ) throws IOException {
        if (message.getAuthor().equals(currentUser)) {
            if (!StringUtils.isEmpty(text)) {
                message.setText(text);
            }
            if (!StringUtils.isEmpty(tag)) {
                message.setTag(tag);
            }
            saveFile(message, file);
            messageRepository.save(message);
        }
        return "redirect:/user-messages/" + user.getId();
    }

    static int[] computePagination(Page page) {
        Integer totalPages = page.getTotalPages();
        if (totalPages > 7) {
            Integer pageNumber = page.getNumber() + 1;
            Integer[] head = pageNumber > 4 ? new Integer[]{1, -1} : new Integer[]{1, 2, 3};
            Integer[] tail = pageNumber < (totalPages - 3) ? new Integer[]{-1, totalPages} : new Integer[]{totalPages - 2, totalPages - 1, totalPages};
            Integer[] bodyBefore = (pageNumber > 4 && pageNumber < (totalPages - 1)) ? new Integer[]{pageNumber - 2, pageNumber - 1} : new Integer[]{};
            Integer[] bodyAfter = (pageNumber > 2 && pageNumber < (totalPages - 3)) ? new Integer[]{pageNumber + 1, pageNumber + 2} : new Integer[]{};

            List<Integer> list = new ArrayList<>();
            Collections.addAll(list, head);
            Collections.addAll(list, bodyBefore);
            Collections.addAll(list, (pageNumber > 3 && pageNumber < totalPages - 2) ? new Integer[]{pageNumber} : new Integer[]{});
            Collections.addAll(list, bodyAfter);
            Collections.addAll(list, tail);
            Integer[] arr = list.toArray(new Integer[0]);
            int[] res = Arrays.stream(arr).mapToInt(Integer::intValue).toArray();
            return res;
        } else {
            return IntStream.rangeClosed(1, totalPages).toArray();
        }

    }
}
