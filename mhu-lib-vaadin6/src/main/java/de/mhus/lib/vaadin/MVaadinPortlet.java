package de.mhus.lib.vaadin;


import com.vaadin.terminal.Sizeable;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class MVaadinPortlet extends MVaadinApplication {


	private static final long serialVersionUID = 1L;
	protected VerticalLayout layout;
	protected Window window;
	private HorizontalLayout control;
	private Button bHeightAdd;
	private Button bHeightSub;
	private Button bWidthAdd;
	private Button bWidthSub;
	private Button bFull;
	private boolean isFull = true;
	private int width;
	private int height;
	private boolean hasButtons = true;

	@Override
	public void init() {
		window = new Window();
		setMainWindow(window);
		
		VerticalLayout innerLayout = (VerticalLayout) getMainWindow().getContent();
		innerLayout.setHeightUnits(Sizeable.UNITS_PIXELS);
		innerLayout.setWidthUnits(Sizeable.UNITS_PIXELS);
		
		layout = new VerticalLayout();
		layout.setSizeFull();
		//layout.setWidth("100%");
		//layout.setHeight("100px");
		innerLayout.addComponent(layout);
		innerLayout.setExpandRatio(layout, 1);
		innerLayout.setMargin(false);
		
		control = new HorizontalLayout();
		innerLayout.addComponent(control);
		innerLayout.setExpandRatio(control, 0);
		innerLayout.setComponentAlignment(control, Alignment.TOP_RIGHT);
		control.setWidth("100%");
		
		if (hasButtons) {
			
			createCustomButtons(control);
			
			bHeightAdd = new Button(" \\/ ");
			bHeightAdd.addListener(new Button.ClickListener() {
				
				@Override
				public void buttonClick(ClickEvent event) {
					doAddHeight();
				}
			});
	
			control.addComponent(bHeightAdd);
			
			bHeightSub = new Button(" /\\ ");
			bHeightSub.addListener(new Button.ClickListener() {
				
				@Override
				public void buttonClick(ClickEvent event) {
					doSubHeight();
				}
			});
	
			control.addComponent(bHeightSub);
			
			bWidthAdd = new Button(" > ");
			bWidthAdd.addListener(new Button.ClickListener() {
				
				@Override
				public void buttonClick(ClickEvent event) {
					doAddWidth();
				}
			});
	
			control.addComponent(bWidthAdd);
			
			bWidthSub = new Button(" < ");
			bWidthSub.addListener(new Button.ClickListener() {
				
				@Override
				public void buttonClick(ClickEvent event) {
					doSubWidth();
				}
			});
	
			control.addComponent(bWidthSub);
	
			bFull = new Button(" * ");
			bFull.addListener(new Button.ClickListener() {
				
				@Override
				public void buttonClick(ClickEvent event) {
					doFull();
				}
			});
	
			control.addComponent(bFull);
			control.addComponent(new Label("  "));
		}
    	window.setResizable(true);
    	
		int[] size = getRememberedSize();
		
		isFull = false;
		setHeight(size[1]);
		setWidth(size[0]);
		setFullSize(size[2] == 1);

    }
	
	protected void createCustomButtons(HorizontalLayout buttonBar) {
	}

	protected void doFull() {
		isFull = ! isFull;
		setFullSize(isFull);
		
	}

	public void setFullSize(boolean full) {
		VerticalLayout innerLayout = (VerticalLayout) getMainWindow().getContent();
		// innerLayout.setSizeUndefined();
		if (full) {
			// innerLayout.setSizeFull();
			innerLayout.setWidth("100%");
		} else {
			isFull = false;
			innerLayout.setHeightUnits(Sizeable.UNITS_PIXELS);
			innerLayout.setWidthUnits(Sizeable.UNITS_PIXELS);
			setWidth(width);
			setHeight(height);
		}
			
		isFull = full;
		if (hasButtons) {
			bFull.setCaption(full ? " * " : " o ");
			//bHeightAdd.setEnabled(!full);
			//bHeightSub.setEnabled(!full);
			bWidthAdd.setEnabled(!full);
			bWidthSub.setEnabled(!full);
		}
	}

	protected void doSubHeight() {
		setHeight(height - 50);
	}

	protected void doAddHeight() {
		setHeight(height + 50);
	}

	protected void doSubWidth() {
		setWidth(width - 50);
	}

	protected void doAddWidth() {
		setWidth(width + 50);
	}

	protected void doRememberSize(int[] opts) {
//		VerticalLayout innerLayout = (VerticalLayout) getMainWindow().getContent();
	}

	protected int[] getRememberedSize() {
		return new int[] {1000,600,1};
	}
	@Override
	public void close() {
		super.close();
	}
	
	
	protected VerticalLayout getContent() {
		return layout;
	}
	
	public void setHeight(int height) {
		if (height < 0) return;
		VerticalLayout innerLayout = (VerticalLayout) getMainWindow().getContent();
		/*if (!isFull)*/ innerLayout.setHeight(height,Sizeable.UNITS_PIXELS);
		this.height = height;
		doRememberSize(new int[] {width,height,isFull?1:0});
	}
	
	public void setWidth(int width) {
		if (width < 0) return;
		VerticalLayout innerLayout = (VerticalLayout) getMainWindow().getContent();
		if (!isFull) innerLayout.setWidth(width,Sizeable.UNITS_PIXELS);
		this.width = width;
		doRememberSize(new int[] {width,height,isFull?1:0});
	}

	public boolean isHasButtons() {
		return hasButtons;
	}

	protected void setHasButtons(boolean hasButtons) {
		this.hasButtons = hasButtons;
	}

}
