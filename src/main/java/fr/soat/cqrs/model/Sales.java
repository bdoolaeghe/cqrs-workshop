package fr.soat.cqrs.model;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@Builder
public class Sales {

    private String productName;
    private Float productMargin;

}
