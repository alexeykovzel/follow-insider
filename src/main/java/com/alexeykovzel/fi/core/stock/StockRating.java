package com.alexeykovzel.fi.core.stock;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "stock_rating")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@EqualsAndHashCode(exclude = {"stock"})
@ToString(exclude = {"stock"})
public class StockRating {

    @Id
    @Column(name = "stock_cik")
    private String cik;

    @MapsId
    @OneToOne
    @JoinColumn(name = "stock_cik")
    private Stock stock;

    @Column(columnDefinition = "Decimal(5,4)")
    private Double trend;

    @Column(columnDefinition = "Decimal(5,4)")
    private Double efficiency;

    @Column(columnDefinition = "Decimal(5,4)")
    private Double overall;
}
