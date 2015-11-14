package de.mhus.lib.vaadin.form2;

import com.vaadin.event.FieldEvents;
import com.vaadin.event.FieldEvents.FocusEvent;
import com.vaadin.event.FieldEvents.FocusNotifier;
import com.vaadin.server.UserError;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.themes.BaseTheme;

import de.mhus.lib.errors.MException;
import de.mhus.lib.form.DataConnector;
import de.mhus.lib.form.DataSource;
import de.mhus.lib.form.LayoutDataElement;
import de.mhus.lib.form.UiComponent;
import de.mhus.lib.form.definition.FmElement;

public abstract class UiVaadin extends UiComponent {

	private AbstractComponent label;

	public void createUi(VaadinFormBuilder builder) throws MException {
		
				
		int o = 0;
		int c = getElement().getParent().getColumns();
		int l = getElement().getParent().getLabelColums();
		
		int row = builder.getCurrentComposite().createRow();
		
		Component label = getLabel();
		Component field = getField();
		
		if (getElement().isFullWidth()) {
			if (!getElement().isTitleInside()) {
				builder.getCurrentComposite().addComponent(getElement(),label,o,row,o+c-1,row);
				row = builder.getCurrentComposite().createRow();
			}
			if (isInformationElement() && builder.getInformationPane() != null)
				builder.getInformationPane().addComponent(field);
			else
				builder.getCurrentComposite().addComponent(getElement(), field,o,row,o+c-1,row);
		} else {
			if (!getElement().isTitleInside()) {
				log().d("add label",o,row,o+l-1,row);
				builder.getCurrentComposite().addComponent(getElement(), label,o,row,o+l-1,row);
			}
			log().d("add field",o+l,row,o+c-1,row);
			builder.getCurrentComposite().addComponent(getElement(), field,o+l,row,o+c-1,row);
		}
	}

	@SuppressWarnings("deprecation")
	protected AbstractComponent getLabel() {
		if (label == null) {
			if (getElement().getConfig().isProperty(FmElement.WIZARD)) {
				label = new Button(((LayoutDataElement)getElement()).getTitle());
				((Button)label).setStyleName(BaseTheme.BUTTON_LINK);
				((Button)label).addClickListener(new Button.ClickListener() {
					
					private static final long serialVersionUID = 1L;

					@Override
					public void buttonClick(ClickEvent event) {
						try {
							getElement().getFormControl().wizard(getElement());
						} catch (Throwable t) {
							log().w(t);
							t.printStackTrace();
						}
					}
				});
			} else
				label = new Label(((LayoutDataElement)getElement()).getTitle());
			label.setWidth("100%");
		}
		return label;
	}
	
	protected abstract Component getField() throws MException;
	
	@Override
	public void doUpdate(DataConnector data) throws MException {
		if (data.getTaskName().equals(DataSource.CONNECTOR_TASK_TITLE)) {
			String arg = data.getString((String)label.getCaption());
			if (equals(arg,label.getCaption())) return;
			label.setCaption(arg);
			return;
		}
		if (data.getTaskName().equals(DataSource.CONNECTOR_TASK_ERROR)) {
			String msg = data.getString("");
			setErrorMessage(msg);
			return;
		}
	}
	
	@Override
	public void setErrorMessage(String msg) {
		getLabel().setComponentError(msg == null ? null : new UserError(msg));
	}
	
	@SuppressWarnings({ "deprecation", "rawtypes" })
	protected void prepareInputField(AbstractField text) {
		if (text instanceof FocusNotifier)
			((FocusNotifier)text).addListener(new FieldEvents.FocusListener() {
				
				private static final long serialVersionUID = 1L;

				@Override
				public void focus(FocusEvent event) {
					getElement().getFormControl().focused(getElement());
				}
			});		
	}
	
	public UI getWindow() {
		return label.getUI();
	}
	
	public boolean isInformationElement() {
		return false;
	}

}
