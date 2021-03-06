package com.example.demoQ.service;

import com.example.demoQ.entity.Order;
import com.example.demoQ.entity.Order_;
import com.example.demoQ.model.OrderDTO;
import com.example.demoQ.model.PageResult;
import com.example.demoQ.model.Report;
import com.example.demoQ.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.Tuple;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
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
        CriteriaQuery<String> query = cb.createQuery(String.class);

        Root<Order> order = query.from(Order.class);

        List<Predicate> predicates = new ArrayList<>();

        Optional.ofNullable(orderDTO.getAmount())
                        .ifPresent(amount -> predicates.add(cb.equal(order.get(Order_.AMOUNT), amount)));

        Optional.ofNullable(orderDTO.getPaymentType())
                        .ifPresent(type -> predicates.add(cb.equal(order.get(Order_.PAYMENT_TYPE), type)));

        Optional.ofNullable(orderDTO.getPaymentStatus())
                         .ifPresent(status -> predicates.add(cb.equal(order.get(Order_.PAYMENT_STATUS), status)));

        query.where(predicates.toArray(new Predicate[0]));

        query.select(order.get(Order_.ID));
         List<String> ids =  entityManager.createQuery(query)
                 .getResultList();

        PageResult<Order> results = new PageResult<>();
        results.setData(getOrderByIds(ids, orderDTO));
        results.setTotal(ids.size());


        return results;
    }

    private List<Order> getOrderByIds(List<String> ids, OrderDTO orderDTO) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Order> query = cb.createQuery(Order.class);
        Root<Order> order = query.from(Order.class);
        query.where(order.get(Order_.ID).in(ids));
//        query.multiselect()
//                TupleQuery
        return entityManager.createQuery(query)
                .setFirstResult(orderDTO.getPage() * orderDTO.getSize())
                .setMaxResults(orderDTO.getSize())
                .getResultList();
    }


    public Report getReport() {
        Report report = new Report();
        report.setCash(getTotalCash());
        report.setAtm(getTotalATM());
        report.setMomo(getTotalMomo());
        return report;
    }

    private Long getTotalCash() {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<Order> order = query.from(Order.class);

        query.where(cb.equal(order.get(Order_.PAYMENT_TYPE), 0));
        query.select(cb.count(order));
        return entityManager.createQuery(query).getSingleResult();
    }

    private Long getTotalATM() {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<Order> order =query.from(Order.class);
        query.where(cb.equal(order.get(Order_.PAYMENT_TYPE), 1));
        query.select(cb.count(order));
        return  entityManager.createQuery(query).getSingleResult();
    }

    private Long getTotalMomo() {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<Order> order = query.from(Order.class);
        query.where(cb.equal(order.get(Order_.PAYMENT_TYPE), 3));
        query.select(cb.count(order));
        return entityManager.createQuery(query).getSingleResult();
    }
    public List<Tuple> getNumberTotal(){
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> query = cb.createTupleQuery();
        Root<Order> order = query.from(Order.class);
        List<Expression<?>> expressions = new ArrayList<>();

        expressions.add(cb.sumAsLong(
                cb.<Integer>selectCase().when(cb.equal(order.get(Order_.PAYMENT_TYPE), 0), order.get(Order_.AMOUNT))
                        .otherwise(0)
        ));

        expressions.add(cb.sumAsLong(
                cb.<Integer>selectCase().when(cb.equal(order.get(Order_.PAYMENT_TYPE), 1), order.get(Order_.AMOUNT))
                        .otherwise(0)
        ));



        expressions.add(cb.sumAsLong(
                cb.<Integer>selectCase().when(cb.equal(order.get(Order_.PAYMENT_TYPE), 3), order.get(Order_.AMOUNT))
                        .otherwise(0)
        ));

//        expressions.add(cb.count(
//                cb.selectCase().when(cb.equal(order.get(Order_.PAYMENT_TYPE), 3),0).otherwise(0)
//                )
//
//        );

        List<Expression<?>> groupByExp = new ArrayList<>();

        groupByExp.add(order.get(Order_.PAYMENT_TYPE));

        query.multiselect(expressions.toArray(new Expression[0]))
                .groupBy(groupByExp.toArray(new Expression[0]));

        List<Tuple> results = entityManager.createQuery(query).getResultList();
        results.stream().forEach(n -> {

            System.out.println(n.get(0));

            System.out.println(n.get(1));
            System.out.println(n.get(2));

        });


        return results;

    }
    public Report getTotalReport() {
        Report report = new Report();

//        report.setCash();
//        report.setAtm();
//        report.setMomo();
       return report;
    }


}
