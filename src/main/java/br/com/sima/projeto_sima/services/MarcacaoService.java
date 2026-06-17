package br.com.sima.projeto_sima.services;

import br.com.sima.projeto_sima.dtos.MarcacaoDTO;
import br.com.sima.projeto_sima.mappers.MarcacaoMapper;
import br.com.sima.projeto_sima.repositorys.MarcacaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MarcacaoService {


    @Autowired
    private MarcacaoMapper marcacaoMapper;


    @Autowired
    private MarcacaoRepository marcacaoRepository;

    public List<MarcacaoDTO> findAll()
    {
        var marcacoes = marcacaoMapper.retornaMarcacoesDTOS(marcacaoRepository.findAll());
        return marcacoes;
    }

    public List<MarcacaoDTO> buscarMarcacoesExpiradas()
    {
        var marcacoes = marcacaoMapper.retornaMarcacoesDTOS(marcacaoRepository.buscarMarcacoesExpiradas());
        return marcacoes;
    }

}
