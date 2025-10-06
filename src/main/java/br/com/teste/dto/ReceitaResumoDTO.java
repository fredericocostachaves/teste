package br.com.teste.dto;

import java.io.Serializable;

/**
 * DTO de projeção para a consulta "Medicamentos por Paciente".
 * Representa uma linha por Receita contendo: id da receita, dados do paciente e
 * o total de medicamentos receitados naquela receita.
 */
public class ReceitaResumoDTO implements Serializable {

    private Long receitaId;
    private Long pacienteId;
    private String pacienteNome;
    private Long totalMedicamentos;

    public ReceitaResumoDTO(Long receitaId, Long pacienteId, String pacienteNome, Long totalMedicamentos) {
        this.receitaId = receitaId;
        this.pacienteId = pacienteId;
        this.pacienteNome = pacienteNome;
        this.totalMedicamentos = totalMedicamentos;
    }

    public Long getReceitaId() {
        return receitaId;
    }

    public Long getPacienteId() {
        return pacienteId;
    }

    public String getPacienteNome() {
        return pacienteNome;
    }

    public Long getTotalMedicamentos() {
        return totalMedicamentos;
    }
}
