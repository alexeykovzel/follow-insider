package com.alexeykovzel.fi.features.trade;

import com.alexeykovzel.fi.features.trade.form4.Form4;
import com.alexeykovzel.fi.features.trade.rating.TradeRating;
import lombok.*;

import javax.persistence.*;
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
}
