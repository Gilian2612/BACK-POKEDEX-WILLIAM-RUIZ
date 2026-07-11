package com.wilddex.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "captured_pokemon",
       uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "pokemon_id"}))
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class CapturedPokemon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "pokemon_id", nullable = false)
    private Integer pokemonId;

    @Column(name = "pokemon_name", nullable = false, length = 50)
    private String pokemonName;

    @CreationTimestamp
    @Column(name = "captured_at", updatable = false)
    private LocalDateTime capturedAt;
}
