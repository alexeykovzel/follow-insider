package com.alexeykovzel.fi.features.company;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "company_rating")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@EqualsAndHashCode(exclude = {"company"})
@ToString(exclude = {"company"})
public class CompanyRating {

    @Id
    @Column(name = "company_cik")
    private String cik;

    @MapsId
    @OneToOne
    @JoinColumn(name = "company_cik")
    private Company company;

    @Column(name = "efficiency", columnDefinition = "Decimal(5,4)")
    private Double efficiency;

    @Column(name = "trend", columnDefinition = "Decimal(5,4)")
    private Double trend;
}
