package br.com.teste.datamodel;

import br.com.teste.model.Medicamento;
import br.com.teste.repository.MedicamentoRepository;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Implementação de LazyDataModel do PrimeFaces para a entidade Medicamento.
 * Encapsula a estratégia de carregamento sob demanda (lazy loading) com paginação, filtros e ordenação.
 */
@Dependent
public class MedicamentoLazyDataModel extends LazyDataModel<Medicamento> implements Serializable {

    private static final long serialVersionUID = 1L;

    /** Repositório usado para executar as consultas paginadas */
    @Inject
    private MedicamentoRepository repository;

    /** Cache da página atual retornada pelo load (usado por getRowData) */
    private List<Medicamento> pageData;

    /** Carrega uma página de medicamentos conforme parâmetros vindos do componente DataTable. */
    @Override
    public List<Medicamento> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, org.primefaces.model.FilterMeta> filters) {
        boolean asc = sortOrder == SortOrder.ASCENDING || sortOrder == SortOrder.UNSORTED;
        pageData = repository.findPage(first, pageSize, filters, sortField, asc);
        int count = repository.count(filters).intValue();
        setRowCount(count);
        return pageData;
    }

    /** Localiza o registro na página corrente a partir da chave da linha (ID). */
    @Override
    public Medicamento getRowData(String rowKey) {
        if (pageData == null) return null;
        Long id = Long.valueOf(rowKey);
        return pageData.stream().filter(m -> m.getId().equals(id)).findFirst().orElse(null);
        
    }

    /** Retorna a chave (ID) do registro, utilizada pelo DataTable. */
    @Override
    public Object getRowKey(Medicamento medicamento) {
        return medicamento != null ? medicamento.getId() : null;
    }
}
