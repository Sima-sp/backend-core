package br.com.back_end.simasp.sensor.mapper;

import br.com.back_end.simasp.sensor.dto.LocalizacaoSensorResponse;
import br.com.back_end.simasp.sensor.dto.SensorRequest;
import br.com.back_end.simasp.sensor.dto.SensorResponse;
import br.com.back_end.simasp.sensor.entity.Sensor;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")

public interface SensorMapper {

    LocalizacaoSensorResponse sensorParaLocalizacaoSensor(Sensor sensor);

    Sensor requestParaSensorEntidade(SensorRequest request);
    Sensor responseParaSensorEntidade(SensorResponse response);
    SensorResponse paraSensorResponse(Sensor sensor);

}
