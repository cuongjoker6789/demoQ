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


}
