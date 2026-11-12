package com.API.ContadorDeImpactos.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.API.ContadorDeImpactos.entities.RegistroContagem;
import java.time.Instant;
import java.util.List;

@Repository
public interface RegistroContagemRepository extends JpaRepository<RegistroContagem, Long> {

    boolean existsByPainelIdAndDataHoraCaptura(String painelId, Instant dataHoraCaptura);

    List<RegistroContagem> findByPainelIdAndDataHoraCapturaBetweenOrderByDataHoraCapturaAsc(
            String painelId,
            Instant inicio,
            Instant fim
    );

    @Query("""
        SELECT
            COALESCE(SUM(r.caminhao), 0) AS totalCaminhoes,
            COALESCE(SUM(r.carro), 0) AS totalCarros,
            COALESCE(SUM(r.moto), 0) AS totalMotos,
            COALESCE(SUM(r.onibus), 0) AS totalOnibus,
            COALESCE(SUM(r.pedestre), 0) AS totalPedestres,
            COALESCE(SUM(r.totalNoMinuto), 0) AS totalGeral
        FROM RegistroContagem r
        WHERE r.painelId = :painelId
          AND r.dataHoraCaptura BETWEEN :inicio AND :fim
    """)
    ResumoPeriodoProjection calcularResumoPorPeriodo(
            @Param("painelId") String painelId,
            @Param("inicio") Instant inicio,
            @Param("fim") Instant fim
    );

    public interface ResumoPeriodoProjection {
        Long getTotalCaminhoes();
        Long getTotalCarros();
        Long getTotalMotos();
        Long getTotalOnibus();
        Long getTotalPedestres();
        Long getTotalGeral();
    }
}