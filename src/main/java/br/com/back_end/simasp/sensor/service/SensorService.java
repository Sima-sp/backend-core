package br.com.back_end.simasp.sensor.service;

import br.com.back_end.simasp.exception.LocalizacaoSensorDuplicadaException;
import br.com.back_end.simasp.exception.RecursoNaoEncontradoException;
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

    public SensorResponse buscarSensorPorId(Long id)
    {
        var sensor = repository.findById(id).orElseThrow(() -> new RecursoNaoEncontradoException("Não há sensor com o id: " + id));
        return mapper.paraSensorResponse(sensor);
    }

    public SensorResponse cadastrarSensor(SensorRequest request) {

        boolean existeSensorNoLocal = repository.existsByLatitudeAndLongitude(request.latitude(), request.longitude());

        if(existeSensorNoLocal)
        {
            throw new LocalizacaoSensorDuplicadaException("A latitude " + request.latitude() + " e longitude " + request.longitude() + " já estão inclusas singularmente no banco.");
        }

        Sensor sensor = repository.save(mapper.requestParaSensorEntidade(request));

        return mapper.paraSensorResponse(sensor);
    }

    public List<SensorResponse> trazerSensoresProximos(BigDecimal longitude, BigDecimal latitude, Integer distanciaMetros)
    {
        var sensores = repository.sensoresProximosDistancia(longitude, latitude, distanciaMetros);

        return sensores.stream().map(mapper::paraSensorResponse).toList();
    }



}
