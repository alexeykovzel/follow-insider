package com.alexeykovzel.fi.core.stock;

import com.alexeykovzel.fi.core.insider.Insider;
import com.alexeykovzel.fi.core.trade.form4.Form4;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.springframework.data.domain.Persistable;

import javax.persistence.*;
import java.util.Collection;
import java.util.UUID;

@Entity
@Table(name = "stocks")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@EqualsAndHashCode(exclude = {"form4s", "insiders", "records"})
@ToString(exclude = {"form4s", "insiders", "records"})
public class Stock implements Persistable<String> {

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

    @OneToMany(mappedBy = "stock", cascade = CascadeType.REMOVE)
    private Collection<Form4> form4s;

    @OneToMany(mappedBy = "stock", cascade = CascadeType.REMOVE)
    private Collection<Insider> insiders;

    @JsonIgnore
    @OneToMany(mappedBy = "stock", cascade = CascadeType.REMOVE)
    private Collection<StockRecord> records;

    @Override
    public String getId() {
        return cik;
    }

    @Override
    public boolean isNew() {
        return true;
    }
}
