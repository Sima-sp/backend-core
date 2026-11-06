package br.com.back_end.simasp.leitura.repository;

import br.com.back_end.simasp.leitura.entity.Leitura;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface LeituraRepository extends JpaRepository<Leitura, Long> {

    /** Leitura mais recente de um sensor (usada para ajustar o nível de risco). */
    Optional<Leitura> findFirstBySensorIdOrderByDataLeituraDesc(Long sensorId);

    /** Leitura mais recente de cada sensor — uma consulta só, para o lote não fazer N+1. */
    @Query("""
            SELECT l FROM Leitura l
            JOIN FETCH l.sensor s
            WHERE l.dataLeitura = (
                SELECT MAX(l2.dataLeitura) FROM Leitura l2 WHERE l2.sensor.id = l.sensor.id
            )
            """)
    List<Leitura> buscarUltimaDeCadaSensor();
}
