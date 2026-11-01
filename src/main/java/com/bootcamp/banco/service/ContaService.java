package com.bootcamp.banco.service;

import com.bootcamp.banco.model.Conta;
import com.bootcamp.banco.model.StatusTransacao;
import com.bootcamp.banco.model.TipoTransacao;
import com.bootcamp.banco.model.Transacao;
import com.bootcamp.banco.repository.ContaRepository;
import com.bootcamp.banco.repository.TransacaoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ContaService {

    private final ContaRepository contaRepository;
    private final TransacaoRepository transacaoRepository;

    public ContaService(ContaRepository contaRepository, TransacaoRepository transacaoRepository) {
        this.contaRepository = contaRepository;
        this.transacaoRepository = transacaoRepository;
    }

    public Conta buscarContaDoUsuario(String username) {
        return contaRepository.findByUsuarioUsername(username)
                .orElseThrow(() -> new IllegalStateException("Conta não encontrada para o usuário logado"));
    }

    @Transactional
    public Transacao realizarPix(String username, String chaveDestino, BigDecimal valor, String descricao) {
        if (valor == null || valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("O valor do PIX deve ser maior que zero");
        }
        if (chaveDestino == null || chaveDestino.isBlank()) {
            throw new IllegalArgumentException("A chave PIX de destino é obrigatória");
        }

        Conta conta = buscarContaDoUsuario(username);

        if (conta.getSaldo().compareTo(valor) < 0) {
            throw new IllegalArgumentException("Saldo insuficiente para realizar o PIX");
        }

        conta.setSaldo(conta.getSaldo().subtract(valor));
        contaRepository.save(conta);

        Transacao transacao = new Transacao(
                TipoTransacao.PIX_ENVIADO,
                valor,
                descricao == null || descricao.isBlank() ? "PIX enviado" : descricao,
                conta);
        transacao.setChavePixDestino(chaveDestino);
        return transacaoRepository.save(transacao);
    }

    public List<Transacao> listarExtrato(String username) {
        Conta conta = buscarContaDoUsuario(username);
        return transacaoRepository.findByContaIdOrderByDataHoraDesc(conta.getId());
    }

    @Transactional
    public Transacao contestarTransacao(String username, Long transacaoId, String motivo) {
        if (motivo == null || motivo.isBlank()) {
            throw new IllegalArgumentException("Informe o motivo da contestação");
        }

        Conta conta = buscarContaDoUsuario(username);

        Transacao transacao = transacaoRepository.findByIdAndContaId(transacaoId, conta.getId())
                .orElseThrow(() -> new IllegalArgumentException("Transação não encontrada"));

        if (transacao.getStatus() != StatusTransacao.CONCLUIDA) {
            throw new IllegalArgumentException("Esta transação já foi contestada e está em análise");
        }

        transacao.setStatus(StatusTransacao.EM_ANALISE);
        transacao.setMotivoContestacao(motivo);
        return transacaoRepository.save(transacao);
    }
}
