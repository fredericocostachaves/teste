package br.com.teste.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Objects;

/**
 * Entidade JPA que representa um item de medicamento receitado em uma Receita.
 * Possui vínculos para a Receita (idReceita) e para o Medicamento (idMedicamento).
 */
@Entity
@Table(name = "medicamento_receitado")
public class MedicamentoReceitado implements Serializable {

    /** Identificador gerado automaticamente (idMedicamentoReceitado - chave primária) */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Receita à qual este item pertence */
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_receita", nullable = false, foreignKey = @ForeignKey(name = "fk_item_receita"))
    private Receita receita;

    /** Medicamento selecionado para esta receita */
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_medicamento", nullable = false, foreignKey = @ForeignKey(name = "fk_item_medicamento"))
    private Medicamento medicamento;

    /** Construtor padrão exigido pelo JPA */
    public MedicamentoReceitado() {
    }

    public MedicamentoReceitado(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Receita getReceita() {
        return receita;
    }

    public void setReceita(Receita receita) {
        this.receita = receita;
    }

    public Medicamento getMedicamento() {
        return medicamento;
    }

    public void setMedicamento(Medicamento medicamento) {
        this.medicamento = medicamento;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MedicamentoReceitado that = (MedicamentoReceitado) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
