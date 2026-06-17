package br.com.sima.projeto_sima.repositorys;

import br.com.sima.projeto_sima.entitys.Marcacao;
import br.com.sima.projeto_sima.entitys.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import tools.jackson.databind.annotation.JsonPOJOBuilder;

import java.util.List;

public interface MarcacaoRepository extends JpaRepository<Marcacao, Integer> {

    @Query(value = "SELECT * FROM TBL_MARCACAO WHERE DATEADD(DAY, +1, TBL_MARCACAO.DATA_MARCACAO) < GETDATE();", nativeQuery = true)
    List<Marcacao> buscarMarcacoesExpiradas();
}
