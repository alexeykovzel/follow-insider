package com.alexeykovzel.fi.features.stock.records;

import com.alexeykovzel.fi.features.stock.Stock;
import lombok.*;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "stock_records")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@EqualsAndHashCode(exclude = {"stock"})
@ToString(exclude = {"stock"})
public class StockRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_cik")
    private Stock stock;

    @Column(columnDefinition = "Decimal(10, 4)")
    private Double dividends;

    @Column(columnDefinition = "Decimal(10, 4)")
    private Double price;

    @Column(nullable = false)
    private Date date;
}
