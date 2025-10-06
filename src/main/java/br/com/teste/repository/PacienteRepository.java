package br.com.teste.repository;

import br.com.teste.model.Paciente;
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
 * Repositório (EJB Stateless) responsável pelas operações de persistência de Paciente.
 * Usa JPA com Criteria API para paginação, ordenação e filtros.
 */
@Stateless
public class PacienteRepository {

    /** EntityManager JTA injetado pelo container (WildFly), atrelado ao PU "testePU" */
    @PersistenceContext(unitName = "testePU")
    private EntityManager em;

    /**
     * Busca um paciente pelo identificador (chave primária).
     */
    public Paciente findById(Long id) {
        return em.find(Paciente.class, id);
    }

    /**
     * Persiste (insert) ou atualiza (merge) o paciente conforme presença de ID.
     * Força um flush para que violações de restrição (ex.: CPF duplicado) sejam detectadas imediatamente.
     */
    public void save(Paciente p) {
        if (p.getId() == null) {
            em.persist(p);
        } else {
            em.merge(p);
        }
        // Força flush para evidenciar violações de restrição (ex.: CPF duplicado) imediatamente
        em.flush();
    }

    /**
     * Remove o paciente, se existir, pelo ID informado.
     */
    public void delete(Long id) {
        Paciente managed = findById(id);
        if (managed != null) {
            em.remove(managed);
        }
    }

    /**
     * Retorna uma página de resultados de Paciente, aplicando filtros e ordenação.
     * @param first índice do primeiro registro (offset)
     * @param pageSize quantidade de registros por página (limit)
     * @param filters filtros vindos do PrimeFaces (por nome e cpf)
     * @param sortField campo de ordenação
     * @param asc true para ascendente; false para descendente
     */
    public List<Paciente> findPage(int first, int pageSize, Map<String, FilterMeta> filters, String sortField, boolean asc) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Paciente> cq = cb.createQuery(Paciente.class);
        Root<Paciente> root = cq.from(Paciente.class);

        // Aplica filtros dinâmicos
        List<Predicate> predicates = buildPredicates(cb, root, filters);
        cq.where(predicates.toArray(new Predicate[0]));

        // Ordenação configurável; default por nome ASC
        if (sortField != null && !sortField.isEmpty()) {
            if (asc) {
                cq.orderBy(cb.asc(root.get(sortField)));
            } else {
                cq.orderBy(cb.desc(root.get(sortField)));
            }
        } else {
            cq.orderBy(cb.asc(root.get("nome")));
        }

        TypedQuery<Paciente> query = em.createQuery(cq);
        query.setFirstResult(first);
        query.setMaxResults(pageSize);
        return query.getResultList();
    }

    /**
     * Retorna a quantidade total de registros considerando os filtros aplicados.
     */
    public Long count(Map<String, FilterMeta> filters) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<Paciente> root = cq.from(Paciente.class);

        List<Predicate> predicates = buildPredicates(cb, root, filters);
        cq.select(cb.count(root)).where(predicates.toArray(new Predicate[0]));
        return em.createQuery(cq).getSingleResult();
    }

    /**
     * Monta os predicados (where) de acordo com os filtros da tela.
     * - Nome: LIKE case-insensitive contendo o termo.
     * - CPF: LIKE case-insensitive (permite pesquisar com ou sem máscara, desde que o termo coincida).
     */
    private List<Predicate> buildPredicates(CriteriaBuilder cb, Root<Paciente> root, Map<String, FilterMeta> filters) {
        List<Predicate> predicates = new ArrayList<>();
        if (filters != null && !filters.isEmpty()) {
            // Filtro por Nome (usa o valor vindo do FilterMeta, modo contains, case-insensitive)
            FilterMeta nomeMeta = filters.get("nome");
            if (nomeMeta != null) {
                Object val = nomeMeta.getFilterValue();
                if (val != null && !val.toString().trim().isEmpty()) {
                    String termo = val.toString().trim().toLowerCase();
                    predicates.add(cb.like(cb.lower(root.get("nome")), "%" + termo + "%"));
                }
            }

            // Filtro por CPF (usa o valor vindo do FilterMeta)
            FilterMeta cpfMeta = filters.get("cpf");
            if (cpfMeta != null) {
                Object val = cpfMeta.getFilterValue();
                if (val != null && !val.toString().trim().isEmpty()) {
                    String termo = val.toString().trim().toLowerCase();
                    predicates.add(cb.like(cb.lower(root.get("cpf")), "%" + termo + "%"));
                }
            }
        }
        return predicates;
    }

    /** Lista todos os pacientes ordenados por nome (para selects). */
    public List<Paciente> findAllOrderedByNome() {
        return em.createQuery("select p from Paciente p order by p.nome asc", Paciente.class)
                .getResultList();
    }
}
