package br.com.back_end.simasp.previsao.repository;

import br.com.back_end.simasp.previsao.entity.Previsao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PrevisaoRepository extends JpaRepository<Previsao, Long> {

    Optional<Previsao> findFirstBySensorIdOrderByGeradaEmDesc(Long sensorId);

    /** Última previsão de cada sensor — uma consulta só, para o mapa não fazer N+1. */
    @Query("""
            SELECT p FROM Previsao p
            JOIN FETCH p.sensor s
            WHERE p.geradaEm = (
                SELECT MAX(p2.geradaEm) FROM Previsao p2 WHERE p2.sensor.id = p.sensor.id
            )
            """)
    List<Previsao> buscarUltimaDeCadaSensor();
}
