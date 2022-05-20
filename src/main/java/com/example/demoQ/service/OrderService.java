package com.example.demoQ.service;

import com.example.demoQ.entity.Order;
import com.example.demoQ.entity.Order_;
import com.example.demoQ.model.OrderDTO;
import com.example.demoQ.model.PageResult;
import com.example.demoQ.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
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

    public PageResult<Order> getAll(OrderDTO orderDTO) {
        // SELECT * FROM tbl_orders
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Order> query = cb.createQuery(Order.class);

        Root<Order> order = query.from(Order.class);

        List<Predicate> predicates = new ArrayList<>();

        Optional.ofNullable(orderDTO.getAmount())
                        .ifPresent(amount -> predicates.add(cb.equal(order.get(Order_.AMOUNT), amount)));

        Optional.ofNullable(orderDTO.getPaymentType())
                        .ifPresent(type -> predicates.add(cb.equal(order.get(Order_.PAYMENT_TYPE), type)));

        Optional.ofNullable(orderDTO.getPaymentStatus())
                         .ifPresent(status -> predicates.add(cb.equal(order.get(Order_.PAYMENT_STATUS), status)));


        query.where(predicates.toArray(new Predicate[0]));

        query.select(order);
         List<Order> orders =  entityManager.createQuery(query)
                 .setFirstResult(orderDTO.getPage() * orderDTO.getSize())
                 .setMaxResults(orderDTO.getSize())
                 .getResultList();

        PageResult<Order> results = new PageResult<>();
        results.setData(orders);
        results.setTotal(100);
        return results;
    }

    public List<Order> pagination(Order orderInput){
        int pageNumber = 1;
        int pageSize = 10;
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Order> query = cb.createQuery(Order.class);
        Root<Order> order = query.from(Order.class);
        List<Predicate> predicates = new ArrayList<>();
//        CriteriaQuery<Long> countQuery = criteriaBuilder
//                .createQuery(Long.class);
//        countQuery.select(criteriaBuilder
//                .count(countQuery.from(Order.class)));
//        Long count = entityManager.createQuery(countQuery)
//                .getSingleResult();
//        CriteriaQuery<Order> criteriaQuery = criteriaBuilder
//                .createQuery(Order.class);
//        Root<Order> from = criteriaQuery.from(Order.class);
//        CriteriaQuery<Order> select = criteriaQuery.select(from);
//
//        TypedQuery<Order> typedQuery = entityManager.createQuery(select);
//        while (pageNumber < count.intValue()) {
//            typedQuery.setFirstResult(pageNumber - 1);
//            typedQuery.setMaxResults(pageSize);
//            System.out.println("Current page: " + typedQuery.getResultList());
//            pageNumber += pageSize;
//        }




        query.where(predicates.toArray(new Predicate[0]));

        query.select(order);
        return entityManager.createQuery(query)
                .setFirstResult(0)
                .setMaxResults(10)
                .getResultList();

    }


}
