package com.example.gccoffee.repository;

import com.example.gccoffee.model.Email;
import com.example.gccoffee.model.Order;
import com.example.gccoffee.model.OrderItem;
import com.example.gccoffee.model.OrderStatus;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

import static com.example.gccoffee.JdbcUtils.toLocalDateTime;
import static com.example.gccoffee.JdbcUtils.toUUID;

@Repository
public class OrderJdbcRepository implements OrderRepository {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public OrderJdbcRepository(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    @Override
    @Transactional
    public Order insert(Order order) {
        namedParameterJdbcTemplate.update("INSERT INTO orders(order_id, email, address, postcode, order_status, created_at, updated_at)" +
                        " VALUES(UUID_TO_BIN(:orderId), :email, :address, :postcode, :orderStatus, :createdAt, :updatedAt)",
                toOrderParamMap(order));

        order.getOrderItems()
                .forEach(item ->
                        namedParameterJdbcTemplate.update("INSERT INTO order_items(order_id, product_id, category, price, quantity, created_at, updated_at)" +
                                        "VALUES (UUID_TO_BIN(:orderId), UUID_TO_BIN(:productId), :category, :price, :quantity, :createdAt, :updatedAt)",
                                toOrderItemParamMap(order.getOrderId(), order.getCreatedAt(), order.getUpdatedAt(), item)
                        ));

        return order;
    }

    @Override
    public Order update(Order order) {
        var update = namedParameterJdbcTemplate.update("UPDATE orders SET email = :email, address = :address, postcode = :postcode, order_status = :orderStatus,updated_at = :updatedAt WHERE order_id =UUID_TO_BIN(:orderId)", toOrderParamMap(order));

        if (update != 1) {
            throw new RuntimeException("Nothing was updated");
        }
        return order;
    }

    @Override
    public List<Order> findAll() {
        return namedParameterJdbcTemplate.query("SELECT * FROM orders", orderRowMapper);
    }

    @Override
    public Optional<Order> findById(UUID orderId) {
        try {
            return Optional.ofNullable(namedParameterJdbcTemplate.queryForObject("SELECT * FROM orders WHERE order_id =UUID_TO_BIN(:orderId)", Collections.singletonMap("orderId", orderId.toString().getBytes()), orderRowMapper));
        } catch (EmptyResultDataAccessException e) {
            e.getMessage();
            return Optional.empty();
        }
    }

    @Override
    public void deleteAll() {
        namedParameterJdbcTemplate.update("DELETE FROM orders", Collections.emptyMap());
    }

    @Override
    public void deleteById(UUID orderId) {
        var paramMap = new HashMap<String, Object>();
        paramMap.put("orderId", orderId.toString().getBytes());

        var delete = namedParameterJdbcTemplate.update("DELETE FROM orders WHERE order_id = UUID_TO_BIN(:orderId)", paramMap);

        if (delete != 1) {
            throw new RuntimeException("Nothing was deleted!");
        }
    }

    private static final RowMapper<Order> orderRowMapper = (resultSet, i) -> {
        var orderId = toUUID(resultSet.getBytes("order_id"));
        var email = resultSet.getString("email");
        var address = resultSet.getString("address");
        var postcode = resultSet.getString("postcode");
        var order_status = OrderStatus.valueOf(resultSet.getString("order_status"));
        var createdAt = toLocalDateTime(resultSet.getTimestamp("created_at"));
        var updatedAt = toLocalDateTime(resultSet.getTimestamp("updated_at"));

        return new Order(orderId, new Email(email), address, postcode, null, order_status, createdAt, updatedAt);
    };


    private Map<String, Object> toOrderParamMap(Order order) {
        var paramMap = new HashMap<String, Object>();

        paramMap.put("orderId", order.getOrderId().toString().getBytes());
        paramMap.put("email", order.getEmail().getAddress());
        paramMap.put("address", order.getAddress());
        paramMap.put("postcode", order.getPostcode());
        paramMap.put("orderStatus", order.getOrderStatus().toString());
        paramMap.put("createdAt", order.getCreatedAt());
        paramMap.put("updatedAt", order.getUpdatedAt());

        return paramMap;
    }

    private Map<String, Object> toOrderItemParamMap(UUID orderId, LocalDateTime createdAt, LocalDateTime updatedAt, OrderItem item) {
        var paramMap = new HashMap<String, Object>();
        paramMap.put("orderId", orderId.toString().getBytes());
        paramMap.put("productId", item.productId().toString().getBytes());
        paramMap.put("category", item.category().toString());
        paramMap.put("price", item.price());
        paramMap.put("quantity", item.quantity());
        paramMap.put("createdAt", createdAt);
        paramMap.put("updatedAt", updatedAt);

        return paramMap;
    }
}
