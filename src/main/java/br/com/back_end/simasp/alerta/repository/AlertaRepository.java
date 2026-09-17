package br.com.back_end.simasp.alerta.repository;

import br.com.back_end.simasp.alerta.entity.Alerta;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlertaRepository extends JpaRepository<Alerta, Long> {

}
