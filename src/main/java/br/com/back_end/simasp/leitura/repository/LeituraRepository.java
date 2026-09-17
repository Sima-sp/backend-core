package br.com.back_end.simasp.leitura.repository;

import br.com.back_end.simasp.leitura.entity.Leitura;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LeituraRepository extends JpaRepository<Leitura, Long> {
}
