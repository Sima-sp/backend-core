package br.com.back_end.simasp.sensor.controller;

import br.com.back_end.simasp.sensor.dto.SensorResponse;
import br.com.back_end.simasp.sensor.service.SensorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/sensores")

public class SensorController {

    @Autowired
    private SensorService service;

    @GetMapping
    public ResponseEntity<List<SensorResponse>> buscarSensores()
    {
        return ResponseEntity.ok(service.buscarSensores());
    }

}
