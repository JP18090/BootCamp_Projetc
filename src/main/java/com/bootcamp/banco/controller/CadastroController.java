package com.bootcamp.banco.controller;

import com.bootcamp.banco.dto.CadastroRequest;
import com.bootcamp.banco.model.Conta;
import com.bootcamp.banco.model.Usuario;
import com.bootcamp.banco.repository.UsuarioRepository;
import com.bootcamp.banco.service.DadosMockService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

@RestController
@RequestMapping("/api/cadastro")
public class CadastroController {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final DadosMockService dadosMockService;

    public CadastroController(UsuarioRepository usuarioRepository,
                              PasswordEncoder passwordEncoder,
                              DadosMockService dadosMockService) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.dadosMockService = dadosMockService;
    }

    @PostMapping
    public ResponseEntity<?> cadastrar(@Valid @RequestBody CadastroRequest request) {
        String username = request.username().trim();

        if (usuarioRepository.findByUsername(username).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("erro", "Este nome de usuário já está em uso"));
        }

        Usuario usuario = new Usuario(
                request.nome().trim(),
                username,
                passwordEncoder.encode(request.senha()));

        String numeroConta = gerarNumeroConta();
        Conta conta = new Conta("0001", numeroConta, BigDecimal.ZERO, usuario);
        usuario.setConta(conta);

        usuarioRepository.save(usuario);

        // Dados de exemplo: R$ 5.000,00 de saldo + 15 transações no extrato
        dadosMockService.popularDadosIniciais(conta);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("mensagem", "Conta criada com sucesso!"));
    }

    private String gerarNumeroConta() {
        int numero = ThreadLocalRandom.current().nextInt(10000, 99999);
        int digito = ThreadLocalRandom.current().nextInt(0, 10);
        return numero + "-" + digito;
    }
}
