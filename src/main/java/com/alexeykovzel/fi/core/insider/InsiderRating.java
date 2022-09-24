package com.alexeykovzel.fi.core.insider;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "insider_rating")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@EqualsAndHashCode(exclude = {"insider"})
@ToString(exclude = {"insider"})
public class InsiderRating {

    @Id
    @Column(name = "insider_cik")
    private String cik;

    @MapsId
    @OneToOne
    @JoinColumn(name = "insider_cik")
    private Insider insider;

    @Column(columnDefinition = "Decimal(5,4)")
    private Double efficiency;
}
