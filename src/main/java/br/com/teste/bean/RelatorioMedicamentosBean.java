package br.com.teste.bean;

import br.com.teste.dto.NomeQuantidadeDTO;
import br.com.teste.dto.PacienteTotalDTO;
import br.com.teste.repository.ReceitaRepository;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * Bean para o Relatório de Medicamentos Prescritos.
 * Exibe:
 *  - Top 2 medicamentos mais prescritos
 *  - Top 2 pacientes com mais medicamentos prescritos
 *  - Lista de pacientes com total de medicamentos receitados em todas as receitas
 */
@Named
@SessionScoped
public class RelatorioMedicamentosBean implements Serializable {

    @Inject
    private ReceitaRepository receitaRepository;

    private List<NomeQuantidadeDTO> topMedicamentos;
    private List<NomeQuantidadeDTO> topPacientes;
    private List<PacienteTotalDTO> totaisPorPaciente;

    @PostConstruct
    public void init() {
        atualizar();
    }

    public void atualizar() {
        try {
            topMedicamentos = receitaRepository.top2Medicamentos();
        } catch (Exception e) {
            topMedicamentos = Collections.emptyList();
        }
        try {
            topPacientes = receitaRepository.top2Pacientes();
        } catch (Exception e) {
            topPacientes = Collections.emptyList();
        }
        try {
            totaisPorPaciente = receitaRepository.totalMedicamentosPorPaciente();
        } catch (Exception e) {
            totaisPorPaciente = Collections.emptyList();
        }
    }

    public List<NomeQuantidadeDTO> getTopMedicamentos() {
        return topMedicamentos;
    }

    public List<NomeQuantidadeDTO> getTopPacientes() {
        return topPacientes;
    }

    public List<PacienteTotalDTO> getTotaisPorPaciente() {
        return totaisPorPaciente;
    }
}
