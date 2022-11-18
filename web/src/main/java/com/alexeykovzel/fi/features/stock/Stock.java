package com.alexeykovzel.fi.features.stock;

import com.alexeykovzel.fi.features.insider.Insider;
import com.alexeykovzel.fi.features.stock.rating.StockRating;
import com.alexeykovzel.fi.features.stock.records.StockRecord;
import com.alexeykovzel.fi.features.trade.form4.Form4;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.rest.core.config.Projection;

import javax.persistence.*;
import java.util.Collection;
import java.util.Date;
import java.util.List;

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

    @OneToOne(mappedBy = "stock")
    private StockRating rating;

    @OneToMany(mappedBy = "stock", cascade = CascadeType.REMOVE)
    private Collection<Form4> form4s;

    @OneToMany(mappedBy = "stock", cascade = CascadeType.REMOVE)
    private Collection<Insider> insiders;

    @JsonIgnore
    @OneToMany(mappedBy = "stock", cascade = CascadeType.REMOVE)
    private Collection<StockRecord> records;

    public String getFullName() {
        return String.format("%s (%s)", name, symbol);
    }

    @Projection(name = "stock", types = Stock.class)
    public interface View {

        @Value("#{target.name}")
        String getName();

        @Value("#{target.symbol}")
        String getSymbol();

        @Value("#{target.description}")
        String getDescription();

        @Value("#{target.rating}")
        Double getRating();

        @Value("#{@stockService.getNews(target)}")
        List<String> getNews();

        @Value("#{@stockService.getLastActive(target)}")
        Date getLastActive();
    }
}
