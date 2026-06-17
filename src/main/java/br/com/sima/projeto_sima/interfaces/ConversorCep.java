package br.com.sima.projeto_sima.interfaces;

import java.util.List;

public interface ConversorCep {

    List<Double> converterCep(String cep);

    String converterLatitudeLongitudeEmCep(List<Double> coordenada);


}
