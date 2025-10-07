package br.com.teste.bean;

import br.com.teste.datamodel.ReceitaResumoLazyDataModel;
import br.com.teste.model.MedicamentoReceitado;
import br.com.teste.repository.ReceitaRepository;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * Bean para a consulta "Medicamentos por Paciente".
 * Mantém os filtros de pesquisa e expõe o LazyDataModel para a tabela.
 */
@Named
@ViewScoped
public class ConsultaMedicamentosBean implements Serializable {

    @Inject
    private ReceitaResumoLazyDataModel lazyModel;

    @Inject
    private ReceitaRepository receitaRepository;

    private String pacienteNomeFiltro;
    private String medicamentoNomeFiltro;

    // Estado para visualizar os itens de uma receita específica
    private Long receitaIdParaItens;
    private List<MedicamentoReceitado> itensDaReceita;

    @PostConstruct
    public void init() {
        limpar();
    }

    public ReceitaResumoLazyDataModel getLazyModel() {
        return lazyModel;
    }

    public String getPacienteNomeFiltro() {
        return pacienteNomeFiltro;
    }

    public void setPacienteNomeFiltro(String pacienteNomeFiltro) {
        this.pacienteNomeFiltro = pacienteNomeFiltro;
    }

    public String getMedicamentoNomeFiltro() {
        return medicamentoNomeFiltro;
    }

    public void setMedicamentoNomeFiltro(String medicamentoNomeFiltro) {
        this.medicamentoNomeFiltro = medicamentoNomeFiltro;
    }

    /** Dispara a pesquisa. O DataTable lerá os filtros pelo LazyDataModel. */
    public void pesquisar() {
        // Intencionalmente em branco: o PrimeFaces recarregará via lazy quando a tabela for atualizada.
    }

    /** Limpa os filtros. */
    public void limpar() {
        this.pacienteNomeFiltro = null;
        this.medicamentoNomeFiltro = null;
        this.receitaIdParaItens = null;
        this.itensDaReceita = Collections.emptyList();
    }

    /** Carrega os itens da receita solicitada para exibição em diálogo. */
    public void abrirItensReceita(Long receitaId) {
        this.receitaIdParaItens = receitaId;
        try {
            this.itensDaReceita = receitaRepository.listItensByReceita(receitaId);
        } catch (Exception e) {
            this.itensDaReceita = Collections.emptyList();
        }
    }

    public Long getReceitaIdParaItens() {
        return receitaIdParaItens;
    }

    public List<MedicamentoReceitado> getItensDaReceita() {
        return itensDaReceita;
    }
}
