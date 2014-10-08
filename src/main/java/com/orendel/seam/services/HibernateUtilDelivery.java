package com.orendel.seam.services;

import java.util.HashMap;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.boot.registry.internal.StandardServiceRegistryImpl;
import org.hibernate.cfg.Configuration;

import com.orendel.seam.config.AppConfig;


public class HibernateUtilDelivery {

	private static StandardServiceRegistryImpl sr;
	private static SessionFactory sessionFactoryDeliveryDB;

	private static String CONFIG_FILE_DELIVERY_DB = "/delivery.cfg.xml";

	static {
		try {
			sessionFactoryDeliveryDB = initDeliverySessionFactory();
		} catch (Throwable ex) {
			ex.printStackTrace();
			throw new ExceptionInInitializerError(ex);
		}
	}
	
	
	private static SessionFactory initDeliverySessionFactory() {
		Configuration configuration = new Configuration();  
		configuration.configure(CONFIG_FILE_DELIVERY_DB);
		// get user defined properties
		String dbURL = AppConfig.INSTANCE.getValue("delivery.database.url");
		String userName = AppConfig.INSTANCE.getValue("delivery.database.username");
		String password = AppConfig.INSTANCE.getValue("delivery.database.password");
		System.out.println("Using DB url: " + dbURL);
		configuration.setProperty("hibernate.connection.url", dbURL);
		configuration.setProperty("hibernate.connection.username", userName);
		configuration.setProperty("hibernate.connection.password", password);
		sr= (StandardServiceRegistryImpl) new StandardServiceRegistryBuilder().applySettings(configuration.getProperties()).build(); 
		return configuration.buildSessionFactory(sr);
	}
	
	

	public static SessionFactory getSessionFactorySQL() {
		return sessionFactoryDeliveryDB;
	}

//	public static SessionFactory getSessionFactoryMySQL() {
//		return sessionFactoryMySQL;
//	}
	
	
	
	
	public static final HashMap<String, Session> sessionMap = new HashMap<String, Session>();
	
	
	public static Session getEditorSession(String editor) throws HibernateException {
		if (editor == null) {
//			LOGGER.error("Error:  el valor del editor es NULL (HibernateUtil)");
		}
		Session s = (Session) sessionMap.get(editor);
		if (s == null) {
			s = sessionFactoryDeliveryDB.openSession();
			sessionMap.put(editor, s);
		}
		return s;
	}
	
	public static void closeEditorSession(String editor) throws HibernateException {
		Session s = (Session) sessionMap.get(editor);
		if (s != null)
			s.close();
		sessionMap.remove(editor);
	}
	
	
	public static void destroy() {
		if (sessionFactoryDeliveryDB != null) {
			sessionFactoryDeliveryDB.close();
			sessionFactoryDeliveryDB = null;
			sr.destroy();
			sr = null;
		}
	}
	
	
	public static void verSesiones() {
//		LOGGER.debug("Total de sesiones de hibernate: " + sessionMap.size());
		for (String s : sessionMap.keySet()) {
			System.out.println("Sesión: " + s);
		}
	}
	
	/**
	 * This is a simple method to reduce the amount of code that needs
	 * to be written every time hibernate is used.
	 */
	public static void rollback(org.hibernate.Transaction tx) {
		if (tx != null) {
			try {
				tx.rollback( );
			} catch (HibernateException ex) {
				// Probably don't need to do anything -	this is likely being
				// called because of another exception, and we don't want to
				// mask it with yet another exception.
			}
		}
	}
	
	public static void procesarError(HibernateException he) {
		MessageBox msgbox = new MessageBox( new Shell(), SWT.OK | SWT.ICON_ERROR );
		msgbox.setText( "Error de Hibernate" );
		msgbox.setMessage( "Error: " + he.toString() + "\n\nStack trace: " + he.getStackTrace()[0] + "\n" + he.getStackTrace()[1] );
		msgbox.open();
		he.printStackTrace();
	}

}
