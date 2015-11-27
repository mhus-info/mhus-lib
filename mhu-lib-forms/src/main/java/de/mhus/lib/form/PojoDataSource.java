package de.mhus.lib.form;

import java.io.IOException;

import de.mhus.lib.core.pojo.MPojo;
import de.mhus.lib.core.pojo.PojoModel;

public class PojoDataSource implements DataSource {

	private PojoProvider pojo;
	private PojoModel model;

	public PojoDataSource(PojoProvider pojo) {
		this.pojo = pojo;
		model = MPojo.getDefaultModelFactory().createPojoModel(pojo.getPojo().getClass());
	}

	@Override
	public boolean getBoolean(UiComponent component, String name, boolean def) {
		try {
			return (boolean)model.getAttribute(getName(component,name)).get(pojo.getPojo());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return def;
	}

	protected String getName(UiComponent component, String name) {
		String ret = (component.getName() + name).toLowerCase();
		return ret;
	}

	@Override
	public int getInt(UiComponent component, String name, int def) {
		try {
			return (int) model.getAttribute(getName(component,name)).get(pojo.getPojo());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return def;
	}

	@Override
	public String getString(UiComponent component, String name, String def) {
		try {
			String ret = (String) model.getAttribute(getName(component,name)).get(pojo.getPojo());
			if (ret == null) return def;
			return ret;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return def;
	}

	@Override
	public Object getObject(UiComponent component, String name, Object def) {
		try {
			Object ret = model.getAttribute(getName(component,name)).get(pojo.getPojo());
			if (ret == null) return def;
			return ret;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return def;
	}

	@Override
	public void setObject(UiComponent component, String name, Object value) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public DataSource getNext() {
		return null;
	}

}