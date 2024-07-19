package aisaac.domain.datatable;

import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DatatablePage<T> {

    private List<T> data;
    private int draw;
    private int length;
    private int recordsTotal;
    private int recordsFiltered;
    
    public DatatablePage(List<T> data) {
        this.data = data;
    }
}