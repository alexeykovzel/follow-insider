package com.alexeykovzel.fi.features.stock;

import com.alexeykovzel.fi.features.insider.Insider;
import com.alexeykovzel.fi.features.stock.record.StockRecord;
import com.alexeykovzel.fi.features.trade.form4.Form4;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.util.Collection;

@Entity
@Table(name = "stocks")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@EqualsAndHashCode(exclude = {"form4s", "insiders", "records"})
@ToString(exclude = {"form4s", "insiders", "records"})
public class Stock {

    @Id
    private String cik;

    @Column(nullable = false)
    private String name;

    @Column(unique = true)
    private String symbol;

    @Column
    private String description;

    @Column
    private String exchange;

    @OneToMany(mappedBy = "stock", cascade = CascadeType.REMOVE)
    private Collection<Form4> form4s;

    @OneToMany(mappedBy = "stock", cascade = CascadeType.REMOVE)
    private Collection<Insider> insiders;

    @JsonIgnore
    @OneToMany(mappedBy = "stock", cascade = CascadeType.REMOVE)
    private Collection<StockRecord> records;
}
