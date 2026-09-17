package br.com.back_end.simasp.leitura.controller;

import br.com.back_end.simasp.leitura.service.LeituraService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/leituras")
public class LeituraController {

    @Autowired
    private LeituraService service;

}
