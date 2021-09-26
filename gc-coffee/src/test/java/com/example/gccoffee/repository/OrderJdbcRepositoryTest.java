package com.example.gccoffee.repository;

import com.example.gccoffee.model.Order;
import com.example.gccoffee.model.*;
import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import javax.sql.DataSource;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@SpringBootTest
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class OrderJdbcRepositoryTest {

    private static final Logger logger = LoggerFactory.getLogger(ProductJdbcRepositoryTest.class);

    @Configuration
    static class Config {
        @Bean
        public DataSource dataSource() {
            return DataSourceBuilder.create()
                    .url("jdbc:mysql://localhost/project")
                    .username("root")
                    .password("skyey9808")
                    .type(HikariDataSource.class)
                    .build();
        }

        @Bean
        public NamedParameterJdbcTemplate namedParameterJdbcTemplate(DataSource dataSource) {
            return new NamedParameterJdbcTemplate(dataSource);
        }

        @Bean
        public OrderJdbcRepository orderJdbcRepository(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
            return new OrderJdbcRepository(namedParameterJdbcTemplate);
        }

        @Bean
        public ProductJdbcRepository productJdbcRepository(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
            return new ProductJdbcRepository(namedParameterJdbcTemplate);
        }
    }

    @Autowired
    OrderJdbcRepository orderJdbcRepository;

    @Autowired
    ProductJdbcRepository productJdbcRepository;

    Order order;
    List<OrderItem> orderItemList = new ArrayList<>();

    Product product;

    @BeforeEach
    void setUp() {
        product = new Product(UUID.randomUUID(), "test-product", Category.COFFEE_BEAN_PANCAKE, 1000L);
        productJdbcRepository.insert(product);
        OrderItem orderItem1 = new OrderItem(product.getProductId(), Category.COFFEE_BEAN_PANCAKE, 1000L, 1);
        orderItemList.add(orderItem1);

        order = new Order(UUID.randomUUID(), new Email("test@gmail.com"), "seoul", "11111", orderItemList, OrderStatus.ACCEPTED, LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS), LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS));
        orderJdbcRepository.insert(order);
    }

    @AfterEach
    void clearUp() {
        orderItemList.clear();
        orderJdbcRepository.deleteAll();
        productJdbcRepository.deleteAll();
    }

    @Test
    @DisplayName("Order를 생성할 수 있다.")
    void insert() {
        Order testOrder = new Order(UUID.randomUUID(), new Email("test2@gmail.com"), "seoul", "22222", orderItemList, OrderStatus.ACCEPTED, LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS), LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS));
        orderJdbcRepository.insert(testOrder);

        var afterAll = orderJdbcRepository.findAll();

        assertThat(afterAll.size(), is(2));

        var findOrder = orderJdbcRepository.findById(testOrder.getOrderId());

        assertThat(findOrder.get().getOrderId(), is(testOrder.getOrderId()));
    }

    @Test
    void update() {
        Order testOrder = order;
        testOrder.setOrderStatus(OrderStatus.PAYMENT_CONFIRMED);
        orderJdbcRepository.update(testOrder);

        var findOrder = orderJdbcRepository.findById(order.getOrderId()).get();

        assertThat(findOrder.getOrderStatus(), is(OrderStatus.PAYMENT_CONFIRMED));
    }

    @Test
    void findAll() {
        var all = orderJdbcRepository.findAll();

        assertThat(all.size(), is(1));
    }

    @Test
    void findById() {
        var findOrder = orderJdbcRepository.findById(order.getOrderId());

        assertThat(order.getOrderId(), is(findOrder.get().getOrderId()));
    }

    @Test
    @DisplayName("모든 주문을 삭제할 수 있다.")
    void deleteAll() {
        var beforeAll = orderJdbcRepository.findAll();
        assertThat(beforeAll.isEmpty(), is(false));

        orderJdbcRepository.deleteAll();

        var afterAll = orderJdbcRepository.findAll();
        assertThat(afterAll.isEmpty(), is(true));
    }
}