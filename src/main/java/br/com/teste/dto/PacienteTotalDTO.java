package br.com.teste.dto;

import java.io.Serializable;

/**
 * DTO para o relatório com totais por paciente.
 */
public class PacienteTotalDTO implements Serializable {

    private final Long pacienteId;
    private final String pacienteNome;
    private final Long total;

    public PacienteTotalDTO(Long pacienteId, String pacienteNome, Long total) {
        this.pacienteId = pacienteId;
        this.pacienteNome = pacienteNome;
        this.total = total;
    }

    public Long getPacienteId() {
        return pacienteId;
    }

    public String getPacienteNome() {
        return pacienteNome;
    }

    public Long getTotal() {
        return total;
    }
}
