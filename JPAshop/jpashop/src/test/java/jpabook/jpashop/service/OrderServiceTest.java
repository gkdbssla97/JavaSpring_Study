package jpabook.jpashop.service;

import jpabook.jpashop.domain.*;
import jpabook.jpashop.exception.NotEnoughStockException;
import jpabook.jpashop.repository.OrderRepository;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class OrderServiceTest {

    @Autowired
    EntityManager em;
    @Autowired OrderService orderService;
    @Autowired
    OrderRepository orderRepository;

    @DisplayName("상품주문")
    @Test
    public void 상품주문() throws Exception {
        Member member = createMember("하윤");

        Book book = createBook("AABOOK", 10000, 10);

        int orderCount = 2;

        Long order = orderService.order(member.getId(), book.getId(), orderCount);
        Order getOrder = orderRepository.findOne(order);
//        assertEquals("상품 주문시 상태는 ORDER", OrderStatus.ORDER, getOrder.getStatus());
//        assertEquals("주문한 상품 주문 상품 수가 정확해야 한다.", 1, getOrder.getOrderItems().size());
//        assertEquals("주문 가격은 가격 * 수량이다.", 10000 * orderCount, getOrder.getTotalPrice());
        assertEquals("주문 수량만큼 재고가 줄어야 한다.", 8, book.getStockQuantity());
    }

    @Test
    public void 주문취소() throws Exception {
        Member member = createMember("HHA");
        Book item = createBook("AA", 10000, 10);

        int orderCount = 2;
        Long orderId = orderService.order(member.getId(), item.getId(), orderCount);
        orderService.cancelOrder(orderId);
        Order order = orderRepository.findOne(orderId);
        assertEquals("주문취소되었습니다", OrderStatus.CANCEL, order.getStatus());
        assertEquals("주문이 취소된 상품은 그만큼 재고가 증가해야 한다.", 10, item.getStockQuantity());
    }

    @Test(expected = NotEnoughStockException.class)
    public void 상품주문_재고수량초과() throws Exception {
        Member member = createMember("하윤");
        Item book = createBook("AABOOK", 10000, 10);
        int orderCount = 11;

        Long order = orderService.order(member.getId(), book.getId(), orderCount);
        fail("재고 수량 부족 예외 발생");
    }
    private Book createBook(String name, int price, int stockQuantity) {
        Book book = new Book();
        book.setName(name);
        book.setPrice(price);
        book.setStockQuantity(stockQuantity);
        em.persist(book);
        return book;
    }

    private Member createMember(String name) {
        Member member = new Member();
        member.setName(name);
        member.setAddress(new Address("서울","광진", "123-123"));
        em.persist(member);
        return member;
    }
}