package net.sourceforge.squirrel_sql.dto;

/**
 * OData-like bean for single entities
 * 
 * @author LV 2018
 *
 */
public class ValueBean<T> {

    private T value;

    public ValueBean() {
    }

    public ValueBean(T value) {
        this.value = value;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }
}
