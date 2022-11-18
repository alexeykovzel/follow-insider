package com.alexeykovzel.fi.features.trade;

import com.alexeykovzel.fi.features.insider.Insider;
import com.alexeykovzel.fi.features.stock.Stock;
import com.alexeykovzel.fi.features.trade.form4.Form4;
import com.alexeykovzel.fi.features.trade.rating.TradeRating;
import lombok.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.rest.core.config.Projection;

import javax.persistence.*;
import java.util.Collection;
import java.util.Date;

@Entity
@Table(name = "trades")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@EqualsAndHashCode(exclude = {"form4", "rating"})
@ToString(exclude = {"form4", "rating"})
public class Trade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "accession_no")
    private Form4 form4;

    @OneToOne(mappedBy = "trade")
    private TradeRating rating;

    @Column(nullable = false)
    private String securityTitle;

    @Column(columnDefinition = "Decimal(10, 4)", nullable = false)
    private double sharePrice;

    @Column(columnDefinition = "float", nullable = false)
    private double shareCount;

    @Column(columnDefinition = "float", nullable = false)
    private double leftShares;

    @Column(nullable = false)
    private boolean isDirect;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TradeCode code;

    @Column(nullable = false)
    private Date date;

    public double getValue() {
        return shareCount * sharePrice;
    }

    @Projection(name = "trade", types = Trade.class)
    public interface View {

        @Value("#{target.id}")
        int getId();

        @Value("#{target.sharePrice}")
        Double getSharePrice();

        @Value("#{target.shareCount}")
        Double getShareCount();

        @Value("#{target.leftShares}")
        Double getLeftShares();

        @Value("#{target.date}")
        Date getDate();

        @Value("#{@tradeService.getType(target)}")
        String getType();

        @Value("#{target.form4.stock.name}")
        String getCompany();

        @Value("#{target.form4.stock.symbol}")
        String getSymbol();

        @Value("#{target.form4.insiders}")
        Collection<Insider.View> getInsiders();

        @Value("#{target.form4.url}")
        String getUrl();
    }

    @Projection(name = "trade", types = Trade.class)
    public interface Point {

        @Value("#{target.shareCount}")
        double getShareCount();

        @Value("#{target.date}")
        Date getDate();
    }
}
