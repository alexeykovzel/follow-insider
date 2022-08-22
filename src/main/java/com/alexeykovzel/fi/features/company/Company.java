package com.alexeykovzel.fi.features.company;

import com.alexeykovzel.fi.features.insider.Insider;
import com.alexeykovzel.fi.features.form4.Form4;
import com.alexeykovzel.fi.features.stock.StockRecord;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.util.Collection;

@Entity
@Table(name = "companies")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@EqualsAndHashCode(exclude = {"form4s", "insiders", "stockRecords"})
@ToString(exclude = {"form4s", "insiders", "stockRecords"})
public class Company {

    @Id
    @Column(name = "cik")
    private String cik;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "symbol", unique = true)
    private String symbol;

    @Column(name = "exchange")
    private String exchange;

    @OneToMany(mappedBy = "company", cascade = CascadeType.REMOVE)
    private Collection<Form4> form4s;

    @OneToMany(mappedBy = "company", cascade = CascadeType.REMOVE)
    private Collection<Insider> insiders;

    @JsonIgnore
    @OneToMany(mappedBy = "company", cascade = CascadeType.REMOVE)
    private Collection<StockRecord> stockRecords;
}
