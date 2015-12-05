package de.mhus.lib.core.cfg;

import de.mhus.lib.core.MSingleton;
import de.mhus.lib.core.directory.ResourceNode;

public class CfgLong extends CfgValue<Long>{

	public CfgLong(Object owner, String path, long def) {
		super(owner, path, def);
	}

	@Override
	protected Long loadValue() {
		int p = getPath().indexOf('@');
		if (p < 0) 
			return MSingleton.getCfg(getOwner()).getLong(getPath(), getDefault());
		ResourceNode node = MSingleton.getCfg(getOwner()).getNodeByPath(getPath().substring(0, p));
		if (node == null) return getDefault();
		return node.getLong(getPath().substring(p+1), getDefault());
	}

}
