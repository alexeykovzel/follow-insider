package com.alexeykovzel.fi.features.trade;

import com.alexeykovzel.fi.features.form4.Form4;
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

    @Column(name = "security_title")
    private String securityTitle;

    @Column(name = "share_price", columnDefinition = "Decimal(10, 4)")
    private Double sharePrice;

    @Column(name = "share_count")
    private Double shareCount;

    @Column(name = "left_shares")
    private Double leftShares;

    @Column(name = "is_direct")
    private Boolean isDirect;

    @Column(name = "code")
    private String code;

    @Column(name = "date")
    private Date date;
}
