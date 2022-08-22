package com.alexeykovzel.fi.features.insider;

import com.alexeykovzel.fi.features.company.Company;
import com.alexeykovzel.fi.features.form4.Form4;
import lombok.*;

import javax.persistence.*;
import java.util.Collection;

@Entity
@Table(name = "insiders")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@EqualsAndHashCode(exclude = {"company", "form4s", "positions"})
@ToString(exclude = {"company", "form4s"})
public class Insider {

    @Id
    @Column(name = "cik")
    private String cik;

    @Column(name = "name", nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_cik")
    private Company company;

    @ManyToMany(mappedBy = "insiders")
    private Collection<Form4> form4s;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "insider_positions",
            joinColumns = @JoinColumn(name = "insider_cik", referencedColumnName = "cik")
    )
    private Collection<String> positions;
}
