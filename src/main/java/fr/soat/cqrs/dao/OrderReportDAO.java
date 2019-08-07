package fr.soat.cqrs.dao;

import java.time.LocalDate;

public interface OrderReportDAO {
    void upsert(Long productReference, String productName, float price, LocalDate orderDate);
}
