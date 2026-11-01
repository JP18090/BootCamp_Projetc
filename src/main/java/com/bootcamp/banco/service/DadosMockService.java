package com.bootcamp.banco.service;

import com.bootcamp.banco.model.Conta;
import com.bootcamp.banco.model.TipoTransacao;
import com.bootcamp.banco.model.Transacao;
import com.bootcamp.banco.repository.ContaRepository;
import com.bootcamp.banco.repository.TransacaoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Popula contas recém-criadas com dados de exemplo:
 * saldo inicial de R$ 5.000,00 e 15 transações variadas.
 */
@Service
public class DadosMockService {

    public static final BigDecimal SALDO_INICIAL = new BigDecimal("5000.00");

    private final TransacaoRepository transacaoRepository;
    private final ContaRepository contaRepository;

    public DadosMockService(TransacaoRepository transacaoRepository, ContaRepository contaRepository) {
        this.transacaoRepository = transacaoRepository;
        this.contaRepository = contaRepository;
    }

    @Transactional
    public void popularDadosIniciais(Conta conta) {
        List<Transacao> transacoes = new ArrayList<>();

        transacoes.add(criar(conta, TipoTransacao.DEPOSITO, "5000.00", "Depósito inicial de boas-vindas 🎉", null, 30, 10));
        transacoes.add(criar(conta, TipoTransacao.PIX_RECEBIDO, "350.00", "PIX recebido de Ana Souza", null, 28, 14));
        transacoes.add(criar(conta, TipoTransacao.PIX_ENVIADO, "89.90", "Pagamento conta de luz", "luz@energia.com", 25, 9));
        transacoes.add(criar(conta, TipoTransacao.PIX_ENVIADO, "45.00", "PIX enviado para Supermercado Bom Preço", "bompreco@email.com", 22, 19));
        transacoes.add(criar(conta, TipoTransacao.PIX_RECEBIDO, "1200.00", "PIX recebido de Carlos Pereira", null, 20, 11));
        transacoes.add(criar(conta, TipoTransacao.PIX_ENVIADO, "199.90", "Assinatura streaming", "streaming@video.com", 18, 21));
        transacoes.add(criar(conta, TipoTransacao.DEPOSITO, "800.00", "Depósito em caixa eletrônico", null, 15, 16));
        transacoes.add(criar(conta, TipoTransacao.PIX_ENVIADO, "60.00", "PIX enviado para Farmácia Saúde", "11999990001", 12, 12));
        transacoes.add(criar(conta, TipoTransacao.PIX_RECEBIDO, "75.50", "PIX recebido de João Lima", null, 10, 18));
        transacoes.add(criar(conta, TipoTransacao.PIX_ENVIADO, "250.00", "Pagamento academia", "academia@fit.com", 8, 8));
        transacoes.add(criar(conta, TipoTransacao.PIX_ENVIADO, "32.90", "PIX enviado para Lanchonete Central", "11988880002", 6, 13));
        transacoes.add(criar(conta, TipoTransacao.PIX_RECEBIDO, "500.00", "PIX recebido de Fernanda Costa", null, 5, 15));
        transacoes.add(criar(conta, TipoTransacao.PIX_ENVIADO, "150.00", "Presente de aniversário 🎁", "aniversario@email.com", 3, 20));
        transacoes.add(criar(conta, TipoTransacao.PIX_RECEBIDO, "220.00", "PIX recebido de Roberto Alves", null, 2, 17));
        transacoes.add(criar(conta, TipoTransacao.PIX_ENVIADO, "99.99", "Compra online", "loja@shop.com", 1, 22));

        transacaoRepository.saveAll(transacoes);

        conta.setSaldo(SALDO_INICIAL);
        contaRepository.save(conta);
    }

    private Transacao criar(Conta conta, TipoTransacao tipo, String valor, String descricao,
                            String chavePix, int diasAtras, int hora) {
        Transacao transacao = new Transacao(tipo, new BigDecimal(valor), descricao, conta);
        transacao.setChavePixDestino(chavePix);
        transacao.setDataHora(LocalDateTime.now().minusDays(diasAtras).withHour(hora).withMinute(30));
        return transacao;
    }
}
