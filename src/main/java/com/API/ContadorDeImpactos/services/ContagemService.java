package com.API.ContadorDeImpactos.services;


import com.API.ContadorDeImpactos.controllers.ContagemController.LoteContagemRequest;
import com.API.ContadorDeImpactos.repositories.RegistroContagemRepository.ResumoPeriodoProjection;
import java.time.Instant;

public interface ContagemService {

    void processarLote(LoteContagemRequest payload);
    ResumoPeriodoProjection obterResumo(String painelId, Instant inicio, Instant fim);
        
    
}