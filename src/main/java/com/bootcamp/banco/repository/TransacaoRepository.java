package com.bootcamp.banco.repository;

import com.bootcamp.banco.model.Transacao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TransacaoRepository extends JpaRepository<Transacao, Long> {

    List<Transacao> findByContaIdOrderByDataHoraDesc(Long contaId);

    Optional<Transacao> findByIdAndContaId(Long id, Long contaId);
}
