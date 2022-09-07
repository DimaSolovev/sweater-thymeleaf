package com.example.sweater;

import com.example.sweater.controller.MessageController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.xpath;

@SpringBootTest
@AutoConfigureMockMvc
@WithUserDetails("dru")//выполнять тест с авторизованым пользователем, на классе -перед каждым тестом,либо на методе
@ActiveProfiles("test")//для .yaml - application-test.yaml
//@TestPropertySource("/application-test.yaml")// указываем пропертя для тестов, миграции применятся к тестовой бд
//@Sql если на классе то скрипты должны быть выполнены перед каждым тестом, либо над методом конкретным
@Sql(value = {"/create-user-before.sql", "/messages-list-before.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
//перед тестом
@Sql(value = {"/messages-list-after.sql", "/create-user-after.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
//после теста
public class MessageControllerTest {

    @Autowired
    private MessageController mainController;

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void mainPageTest() throws Exception {
        this.mockMvc.perform(get("/main"))
                .andDo(print())
                .andExpect(authenticated())//
                .andExpect(xpath("//div[@id='navbarSupportedContent']/div/div").string("dru"));
        //xpath берем в браузер дебагере, на элемент наводим, правой, copy, xpath
    }

    @Test
    public void messageListTest() throws Exception {
        this.mockMvc.perform(get("/main"))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(xpath("//div[@id='message-list']/div").nodeCount(4));
        //неизвестно сколько nodeCount(message) будет у разных разработчиков, поэтому создаем бд для тестов
        //application-test.yaml
    }

    @Test
    public void filterMessageTest() throws Exception {//тестируем поиск по фильтру
        this.mockMvc.perform(get("/main").param("filter", "my-tag"))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(xpath("//div[@id='message-list']/div").nodeCount(2))
                .andExpect(xpath("//div[@id='message-list']/div/div[@id=1]").exists())
                .andExpect(xpath("//div[@id='message-list']/div/div[@id=3]").exists());
        //добавляем каждой карточке data-th-id, [@id=3] - передаем параметры
    }

    @Test
    public void addMessageToListTest() throws Exception {
        MockHttpServletRequestBuilder multipart = multipart("/main")
                .file("file", "123".getBytes())//добавляем file, строку и переводим в байты
                .param("text", "fifth")//поля класса message
                .param("tag", "new")//поля класса message
                .with(csrf());
        this.mockMvc.perform(multipart)
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(xpath("//div[@id='message-list']/div").nodeCount(5))//к 4 тестовым добавляем пятое
                .andExpect(xpath("//div[@id='message-list']/div/div[@id=10]").exists())//id 10 потому что нумерация новых элементов начинается с 10, sequence
                .andExpect(xpath("//div[@id='message-list']/div/div[@id=10]/div/span").string("fifth"))
                .andExpect(xpath("//div[@id='message-list']/div/div[@id=10]/div/i").string("new"));
    }
}
