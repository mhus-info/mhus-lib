package de.mhus.lib.core.crypt.pem;

import de.mhus.lib.errors.MException;

public interface PemPriv extends PemBlock {

	/**
	 * Returns the identifier of the encoding method
	 * 
	 * @return
	 */
	String getMethod() throws MException;

}

