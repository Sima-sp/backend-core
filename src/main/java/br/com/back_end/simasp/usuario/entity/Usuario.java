package br.com.back_end.simasp.usuario.entity;

import br.com.back_end.simasp.usuario.enums.SimNaoEnum;
import br.com.back_end.simasp.usuario.enums.TipoUsuarioEnum;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Table (name = "TBL_USUARIO")
@Entity
@Getter
@Setter

public class Usuario {
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY )
    @Column (name = "ID_USUARIO")
    private Long id;

    @Column (name = "TX_EMAIL")
    private String email;

    @Column (name = "TX_SENHA_HASH")
    private String senhaHash;

    @Column (name = "NR_LATITUDE")
    private BigDecimal latitude;

    @Column (name = "NR_LONGITUDE")
    private BigDecimal longitude;

    @Column (name = "TX_PERMISSAO_LOCALIZACAO")
    @Enumerated(EnumType.STRING)
    private SimNaoEnum permissaoLocalizacao;

    @Column (name = "TX_PERMISSAO_ALERTA")
    @Enumerated(EnumType.STRING)
    private SimNaoEnum permissaoAlerta;

    @Column (name = "TX_TIPO_USUARIO")
    @Enumerated(EnumType.STRING)
    private TipoUsuarioEnum tipoUsuario = TipoUsuarioEnum.CIDADAO;
}
