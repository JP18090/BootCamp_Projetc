package com.bootcamp.banco.dto;

import jakarta.validation.constraints.NotBlank;

public record ContestacaoRequest(
        @NotBlank(message = "O motivo da contestação é obrigatório")
        String motivo
) {
}
