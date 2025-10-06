package br.com.teste.bean;

import br.com.teste.model.Medicamento;
import br.com.teste.model.MedicamentoReceitado;
import br.com.teste.model.Paciente;
import br.com.teste.model.Receita;
import br.com.teste.repository.MedicamentoRepository;
import br.com.teste.repository.PacienteRepository;
import br.com.teste.repository.ReceitaRepository;
import org.primefaces.PrimeFaces;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * Bean de sessão para cadastro/associação de Medicamentos a uma Receita de um Paciente.
 * Fluxo básico:
 * 1) Seleciona-se um Paciente e clica em "Criar Receita" (cria e persiste a receita).
 * 2) Adicionam-se um ou mais Medicamentos (cria itens de MedicamentoReceitado).
 * 3) Pode-se remover itens. 
 */
@Named
@SessionScoped
public class ReceitaBean implements Serializable {

    @Inject
    private PacienteRepository pacienteRepository;

    @Inject
    private MedicamentoRepository medicamentoRepository;

    @Inject
    private ReceitaRepository receitaRepository;

    /** Receita em edição (criada para o paciente selecionado) */
    private Receita receita;

    /** ID do Paciente selecionado para a receita (evita necessidade de converter no JSF) */
    private Long pacienteIdSelecionado;

    /** ID do Medicamento selecionado para adicionar à receita */
    private Long medicamentoIdSelecionado;

    /** Listas para popular selects */
    private List<Paciente> pacientes;
    private List<Medicamento> medicamentos;

    @PostConstruct
    public void init() {
        carregarListas();
    }

    private void carregarListas() {
        try {
            pacientes = pacienteRepository.findAllOrderedByNome();
        } catch (Exception e) {
            pacientes = Collections.emptyList();
        }
        try {
            medicamentos = medicamentoRepository.findAllOrderedByNome();
        } catch (Exception e) {
            medicamentos = Collections.emptyList();
        }
    }

    public List<Paciente> getPacientes() {
        return pacientes;
    }

    public List<Medicamento> getMedicamentos() {
        return medicamentos;
    }

    public Receita getReceita() {
        return receita;
    }

    public Long getPacienteIdSelecionado() {
        return pacienteIdSelecionado;
    }

    public void setPacienteIdSelecionado(Long pacienteIdSelecionado) {
        this.pacienteIdSelecionado = pacienteIdSelecionado;
    }

    public Long getMedicamentoIdSelecionado() {
        return medicamentoIdSelecionado;
    }

    public void setMedicamentoIdSelecionado(Long medicamentoIdSelecionado) {
        this.medicamentoIdSelecionado = medicamentoIdSelecionado;
    }

    /** Cria e persiste uma nova receita para o paciente selecionado. */
    public void criarNovaReceita() {
        if (pacienteIdSelecionado == null) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_WARN, "Selecione um paciente para criar a receita.", null));
            PrimeFaces.current().ajax().update("formReceita:growl");
            return;
        }
        try {
            Receita r = new Receita();
            // Garantir que o paciente esteja gerenciado; recarregar do banco
            Paciente p = pacienteRepository.findById(pacienteIdSelecionado);
            r.setPaciente(p);
            receita = receitaRepository.save(r);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Receita criada com sucesso (ID: " + receita.getId() + ")", null));
            PrimeFaces.current().ajax().update("formReceita:growl formReceita:painelItens");
        } catch (Exception e) {
            String msg = "Erro ao criar receita: " + e.getMessage();
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, msg, null));
            PrimeFaces.current().ajax().update("formReceita:growl");
        }
    }

    /** Adiciona o medicamento selecionado à receita corrente. */
    public void adicionarMedicamento() {
        if (!validarReceitaEMedicamentoSelecionados()) return;
        try {
            MedicamentoReceitado item = receitaRepository.addMedicamento(receita.getId(), medicamentoIdSelecionado);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Medicamento adicionado (Item ID: " + item.getId() + ")", null));
            // Limpa seleção e atualiza a tabela
            medicamentoIdSelecionado = null;
            PrimeFaces.current().ajax().update("formReceita:growl formReceita:tabelaItens formReceita:selectMedicamento");
        } catch (Exception e) {
            String msg = "Erro ao adicionar medicamento: " + e.getMessage();
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, msg, null));
            PrimeFaces.current().ajax().update("formReceita:growl");
        }
    }

    /** Remove um item de medicamento da receita. */
    public void removerItem(Long idItem) {
        if (receita == null || receita.getId() == null) return;
        receitaRepository.deleteItem(idItem);
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Item removido com sucesso", null));
        PrimeFaces.current().ajax().update("formReceita:tabelaItens formReceita:growl");
    }

    /** Lista os itens atuais da receita para exibição na tabela. */
    public List<MedicamentoReceitado> getItensDaReceita() {
        if (receita == null || receita.getId() == null) {
            return Collections.emptyList();
        }
        return receitaRepository.listItensByReceita(receita.getId());
    }

    /** Reinicia o fluxo, limpando a receita e seleções. */
    public void novaAssociacao() {
        this.receita = null;
        this.pacienteIdSelecionado = null;
        this.medicamentoIdSelecionado = null;
        PrimeFaces.current().ajax().update("formReceita");
    }

    private boolean validarReceitaEMedicamentoSelecionados() {
        if (receita == null || receita.getId() == null) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_WARN, "Crie a receita antes de adicionar medicamentos.", null));
            PrimeFaces.current().ajax().update("formReceita:growl");
            return false;
        }
        if (medicamentoIdSelecionado == null) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_WARN, "Selecione um medicamento para adicionar.", null));
            PrimeFaces.current().ajax().update("formReceita:growl");
            return false;
        }
        return true;
    }
}
