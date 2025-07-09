package it.trustflow.document.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Document implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String filename; // nome generato internamente

    private String contentType;

    @Column(nullable = false)
    private String ownerId;

    @Column(nullable = false)
    private Long tenantId;

    private String status; // IN_REVISIONE, APPROVATO, ecc.

    private String path; // path su filesystem o archivio esterno

    @Column(name = "data_inserimento", updatable = false)
    private LocalDateTime dataInserimento;

    @Column(name = "data_modifica")
    private LocalDateTime dataModifica;

    @PrePersist
    protected void onCreate() {
        this.dataInserimento = LocalDateTime.now();
        this.dataModifica = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.dataModifica = LocalDateTime.now();
    }
}
