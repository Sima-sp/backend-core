package br.com.back_end.simasp.previsao.service;

import br.com.back_end.simasp.previsao.config.PrevisaoProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Recalcula as previsões de tempos em tempos.
 *
 * <p>10 minutos é suficiente: o Open-Meteo atualiza de hora em hora, então rodar mais vezes não
 * traz chuva nova. O que muda antes disso é a leitura do sensor, e essa já dispara o recálculo
 * pelo próprio fluxo da leitura.</p>
 */
@Component
@ConditionalOnProperty(name = "ia.habilitada", havingValue = "true")
public class PrevisaoAgendador {

    private static final Logger log = LoggerFactory.getLogger(PrevisaoAgendador.class);

    private final PrevisaoService servico;
    private final PrevisaoProperties propriedades;

    public PrevisaoAgendador(PrevisaoService servico, PrevisaoProperties propriedades) {
        this.servico = servico;
        this.propriedades = propriedades;
    }

    @Scheduled(initialDelayString = "${ia.atraso-inicial:PT30S}", fixedDelayString = "${ia.intervalo:PT10M}")
    public void atualizar() {
        if (!propriedades.habilitada()) {
            return;
        }
        try {
            servico.atualizarTodos();
        } catch (Exception erro) {
            // O agendador não pode morrer: se lançar, o Spring para de agendar esta tarefa.
            log.error("Falha ao atualizar as previsões: {}", erro.toString(), erro);
        }
    }
}
