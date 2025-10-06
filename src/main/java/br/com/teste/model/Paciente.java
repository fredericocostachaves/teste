package br.com.teste.model;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Objects;

/**
 * Entidade JPA que representa um Paciente.
 * Possui restrição de unicidade no CPF (uk_paciente_cpf) e validações básicas.
 */
@Entity
@Table(name = "paciente", uniqueConstraints = {
        @UniqueConstraint(name = "uk_paciente_cpf", columnNames = {"cpf"})
})
public class Paciente implements Serializable {

    /** Identificador gerado automaticamente (chave primária) */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Nome completo do paciente (obrigatório, até 150 caracteres) */
    @NotBlank
    @Size(max = 150)
    @Column(name = "nome", nullable = false, length = 150)
    private String nome;

    /** CPF do paciente (obrigatório). Usamos 11-14 caracteres para comportar máscara opcional. */
    @NotBlank
    @Size(min = 11, max = 14)
    @Column(name = "cpf", nullable = false, length = 14)
    private String cpf;

    /** Construtor padrão exigido pelo JPA */
    public Paciente() {
    }

    public Paciente(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    /**
     * Duas instâncias são consideradas iguais se possuírem o mesmo ID.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Paciente paciente = (Paciente) o;
        return Objects.equals(id, paciente.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
