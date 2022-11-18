package com.alexeykovzel.fi.features.insider;

import com.alexeykovzel.fi.features.stock.Stock;
import com.alexeykovzel.fi.features.trade.form4.Form4;
import lombok.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.rest.core.config.Projection;

import javax.persistence.*;
import java.util.Collection;
import java.util.Date;

@Entity
@Table(name = "insiders")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@EqualsAndHashCode(exclude = {"stock", "form4s", "positions"})
@ToString(exclude = {"stock", "form4s"})
public class Insider {

    @Id
    private String cik;

    @Column(nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_cik")
    private Stock stock;

    @ManyToMany(mappedBy = "insiders")
    private Collection<Form4> form4s;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(joinColumns = @JoinColumn(name = "insider_cik", referencedColumnName = "cik"))
    private Collection<String> positions;

    @Projection(name = "insider", types = Insider.class)
    public interface View {

        @Value("#{target.name}")
        String getName();

        @Value("#{target.positions}")
        Collection<String> getPositions();
    }

    public interface TableView {
        String getName();

        Collection<String> getPositions();

        Date getLastActive();

        double getTotalShares();
    }
}
