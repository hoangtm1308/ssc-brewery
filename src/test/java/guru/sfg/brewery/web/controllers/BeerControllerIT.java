package guru.sfg.brewery.web.controllers;

import guru.sfg.brewery.domain.Beer;
import guru.sfg.brewery.repositories.BeerRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.anonymous;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@SpringBootTest
public class BeerControllerIT extends BaseIT {

    @Autowired
    BeerRepository beerRepository;

    @DisplayName("Init New Form")
    @Nested
    class InitNewForm{

        @ParameterizedTest(name = "#{index} with [{arguments}]")
        @MethodSource("guru.sfg.brewery.web.controllers.BeerControllerIT#getStreamAdmin")
        void initCreationFormAuth(String username, String password) throws Exception {
            mockMvc.perform(get("/beers/new")
                    .with(httpBasic(username,password)))
                    .andExpect(status().isOk())
                    .andExpect(view().name("beers/createBeer"))
                    .andExpect(model().attributeExists("beer"));
        }

        @Test
        void initCreationFormNotAuth() throws Exception {
            mockMvc.perform(get("/beers/new"))
                    .andExpect(status().isUnauthorized());
        }
    }

    @DisplayName("Process Find Beer Form")
    @Nested
    class ProcessFindForm{
        @Test
        void findBeerForm() throws Exception {
            mockMvc.perform(get("/beers").param("beerName",""))
                    .andExpect(status().isUnauthorized());
        }

        @ParameterizedTest(name = "#{index} with [{arguments}]")
        @MethodSource("guru.sfg.brewery.web.controllers.BeerControllerIT#getStreamAllUsers")
        void findBeerFormAUTH(String username, String password) throws Exception {
            mockMvc.perform(get("/beers").param("beerName","")
                            .with(httpBasic(username, password)))
                    .andExpect(model().attributeExists("beer"))
                    .andExpect(status().isOk());
        }
    }

    @DisplayName("Get Beer By Id")
    @Nested
    class getBeerById{
        @Test
        void getBeerByIdNotAUTH() throws Exception {
            Beer beer = beerRepository.findAll().get(0);

            mockMvc.perform(get("/beers/" + beer.getId()))
                    .andExpect(status().isUnauthorized());
        }

        @ParameterizedTest(name = "#{index} with [{arguments}]")
        @MethodSource("guru.sfg.brewery.web.controllers.BeerControllerIT#getStreamAllUsers")
        void getBeerByIdAUTH(String username, String password) throws Exception{
            Beer beer = beerRepository.findAll().get(0);

            mockMvc.perform(get("/beers/" + beer.getId())
                    .with(httpBasic(username,password)))
                    .andExpect(view().name("beers/beerDetails"))
                    .andExpect(model().attributeExists("beer"))
                    .andExpect(status().isOk());
        }
    }

    @DisplayName("Init Find Beer Form")
    @Nested
    class FindForm{

        @Test
        void findBeersWithAnonymous() throws Exception{
            mockMvc.perform(get("/beers/find").with(anonymous()))
                    .andExpect(status().isUnauthorized());
        }

        @ParameterizedTest(name = "#{index} with [{arguments}]")
        @MethodSource("guru.sfg.brewery.web.controllers.BeerControllerIT#getStreamAllUsers")
        void findBeerFormAUTH(String username, String password) throws Exception {
            mockMvc.perform(get("/beers/find")
                    .with(httpBasic(username, password)))
                    .andExpect(view().name("beers/findBeers"))
                    .andExpect(model().attributeExists("beer"))
                    .andExpect(status().isOk());
        }

    }

    //Mock user để pass qua spring security đang áp dụng trong test
    //Dùng để check Security Logic (cần có username mới pass)
//    @WithMockUser("hoangtm")
//    @Test
//    void findBeers() throws Exception {
//        mockMvc.perform(get("/beers/find"))
//                .andExpect(status().isOk()).andExpect(view().name("beers/findBeers"))
//                .andExpect(model().attributeExists("beer"));
//    }

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
                        .with(httpBasic("hoangtm", "hoangtm")))
                .andExpect(status().isOk()).andExpect(view().name("beers/createBeer"))
                .andExpect(model().attributeExists("beer"));
    }

    @Test
    void createNewBeersUsingScottUser() throws Exception {
        mockMvc.perform(get("/beers/new")
                        .with(httpBasic("scott", "hoangtm")))
                .andExpect(status().isForbidden());
                //.andExpect(view().name("beers/createBeer"))
                //.andExpect(model().attributeExists("beer"));
    }

}
