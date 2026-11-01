package com.bootcamp.banco.controller;

import com.bootcamp.banco.dto.SaldoResponse;
import com.bootcamp.banco.model.Conta;
import com.bootcamp.banco.service.ContaService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/conta")
public class ContaController {

    private final ContaService contaService;

    public ContaController(ContaService contaService) {
        this.contaService = contaService;
    }

    @GetMapping("/saldo")
    public SaldoResponse consultarSaldo(@AuthenticationPrincipal UserDetails usuarioLogado) {
        Conta conta = contaService.buscarContaDoUsuario(usuarioLogado.getUsername());
        return new SaldoResponse(
                conta.getUsuario().getNome(),
                conta.getAgencia(),
                conta.getNumero(),
                conta.getSaldo());
    }
}
