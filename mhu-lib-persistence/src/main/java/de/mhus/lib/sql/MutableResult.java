package de.mhus.lib.sql;

import java.io.InputStream;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import de.mhus.lib.core.MCast;
import de.mhus.lib.core.MDate;

public class MutableResult extends DbResult {

	private LinkedList<Map<String, Object>> list = new LinkedList<Map<String,Object>>();
	private Map<String, Object> current = null;
	private LinkedList<String> columnList = new LinkedList<String>();
	private List<String> roColumnNames = Collections.unmodifiableList(columnList);
	private Iterator<Map<String, Object>> iterator;

	public void add(Map<String, Object> row) {
		list.add(row);
	}

	public void reset() {
		iterator = list.iterator();
	}

	public void addColumnName(String name) {
		columnList.add(name);
	}

	@Override
	public void close() throws Exception {
		list = null;
	}

	@Override
	public String getString(String columnLabel) throws Exception {
		return String.valueOf(current.get(columnLabel));
	}

	@Override
	public boolean next() throws Exception {
		boolean has = iterator.hasNext();
		if (!has) return false;
		current = iterator.next();
		return true;
	}

	@Override
	public InputStream getBinaryStream(String columnLabel) throws Exception {
		return (InputStream)current.get(columnLabel);
	}

	@Override
	public boolean getBoolean(String columnLabel) throws Exception {
		return MCast.toboolean(getString(columnLabel), false);
	}

	@Override
	public int getInt(String columnLabel) throws Exception {
		return MCast.toint(getString(columnLabel),0);
	}

	@Override
	public long getLong(String columnLabel) throws Exception {
		return MCast.tolong(getString(columnLabel),0);
	}

	@Override
	public float getFloat(String columnLabel) throws Exception {
		return MCast.tofloat(getString(columnLabel),0);
	}

	@Override
	public double getDouble(String columnLabel) throws Exception {
		return MCast.todouble(getString(columnLabel),0);
	}

	@Override
	public Date getDate(String columnLabel) throws Exception {
		return getMDate(columnLabel).toSqlDate();
	}

	@Override
	public MDate getMDate(String columnLabel) throws Exception {
		return new MDate(getString(columnLabel));
	}

	@Override
	public List<String> getColumnNames() throws Exception {
		return roColumnNames;
	}

	@Override
	public Time getTime(String columnLabel) throws Exception {
		return getMDate(columnLabel).toSqlTime();
	}

	@Override
	public Timestamp getTimestamp(String columnLabel) throws Exception {
		return getMDate(columnLabel).toSqlTimestamp();
	}

	@Override
	public Object getObject(String columnLabel) throws Exception {
		return current.get(columnLabel);
	}

}
