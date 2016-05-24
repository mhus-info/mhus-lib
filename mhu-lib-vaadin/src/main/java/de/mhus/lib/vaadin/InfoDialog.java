package de.mhus.lib.vaadin;

import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

public class InfoDialog extends ModalDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Action cancel;
	private String message;
	private Listener listener;
	protected Label label;

	public InfoDialog(String title, String message, String txtCancel, Listener listener) throws Exception {

		this.message = message;
		this.listener = listener;
		cancel = new Action("cancel", txtCancel);
		actions = new Action[] {cancel};
		setPack(true);
		initUI();
		setCaption(title);
		
	}
	
	public Label getLabel() {
		return label;
	}
	
	@SuppressWarnings("deprecation")
	@Override
	protected void initContent(VerticalLayout layout) throws Exception {
		label = new Label(message);
		label.setContentMode(Label.CONTENT_XHTML);
		layout.addComponent(label);
	}

	@Override
	protected boolean doAction(Action action) {
		if (listener != null) listener.onClose(this);
		return true;
	}

	public static void show(UI ui, String title, String message, String txtCancel, Listener listener) {
		try {
			new InfoDialog(title,message,txtCancel,listener).show(ui);
		} catch (Exception e) {
		}
	}

	public static interface Listener {

		public void onClose(InfoDialog dialog);
		
	}

}
