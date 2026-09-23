package br.com.back_end.simasp.sensor.repository;

import br.com.back_end.simasp.sensor.entity.Sensor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository

public interface SensorRepository extends JpaRepository <Sensor, Long> {

    boolean existsByLatitudeAndLongitude(Double latitude, Double longitude);



    @Query(value = "SELECT s FROM Sensor s WHERE ST_Distance_Sphere(POINT(s.longitude, s.latitude), POINT(:longitude_usuario, :latitude_usuario)) <= :distancia_metros AND s.status = 'ATIVO'")
    List<Sensor> sensoresProximosDistancia(@Param("longitude_usuario") BigDecimal longitude, @Param("latitude_usuario") BigDecimal latitude, @Param("distancia_metros") Integer distanciaMetros);

}
