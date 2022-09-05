package com.example.sweater;

import com.example.sweater.controller.MainController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
//@WithUserDetails("dru")//выполнять тест с авторизованым пользователем, на классе -перед каждым тестом,либо на методе
@ActiveProfiles("test")//для .yaml - application-test.yaml
//@TestPropertySource("/application-test.yaml")// указываем пропертя для тестов, миграции применятся к тестовой бд
class SweaterApplicationTests {

    @Autowired
    private MainController mainController;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void test() {//сонтроллер не нул
        assertThat(mainController).isNotNull();
    }

    @Test
    void contextLoad() throws Exception {
        this.mockMvc.perform(get("/"))//выполняет запрос get
                .andDo(print())//выводит полученые рез в консоль
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Hello, guest")))//содержит строки
                .andExpect(content().string(containsString("Please login")));
    }

    @Test
    public void accessDeniedTest() throws Exception {
        this.mockMvc.perform(get("/main"))//выполняет запрос get
                .andDo(print())//выводит полученые рез в консоль
                .andExpect(status().is3xxRedirection())//перенаправление
                .andExpect(redirectedUrl("http://localhost/login"));//ожидаемый url
    }

    @Test
    //@Sql если на классе то скрипты должны быть выполнены перед каждым тестом, либо над методом конкретным
    @Sql(value = {"/create-user-before.sql", "/messages-list-before.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
//перед тестом
    @Sql(value = {"/messages-list-after.sql", "/create-user-after.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
//после теста
    public void correctLoginTest() throws Exception {
        this.mockMvc.perform(formLogin().user("dru").password("1"))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
    }

    @Test
    public void badCredentials() throws Exception {
        this.mockMvc.perform(post("/login").param("user","Ramil"))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

}
