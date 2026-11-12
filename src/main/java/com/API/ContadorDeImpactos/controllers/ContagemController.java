package com.API.ContadorDeImpactos.controllers;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.API.ContadorDeImpactos.repositories.RegistroContagemRepository.ResumoPeriodoProjection;
import com.API.ContadorDeImpactos.services.ContagemService;
import java.time.Instant;
import java.util.Map;

@RestController
@RequestMapping("/api/contagem")
public class ContagemController {

    private final ContagemService contagemService;

    @Value("${app.edge.api-key}")
    private String edgeApiKey;

    public ContagemController(ContagemService contagemService) {
        this.contagemService = contagemService;
    }

    public record LoteContagemRequest(
            @NotBlank String painelId,
            @NotNull Long total_acumulado_geral,
            @NotNull Integer total_no_minuto_atual,
            @NotNull Map<Integer, Integer> classes,
            @NotNull Double timestamp
    ) {}

    @PostMapping
    public ResponseEntity<Void> registrarLote(
            @RequestHeader(value = "X-API-Key", required = false) String apiKey,
            @Valid @RequestBody LoteContagemRequest payload
    ) {
        if (apiKey == null || !apiKey.equals(edgeApiKey)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        contagemService.processarLote(payload);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/resumo/{painelId}")
    public ResponseEntity<ResumoPeriodoProjection> consultarResumo(
            @PathVariable String painelId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant fim
    ) {
        return ResponseEntity.ok(contagemService.obterResumo(painelId, inicio, fim));
    }

    @GetMapping("teste")
    public String endpointDeTeste(){
        return "Hello World!";
    }
}