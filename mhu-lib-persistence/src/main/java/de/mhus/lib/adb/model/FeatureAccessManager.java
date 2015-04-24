package de.mhus.lib.adb.model;

import de.mhus.lib.adb.DbAccessManager;
import de.mhus.lib.adb.DbManager;
import de.mhus.lib.sql.DbConnection;
import de.mhus.lib.sql.DbResult;

public class FeatureAccessManager extends Feature {

	public DbAccessManager accessManager;

	@Override
	protected void doInit() {
		accessManager = manager.getSchema().getAccessManager(table);
	}
	
	@Override
	public void postFillObject(Object obj, DbConnection con) throws Exception {
		if (accessManager != null) accessManager.hasAccess(manager, table, con, obj, DbAccessManager.ACCESS.READ);
	}
	
	@Override
	public void preCreateObject(DbConnection con, Object object) throws Exception {
		if (accessManager != null) accessManager.hasAccess(manager, table, con, object, DbAccessManager.ACCESS.CREATE);
	}

	@Override
	public void preSaveObject(DbConnection con, Object object) throws Exception {
		if (accessManager != null) accessManager.hasAccess(manager, table, con, object, DbAccessManager.ACCESS.UPDATE);
	}

	@Override
	public void deleteObject(DbConnection con, Object object) throws Exception {
		if (accessManager != null) accessManager.hasAccess(manager, table, con, object, DbAccessManager.ACCESS.DELETE);
	}

}
