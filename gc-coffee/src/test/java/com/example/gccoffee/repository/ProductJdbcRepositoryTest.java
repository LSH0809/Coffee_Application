package com.example.gccoffee.repository;

import com.example.gccoffee.model.Category;
import com.example.gccoffee.model.Product;
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
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.samePropertyValuesAs;

@SpringBootTest
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ProductJdbcRepositoryTest {

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
        public ProductJdbcRepository productJdbcRepository(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
            return new ProductJdbcRepository(namedParameterJdbcTemplate);
        }
    }

    @Autowired
    ProductJdbcRepository repository;

    Product product;

    @BeforeEach
    void setUp() {
        product = new Product(UUID.randomUUID(), "product", Category.COFFEE_BEAN_PANCAKE, 1000L);
        repository.insert(product);
    }

    @AfterEach
    void clearUp() {
        repository.deleteAll();
    }

    @Test
    @DisplayName("상품을 추가할 수 있다.")
    void testInsert() {
        var testProduct = new Product(UUID.randomUUID(), "test-product", Category.COFFEE_BEAN_PANCAKE, 2000L);
        var beforeAll = repository.findAll();
        assertThat(beforeAll.size(), is(1));

        repository.insert(testProduct);
        var afterAll = repository.findAll();
        assertThat(afterAll.size(), is(2));

        var findProduct = repository.findById(testProduct.getProductId());
        assertThat(findProduct.get(), samePropertyValuesAs(testProduct));
    }

    @Test
    @DisplayName("상품을 이름으로 조회할 수 있다.")
    void testFindByName() {
        var findProduct = repository.findByName(product.getProductName());

        assertThat(findProduct.get().getProductName(), is("product"));
        assertThat(findProduct.get(), samePropertyValuesAs(product));
    }

    @Test
    @DisplayName("상품을아이디로 조회할 수 있다.")
    void testFindById() {
        var findProduct = repository.findById(product.getProductId());

        assertThat(findProduct.get(), samePropertyValuesAs(product));
    }

    @Test
    @DisplayName("상품들을 카테고리로 조회할 수 있다.")
    void testFindByCategory() {
        var product = repository.findByCategory(Category.COFFEE_BEAN_PANCAKE);

        assertThat(product.isEmpty(), is(false));
    }

    @Test
    @DisplayName("상품을 수정할 수 있다.")
    void testUpdate() {
        var newProduct = product;
        newProduct.setProductName("new-product");

        repository.update(newProduct);

        var findProduct = repository.findById(product.getProductId()).get();


        assertThat(findProduct.getProductName(), is("new-product"));
        assertThat(findProduct.getProductId(), is(product.getProductId()));
        assertThat(findProduct.getCategory(), is(product.getCategory()));
        assertThat(findProduct.getPrice(), is(product.getPrice()));
    }

    @Test
    @DisplayName("상품을 전체 삭제한다.")
    void testDeleteAll() {
        var beforeAll = repository.findAll();
        assertThat(beforeAll.isEmpty(), is(false));

        repository.deleteAll();

        var afterAll = repository.findAll();
        assertThat(afterAll.isEmpty(), is(true));
    }
}