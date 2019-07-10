package fr.soat.cqrs.model;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class Product {
    private Long reference;
    private String name;
    private float price;
    private float supplyPrice;
}
