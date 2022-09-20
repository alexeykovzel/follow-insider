package com.alexeykovzel.fi.features.trade;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "trade_rating")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@EqualsAndHashCode(exclude = {"trade"})
@ToString(exclude = {"trade"})
public class TradeRating {

    @Id
    @Column(name = "trade_id")
    private Long id;

    @MapsId
    @OneToOne
    @JoinColumn(name = "trade_id")
    private Trade trade;

    @Column(columnDefinition = "Decimal(5,4)")
    private Double efficiency;

    @Column(columnDefinition = "Decimal(5,4)")
    private Double weight;
}
