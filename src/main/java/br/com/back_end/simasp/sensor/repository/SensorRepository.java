package br.com.back_end.simasp.sensor.repository;

import br.com.back_end.simasp.sensor.entity.Sensor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface SensorRepository extends JpaRepository<Sensor, Long> {

    /** Os tipos têm que ser os mesmos da entidade (BigDecimal), senão o Spring Data recusa o método. */
    boolean existsByLatitudeAndLongitude(BigDecimal latitude, BigDecimal longitude);

    /**
     * Sensores a até {@code distanciaMetros} de um ponto.
     *
     * <p>Consulta nativa: {@code ST_Distance_Sphere} é função do MySQL e não existe em JPQL.</p>
     */
    @Query(value = """
            SELECT *
            FROM TBL_SENSOR s
            WHERE ST_Distance_Sphere(
                      POINT(s.NR_LONGITUDE, s.NR_LATITUDE),
                      POINT(:longitude, :latitude)
                  ) <= :distanciaMetros
            """, nativeQuery = true)
    List<Sensor> sensoresProximos(@Param("latitude") double latitude,
                                  @Param("longitude") double longitude,
                                  @Param("distanciaMetros") double distanciaMetros);
}
