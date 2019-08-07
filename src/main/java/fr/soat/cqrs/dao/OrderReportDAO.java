package fr.soat.cqrs.dao;

import java.time.LocalDate;
import java.util.List;

public interface OrderReportDAO {
    void upsert(Long productReference, String productName, float price, LocalDate orderDate);

    List<String> getAllSortByDate();
}
