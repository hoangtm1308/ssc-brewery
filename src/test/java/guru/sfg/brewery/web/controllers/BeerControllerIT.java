package guru.sfg.brewery.web.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@WebMvcTest
public class BeerControllerIT extends BaseIT {

    //Mock user để pass qua spring security đang áp dụng trong test
    //Dùng để check Security Logic (cần có username mới pass)
    @WithMockUser("hoangtm")
    @Test
    void findBeers() throws Exception {
        mockMvc.perform(get("/beers/find"))
                .andExpect(status().isOk()).andExpect(view().name("beers/findBeers"))
                .andExpect(model().attributeExists("beer"));
    }

    //using username and password config in application.properties with HttpBasic Auth
    //check authentication logic (cần đúng credential mới pass)
    @Test
    void findBeersWithHttpBasic() throws Exception {
        mockMvc.perform(get("/beers/find")
                        .with(httpBasic("hoangtm", "hoangtm"))) // đang được encode theo LDAP nên case này fail
                .andExpect(status().isOk()).andExpect(view().name("beers/findBeers"))
                .andExpect(model().attributeExists("beer"));
    }

    //test case fail do đã config trong SecurityConfig
//    @Test
//    void findBeersWithAnonymous() throws Exception {
//        mockMvc.perform(get("/beers/find")
//                        .with(anonymous()))
//                .andExpect(status().isOk()).andExpect(view().name("beers/findBeers"))
//                .andExpect(model().attributeExists("beer"));
//    }

    //Test case to test that In Memory User Details Entity is existed
    @Test
    void createNewBeersUsingUserDetails() throws Exception {
        mockMvc.perform(get("/beers/new")
                        .with(httpBasic("user", "hoangtm")))
                .andExpect(status().isOk()).andExpect(view().name("beers/createBeer"))
                .andExpect(model().attributeExists("beer"));
    }

    @Test
    void createNewBeersUsingScottUser() throws Exception {
        mockMvc.perform(get("/beers/new")
                        .with(httpBasic("scott", "tiger")))
                .andExpect(status().isOk()).andExpect(view().name("beers/createBeer"))
                .andExpect(model().attributeExists("beer"));
    }

}
