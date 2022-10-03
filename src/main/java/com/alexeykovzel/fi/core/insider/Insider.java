package com.alexeykovzel.fi.core.insider;

import com.alexeykovzel.fi.core.stock.Stock;
import com.alexeykovzel.fi.core.trade.form4.Form4;
import lombok.*;
import org.springframework.data.domain.Persistable;

import javax.persistence.*;
import java.util.Collection;

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
    @CollectionTable(
            name = "insider_positions",
            joinColumns = @JoinColumn(name = "insider_cik", referencedColumnName = "cik")
    )
    private Collection<String> positions;
}
