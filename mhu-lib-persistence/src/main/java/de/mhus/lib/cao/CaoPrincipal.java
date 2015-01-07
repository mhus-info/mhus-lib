package de.mhus.lib.cao;

import java.util.LinkedList;

import de.mhus.lib.cao.CaoMetaDefinition.TYPE;

public class CaoPrincipal extends CaoPolicy {

	public static final String NAME = "name";
	public static final String PRINCIPAL_TYPE = "principal_type";
	public static enum PRINCIPAL_TYPES {USER,GROUP,ROLE,OTHER};
	
	private String name;
	protected int principalType;

	public CaoPrincipal(CaoNode element, String name, PRINCIPAL_TYPES type, boolean readable, boolean writable)
			throws CaoException {
		super(element, readable, writable);
		this.name = name;
		principalType = type.ordinal();
	}
	
	protected void fillMetaData(LinkedList<CaoMetaDefinition> definition) {
		definition.add(new CaoMetaDefinition(meta,NAME,TYPE.STRING,null,256) );
		definition.add(new CaoMetaDefinition(meta,PRINCIPAL_TYPE,TYPE.LONG,null,0) );
	}

	public String getName() throws CaoException {
		return name;
	}

	public PRINCIPAL_TYPES getPrincipalType() {
		try {
			long index = getLong(PRINCIPAL_TYPE, PRINCIPAL_TYPES.OTHER.ordinal());
			if (index < 0 || index >+ PRINCIPAL_TYPES.values().length) return PRINCIPAL_TYPES.OTHER;
			return PRINCIPAL_TYPES.values()[(int)index];
		} catch (Exception e) {
			return PRINCIPAL_TYPES.OTHER;
		}
	}
	
	@Override
	public String getProperty(String name) {
		if (NAME.equals(name))
			return this.name;
		if (PRINCIPAL_TYPE.equals(name))
			return String.valueOf(principalType);
		return super.getString(name, null);
	}
	
}