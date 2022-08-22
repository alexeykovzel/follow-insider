package com.alexeykovzel.fi.features.stock;

import com.alexeykovzel.fi.features.company.Company;
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
@EqualsAndHashCode(exclude = {"company"})
@ToString(exclude = {"company"})
public class StockRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_cik")
    private Company company;

    @Column(name = "dividends", columnDefinition = "Decimal(10, 4)")
    private Double dividends;

    @Column(name = "price", columnDefinition = "Decimal(10, 4)")
    private Double price;

    @Column(name = "date")
    private Date date;
}
