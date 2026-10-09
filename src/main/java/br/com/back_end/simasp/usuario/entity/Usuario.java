package br.com.back_end.simasp.usuario.entity;

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
    @GeneratedValue(strategy = GenerationType.IDENTITY )
    @Column(name = "ID_USUARIO")
    private Long id;

    @Column(name = "TX_TELEFONE")
    private String telefone;

    @Column(name = "TX_EMAIL")
    private String email;

    @Column(name = "TX_SENHA_HASH")
    private String senhaHash;

    @Column(name = "NR_LATITUDE")
    private BigDecimal latitude;

    @Column(name = "NR_LONGITUDE")
    private BigDecimal longitude;

    @Column(name = "TX_PERMISSAO_ALERTA")
    private Boolean permissaoAlerta;

    @Column(name = "TX_TIPO_USUARIO")
    @Enumerated(EnumType.STRING)
    private TipoUsuarioEnum tipoUsuario = TipoUsuarioEnum.CIDADAO;

    @Column(name = "FL_ATIVO")
    private Boolean ativo = Boolean.TRUE;
}
