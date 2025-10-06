package br.com.teste.repository;

import br.com.teste.dto.ReceitaResumoDTO;
import br.com.teste.dto.NomeQuantidadeDTO;
import br.com.teste.dto.PacienteTotalDTO;
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

    /**
     * Consulta paginada de receitas (uma linha por receita) com filtros opcionais por paciente e medicamento.
     * Retorna projeções (ReceitaResumoDTO) contendo o total de medicamentos por receita.
     * @param first offset
     * @param pageSize limite
     * @param sortField campo para ordenação (pacienteNome ou receitaId)
     * @param asc ascendente/descendente
     * @param pacienteNome filtro opcional por nome do paciente (contains, case-insensitive)
     * @param medicamentoNome filtro opcional por nome do medicamento (contains, case-insensitive)
     */
    public List<ReceitaResumoDTO> findResumoPage(int first, int pageSize, String sortField, boolean asc,
                                                 String pacienteNome, String medicamentoNome) {
        String pLike = normalizeLike(pacienteNome);
        String mLike = normalizeLike(medicamentoNome);

        StringBuilder jpql = new StringBuilder();
        jpql.append("select new br.com.teste.dto.ReceitaResumoDTO(")
            .append(" r.id, p.id, p.nome, ")
            .append(" (select count(i2.id) from MedicamentoReceitado i2 where i2.receita = r) ")
            .append(") from Receita r ")
            .append(" join r.paciente p ")
            .append(" where 1=1 ");

        if (pLike != null) {
            jpql.append(" and lower(p.nome) like :pNome ");
        }
        if (mLike != null) {
            jpql.append(" and exists (select 1 from MedicamentoReceitado ix join ix.medicamento mx ")
                .append(" where ix.receita = r and lower(mx.nome) like :mNome) ");
        }

        // Ordenação suportada
        String order = " order by ";
        if ("pacienteNome".equals(sortField)) {
            order += " p.nome ";
        } else if ("receitaId".equals(sortField)) {
            order += " r.id ";
        } else {
            // padrão
            order += " r.id ";
        }
        order += asc ? " asc" : " desc";
        jpql.append(order);

        TypedQuery<ReceitaResumoDTO> q = em.createQuery(jpql.toString(), ReceitaResumoDTO.class);
        if (pLike != null) q.setParameter("pNome", pLike);
        if (mLike != null) q.setParameter("mNome", mLike);
        q.setFirstResult(first);
        q.setMaxResults(pageSize);
        return q.getResultList();
    }

    /** Conta o total de receitas na consulta de resumo com os mesmos filtros. */
    public Long countResumo(String pacienteNome, String medicamentoNome) {
        String pLike = normalizeLike(pacienteNome);
        String mLike = normalizeLike(medicamentoNome);

        StringBuilder jpql = new StringBuilder();
        jpql.append("select count(r) from Receita r join r.paciente p where 1=1 ");
        if (pLike != null) {
            jpql.append(" and lower(p.nome) like :pNome ");
        }
        if (mLike != null) {
            jpql.append(" and exists (select 1 from MedicamentoReceitado ix join ix.medicamento mx ")
                .append(" where ix.receita = r and lower(mx.nome) like :mNome) ");
        }

        TypedQuery<Long> q = em.createQuery(jpql.toString(), Long.class);
        if (pLike != null) q.setParameter("pNome", pLike);
        if (mLike != null) q.setParameter("mNome", mLike);
        return q.getSingleResult();
    }

    private String normalizeLike(String val) {
        if (val == null) return null;
        String t = val.trim().toLowerCase();
        return t.isEmpty() ? null : ("%" + t + "%");
    }

    /** Retorna os 2 medicamentos mais prescritos (ranking por quantidade de itens nas receitas). */
    public List<NomeQuantidadeDTO> top2Medicamentos() {
        String jpql = "select new br.com.teste.dto.NomeQuantidadeDTO(m.nome, count(i.id)) " +
                " from MedicamentoReceitado i join i.medicamento m " +
                " group by m.id, m.nome " +
                " order by count(i.id) desc";
        TypedQuery<NomeQuantidadeDTO> q = em.createQuery(jpql, NomeQuantidadeDTO.class);
        q.setMaxResults(2);
        return q.getResultList();
    }

    /** Retorna os 2 pacientes com mais medicamentos prescritos (soma de todos os itens em todas as receitas). */
    public List<NomeQuantidadeDTO> top2Pacientes() {
        String jpql = "select new br.com.teste.dto.NomeQuantidadeDTO(p.nome, count(i.id)) " +
                " from MedicamentoReceitado i join i.receita r join r.paciente p " +
                " group by p.id, p.nome " +
                " order by count(i.id) desc";
        TypedQuery<NomeQuantidadeDTO> q = em.createQuery(jpql, NomeQuantidadeDTO.class);
        q.setMaxResults(2);
        return q.getResultList();
    }

    /** Lista todos os pacientes com a quantidade total de medicamentos receitados em todas as receitas. */
    public List<PacienteTotalDTO> totalMedicamentosPorPaciente() {
        String jpql = "select p.id, p.nome, (select count(i2.id) from MedicamentoReceitado i2 where i2.receita.paciente = p) " +
                " from Paciente p order by p.nome asc";
        TypedQuery<Object[]> q = em.createQuery(jpql, Object[].class);
        List<Object[]> rows = q.getResultList();
        List<PacienteTotalDTO> dtos = new java.util.ArrayList<>(rows.size());
        for (Object[] r : rows) {
            Long id = (Long) r[0];
            String nome = (String) r[1];
            Long total = (r[2] instanceof Long) ? (Long) r[2] : Long.valueOf(((Number) r[2]).longValue());
            dtos.add(new PacienteTotalDTO(id, nome, total));
        }
        return dtos;
    }
}
