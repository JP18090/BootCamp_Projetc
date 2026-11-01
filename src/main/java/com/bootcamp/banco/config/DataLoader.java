package com.bootcamp.banco.config;

import com.bootcamp.banco.model.Conta;
import com.bootcamp.banco.model.TipoTransacao;
import com.bootcamp.banco.model.Transacao;
import com.bootcamp.banco.model.Usuario;
import com.bootcamp.banco.repository.TransacaoRepository;
import com.bootcamp.banco.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Popula o banco em memória com dados de exemplo ao iniciar a aplicação.
 *
 * Usuário de teste:
 *   username: cliente
 *   senha:    123456
 */
@Component
public class DataLoader implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final TransacaoRepository transacaoRepository;
    private final PasswordEncoder passwordEncoder;

    public DataLoader(UsuarioRepository usuarioRepository,
                      TransacaoRepository transacaoRepository,
                      PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.transacaoRepository = transacaoRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        Usuario usuario = new Usuario("Cliente Bootcamp", "cliente", passwordEncoder.encode("123456"));
        Conta conta = new Conta("0001", "12345-6", new BigDecimal("5000.00"), usuario);
        usuario.setConta(conta);
        usuarioRepository.save(usuario);

        Transacao deposito = new Transacao(TipoTransacao.DEPOSITO, new BigDecimal("5000.00"),
                "Depósito inicial", conta);
        deposito.setDataHora(LocalDateTime.now().minusDays(5));
        transacaoRepository.save(deposito);

        Transacao pixRecebido = new Transacao(TipoTransacao.PIX_RECEBIDO, new BigDecimal("250.00"),
                "PIX recebido de Maria Silva", conta);
        pixRecebido.setDataHora(LocalDateTime.now().minusDays(2));
        transacaoRepository.save(pixRecebido);

        // Ajusta o saldo para refletir as transações de exemplo
        conta.setSaldo(conta.getSaldo().add(new BigDecimal("250.00")));

        Transacao pixEnviado = new Transacao(TipoTransacao.PIX_ENVIADO, new BigDecimal("120.50"),
                "PIX enviado para Padaria Pão Quente", conta);
        pixEnviado.setChavePixDestino("padaria@email.com");
        pixEnviado.setDataHora(LocalDateTime.now().minusDays(1));
        transacaoRepository.save(pixEnviado);

        conta.setSaldo(conta.getSaldo().subtract(new BigDecimal("120.50")));
    }
}
