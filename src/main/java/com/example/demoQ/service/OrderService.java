package com.example.demoQ.service;

import com.example.demoQ.entity.Order;
import com.example.demoQ.entity.Order_;
import com.example.demoQ.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private OrderRepository orderRepository;

    public List<Order> getAll(Order orderInput) {
        // SELECT * FROM tbl_orders
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Order> query = cb.createQuery(Order.class);
        Root<Order> order = query.from(Order.class);

        List<Predicate> predicates = new ArrayList<>();

        Optional.ofNullable(orderInput.getAmount())
                        .ifPresent(amount -> predicates.add(cb.equal(order.get(Order_.AMOUNT), amount)));

        Optional.ofNullable(orderInput.getPaymentType())
                        .ifPresent(type -> predicates.add(cb.equal(order.get(Order_.PAYMENT_TYPE), type)));

        Optional.ofNullable(orderInput.getPaymentStatus())
                         .ifPresent(status -> predicates.add(cb.equal(order.get(Order_.PAYMENT_STATUS), status)));


        query.where(predicates.toArray(new Predicate[0]));

        query.select(order);
         return entityManager.createQuery(query).getResultList();



    }
}
