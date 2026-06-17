package br.com.sima.projeto_sima.repositorys;

import br.com.sima.projeto_sima.entitys.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    @Query(value = "SELECT * FROM TBL_USUARIO WHERE TBL_USUARIO.GOVERNAMENTAL = :governamental", nativeQuery = true)
    List<Usuario> buscarUsuariosGovernamentais(@Param("governamental") String governamental);

    @Query(value = "SELECT * FROM TBL_USUARIO WHERE geography::Point(TBL_USUARIO.LATITUDE, TBL_USUARIO.LONGITUDE, 4326).STDistance(geography::Point(:latitude, :longitude, 4326)) <= :raio AND TBL_USUARIO.PERMISSAO_ALERTA = :permissao_alerta", nativeQuery = true)
    List<Usuario> buscarUsuariosProximosPorOpcaoDeAlerta(@Param("latitude") Double latitude, @Param("longitude") Double longitude, @Param("raio") Integer raio, @Param("permissao_alerta") String permissaoAlerta);

}
