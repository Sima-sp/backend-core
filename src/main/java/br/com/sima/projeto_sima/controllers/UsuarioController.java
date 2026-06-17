package br.com.sima.projeto_sima.controllers;

import br.com.sima.projeto_sima.dtos.UsuarioDTO;
import br.com.sima.projeto_sima.services.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    public List<UsuarioDTO> listarUsuarios()
    {
        return usuarioService.findAll();
    }

    @GetMapping("/{governamental}")
    public List<UsuarioDTO> listarUsuariosGovernamentais(@PathVariable String governamental)
    {
        return usuarioService.buscarUsuariosGovernamentais(governamental);
    }

    @GetMapping("/proximos/alerta/{latitude}/{longitude}/{raio}/{permissaoAlerta}")
    public ResponseEntity<List<UsuarioDTO>> listarUsuariosProximosPorOpcaoDeAlerta(@PathVariable Double latitude, @PathVariable Double longitude, @PathVariable Integer raio, @PathVariable String permissaoAlerta)
    {
        var usuarios = usuarioService.buscarUsuariosProximosPorOpcaoDeAlerta(latitude, longitude, raio, permissaoAlerta);
        if(usuarios.isEmpty())
        {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(usuarios);
    }

}
