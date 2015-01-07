package de.mhus.lib.cao;

import java.net.URI;

import de.mhus.lib.core.form.MForm;

public abstract class CaoLoginForm extends MForm {

	public abstract URI getURI();
	
	public abstract String getAuthentication();
	
}