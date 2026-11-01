package com.bootcamp.banco.dto;

import java.math.BigDecimal;

public record SaldoResponse(
        String nome,
        String agencia,
        String numero,
        BigDecimal saldo
) {
}
