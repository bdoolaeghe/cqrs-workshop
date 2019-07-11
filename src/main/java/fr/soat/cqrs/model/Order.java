package fr.soat.cqrs.model;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class Order {
    private Long id;
    private List<OrderLine> lines = new ArrayList<>();
}
