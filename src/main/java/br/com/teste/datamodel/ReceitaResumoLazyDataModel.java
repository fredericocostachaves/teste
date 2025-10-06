package br.com.teste.datamodel;

import br.com.teste.dto.ReceitaResumoDTO;
import br.com.teste.repository.ReceitaRepository;
import br.com.teste.bean.ConsultaMedicamentosBean;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * LazyDataModel para a consulta de "Medicamentos por Paciente".
 * Carrega páginas sob demanda aplicando filtros por nome do paciente e do medicamento.
 */
@Dependent
public class ReceitaResumoLazyDataModel extends LazyDataModel<ReceitaResumoDTO> implements Serializable {

    @Inject
    private ReceitaRepository repository;

    // Bean que detém os filtros digitados na tela
    @Inject
    private ConsultaMedicamentosBean filtrosBean;

    private List<ReceitaResumoDTO> pageData;

    @Override
    public List<ReceitaResumoDTO> load(int first, int pageSize, String sortField, SortOrder sortOrder,
                                       Map<String, org.primefaces.model.FilterMeta> filters) {
        boolean asc = sortOrder == SortOrder.ASCENDING || sortOrder == SortOrder.UNSORTED;
        String pacienteNome = filtrosBean.getPacienteNomeFiltro();
        String medicamentoNome = filtrosBean.getMedicamentoNomeFiltro();

        pageData = repository.findResumoPage(first, pageSize, sortField, asc, pacienteNome, medicamentoNome);
        int count = repository.countResumo(pacienteNome, medicamentoNome).intValue();
        setRowCount(count);
        return pageData;
    }

    @Override
    public ReceitaResumoDTO getRowData(String rowKey) {
        if (pageData == null) return null;
        Long id = Long.valueOf(rowKey);
        return pageData.stream().filter(r -> r.getReceitaId().equals(id)).findFirst().orElse(null);
    }

    @Override
    public Object getRowKey(ReceitaResumoDTO dto) {
        return dto != null ? dto.getReceitaId() : null;
    }
}
