package fr.soat.cqrs.model;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class OrderLine {
    private Long id;
    private Long productReference;
    private int quantity;
}
