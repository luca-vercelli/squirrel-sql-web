package net.sourceforge.squirrel_sql.ws.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Naive structure representing a whole ResultSet in memory
 * 
 * @author lv 2020
 *
 */
public class Table {
	private String[] columnHeaders;
	private List<Object[]> rows = new ArrayList<>();

	public String[] getColumnHeaders() {
		return columnHeaders;
	}

	public void setColumnHeaders(String[] columnHeaders) {
		this.columnHeaders = columnHeaders;
	}

	public List<Object[]> getRows() {
		return rows;
	}

	public void setItems(List<Object[]> rows) {
		this.rows = rows;
	}
}
