package br.com.teste.bean;

import br.com.teste.datamodel.MedicamentoLazyDataModel;
import br.com.teste.model.Medicamento;
import br.com.teste.repository.MedicamentoRepository;
import org.primefaces.PrimeFaces;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;

/**
 * Bean de sessão responsável por orquestrar o CRUD de Medicamento na camada de visão (JSF/PrimeFaces).
 * - Mantém o objeto em edição (medicamento).
 * - Fornece o LazyDataModel para paginação com PrimeFaces.
 * - Trata mensagens de sucesso/erro e o ciclo de abrir/fechar o diálogo.
 */
@Named
@ViewScoped
public class MedicamentoBean implements Serializable {

    /** Repositório/EJB com operações de persistência para Medicamento */
    @Inject
    private MedicamentoRepository repository;

    /** DataModel lazy utilizado pelo DataTable do PrimeFaces (listagem paginada) */
    @Inject
    private MedicamentoLazyDataModel lazyModel;

    /** Entidade em edição no diálogo de cadastro/edição */
    private Medicamento medicamento;

    /** Inicializa o bean logo após a construção, preparando um novo Medicamento. */
    @PostConstruct
    public void init() {
        medicamento = new Medicamento();
    }

    /** Exposto para a tabela do PrimeFaces consumir a paginação lazy. */
    public MedicamentoLazyDataModel getLazyModel() {
        return lazyModel;
    }

    /** Objeto atual em edição no formulário. */
    public Medicamento getMedicamento() {
        return medicamento;
    }

    /** Prepara a tela para um novo cadastro, limpando o objeto em edição. */
    public void prepararNovo() {
        this.medicamento = new Medicamento();
    }

    /** Prepara a edição carregando do banco o registro selecionado (garante dados atualizados). */
    public void prepararEdicao(Medicamento m) {
        this.medicamento = repository.findById(m.getId());
    }

    /** Efetiva o salvamento (insert/update) e exibe mensagens apropriadas. */
    public void salvar() {
        try {
            repository.save(medicamento);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Medicamento salvo com sucesso", null));
            // Atualiza a lista e o growl e fecha o diálogo
            PrimeFaces.current().ajax().update("formLista:tabela", "formLista:growl");
            PrimeFaces.current().executeScript("PF('dlgMedicamento').hide()");
        } catch (Exception e) {
            Throwable root = getRootCause(e);
            String msg = "Erro ao salvar medicamento: " + (root.getMessage() != null ? root.getMessage() : e.getClass().getSimpleName());
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, msg, null));
            // Mantém o diálogo aberto e atualiza a área de mensagens
            PrimeFaces.current().ajax().update("formDialog:msgs");
        }
    }

    /** Caminha pela cadeia de exceções até encontrar a causa raiz (útil para mensagens). */
    private Throwable getRootCause(Throwable t) {
        Throwable result = t;
        while (result.getCause() != null && result.getCause() != result) {
            result = result.getCause();
        }
        return result;
    }

    /** Exclui um medicamento pelo ID e atualiza a tabela e o growl com feedback ao usuário. */
    private boolean isForeignKeyViolationToItem(Throwable t) {
        // Verifica violações de chave estrangeira (PostgreSQL 23503) e o nome da constraint fk_item_medicamento
        if (t instanceof org.hibernate.exception.ConstraintViolationException) {
            org.hibernate.exception.ConstraintViolationException cve = (org.hibernate.exception.ConstraintViolationException) t;
            String sqlState = cve.getSQLException() != null ? cve.getSQLException().getSQLState() : null;
            String constraintName = cve.getConstraintName();
            if ("23503".equals(sqlState)) return true; // Violação de FK (PostgreSQL)
            if (constraintName != null && constraintName.equalsIgnoreCase("fk_item_medicamento")) return true;
            String msg = cve.getMessage();
            if (msg != null && msg.toLowerCase().contains("fk_item_medicamento")) return true;
        }
        if (t instanceof java.sql.SQLIntegrityConstraintViolationException) return true;
        if (t instanceof java.sql.SQLException) {
            String sqlState = ((java.sql.SQLException) t).getSQLState();
            if ("23503".equals(sqlState)) return true;
            String msg = ((java.sql.SQLException) t).getMessage();
            if (msg != null && msg.toLowerCase().contains("fk_item_medicamento")) return true;
        }
        String msg = t != null ? t.getMessage() : null;
        if (msg != null && msg.toLowerCase().contains("fk_item_medicamento")) return true;
        return false;
    }

    public void excluir(Long id) {
        try {
            repository.delete(id);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Medicamento excluído com sucesso", null));
        } catch (Exception e) {
            Throwable root = getRootCause(e);
            String msg;
            if (isForeignKeyViolationToItem(root)) {
                msg = "Não é possível excluir medicamento vinculado a uma receita.";
            } else {
                msg = "Erro ao excluir medicamento: " + (root != null && root.getMessage() != null ? root.getMessage() : e.getClass().getSimpleName());
            }
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, msg, null));
        }
        PrimeFaces.current().ajax().update("formLista:tabela", "formLista:growl");
    }
}
