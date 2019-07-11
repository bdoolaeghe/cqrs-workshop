package fr.soat.cqrs.model;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@Builder
public class Order {

    private Long id;
    private List<OrderLine> lines = new ArrayList<>();

}
