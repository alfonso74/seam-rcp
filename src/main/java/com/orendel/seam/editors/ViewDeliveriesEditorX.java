package com.orendel.seam.editors;

import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.layout.FillLayout;

import com.orendel.seam.controllers.DeliveriesController;
import com.orendel.seam.controllers.InvoicesController;
import com.orendel.seam.dialogs.DeliveryDetailDialog;
import com.orendel.seam.domain.delivery.Delivery;
import com.orendel.seam.services.DateUtil;


public class ViewDeliveriesEditorX extends Composite {
	private Logger logger = Logger.getLogger(ViewDeliveriesEditorX.class);
	
	private DeliveriesController controller;
	private InvoicesController c2;
	private final String[] searchModeItems = new String[] {"Número de factura", "Número de entrega", "Rango de fecha"}; 
	
	private Table table;
	private Text txtFactura;
	private Text txtEntrega;
	
	private Combo comboBuscar;
	
	private Composite compositeBuscar;
	
	private Shell shell;

	
	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public ViewDeliveriesEditorX(Composite parent, int style) {
		super(parent, style);

		shell = parent.getShell();
		controller = new DeliveriesController();
		c2 = new InvoicesController();
		logger.info("BBBB: " + controller.getListado().size());
		logger.info("BBBB: " + controller.findAllDeliveries().size());
		logger.info("BBBB: " + c2.getListado().size());
		
		setLayout(new GridLayout(4, false));
		
		Label lblBuscarPor = new Label(this, SWT.NONE);
		lblBuscarPor.setFont(SWTResourceManager.getFont("Segoe UI", 12, SWT.NORMAL));
		lblBuscarPor.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblBuscarPor.setText("Buscar por:");
		
		comboBuscar = new Combo(this, SWT.READ_ONLY);
		comboBuscar.setItems(searchModeItems);
		comboBuscar.setFont(SWTResourceManager.getFont("Segoe UI", 11, SWT.NORMAL));
		
		Button btnBuscar = new Button(this, SWT.NONE);
		btnBuscar.setFont(SWTResourceManager.getFont("Segoe UI", 11, SWT.NORMAL));
		btnBuscar.setText("Buscar");
		btnBuscar.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				executeSearch();
			}
		});
		new Label(this, SWT.NONE);
		
		compositeBuscar = new Composite(this, SWT.NONE);
		compositeBuscar.setLayout(new FillLayout(SWT.HORIZONTAL));
		compositeBuscar.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 4, 1));
		
		createGrupoFactura(compositeBuscar);
		
		table = new Table(this, SWT.BORDER | SWT.FULL_SELECTION);
		table.setFont(SWTResourceManager.getFont("Segoe UI", 12, SWT.NORMAL));
		GridData gd_table = new GridData(SWT.FILL, SWT.FILL, true, true, 4, 1);
		gd_table.verticalIndent = 10;
		gd_table.widthHint = 440;
		table.setLayoutData(gd_table);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		
		TableColumn tblclmnFactura = new TableColumn(table, SWT.NONE);
		tblclmnFactura.setWidth(100);
		tblclmnFactura.setText("Factura");
		
		TableColumn tblclmnEntrega = new TableColumn(table, SWT.CENTER);
		tblclmnEntrega.setWidth(100);
		tblclmnEntrega.setText("Entrega");
		
		TableColumn tblclmnLneas = new TableColumn(table, SWT.CENTER);
		tblclmnLneas.setWidth(60);
		tblclmnLneas.setText("Líneas");
		
		TableColumn tblclmnItems = new TableColumn(table, SWT.CENTER);
		tblclmnItems.setWidth(60);
		tblclmnItems.setText("Items");
		
		TableColumn tblclmnUsuario = new TableColumn(table, SWT.NONE);
		tblclmnUsuario.setWidth(200);
		tblclmnUsuario.setText("Usuario");
		
		TableColumn tblclmnFecha = new TableColumn(table, SWT.NONE);
		tblclmnFecha.setWidth(175);
		tblclmnFecha.setText("Fecha");
		
//		table.addSelectionListener(new SelectionAdapter() {
//			@Override
//			public void widgetSelected(SelectionEvent e) {
//				super.widgetSelected(e);
//				logger.info("JAA!: " + e.getSource());
//				if (e.getSource() instanceof Delivery) {
//					Delivery d = (Delivery) e.getSource();
//					logger.info("D: " + d.getId());
//				}
//				if (e.getSource() instanceof Table) {
//					Table t = (Table) e.getSource();
//					logger.info("T: " + t.getSelectionIndex());
//				}
//			}
//		});
		
		addDoubleClickListener(table);

		comboBuscar.select(0);
		comboBuscar.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				toggleGroup();
			}
		});
	}
	
	
	private void executeSearch() {
		List<Delivery> deliveryList = controller.getListado();
		logger.info("DELIVERIES: " + deliveryList.size());
		refreshTableDetails(deliveryList);
	}
	
	
	private void refreshTableDetails(List<Delivery> deliveryList) {
		table.removeAll();
		TableItem item;
		
		for (Delivery v : deliveryList) {
			item = new TableItem(table, SWT.NONE);
			int column = 0;
			item.setData(v);
			item.setText(column++, " " + v.getTicketNumber());
			item.setText(column++, v.getId().toString());
			item.setText(column++, Integer.toString(v.getDeliveryLines().size()));
			item.setText(column++, Integer.toString(v.getDeliveredItemsTotalQty()));
			item.setText(column++, "Usuario x");
			item.setText(column++, DateUtil.toString(v.getCreated(), DateUtil.formatoFechaHora));
			
		}
	}
	
	
	private void toggleGroup() {
		for (Control c : compositeBuscar.getChildren()) {
			c.dispose();
		}
		int searchMode = comboBuscar.getSelectionIndex();
		if (searchMode == 0) {
			createGrupoFactura(compositeBuscar);
		} else if (searchMode == 1) {
			createGrupoEntrega(compositeBuscar);
		} else if (searchMode == 2) {
			createGroupRangoFecha(compositeBuscar);
		}
		compositeBuscar.layout();
	}
	
	
	private Group createGrupoFactura(Composite composite) {
		Group groupFactura = new Group(composite, SWT.NONE);
		groupFactura.setLayout(new GridLayout(2, false));
		
		Label lblNoFactura = new Label(groupFactura, SWT.NONE);
		lblNoFactura.setFont(SWTResourceManager.getFont("Segoe UI", 12, SWT.NORMAL));
		lblNoFactura.setText("No. Factura:");
		
		txtFactura = new Text(groupFactura, SWT.BORDER);
		GridData gd_txtFactura = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_txtFactura.widthHint = 120;
		txtFactura.setLayoutData(gd_txtFactura);
		txtFactura.setFont(SWTResourceManager.getFont("Segoe UI", 12, SWT.NORMAL));
		return groupFactura;
	}
	
	
	private Group createGrupoEntrega(Composite composite) {
		Group groupEntrega = new Group(composite, SWT.NONE);
		groupEntrega.setLayout(new GridLayout(2, false));
		groupEntrega.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 4, 1));
		
		Label lblNoEntrega = new Label(groupEntrega, SWT.NONE);
		lblNoEntrega.setFont(SWTResourceManager.getFont("Segoe UI", 12, SWT.NORMAL));
		lblNoEntrega.setText("No. Entrega:");
		
		txtEntrega = new Text(groupEntrega, SWT.BORDER);
		GridData gd_txtEntrega = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_txtEntrega.widthHint = 120;
		txtEntrega.setLayoutData(gd_txtEntrega);
		txtEntrega.setFont(SWTResourceManager.getFont("Segoe UI", 12, SWT.NORMAL));
		return groupEntrega;
	}
	
	
	private Group createGroupRangoFecha(Composite composite) {
		Group groupRangoFecha = new Group(composite, SWT.NONE);
		groupRangoFecha.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 4, 1));
		groupRangoFecha.setLayout(new GridLayout(5, false));
		
		Label lblFechaIni = new Label(groupRangoFecha, SWT.NONE);
		lblFechaIni.setFont(SWTResourceManager.getFont("Segoe UI", 12, SWT.NORMAL));
		lblFechaIni.setText("Fecha inicial:");
		
		DateTime dateTimeIni = new DateTime(groupRangoFecha, SWT.DROP_DOWN);
		dateTimeIni.setFont(SWTResourceManager.getFont("Segoe UI", 11, SWT.NORMAL));
		new Label(groupRangoFecha, SWT.NONE);
		
		Label lblFechaFin = new Label(groupRangoFecha, SWT.NONE);
		lblFechaFin.setFont(SWTResourceManager.getFont("Segoe UI", 12, SWT.NORMAL));
		lblFechaFin.setText("Fecha final:");
		
		DateTime dateTimeFin = new DateTime(groupRangoFecha, SWT.DROP_DOWN);
		dateTimeFin.setFont(SWTResourceManager.getFont("Segoe UI", 11, SWT.NORMAL));
		return groupRangoFecha;
	}
	
	
	private void addDoubleClickListener(Table table) {
		final Display display = this.getDisplay();
		table.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseUp(MouseEvent arg0) {
			}
			
			@Override
			public void mouseDown(MouseEvent arg0) {
			}
			
			@Override
			public void mouseDoubleClick(MouseEvent arg0) {
				logger.info("ARG0: " + arg0);
				Table t = (Table) arg0.getSource();
				TableItem item = t.getItem(t.getSelectionIndex());
				Delivery delivery = (Delivery) item.getData();
				logger.info("Delivery: " + item.getText(1) + ", " + delivery);
//				ConsultasEditor editor = new ConsultasEditor(display, SWT.None);
				DeliveryDetailDialog dialog = new DeliveryDetailDialog(shell, SWT.APPLICATION_MODAL, delivery.getId());
				dialog.setText("JA!");
				dialog.open();
			}
		});
	}
	

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
}
