package com.API.ContadorDeImpactos.services.impl;

import java.time.Instant;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.API.ContadorDeImpactos.controllers.ContagemController.LoteContagemRequest;
import com.API.ContadorDeImpactos.entities.RegistroContagem;
import com.API.ContadorDeImpactos.repositories.RegistroContagemRepository;
import com.API.ContadorDeImpactos.repositories.RegistroContagemRepository.ResumoPeriodoProjection;
import com.API.ContadorDeImpactos.services.ContagemService;


@Service 
public class ContagemServiceImpl implements ContagemService {



    private final RegistroContagemRepository repository;

    public ContagemServiceImpl(RegistroContagemRepository repository) {
        this.repository = repository;
    }


    @Transactional
    public void processarLote(LoteContagemRequest payload) {
        long epochMillis = (long) (payload.timestamp() * 1000L);
        Instant dataCaptura = Instant.ofEpochMilli(epochMillis);

        if (repository.existsByPainelIdAndDataHoraCaptura(payload.painelId(), dataCaptura)) {
            return;
        }

        Map<Integer, Integer> classes = payload.classes();

        RegistroContagem registro = new RegistroContagem();
        registro.setPainelId(payload.painelId());
        registro.setCaminhao(classes.getOrDefault(0, 0));
        registro.setCarro(classes.getOrDefault(1, 0));
        registro.setMoto(classes.getOrDefault(2, 0));
        registro.setOnibus(classes.getOrDefault(3, 0));
        registro.setPedestre(classes.getOrDefault(4, 0));
        registro.setTotalNoMinuto(payload.total_no_minuto_atual());
        registro.setTotalAcumuladoSessao(payload.total_acumulado_geral());
        registro.setDataHoraCaptura(dataCaptura);

        repository.save(registro);

    }



    @Transactional(readOnly = true)
    public ResumoPeriodoProjection obterResumo(String painelId, Instant inicio, Instant fim) {
        return repository.calcularResumoPorPeriodo(painelId, inicio, fim);
    }
    
}
