package de.mhus.lib.vaadin.form;

import de.mhus.lib.core.activator.DefaultActivator;
import de.mhus.lib.form.ActivatorAdapterProvider;
import de.mhus.lib.form.ui.FmText;

public class DefaultAdapterProvider extends ActivatorAdapterProvider {

	public DefaultAdapterProvider() {
		super(null);
		DefaultActivator a = new DefaultActivator();
		activator = a;
		
		a.addMap("text", UiText.Adapter.class);
		a.addMap("checkbox", UiCheckbox.Adapter.class);
		a.addMap("date", UiDate.Adapter.class);
		a.addMap("password", UiPassword.Adapter.class);
		a.addMap("number", UiNumber.Adapter.class);
		a.addMap("textarea", UiTextArea.Adapter.class);
		a.addMap("richtext", UiRichTextArea.Adapter.class);
		
	}

}
