package com.alexeykovzel.fi.features.trade.form4;

import com.alexeykovzel.fi.features.stock.Stock;
import com.alexeykovzel.fi.features.insider.Insider;
import com.alexeykovzel.fi.features.trade.Trade;
import lombok.*;

import javax.persistence.*;
import java.util.Collection;
import java.util.Date;

@Entity
@Table(name = "form4s")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(exclude = {"trades", "insiders", "stock"})
@ToString(exclude = {"trades", "insiders", "stock"})
public class Form4 {

    @Id
    private String accessionNo;

    @OneToMany(mappedBy = "form4", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Collection<Trade> trades;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "form4_reports",
            joinColumns = @JoinColumn(name = "accession_no"),
            inverseJoinColumns = @JoinColumn(name = "insider_cik")
    )
    private Collection<Insider> insiders;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_cik")
    private Stock stock;

    @Column(name = "filing_date")
    private Date date;

    @Column
    private String url;

    public Form4(String accessionNo, Date date, String url) {
        this.accessionNo = accessionNo;
        this.date = date;
        this.url = url;
    }
}
