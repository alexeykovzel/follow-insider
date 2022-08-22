package com.alexeykovzel.fi.features.form4;

import com.alexeykovzel.fi.features.company.Company;
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
@EqualsAndHashCode(exclude = {"trades", "insiders", "company"})
@ToString(exclude = {"trades", "insiders", "company"})
public class Form4 {

    @Id
    @Column(name = "accession_no")
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
    @JoinColumn(name = "company_cik")
    private Company company;

    @Column(name = "filing_date")
    private Date date;

    @Column(name = "url")
    private String url;

    public Form4(String accessionNo, Date date, String url) {
        this.accessionNo = accessionNo;
        this.date = date;
        this.url = url;
    }
}
