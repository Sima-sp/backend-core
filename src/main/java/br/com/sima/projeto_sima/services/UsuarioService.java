package br.com.sima.projeto_sima.services;

import br.com.sima.projeto_sima.dtos.UsuarioDTO;
import br.com.sima.projeto_sima.mappers.UsuarioMapper;
import br.com.sima.projeto_sima.repositorys.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.List;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private UsuarioMapper usuarioMapper;

    public List<UsuarioDTO> findAll()
    {
        var usuarios = usuarioMapper.retornaTodosUsuariosDTO(usuarioRepository.findAll());
        return usuarios;
    }

    public List<UsuarioDTO> buscarUsuariosGovernamentais(String governamental)
    {
        var usuarios = usuarioMapper.retornaTodosUsuariosDTO(usuarioRepository.buscarUsuariosGovernamentais(governamental));
        return usuarios;
    }

    public List<UsuarioDTO> buscarUsuariosProximosPorOpcaoDeAlerta(Double latitude, Double longitude, Integer raio, String permissaoAlerta)
    {
        var usuarios = usuarioMapper.retornaTodosUsuariosDTO(usuarioRepository.buscarUsuariosProximosPorOpcaoDeAlerta(latitude, longitude, raio, permissaoAlerta));
        return usuarios;
    }
}
