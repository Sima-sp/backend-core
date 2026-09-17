package br.com.back_end.simasp.sensor.controller;

import br.com.back_end.simasp.sensor.dto.SensorRequest;
import br.com.back_end.simasp.sensor.dto.SensorResponse;
import br.com.back_end.simasp.sensor.entity.Sensor;
import br.com.back_end.simasp.sensor.service.SensorService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/sensores")
public class SensorAdminController {

    @Autowired
    private SensorService service;

    @PostMapping("/cadastro-sensor")
    public ResponseEntity<SensorResponse> cadastrarSensor(@Valid @RequestBody SensorRequest sensor) {

        SensorResponse sensorCriado = service.cadastrarSensor(sensor);

        return ResponseEntity.status(HttpStatus.CREATED).body(sensorCriado);
    }



}
