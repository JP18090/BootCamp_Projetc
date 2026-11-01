package com.bootcamp.banco.dto;

import com.bootcamp.banco.model.Transacao;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransacaoResponse(
        Long id,
        String tipo,
        BigDecimal valor,
        String descricao,
        String chavePixDestino,
        LocalDateTime dataHora,
        String status,
        String motivoContestacao
) {
    public static TransacaoResponse from(Transacao t) {
        return new TransacaoResponse(
                t.getId(),
                t.getTipo().name(),
                t.getValor(),
                t.getDescricao(),
                t.getChavePixDestino(),
                t.getDataHora(),
                t.getStatus().name(),
                t.getMotivoContestacao());
    }
}
