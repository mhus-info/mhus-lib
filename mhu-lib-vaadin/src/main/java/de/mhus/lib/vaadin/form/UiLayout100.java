package de.mhus.lib.vaadin.form;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.event.FieldEvents;
import com.vaadin.event.FieldEvents.FocusEvent;
import com.vaadin.event.FieldEvents.FocusNotifier;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Button.ClickEvent;

import de.mhus.lib.form.UiWizard;

public class UiLayout100 extends UiLayout {

	private GridLayout layout;
	private int rows;
	
	public UiLayout100() {
		this.layout = new GridLayout(3,1);
		layout.setMargin(true);
		layout.setSpacing(true);
		layout.setHideEmptyRowsAndColumns(true);
		layout.setColumnExpandRatio(0, 0.3f);
		layout.setColumnExpandRatio(1, 0.7f);
		layout.setColumnExpandRatio(2, 0);
//		layout.setSizeFull();
		layout.setWidth("100%");
		rows = 0;
	}
	
	@Override
	public void createRow(final UiVaadin c) {
		final UiWizard wizard = c.getWizard();
		Component e = c.createEditor();
		if (e == null) return;
		
		e.setWidth("100%");
		//e.setHeight("30px");
		
		
		if (e instanceof AbstractField) {
			((AbstractField)e).setImmediate(true);
			((AbstractField)e).addValueChangeListener(new Property.ValueChangeListener() {
				
				@Override
				public void valueChange(ValueChangeEvent event) {
					c.fieldValueChangedEvent();
				}
			});
		}
		if (e instanceof FocusNotifier) {
			((FocusNotifier)e).addFocusListener(new FieldEvents.FocusListener() {
				
				@Override
				public void focus(FocusEvent event) {
					c.focusEvent();
				}
			});
		}
		
		c.setComponentEditor(e);

		if (c.isFullSize()) {
			UiRow row1 = createRow();
			row1.setFull(true);
			
			Label l = new Label();
			l.setWidth("100%");
			c.setComponentLabel(l);
			row1.setComponent(l);

			UiRow row2 = createRow();
			row2.setFull(true);
			
			if (wizard != null) {
				Button b = new Button("W");
				b.addClickListener(new Button.ClickListener() {
					
					@Override
					public void buttonClick(ClickEvent event) {
						wizard.showWizard(c);
					}
				});
				b.setWidth("100%");
				row2.setWizard(b);
				c.setComponentWizard(b);
			}
			
			row2.setComponent(e);
			
		} else {
			
			UiRow row1 = createRow();
			
			Label l = new Label();
			l.setWidth("100%");
			c.setComponentLabel(l);
			row1.setLeft(l);

			if (wizard != null) {
				Button b = new Button("W");
				b.addClickListener(new Button.ClickListener() {
					
					@Override
					public void buttonClick(ClickEvent event) {
						wizard.showWizard(c);
					}
				});
				b.setWidth("100%");
				row1.setWizard(b);
				c.setComponentWizard(b);
			}

			row1.setRight(e);
			
		}
		
		UiRow row3 = createRow();
		row3.setFull(c.isFullSize());
		Label le = new Label();
		le.setStyleName("error-text");
		le.setWidth("100%");
		c.setComponentError(le);
		row3.setComponent(le);
		
	}
	
	protected UiRow createRow() {
		rows++;
		layout.setRows(rows);
		return new UiRow(layout, rows-1);
	}

	public Component getComponent() {
		return layout;
	}

	/**
	 * | 0       | 1     | 2      |
	 * | Caption | Field | Wizard |
	 * | Caption | Field          |
	 * 
	 * |         | Error          |
	 * 
	 * | Caption                  |
	 * | Field                    |
	 * | Field           | Wizard |
	 * | Error                    |
	 * 
	 * @author mikehummel
	 *
	 */
	public class UiRow {

		private GridLayout layout;
		private int row;
		private boolean full;
		private boolean wizard;

		public UiRow(GridLayout layout, int row) {
			this.layout = layout;
			this.row = row;
		}
		
		public void setLeft(Component component) {
			if (full) return;
			layout.addComponent(component, 0, row);
		}

		public void setRight(Component component) {
			if (full) return;
			if (wizard)
				layout.addComponent(component, 1, row);
			else
				layout.addComponent(component, 1, row, 2, row);
		}
		
		public void setComponent(Component component) {
			if (!full) {
				setRight(component);
				return;
			}
			if (wizard)
				layout.addComponent(component, 0, row, 1, row);
			else
				layout.addComponent(component, 0, row, 2, row);
		}

		public boolean isFull() {
			return full;
		}

		public void setFull(boolean full) {
			this.full = full;
		}

		public boolean isWizard() {
			return wizard;
		}

		public void setWizard(Component wizard) {
			this.wizard = wizard != null;
			if (wizard == null) return;
			layout.addComponent(wizard, 2, row );
		}
		
	}
	
}
