package br.com.sima.projeto_sima.controllers;


import br.com.sima.projeto_sima.dtos.MarcacaoDTO;
import br.com.sima.projeto_sima.services.MarcacaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/marcacoes")
public class MarcacaoController {

    @Autowired
    private MarcacaoService marcacaoService;

    @GetMapping
    public ResponseEntity<List<MarcacaoDTO>> listarMarcacoes()
    {
        var marcacoes = marcacaoService.findAll();
        return ResponseEntity.ok(marcacoes);
    }

    @GetMapping("/expirado")
    public ResponseEntity<List<MarcacaoDTO>> listarMarcacoesExpiradas()
    {
        var marcacoes = marcacaoService.buscarMarcacoesExpiradas();

        if (marcacoes.isEmpty())
        {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(marcacoes);
    }
}
