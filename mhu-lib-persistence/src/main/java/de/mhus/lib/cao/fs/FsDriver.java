package de.mhus.lib.cao.fs;

import java.io.File;
import java.net.URI;
import java.util.UUID;

import de.mhus.lib.cao.CaoConnection;
import de.mhus.lib.cao.CaoCore;
import de.mhus.lib.cao.CaoDriver;
import de.mhus.lib.cao.CaoLoginForm;

public class FsDriver extends CaoDriver {

	@Override
	public CaoCore connect(URI uri, String authentication) {
		return new FsCore("fs_" + UUID.randomUUID(), this, new File(  uri.getPath() ) );
	}

	@Override
	public CaoLoginForm createLoginForm(URI uri, String authentication) {
		return null;
	}

}