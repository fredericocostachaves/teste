package br.com.teste.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Entidade JPA que representa uma Receita médica.
 * Cada receita pertence a um Paciente e pode conter um ou mais medicamentos receitados.
 */
@Entity
@Table(name = "receita")
public class Receita implements Serializable {

    /** Identificador gerado automaticamente (idReceita - chave primária) */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Paciente ao qual a receita pertence (idPaciente) */
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_paciente", nullable = false, foreignKey = @ForeignKey(name = "fk_receita_paciente"))
    private Paciente paciente;

    /** Itens (medicamentos receitados) desta receita */
    @OneToMany(mappedBy = "receita", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MedicamentoReceitado> itens = new ArrayList<>();

    /** Construtor padrão exigido pelo JPA */
    public Receita() {
    }

    public Receita(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Paciente getPaciente() {
        return paciente;
    }

    public void setPaciente(Paciente paciente) {
        this.paciente = paciente;
    }

    public List<MedicamentoReceitado> getItens() {
        return itens;
    }

    public void setItens(List<MedicamentoReceitado> itens) {
        this.itens = itens;
    }

    /** Adiciona um item nesta receita e mantém a associação bidirecional */
    public void addItem(MedicamentoReceitado item) {
        if (item != null) {
            item.setReceita(this);
            this.itens.add(item);
        }
    }

    /** Remove um item desta receita e mantém a associação bidirecional */
    public void removeItem(MedicamentoReceitado item) {
        if (item != null) {
            this.itens.remove(item);
            item.setReceita(null);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Receita receita = (Receita) o;
        return Objects.equals(id, receita.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
