package br.com.back_end.simasp.usuario.repository;

import br.com.back_end.simasp.sensor.entity.Sensor;
import br.com.back_end.simasp.usuario.entity.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    boolean existsByEmailIgnoreCase(String email);
    boolean existsByTelefone(String telefone);
    Optional<Usuario> findByEmailIgnoreCase(String email);

    // Busca usuarios filtrando pelos ativos e por uma distância máxima em metros.
    @Query(value = "SELECT * FROM TBL_USUARIO u WHERE ST_Distance_Sphere(POINT(u.NR_LONGITUDE, u.NR_LATITUDE), POINT(:longitude_usuario, :latitude_usuario)) <= :distancia_metros AND u.FL_ATIVO = TRUE", countQuery = "SELECT COUNT(*) FROM TBL_USUARIO u WHERE ST_Distance_Sphere( POINT(u.NR_LONGITUDE, u.NR_LATITUDE), POINT(:longitude_usuario, :latitude_usuario)) <= :distancia_metros AND u.FL_ATIVO = TRUE", nativeQuery = true)
    Page<Usuario> buscarUsuariosProximos(@Param("longitude_usuario") BigDecimal longitude, @Param("latitude_usuario") BigDecimal latitude, @Param("distancia_metros") Integer distanciaMetros, Pageable pageable);



}
