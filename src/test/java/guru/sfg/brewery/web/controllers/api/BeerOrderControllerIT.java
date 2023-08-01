package guru.sfg.brewery.web.controllers.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import guru.sfg.brewery.bootstrap.DefaultBreweryLoader;
import guru.sfg.brewery.domain.Beer;
import guru.sfg.brewery.domain.BeerOrder;
import guru.sfg.brewery.domain.Customer;
import guru.sfg.brewery.repositories.BeerOrderRepository;
import guru.sfg.brewery.repositories.BeerRepository;
import guru.sfg.brewery.repositories.CustomerRepository;
import guru.sfg.brewery.web.controllers.BaseIT;
import guru.sfg.brewery.web.model.BeerOrderDto;
import guru.sfg.brewery.web.model.BeerOrderLineDto;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class BeerOrderControllerTest extends BaseIT {

    public static final String API_ROOT = "/api/v1/customers/";

    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    BeerOrderRepository beerOrderRepository;

    @Autowired
    BeerRepository beerRepository;

    @Autowired
    ObjectMapper objectMapper;

    Customer stPeteCustomer;
    Customer dunedinCustomer;
    Customer keyWestCustomer;
    List<Beer> loadedBeers;

    @BeforeEach
    void setUp() {
        stPeteCustomer = customerRepository.findAllByCustomerName(DefaultBreweryLoader.ST_PETE_DISTRIBUTING).orElseThrow();
        dunedinCustomer = customerRepository.findAllByCustomerName(DefaultBreweryLoader.DUNEDIN_DISTRIBUTING).orElseThrow();
        keyWestCustomer = customerRepository.findAllByCustomerName(DefaultBreweryLoader.KEY_WEST_DISTRIBUTING).orElseThrow();
        loadedBeers = beerRepository.findAll();
    }

    //cant use nested tests bug - https://github.com/spring-projects/spring-security/issues/8793
//    @DisplayName("Create Test")
//    @Nested
//    class createOrderTests {


    @Test
    void createOrderNotAuth() throws Exception {
        BeerOrderDto beerOrderDto = buildOrderDto(stPeteCustomer, loadedBeers.get(0).getId());

        mockMvc.perform(post(API_ROOT + stPeteCustomer.getId() + "/orders")
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(beerOrderDto)))
                .andExpect(status().isUnauthorized());
    }

    @WithUserDetails("hoangtm")
    @Test
    void createOrderUserAdmin() throws Exception {
        BeerOrderDto beerOrderDto = buildOrderDto(stPeteCustomer, loadedBeers.get(0).getId());

        mockMvc.perform(post(API_ROOT + stPeteCustomer.getId() + "/orders")
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(beerOrderDto)))
                .andExpect(status().isCreated());
    }

    @WithUserDetails(DefaultBreweryLoader.ST_PETE_USER)
    @Test
    void createOrderUserAuthCustomer() throws Exception {
        BeerOrderDto beerOrderDto = buildOrderDto(stPeteCustomer, loadedBeers.get(0).getId());

        mockMvc.perform(post(API_ROOT + stPeteCustomer.getId() + "/orders")
                        //.with(httpBasic(DefaultBreweryLoader.ST_PETE_USER, "hoangtm"))
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(beerOrderDto))
                )
                .andExpect(status().isCreated());
    }

    @WithUserDetails(DefaultBreweryLoader.KEY_WEST_USER)
    @Test
    void createOrderUserNOTAuthCustomer() throws Exception {
        BeerOrderDto beerOrderDto = buildOrderDto(stPeteCustomer, loadedBeers.get(0).getId());

        mockMvc.perform(post(API_ROOT + stPeteCustomer.getId() + "/orders")
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(beerOrderDto)))
                .andExpect(status().isForbidden());
    }

//    }

    @Test
    void listOrdersNotAuth() throws Exception {
        mockMvc.perform(get(API_ROOT + stPeteCustomer.getId() + "/orders"))
                .andExpect(status().isUnauthorized());
    }

    @WithUserDetails(value = "hoangtm")
    @Test
    void listOrdersAdminAuth() throws Exception {
        mockMvc.perform(get(API_ROOT + stPeteCustomer.getId() + "/orders"))
                .andExpect(status().isOk());
    }

    @WithUserDetails(value = DefaultBreweryLoader.ST_PETE_USER)
    @Test
    void listOrdersCustomerAuth() throws Exception {
        mockMvc.perform(get(API_ROOT + stPeteCustomer.getId() + "/orders"))
                .andExpect(status().isOk());
    }

    @WithUserDetails(value = DefaultBreweryLoader.DUNEDIN_USER)
    @Test
    void listOrdersCustomerNOTAuth() throws Exception {
        mockMvc.perform(get(API_ROOT + stPeteCustomer.getId() + "/orders"))
                .andExpect(status().isForbidden());
    }

    @Test
    void getOrderByIdNotAuth() throws Exception {
        BeerOrder beerOrder = stPeteCustomer.getBeerOrders().stream().findFirst().orElseThrow();
        mockMvc.perform(get(API_ROOT + stPeteCustomer.getId() + "/orders/" + beerOrder.getId()))
                .andExpect(status().isUnauthorized());
    }

    @WithUserDetails("hoangtm")
    @Test
    void getOrderByIdADMIN() throws Exception {
        BeerOrder beerOrder = stPeteCustomer.getBeerOrders().stream().findFirst().orElseThrow();
        mockMvc.perform(get(API_ROOT + stPeteCustomer.getId() + "/orders/" + beerOrder.getId()))
                .andExpect(status().isOk());
    }

    @WithUserDetails(DefaultBreweryLoader.ST_PETE_USER)
    @Test
    void getOrderByIdCustomerAuth() throws Exception {
        BeerOrder beerOrder = stPeteCustomer.getBeerOrders().stream().findFirst().orElseThrow();
        mockMvc.perform(get(API_ROOT + stPeteCustomer.getId() + "/orders/" + beerOrder.getId()))
                .andExpect(status().isOk());
    }

    @WithUserDetails(DefaultBreweryLoader.DUNEDIN_USER)
    @Test
    void getOrderByIdCustomerNOTAuth1() throws Exception {
        BeerOrder beerOrder = stPeteCustomer.getBeerOrders().stream().findFirst().orElseThrow();
        mockMvc.perform(get(API_ROOT + stPeteCustomer.getId() + "/orders/" + beerOrder.getId()))
                .andExpect(status().isForbidden());
    }

    @WithUserDetails(DefaultBreweryLoader.KEY_WEST_USER)
    @Test
    void getOrderByIdCustomerNOTAuth2() throws Exception {
        BeerOrder beerOrder = stPeteCustomer.getBeerOrders().stream().findFirst().orElseThrow();
        mockMvc.perform(get(API_ROOT + stPeteCustomer.getId() + "/orders/" + beerOrder.getId()))
                .andExpect(status().isForbidden());
    }

    @Test
    void pickUpOrderNotAuth() throws Exception {
        BeerOrder beerOrder = stPeteCustomer.getBeerOrders().stream().findFirst().orElseThrow();
        mockMvc.perform(get(API_ROOT + stPeteCustomer.getId() + "/orders/" + beerOrder.getId() + "/pickup"))
                .andExpect(status().isUnauthorized());
    }

    @WithUserDetails("hoangtm")
    @Test
    void pickUpOrderAdminUser() throws Exception {
        BeerOrder beerOrder = stPeteCustomer.getBeerOrders().stream().findFirst().orElseThrow();
        mockMvc.perform(get(API_ROOT + stPeteCustomer.getId() + "/orders/" + beerOrder.getId() + "/pickup"))
                .andExpect(status().isOk());
    }

    @WithUserDetails(DefaultBreweryLoader.ST_PETE_USER)
    @Test
    void pickUpOrderCustomerUserAUTH() throws Exception {
        BeerOrder beerOrder = stPeteCustomer.getBeerOrders().stream().findFirst().orElseThrow();
        mockMvc.perform(get(API_ROOT + stPeteCustomer.getId() + "/orders/" + beerOrder.getId() + "/pickup"))
                .andExpect(status().isOk());
    }

    @WithUserDetails(DefaultBreweryLoader.KEY_WEST_USER)
    @Test
    void pickUpOrderCustomerUserNOT_AUTH() throws Exception {
        BeerOrder beerOrder = stPeteCustomer.getBeerOrders().stream().findFirst().orElseThrow();
        mockMvc.perform(get(API_ROOT + stPeteCustomer.getId() + "/orders/" + beerOrder.getId() + "/pickup"))
                .andExpect(status().isForbidden());
    }

    private BeerOrderDto buildOrderDto(Customer customer, UUID beerId) {
        List<BeerOrderLineDto> orderLines = Arrays.asList(BeerOrderLineDto.builder()
                .id(UUID.randomUUID())
                .beerId(beerId)
                .orderQuantity(5)
                .build());

        return BeerOrderDto.builder()
                .customerId(customer.getId())
                .customerRef("123")
                .orderStatusCallbackUrl("http://example.com")
                .beerOrderLines(orderLines)
                .build();
    }
}