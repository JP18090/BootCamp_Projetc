package com.bootcamp.banco.repository;

import com.bootcamp.banco.model.Conta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ContaRepository extends JpaRepository<Conta, Long> {

    Optional<Conta> findByUsuarioUsername(String username);

    Optional<Conta> findByNumero(String numero);
}
