package br.com.sima.projeto_sima.repositorys;

import br.com.sima.projeto_sima.entitys.Monitoramento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MonitoramentoRepository extends JpaRepository<Monitoramento, Integer> {

    @Query(value = "SELECT * FROM TBL_MONITORAMENTO WHERE TBL_MONITORAMENTO.ESTADO_BUEIRO = :estado_bueiro", nativeQuery = true)
    List<Monitoramento> buscarMonitoramentoPorEstadoBueiro(@Param("estado_bueiro") String estadoBueiro);

    @Query(value = "SELECT * FROM TBL_MONITORAMENTO WHERE geography::Point(TBL_MONITORAMENTO.LATITUDE, TBL_MONITORAMENTO.LONGITUDE, 4326).STDistance(geography::Point(:latitude, :longitude, 4326)) <= :raio", nativeQuery = true)
    List<Monitoramento> buscarMonitoramentoProximo(@Param("latitude") Double latitude, @Param("longitude") Double longitude, @Param("raio") Integer raio);
}
