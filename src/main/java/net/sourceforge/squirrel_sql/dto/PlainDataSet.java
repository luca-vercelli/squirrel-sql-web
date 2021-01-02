package net.sourceforge.squirrel_sql.dto;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetDefinition;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetException;
import net.sourceforge.squirrel_sql.fw.datasetviewer.IDataSet;
import net.sourceforge.squirrel_sql.fw.util.IMessageHandler;
import net.sourceforge.squirrel_sql.fw.util.NullMessageHandler;

/**
 * This is more or less an ObjectArrayDataSet, with public access to underlying
 * array with getAllDataForReadOnly(). The name and signature of this method is
 * compatible with ResultSetDataSet class.
 * 
 * @author lv 2021
 *
 */
public class PlainDataSet implements IDataSet {

	private IDataSet wrappedDataSet;
	private List<Object[]> allDataForReadOnly;
	private int curRow = -1;
	private int columnCount = 0;

	public PlainDataSet(IDataSet wrappedDataSet) throws DataSetException {
		this.wrappedDataSet = wrappedDataSet;
		createAllData();
	}

	private void createAllData() throws DataSetException {
		allDataForReadOnly = new ArrayList<>();
		columnCount = wrappedDataSet.getColumnCount();
		while (wrappedDataSet.next(NullMessageHandler.getInstance())) {
			Object[] row = new Object[columnCount];
			for (int i = 0; i < columnCount; ++i) {
				row[i] = wrappedDataSet.get(i);
			}
			allDataForReadOnly.add(row);
		}
	}

	public List<Object[]> getAllDataForReadOnly() {
		return allDataForReadOnly;
	}

	@Override
	public Object get(int columnIndex) throws DataSetException {
		return allDataForReadOnly.get(curRow)[columnIndex];
	}

	@Override
	public int getColumnCount() throws DataSetException {
		return columnCount;
	}

	@Override
	public DataSetDefinition getDataSetDefinition() throws DataSetException {
		return wrappedDataSet.getDataSetDefinition();
	}

	@Override
	public boolean next(IMessageHandler msgHandler) throws DataSetException {
		return ++curRow < allDataForReadOnly.size();
	}

}