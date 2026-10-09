package br.com.back_end.simasp.sensor.controller;

import br.com.back_end.simasp.sensor.dto.SensorRequest;
import br.com.back_end.simasp.sensor.dto.SensorResponse;
import br.com.back_end.simasp.sensor.service.SensorService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/admin/sensores")
public class SensorAdminController {

    @Autowired
    private SensorService service;

    @GetMapping("/busca-sensor/{id}")
    public ResponseEntity<SensorResponse> buscarSensorPorId(@PathVariable Long id)
    {
        SensorResponse response = service.buscarSensorPorId(id);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/sensores-proximos/MANUTENCAO/{latitude}/{longitude}/{distanciaMetros};")
    public ResponseEntity<List<SensorResponse>> buscarSensoresEmManutencaoProximos(@PathVariable BigDecimal latitude, @PathVariable BigDecimal longitude, @PathVariable Integer distanciaMetros)
    {
        return ResponseEntity.ok(service.buscarSensoresEmManutencaoProximos(longitude, latitude, distanciaMetros));
    }

    @GetMapping("/sensores-proximos/INATIVO/{latitude}/{longitude}/{distanciaMetros};")
    public ResponseEntity<List<SensorResponse>> buscarSensoresInativosProximos(@PathVariable BigDecimal latitude, @PathVariable BigDecimal longitude, @PathVariable Integer distanciaMetros)
    {
        return ResponseEntity.ok(service.buscarSensoresInativosProximos(longitude, latitude, distanciaMetros));
    }

    @PostMapping("/cadastro-sensor")
    public ResponseEntity<SensorResponse> cadastrarSensor(@Valid @RequestBody SensorRequest sensor) {

        SensorResponse sensorCriado = service.cadastrarSensor(sensor);

        return ResponseEntity.status(HttpStatus.CREATED).body(sensorCriado);
    }



}
