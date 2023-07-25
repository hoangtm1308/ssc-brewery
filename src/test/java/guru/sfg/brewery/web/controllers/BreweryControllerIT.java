package guru.sfg.brewery.web.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@SpringBootTest
class BreweryControllerIT extends BaseIT{

    @Test
    void listBreweries_with_CUSTOMER() throws Exception {
        mockMvc.perform(get("/brewery/breweries")
                .with(httpBasic("scott","hoangtm")))
                .andExpect(status().is2xxSuccessful());

    }

    @Test
    void listBreweries_with_ADMIN() throws Exception {
        mockMvc.perform(get("/brewery/breweries")
                        .with(httpBasic("hoangtm","hoangtm")))
                .andExpect(status().is2xxSuccessful());

    }

    @Test
    void listBreweries_with_USER() throws Exception {
        mockMvc.perform(get("/brewery/breweries")
                        .with(httpBasic("user","hoangtm")))
                .andExpect(status().isForbidden());

    }

    @Test
    void listBreweries_with_NOAUTH() throws Exception {
        mockMvc.perform(get("/brewery/breweries"))
                .andExpect(status().isUnauthorized());

    }

    @Test
    void listBreweries_with_CUSTOMER_API() throws Exception {
        mockMvc.perform(get("/brewery/api/v1/breweries")
                        .with(httpBasic("scott","hoangtm")))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    void listBreweries_with_ADMIN_API() throws Exception {
        mockMvc.perform(get("/brewery/api/v1/breweries")
                        .with(httpBasic("hoangtm","hoangtm")))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    void listBreweries_with_USER_API() throws Exception {
        mockMvc.perform(get("/brewery/api/v1/breweries")
                        .with(httpBasic("user","hoangtm")))
                .andExpect(status().isForbidden());
    }

    @Test
    void listBreweries_with_NOAUTH_API() throws Exception {
        mockMvc.perform(get("/brewery/api/v1/breweries"))
                .andExpect(status().isUnauthorized());
    }

}