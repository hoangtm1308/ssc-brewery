package guru.sfg.brewery.web.controllers;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@SpringBootTest
public class CustomerControllerIT extends BaseIT{

    @DisplayName("List Customers Test")
    @Nested
    class ListCustomersTest {
        @ParameterizedTest(name = "#{index} with [{arguments}]")
        @MethodSource("guru.sfg.brewery.web.controllers.CustomerControllerIT#getStreamAdminCustomer")
        void testListCustomersAUTH(String username, String password) throws Exception {
            mockMvc.perform(get("/customers")
                            .with(httpBasic(username, password)))
                    .andExpect(status().isOk());
        }
        @Test
        void testListCustomerNOTAUTH() throws Exception {
            mockMvc.perform(get("/customers")
                    .with(httpBasic("user","hoangtm")))
                    .andExpect(status().isForbidden());
        }

        @Test
        void testListCustomerNOTLOGIN() throws Exception {
            mockMvc.perform(get("/customers"))
                    .andExpect(status().isUnauthorized());
        }
    }

    @DisplayName("Add Customers")
    @Nested
    class AddCustomers{

        @Rollback
        @Test
        void processCreationForm() throws Exception{
            mockMvc.perform(post("/customers/new").with(csrf())
                    .param("customerName", "Test Customer")
                    .with(httpBasic("hoangtm", "hoangtm")))
                    .andExpect(status().is3xxRedirection());
        }

        @Rollback
        @ParameterizedTest(name = "#{index} with [{arguments}]")
        @MethodSource("guru.sfg.brewery.web.controllers.CustomerControllerIT#getStreamNotAdmin")
        void processCreationFormNOTAUTH(String username, String password) throws Exception {
            mockMvc.perform(post("/customers/new")
                    .param("customerName","Test Customer")
                    .with(httpBasic(username,password)))
                    .andExpect(status().isForbidden());
        }

        @Test
        void processCreationFormNOAUTH() throws Exception {
            mockMvc.perform(post("/customers/new").with(csrf())
                            .param("customerName","Test Customer"))
                    .andExpect(status().isUnauthorized());
        }


    }
}
