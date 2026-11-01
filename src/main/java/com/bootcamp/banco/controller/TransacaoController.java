package com.bootcamp.banco.controller;

import com.bootcamp.banco.dto.ContestacaoRequest;
import com.bootcamp.banco.dto.PixRequest;
import com.bootcamp.banco.dto.TransacaoResponse;
import com.bootcamp.banco.service.ContaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class TransacaoController {

    private final ContaService contaService;

    public TransacaoController(ContaService contaService) {
        this.contaService = contaService;
    }

    @PostMapping("/pix")
    public ResponseEntity<?> realizarPix(@AuthenticationPrincipal UserDetails usuarioLogado,
                                         @Valid @RequestBody PixRequest request) {
        try {
            var transacao = contaService.realizarPix(
                    usuarioLogado.getUsername(),
                    request.chaveDestino(),
                    request.valor(),
                    request.descricao());
            return ResponseEntity.status(HttpStatus.CREATED).body(TransacaoResponse.from(transacao));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("erro", e.getMessage()));
        }
    }

    @GetMapping("/transacoes")
    public List<TransacaoResponse> extrato(@AuthenticationPrincipal UserDetails usuarioLogado) {
        return contaService.listarExtrato(usuarioLogado.getUsername())
                .stream()
                .map(TransacaoResponse::from)
                .toList();
    }

    @PostMapping("/transacoes/{id}/contestar")
    public ResponseEntity<?> contestarTransacao(@AuthenticationPrincipal UserDetails usuarioLogado,
                                                @PathVariable Long id,
                                                @Valid @RequestBody ContestacaoRequest request) {
        try {
            var transacao = contaService.contestarTransacao(usuarioLogado.getUsername(), id, request.motivo());
            return ResponseEntity.ok(TransacaoResponse.from(transacao));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("erro", e.getMessage()));
        }
    }
}
