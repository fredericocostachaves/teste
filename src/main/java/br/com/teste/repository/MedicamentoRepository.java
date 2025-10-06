package br.com.teste.repository;

import br.com.teste.model.Medicamento;
import org.primefaces.model.FilterMeta;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Repositório (EJB Stateless) responsável pelas operações de persistência de Medicamento.
 * Usa JPA com Criteria API para paginação, ordenação e filtros (por nome).
 */
@Stateless
public class MedicamentoRepository {

    /** EntityManager JTA injetado pelo container (WildFly), atrelado ao PU "testePU" */
    @PersistenceContext(unitName = "testePU")
    private EntityManager em;

    /** Busca um medicamento pelo identificador (chave primária). */
    public Medicamento findById(Long id) {
        return em.find(Medicamento.class, id);
    }

    /**
     * Persiste (insert) ou atualiza (merge) o medicamento conforme presença de ID.
     * Força um flush para que violações sejam detectadas imediatamente.
     */
    public void save(Medicamento m) {
        if (m.getId() == null) {
            em.persist(m);
        } else {
            em.merge(m);
        }
        em.flush();
    }

    /** Remove o medicamento, se existir, pelo ID informado. */
    public void delete(Long id) {
        Medicamento managed = findById(id);
        if (managed != null) {
            em.remove(managed);
        }
    }

    /**
     * Retorna uma página de resultados de Medicamento, aplicando filtros e ordenação.
     * @param first índice do primeiro registro (offset)
     * @param pageSize quantidade de registros por página (limit)
     * @param filters filtros vindos do PrimeFaces (apenas por nome)
     * @param sortField campo de ordenação
     * @param asc true para ascendente; false para descendente
     */
    public List<Medicamento> findPage(int first, int pageSize, Map<String, FilterMeta> filters, String sortField, boolean asc) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Medicamento> cq = cb.createQuery(Medicamento.class);
        Root<Medicamento> root = cq.from(Medicamento.class);

        // Aplica filtros dinâmicos
        List<Predicate> predicates = buildPredicates(cb, root, filters);
        cq.where(predicates.toArray(new Predicate[0]));

        // Ordenação configurável; default por nome ASC
        if (sortField != null && !sortField.isEmpty()) {
            cq.orderBy(asc ? cb.asc(root.get(sortField)) : cb.desc(root.get(sortField)));
        } else {
            cq.orderBy(cb.asc(root.get("nome")));
        }

        TypedQuery<Medicamento> query = em.createQuery(cq);
        query.setFirstResult(first);
        query.setMaxResults(pageSize);
        return query.getResultList();
    }

    /** Retorna a quantidade total de registros considerando os filtros aplicados. */
    public Long count(Map<String, FilterMeta> filters) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<Medicamento> root = cq.from(Medicamento.class);

        List<Predicate> predicates = buildPredicates(cb, root, filters);
        cq.select(cb.count(root)).where(predicates.toArray(new Predicate[0]));
        return em.createQuery(cq).getSingleResult();
    }

    /** Monta os predicados (where) de acordo com os filtros da tela (apenas Nome: LIKE case-insensitive). */
    private List<Predicate> buildPredicates(CriteriaBuilder cb, Root<Medicamento> root, Map<String, FilterMeta> filters) {
        List<Predicate> predicates = new ArrayList<>();
        if (filters != null && !filters.isEmpty()) {
            FilterMeta nomeMeta = filters.get("nome");
            if (nomeMeta != null) {
                Object val = nomeMeta.getFilterValue();
                if (val != null && !val.toString().trim().isEmpty()) {
                    String termo = val.toString().trim().toLowerCase();
                    predicates.add(cb.like(cb.lower(root.get("nome")), "%" + termo + "%"));
                }
            }
        }
        return predicates;
    }

    /** Lista todos os medicamentos ordenados por nome (para selects). */
    public List<Medicamento> findAllOrderedByNome() {
        return em.createQuery("select m from Medicamento m order by m.nome asc", Medicamento.class)
                .getResultList();
    }
}
