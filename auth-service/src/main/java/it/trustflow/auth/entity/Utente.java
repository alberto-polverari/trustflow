package it.trustflow.auth.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Utente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String codiceFiscale;
    private String password;
    private String role;

    @ManyToOne
    @JoinColumn(name = "tenant_id")
    private Tenant tenant;
}

