package com.hshhh.shopping.modules.order.repository;

import com.hshhh.shopping.modules.admin.dto.DailySalesDTO;
import com.hshhh.shopping.modules.order.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    Page<Order> findByUserId(Long userId, Pageable pageable);
    List<Order> findByUserId(Long userId);

    @Query("SELECT SUM(o.totalAmount) FROM Order o WHERE o.status > 0")
    BigDecimal sumTotalAmountPaid();

    @Query("SELECT SUM(o.totalAmount) FROM Order o WHERE o.status > 0 AND o.createTime BETWEEN :start AND :end")
    BigDecimal sumTotalAmountPaidBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT new com.hshhh.shopping.modules.admin.dto.DailySalesDTO(FUNCTION('DATE_FORMAT', o.createTime, :format), SUM(o.totalAmount)) " +
            "FROM Order o WHERE o.status > 0 AND o.createTime BETWEEN :start AND :end " +
            "GROUP BY FUNCTION('DATE_FORMAT', o.createTime, :format) " +
            "ORDER BY FUNCTION('DATE_FORMAT', o.createTime, :format)")
    List<DailySalesDTO> getSalesStats(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end, @Param("format") String format);
}
