package br.com.back_end.simasp.usuario.controller;

import br.com.back_end.simasp.usuario.dto.CriarUsuarioRequest;
import br.com.back_end.simasp.usuario.dto.UsuarioResponse;
import br.com.back_end.simasp.usuario.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/usuarios")
@CrossOrigin("*")
public class UsuarioController {

    @Autowired
    private UsuarioService service;


    @GetMapping("/proximos")
    public ResponseEntity<Page<UsuarioResponse>> buscarUsuariosProximos(@RequestParam BigDecimal longitude, @RequestParam BigDecimal latitude, @RequestParam Integer distanciaMetros, Pageable pageable)
    {
        return ResponseEntity.ok(service.buscarUsuariosProximos(longitude, latitude, distanciaMetros, pageable));
    }


    @PostMapping("/cadastro")
    public ResponseEntity<UsuarioResponse> cadastrarUsuario(@Valid @RequestBody CriarUsuarioRequest request)
    {
        UsuarioResponse response = service.cadastrarUsuario(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

}
