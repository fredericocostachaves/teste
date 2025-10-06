package br.com.teste.bean;

import br.com.teste.datamodel.PacienteLazyDataModel;
import br.com.teste.model.Paciente;
import br.com.teste.repository.PacienteRepository;
import org.primefaces.PrimeFaces;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;

/**
 * Bean de sessão responsável por orquestrar o CRUD de Paciente na camada de visão (JSF/PrimeFaces).
 * - Mantém o objeto em edição (paciente).
 * - Fornece o LazyDataModel para paginação com PrimeFaces.
 * - Trata mensagens de sucesso/erro e o ciclo de abrir/fechar o diálogo.
 */
@Named
@SessionScoped
public class PacienteBean implements Serializable {

    /** Repositório/EJB com operações de persistência para Paciente */
    @Inject
    private PacienteRepository repository;

    /** DataModel lazy utilizado pelo DataTable do PrimeFaces (listagem paginada) */
    @Inject
    private PacienteLazyDataModel lazyModel;

    /** Entidade em edição no diálogo de cadastro/edição */
    private Paciente paciente;

    /**
     * Inicializa o bean logo após a construção, preparando um novo Paciente.
     */
    @PostConstruct
    public void init() {
        paciente = new Paciente();
    }

    /**
     * Exposto para a tabela do PrimeFaces consumir a paginação lazy.
     */
    public PacienteLazyDataModel getLazyModel() {
        return lazyModel;
    }

    /**
     * Objeto atual em edição no formulário.
     */
    public Paciente getPaciente() {
        return paciente;
    }

    /**
     * Prepara a tela para um novo cadastro, limpando o objeto em edição.
     */
    public void prepararNovo() {
        this.paciente = new Paciente();
    }

    /**
     * Prepara a edição carregando do banco o registro selecionado (garante dados atualizados).
     * @param p registro selecionado na tabela
     */
    public void prepararEdicao(Paciente p) {
        this.paciente = repository.findById(p.getId());
    }

    /**
     * Efetiva o salvamento (insert/update) e exibe mensagens apropriadas.
     * Em caso de violação de unicidade (CPF duplicado), mantém o diálogo aberto e mostra o erro ao usuário.
     */
    public void salvar() {
        try {
            repository.save(paciente);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Paciente salvo com sucesso", null));
            // Atualiza a lista, o growl e as mensagens do diálogo, e fecha o diálogo
            PrimeFaces.current().ajax().update("formLista:tabela", "formLista:growl");
            PrimeFaces.current().executeScript("PF('dlgPaciente').hide()");
        } catch (Exception e) {
            // Descobre a causa raiz para decidir a mensagem apropriada
            Throwable root = getRootCause(e);
            String msg;
            if (isUniqueConstraintViolation(root)) {
                msg = "Já existe um paciente cadastrado com este CPF.";
            } else {
                msg = "Erro ao salvar paciente: " + (root.getMessage() != null ? root.getMessage() : e.getClass().getSimpleName());
            }
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, msg, null));
            // Mantém o diálogo aberto e atualiza a área de mensagens
            PrimeFaces.current().ajax().update("formDialog:msgs");
        }
    }

    /**
     * Caminha pela cadeia de exceções até encontrar a causa raiz (útil para mensagens).
     */
    private Throwable getRootCause(Throwable t) {
        Throwable result = t;
        while (result.getCause() != null && result.getCause() != result) {
            result = result.getCause();
        }
        return result;
    }

    /**
     * Heurística para identificar violação de restrição única (CPF) em diferentes camadas/fornecedores.
     *
     * Considera:
     * - ConstraintViolationException do Hibernate (incluindo SQLState 23505 do PostgreSQL)
     * - SQLIntegrityConstraintViolationException (JDBC)
     * - SQLException com SQLState 23505 (violação de unicidade no PostgreSQL)
     * A vantagem dessa abordagem é que evita consultas ao banco de dados. A desvantagem é que "polui" o log.
     */
    private boolean isUniqueConstraintViolation(Throwable t) {
        if (t instanceof org.hibernate.exception.ConstraintViolationException) {
            org.hibernate.exception.ConstraintViolationException cve = (org.hibernate.exception.ConstraintViolationException) t;
            String sqlState = cve.getSQLException() != null ? cve.getSQLException().getSQLState() : null;
            String constraintName = cve.getConstraintName();
            if ("23505".equals(sqlState)) return true; // Violação de unicidade (PostgreSQL)
            if (constraintName != null && constraintName.equalsIgnoreCase("uk_paciente_cpf")) return true;
            return true; // fallback seguro quando a exceção já indica violação de restrição
        }
        // JDBC SQLIntegrityConstraintViolationException
        if (t instanceof java.sql.SQLIntegrityConstraintViolationException) return true;
        // SQLException genérica com estado de erro de violação única do Postgres
        if (t instanceof java.sql.SQLException) {
            String sqlState = ((java.sql.SQLException) t).getSQLState();
            if ("23505".equals(sqlState)) return true;
        }
        return false;
    }

    /**
     * Exclui um paciente pelo ID e atualiza a tabela e o growl com feedback ao usuário.
     */
    public void excluir(Long id) {
        repository.delete(id);
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Paciente excluído com sucesso", null));
        PrimeFaces.current().ajax().update("formLista:tabela");
    }
}
