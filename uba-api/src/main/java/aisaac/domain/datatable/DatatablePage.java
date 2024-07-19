package aisaac.domain.datatable;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
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