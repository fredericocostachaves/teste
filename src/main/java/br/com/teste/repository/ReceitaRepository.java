package br.com.teste.repository;

import br.com.teste.model.Medicamento;
import br.com.teste.model.MedicamentoReceitado;
import br.com.teste.model.Receita;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;

/**
 * Repositório (EJB Stateless) para operações relacionadas à Receita e seus itens (MedicamentoReceitado).
 */
@Stateless
public class ReceitaRepository {

    /** EntityManager JTA injetado pelo container (WildFly), atrelado ao PU "testePU" */
    @PersistenceContext(unitName = "testePU")
    private EntityManager em;

    /** Busca uma receita pelo ID. */
    public Receita findById(Long id) {
        return em.find(Receita.class, id);
    }

    /** Persiste/atualiza uma receita e força flush (para obter ID imediatamente). */
    public Receita save(Receita r) {
        if (r.getId() == null) {
            em.persist(r);
        } else {
            r = em.merge(r);
        }
        em.flush();
        return r;
    }

    /** Inclui um medicamento na receita (cria um item MedicamentoReceitado). */
    public MedicamentoReceitado addMedicamento(Long idReceita, Long idMedicamento) {
        Receita receita = em.getReference(Receita.class, idReceita);
        Medicamento medicamento = em.getReference(Medicamento.class, idMedicamento);
        MedicamentoReceitado item = new MedicamentoReceitado();
        item.setReceita(receita);
        item.setMedicamento(medicamento);
        em.persist(item);
        em.flush();
        return item;
    }

    /** Exclui um item (MedicamentoReceitado) pelo seu ID. */
    public void deleteItem(Long idItem) {
        MedicamentoReceitado managed = em.find(MedicamentoReceitado.class, idItem);
        if (managed != null) {
            em.remove(managed);
        }
    }

    /** Lista os itens (MedicamentoReceitado) de uma receita específica. */
    public List<MedicamentoReceitado> listItensByReceita(Long idReceita) {
        TypedQuery<MedicamentoReceitado> q = em.createQuery(
                "select i from MedicamentoReceitado i " +
                        "join fetch i.medicamento m " +
                        "join i.receita r " +
                        "where r.id = :id order by i.id",
                MedicamentoReceitado.class);
        q.setParameter("id", idReceita);
        return q.getResultList();
    }
}
