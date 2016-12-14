package de.mhus.lib.cao.auth;

import java.io.File;

import de.mhus.lib.cao.CaoAspectFactory;
import de.mhus.lib.cao.CaoCore;
import de.mhus.lib.cao.CaoNode;
import de.mhus.lib.cao.aspect.StructureControl;
import de.mhus.lib.cao.util.MutableActionList;
import de.mhus.lib.core.IProperties;

public class AuthStructureControl implements CaoAspectFactory<StructureControl> {

	private AuthCore core;

	@Override
	public StructureControl getAspectFor(CaoNode node) {
		CaoNode orgNode = core.getInstance((AuthNode)node);
		return new Aspect(node, orgNode);
	}

	@Override
	public void doInitialize(CaoCore core, MutableActionList actionList) {
		this.core = (AuthCore) core;
	}

	private class Aspect implements StructureControl {

		private CaoNode node;
		private StructureControl instance;
		private CaoNode orgNode;

		public Aspect(CaoNode node, CaoNode orgNode) {
			this.node = node;
			this.orgNode = orgNode;
			this.instance = orgNode.adaptTo(StructureControl.class);;
		}

		@Override
		public void setBehavior(Behavior<?> behavior) {
			// TODO should use an adapter to hide nodes
			instance.setBehavior(behavior);
		}

		@Override
		public boolean moveUp() {
			if (!core.hasStructureAccess(orgNode)) return false;
			return instance.moveUp();
		}

		@Override
		public boolean moveDown() {
			if (!core.hasStructureAccess(orgNode)) return false;
			return instance.moveDown();
		}

		@Override
		public boolean moveToTop() {
			if (!core.hasStructureAccess(orgNode)) return false;
			return instance.moveToTop();
		}

		@Override
		public boolean moveToBottom() {
			if (!core.hasStructureAccess(orgNode)) return false;
			return instance.moveToBottom();
		}

		@Override
		public boolean moveAfter(CaoNode predecessor) {
			if (!core.hasStructureAccess(orgNode)) return false;
			return instance.moveAfter(predecessor);
		}

		@Override
		public int getPositionIndex() {
			return instance.getPositionIndex();
		}

		@Override
		public boolean isAtTop() {
			return instance.isAtTop();
		}

		@Override
		public boolean isAtBottom() {
			return instance.isAtBottom();
		}

		@Override
		public boolean moveTo(CaoNode parent) {
			if (!core.hasStructureAccess(orgNode)) return false;
			return instance.moveTo(parent);
		}

		@Override
		public boolean delete(boolean recursive) {
			if (!core.hasDeleteAccess(orgNode)) return false;
			return instance.delete(recursive);
		}

		@Override
		public CaoNode createChildNode(String name, IProperties properties) {
			if (!core.hasCreateAccess(orgNode, name, properties)) return null;
			return instance.createChildNode(name, properties);
		}

		@Override
		public boolean uploadRendition(String name, File file) {
			if (!core.hasContentWriteAccess(orgNode, name)) return false;
			return instance.uploadRendition(name, file);
		}

		@Override
		public CaoNode copyTo(CaoNode parent, boolean recursive) {
			if (!core.hasWriteAccess(parent)) return null;
			return instance.copyTo(parent, recursive);
		}

		@Override
		public boolean rename(String name) {
			if (!core.hasStructureAccess(orgNode)) return false;
			return instance.rename(name);
		}
		
	}
}
