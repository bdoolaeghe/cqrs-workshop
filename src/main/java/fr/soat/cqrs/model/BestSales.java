package fr.soat.cqrs.model;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@Builder
@Getter
public class BestSales {

    private List<Sales> sales = new ArrayList<>();

    public int getSize() {
        return sales.size();
    }

}
