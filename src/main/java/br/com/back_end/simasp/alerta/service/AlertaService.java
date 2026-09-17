package br.com.back_end.simasp.alerta.service;

import br.com.back_end.simasp.alerta.entity.Alerta;
import br.com.back_end.simasp.alerta.repository.AlertaRepository;
import br.com.back_end.simasp.exception.RecursoNaoEncontradoException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AlertaService {

    @Autowired
    private AlertaRepository repository;

     public Alerta findByIdAlerta(Long id)
     {
         return repository.findById(id).orElseThrow(() -> new RecursoNaoEncontradoException("O alerta com ID " + id + " não foi encontrado."));
     }
}
