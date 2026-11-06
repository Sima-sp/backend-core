package br.com.back_end.simasp.sensor.service;

import br.com.back_end.simasp.exception.LocalizacaoSensorDuplicadaException;
import br.com.back_end.simasp.sensor.dto.SensorRequest;
import br.com.back_end.simasp.sensor.dto.SensorResponse;
import br.com.back_end.simasp.sensor.entity.Sensor;
import br.com.back_end.simasp.sensor.mapper.SensorMapper;
import br.com.back_end.simasp.sensor.repository.SensorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service

public class SensorService {
    @Autowired
    private SensorRepository repository;

    @Autowired
    private SensorMapper mapper;

    public List<SensorResponse> buscarSensores()
    {
        return repository.findAll().stream().map(mapper::paraSensorResponse).toList();
    }

    public SensorResponse cadastrarSensor(SensorRequest request) {

        boolean existeSensorNoLocal = repository.existsByLatitudeAndLongitude(
                BigDecimal.valueOf(request.latitude()), BigDecimal.valueOf(request.longitude()));

        if(existeSensorNoLocal)
        {
            throw new LocalizacaoSensorDuplicadaException("A latitude " + request.latitude() + " e longitude " + request.longitude() + " já estão inclusas singularmente no banco.");
        }

        Sensor sensor = repository.save(mapper.paraSensorEntidade(request));

        return mapper.paraSensorResponse(sensor);
    }

    /** Sensores a até {@code distanciaMetros} do usuário (padrão de 2 km se nada for informado). */
    public List<SensorResponse> trazerSensoresProximos(Double latitude, Double longitude, Double distanciaMetros)
    {
        double raio = distanciaMetros == null ? 2000.0 : distanciaMetros;

        return repository.sensoresProximos(latitude, longitude, raio)
                .stream().map(mapper::paraSensorResponse).toList();
    }



}
