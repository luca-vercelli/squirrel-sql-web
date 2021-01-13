package net.sourceforge.squirrel_sql.dto;

import java.util.List;

/**
 * OData-like bean for lists
 * 
 * @author LV 2018
 *
 */
public class ListBean<T> {

    private List<T> data;
    private Long count;

    public ListBean() {
    }

    public ListBean(List<T> data, Long totalCount) {
        this.data = data;
        this.count = totalCount;
    }

    public ListBean(List<T> data) {
        this(data, (long) data.size());
    }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }
}
