package de.mhus.lib.test.adb.model;

import java.util.UUID;

import de.mhus.lib.adb.DbManager;
import de.mhus.lib.adb.DbObject;
import de.mhus.lib.adb.DbObjectHandler;
import de.mhus.lib.adb.relation.RelSingle;
import de.mhus.lib.annotations.adb.DbIndex;
import de.mhus.lib.annotations.adb.DbPersistent;
import de.mhus.lib.annotations.adb.DbPrimaryKey;
import de.mhus.lib.annotations.adb.DbRelation;
import de.mhus.lib.annotations.adb.DbTable;
import de.mhus.lib.errors.MException;
import de.mhus.lib.sql.DbConnection;

@DbTable(tableName="book")
public class Book implements DbObject {

	private UUID id;
	private String name;
	private UUID[] authorId;
	private int pages;
	private UUID lendToId;
	private RelSingle<Person> lendTo = new RelSingle<Person>();
	private DbObjectHandler manager;

	@DbPrimaryKey
	public UUID getId() {
		return id;
	}
	public void setId(UUID id) {
		this.id = id;
	}

	@DbPersistent
	@DbIndex("1")
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	@DbPersistent
	public UUID[] getAuthorId() {
		return authorId;
	}
	public void setAuthorId(UUID[] author) {
		this.authorId = author;
	}

	@DbPersistent
	public int getPages() {
		return pages;
	}
	public void setPages(int pages) {
		this.pages = pages;
	}

	@DbPersistent
	public UUID getLendToId() {
		return lendToId;
	}
	public void setLendToId(UUID lendTo) {
		this.lendToId = lendTo;
	}
	public Person getLendToPerson() throws MException {
		if (lendToId == null) return null;
		return ((DbManager)manager).getObject(Person.class, getLendToId());
	}

	@Override
	public void doPreCreate(DbConnection con) {
		// TODO Auto-generated method stub

	}
	@Override
	public void doPreSave(DbConnection con) {
		// TODO Auto-generated method stub

	}
	@Override
	public void doInit(DbObjectHandler manager, String registryName, boolean isPersistent) {
		this.manager = manager;
	}
	@Override
	public void doPreDelete(DbConnection con) {
		// TODO Auto-generated method stub

	}
	@Override
	public void doPostLoad(DbConnection con) {
		// TODO Auto-generated method stub

	}
	@Override
	public void doPostDelete(DbConnection con) {
		// TODO Auto-generated method stub

	}

	@DbRelation(target=Person.class)
	public RelSingle<Person> getLendTo() {
		return lendTo;
	}

	@Override
	public boolean isAdbPersistent() {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public void doPostCreate(DbConnection con) {
		// TODO Auto-generated method stub

	}
	@Override
	public DbObjectHandler getDbHandler() {
		return manager;
	}
	@Override
	public boolean setDbHandler(DbObjectHandler manager) {
		return false;
	}



}
