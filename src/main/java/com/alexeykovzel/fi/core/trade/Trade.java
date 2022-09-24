package com.alexeykovzel.fi.core.trade;

import com.alexeykovzel.fi.core.trade.form4.Form4;
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
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "accession_no")
    private Form4 form4;

    @OneToOne(mappedBy = "trade")
    private TradeRating rating;

    @Column
    private String securityTitle;

    @Column(columnDefinition = "Decimal(10, 4)")
    private Double sharePrice;

    @Column(columnDefinition = "float")
    private Double shareCount;

    @Column(columnDefinition = "float")
    private Double leftShares;

    @Column
    private Boolean isDirect;

    @Column(nullable = false)
    private String code;

    @Column(nullable = false)
    private Date date;
}
