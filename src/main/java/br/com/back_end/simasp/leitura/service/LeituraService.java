package br.com.back_end.simasp.leitura.service;

import br.com.back_end.simasp.leitura.mapper.LeituraMapper;
import br.com.back_end.simasp.leitura.repository.LeituraRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LeituraService {

    @Autowired
    private LeituraRepository repository;

    @Autowired
    private LeituraMapper mapper;


}
