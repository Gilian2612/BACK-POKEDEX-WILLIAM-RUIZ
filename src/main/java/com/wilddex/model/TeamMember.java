package com.wilddex.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "team_members",
       uniqueConstraints = @UniqueConstraint(columnNames = {"team_id", "slot"}))
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class TeamMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;

    @Column(name = "pokemon_id", nullable = false)
    private Integer pokemonId;

    @Column(name = "pokemon_name", nullable = false, length = 50)
    private String pokemonName;

    /** Slot 1-6 en el equipo */
    @Column(nullable = false)
    private Integer slot;
}
