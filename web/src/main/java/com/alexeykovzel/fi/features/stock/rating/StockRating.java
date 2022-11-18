package com.alexeykovzel.fi.features.stock.rating;

import com.alexeykovzel.fi.features.stock.Stock;
import lombok.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.rest.core.config.Projection;

import javax.persistence.*;

@Entity
@Table(name = "stock_rating")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@EqualsAndHashCode(exclude = {"stock"})
@ToString(exclude = {"stock"})
public class StockRating {

    @Id
    @Column(name = "stock_cik")
    private String cik;

    @MapsId
    @OneToOne
    @JoinColumn(name = "stock_cik")
    private Stock stock;

    @Column(columnDefinition = "Decimal(5,4)")
    private Double trend;

    @Column(columnDefinition = "Decimal(5,4)")
    private Double efficiency;

    @Column(columnDefinition = "Decimal(5,4)")
    private Double overall;

    @Projection(name = "rating", types = StockRating.class)
    public interface View {

        @Value("#{target.trend}")
        Double getTrend();

        @Value("#{target.efficiency}")
        Double getEfficiency();

        @Value("#{target.overall}")
        Double getOverall();
    }
}
