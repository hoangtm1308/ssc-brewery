package guru.sfg.brewery.web.controllers.api;

import guru.sfg.brewery.web.controllers.BaseIT;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@SpringBootTest
public class BeerRestControllerIT extends BaseIT {

//    @Test
//    void deleteBeerUrl() throws Exception {
//        mockMvc.perform(delete("/api/v1/beer/3c3c9cfe-e876-4783-bce4-900a8c7ba50f")
//                .param("apiKey","hoangtm")
//                        .param("apiSecret","hoangtm"))
//                .andExpect(status().isOk());
//    }
//
//    @Test
//    void deleteBeerUrlWithBadCreds() throws Exception {
//        mockMvc.perform(delete("/api/v1/beer/3c3c9cfe-e876-4783-bce4-900a8c7ba50f")
//                        .param("apiKey","hoangtm1")
//                        .param("apiSecret","hoangtm"))
//                .andExpect(status().isUnauthorized());
//    }

//    @Test
//    void deleteBeerWithBadCred() throws Exception {
//        mockMvc.perform(delete("/api/v1/beer/3c3c9cfe-e876-4783-bce4-900a8c7ba50f")
//                        .header("Api-Key", "hoangtm1")
//                        .header("Api-Secret","hoangtm"))
//                .andExpect(status().isUnauthorized());
//    }
//
//    @Test
//    void deleteBeer() throws Exception {
//        mockMvc.perform(delete("/api/v1/beer/3c3c9cfe-e876-4783-bce4-900a8c7ba50f")
//                .header("Api-Key", "hoangtm")
//                .header("Api-Secret","hoangtm"))
//                .andExpect(status().isOk());
//    }

    @Test
    void deleteBeerWithHttpBasic_adminRole() throws Exception {
        mockMvc.perform(delete("/api/v1/beer/3c3c9cfe-e876-4783-bce4-900a8c7ba50f")
                .with(httpBasic("hoangtm","hoangtm")))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    void deleteBeerWithHttpBasic_userRole() throws Exception {
        mockMvc.perform(delete("/api/v1/beer/3c3c9cfe-e876-4783-bce4-900a8c7ba50f")
                        .with(httpBasic("user","hoangtm")))
                .andExpect(status().isForbidden());
    }

    @Test
    void deleteBeerWithHttpBasic_customerRole() throws Exception {
        mockMvc.perform(delete("/api/v1/beer/3c3c9cfe-e876-4783-bce4-900a8c7ba50f")
                        .with(httpBasic("scott","hoangtm")))
                .andExpect(status().isForbidden());
    }

    @Test
    void deleteBeerWithNoAuth() throws Exception {
        mockMvc.perform(delete("/api/v1/beer/3c3c9cfe-e876-4783-bce4-900a8c7ba50f"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testFindBeers() throws Exception{
        mockMvc.perform(get("/api/v1/beer/")).andExpect(status().isOk());
    }

    @Test
    void testFindBeersByUpc() throws Exception{
        mockMvc.perform(get("/api/v1/beerUpc/0631234200036")).andExpect(status().isOk());
    }
}