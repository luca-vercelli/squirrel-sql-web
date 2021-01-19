package net.sourceforge.squirrel_sql.dto;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetDefinition;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetException;
import net.sourceforge.squirrel_sql.fw.datasetviewer.IDataSet;
import net.sourceforge.squirrel_sql.fw.util.NullMessageHandler;

/**
 * POJO in-memory representation of a IDataSet
 * 
 * @author lv 2021
 *
 */
public class DataSetBean extends ListBean<Object[]> {

    private DataSetDefinition dataSetDefinition;

    public DataSetBean() {
        this.setData(new ArrayList<>());
    }

    public DataSetBean(IDataSet ds) throws DataSetException {
        createData(ds);
    }

    private void createData(IDataSet ds) throws DataSetException {
        List<Object[]> data = new ArrayList<>();
        int columnCount = ds.getColumnCount();
        long count = 0;
        while (ds.next(NullMessageHandler.getInstance())) {
            Object[] row = new Object[columnCount];
            for (int i = 0; i < columnCount; ++i) {
                row[i] = ds.get(i);
            }
            data.add(row);
            ++count;
        }
        setData(data);
        setCount(count);
        setDataSetDefinition(ds.getDataSetDefinition());
    }

    public DataSetDefinition getDataSetDefinition() {
        return dataSetDefinition;
    }

    public void setDataSetDefinition(DataSetDefinition dataSetDefinition) {
        this.dataSetDefinition = dataSetDefinition;
    }
}
