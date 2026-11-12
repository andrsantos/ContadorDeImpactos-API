package com.API.ContadorDeImpactos.entities;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(
    name = "registros_contagem",
    indexes = {
        @Index(name = "idx_painel_data_captura", columnList = "painel_id, data_hora_captura")
    },
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_painel_timestamp", columnNames = {"painel_id", "data_hora_captura"})
    }
)
public class RegistroContagem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "painel_id", nullable = false, length = 50)
    private String painelId;

    @Column(nullable = false)
    private Integer caminhao = 0;

    @Column(nullable = false)
    private Integer carro = 0;

    @Column(nullable = false)
    private Integer moto = 0;

    @Column(nullable = false)
    private Integer onibus = 0;

    @Column(nullable = false)
    private Integer pedestre = 0;

    @Column(name = "total_no_minuto", nullable = false)
    private Integer totalNoMinuto = 0;

    @Column(name = "total_acumulado_sessao", nullable = false)
    private Long totalAcumuladoSessao = 0L;

    @Column(name = "data_hora_captura", nullable = false)
    private Instant dataHoraCaptura;

    @Column(name = "criado_em", nullable = false, updatable = false)
    private Instant criadoEm;

    @PrePersist
    protected void onCreate() {
        this.criadoEm = Instant.now();
    }

    public RegistroContagem() {}

    public Long getId() { return id; }

    public String getPainelId() { return painelId; }
    public void setPainelId(String painelId) { this.painelId = painelId; }

    public Integer getCaminhao() { return caminhao; }
    public void setCaminhao(Integer caminhao) { this.caminhao = caminhao; }

    public Integer getCarro() { return carro; }
    public void setCarro(Integer carro) { this.carro = carro; }

    public Integer getMoto() { return moto; }
    public void setMoto(Integer moto) { this.moto = moto; }

    public Integer getOnibus() { return onibus; }
    public void setOnibus(Integer onibus) { this.onibus = onibus; }

    public Integer getPedestre() { return pedestre; }
    public void setPedestre(Integer pedestre) { this.pedestre = pedestre; }

    public Integer getTotalNoMinuto() { return totalNoMinuto; }
    public void setTotalNoMinuto(Integer totalNoMinuto) { this.totalNoMinuto = totalNoMinuto; }

    public Long getTotalAcumuladoSessao() { return totalAcumuladoSessao; }
    public void setTotalAcumuladoSessao(Long totalAcumuladoSessao) { this.totalAcumuladoSessao = totalAcumuladoSessao; }

    public Instant getDataHoraCaptura() { return dataHoraCaptura; }
    public void setDataHoraCaptura(Instant dataHoraCaptura) { this.dataHoraCaptura = dataHoraCaptura; }

    public Instant getCriadoEm() { return criadoEm; }
}