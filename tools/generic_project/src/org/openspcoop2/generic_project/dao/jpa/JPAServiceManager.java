/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2015 Link.it srl (http://link.it).
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package org.openspcoop2.generic_project.dao.jpa;

import java.io.File;
import java.util.Properties;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.openspcoop2.generic_project.beans.IProjectInfo;
import org.openspcoop2.generic_project.exception.ServiceException;
import org.openspcoop2.generic_project.utils.ServiceManagerProperties;
import org.openspcoop2.utils.resources.GestoreJNDI;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.persistenceunit.PersistenceUnitPostProcessor;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

/**
 * JPAServiceManager
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class JPAServiceManager {

	private EntityManagerFactory emf;
	private EntityManager em;
		
	/** DataSource. */
	private DataSource datasource;
	/** Logger */
	private Logger log = null;
	/** JPA Properties */
	private JPAServiceManagerProperties jpaProperties;
	
	private String persistenceUnitName = null;
	
	public JPAServiceManager(String persistenceUnitName,String jndiName, Properties contextJNDI,ServiceManagerProperties serviceManagerProperties) throws ServiceException{
		this(persistenceUnitName,jndiName, contextJNDI, new JPAServiceManagerProperties(serviceManagerProperties), null);
	}
	public JPAServiceManager(String persistenceUnitName,String jndiName, Properties contextJNDI,JPAServiceManagerProperties props) throws ServiceException{
		this(persistenceUnitName,jndiName, contextJNDI, props, null);
	}
	public JPAServiceManager(String persistenceUnitName,String jndiName, Properties contextJNDI,ServiceManagerProperties serviceManagerProperties,Logger alog) throws ServiceException{
		this(persistenceUnitName,jndiName, contextJNDI, new JPAServiceManagerProperties(serviceManagerProperties), alog);
	}
	public JPAServiceManager(String persistenceUnitName,String jndiName, Properties contextJNDI,JPAServiceManagerProperties props,Logger alog) throws ServiceException{
		
		if(alog==null){
			this.log = Logger.getLogger(JPAServiceManager.class);
		}
		else
			this.log = alog;
		
		try{
			
			this.persistenceUnitName = persistenceUnitName;
			this.jpaProperties = props;
			
			GestoreJNDI gestoreJNDI = new GestoreJNDI(contextJNDI);
			this.datasource = (DataSource) gestoreJNDI.lookup(jndiName);
			if(this.datasource == null){
				throw new Exception("Datasource is null");
			}
			
			this.log.debug("Create JPA Vendor adapter");
			HibernateJpaVendorAdapter hjva = new HibernateJpaVendorAdapter();
			hjva.setGenerateDdl(props.isGenerateDdl());
			hjva.setShowSql(props.isShowSql());
			this.log.debug("JPA Vendor adapter created");

			this.log.debug("LocalContainerEntityManagerFactoryBean ...");
			LocalContainerEntityManagerFactoryBean lcemfb = new LocalContainerEntityManagerFactoryBean();
			lcemfb.setDataSource(this.datasource);
			lcemfb.setJpaVendorAdapter(hjva);
			lcemfb.setPersistenceUnitName(this.persistenceUnitName);
			PersistenceUnitPostProcessor pupp = new JPAPersistenceUnitePostProcessor();
			PersistenceUnitPostProcessor[] pupptable = { pupp };
			lcemfb.setPersistenceUnitPostProcessors(pupptable);
			lcemfb.afterPropertiesSet();
			this.log.debug("LocalContainerEntityManagerFactoryBean created ...");

			JpaTransactionManager transactionManager = new JpaTransactionManager();
			transactionManager.setEntityManagerFactory(lcemfb.getObject());
			transactionManager.setDataSource(this.datasource);
			transactionManager.afterPropertiesSet();

			this.emf = lcemfb.getObject();
			if(this.emf==null){
				throw new Exception("Entity Manager Factory is not initialized");
			}
			this.em = this.emf.createEntityManager();
			if(this.em==null){
				throw new Exception("Entity Manager is not initialized");
			}
			
		} catch(Exception e) {
			this.log.error("Initialization failure: "+e.getMessage(),e);
			throw new ServiceException("Initialization failure: "+e.getMessage(),e);
		}
	}
	
	public JPAServiceManager(String persistenceUnitName,DataSource ds,ServiceManagerProperties serviceManagerProperties) throws ServiceException{
		this(persistenceUnitName,ds, new JPAServiceManagerProperties(serviceManagerProperties), null);
	}
	public JPAServiceManager(String persistenceUnitName,DataSource ds,JPAServiceManagerProperties props) throws ServiceException{
		this(persistenceUnitName,ds, props, null);
	}
	public JPAServiceManager(String persistenceUnitName,DataSource ds,ServiceManagerProperties serviceManagerProperties,Logger alog) throws ServiceException{
		this(persistenceUnitName,ds, new JPAServiceManagerProperties(serviceManagerProperties), alog);
	}
	public JPAServiceManager(String persistenceUnitName,DataSource ds,JPAServiceManagerProperties props,Logger alog) throws ServiceException{
		
		if(alog==null){
			this.log = Logger.getLogger(JPAServiceManager.class);
		}else
			this.log = alog;
		
		try{
			
			this.persistenceUnitName = persistenceUnitName;
			this.jpaProperties = props;
			
			this.datasource = ds;
			if(this.datasource == null){
				throw new Exception("Datasource is null");
			}
						
			this.log.debug("Create JPA Vendor adapter");
			HibernateJpaVendorAdapter hjva = new HibernateJpaVendorAdapter();
			hjva.setGenerateDdl(props.isGenerateDdl());
			hjva.setShowSql(props.isShowSql());
			this.log.debug("JPA Vendor adapter created");
			
			this.log.debug("LocalContainerEntityManagerFactoryBean ...");
			LocalContainerEntityManagerFactoryBean lcemfb = new LocalContainerEntityManagerFactoryBean();
			lcemfb.setDataSource(this.datasource);
			lcemfb.setJpaVendorAdapter(hjva);
			lcemfb.setPersistenceUnitName(this.persistenceUnitName);
			PersistenceUnitPostProcessor pupp = new JPAPersistenceUnitePostProcessor();
			PersistenceUnitPostProcessor[] pupptable = { pupp };
			lcemfb.setPersistenceUnitPostProcessors(pupptable);
			lcemfb.afterPropertiesSet();
			this.log.debug("LocalContainerEntityManagerFactoryBean created ...");
			
			JpaTransactionManager transactionManager = new JpaTransactionManager();
			transactionManager.setEntityManagerFactory(lcemfb.getObject());
			transactionManager.setDataSource(this.datasource);
			transactionManager.afterPropertiesSet();
			
			
			this.emf = lcemfb.getObject();
			if(this.emf==null){
				throw new Exception("Entity Manager Factory is not initialized");
			}
			this.em = this.emf.createEntityManager();
			if(this.em==null){
				throw new Exception("Entity Manager is not initialized");
			}
						
		} catch(Exception e) {
			this.log.error("Initialization failure: "+e.getMessage(),e);
			throw new ServiceException("Initialization failure: "+e.getMessage(),e);
		}
	}
	
	public JPAServiceManager(LocalContainerEntityManagerFactoryBean localContainerFactory,ServiceManagerProperties serviceManagerProperties) throws ServiceException{
		initLocalContainerEntity(localContainerFactory, new JPAServiceManagerProperties(serviceManagerProperties), null);
	}
	public JPAServiceManager(LocalContainerEntityManagerFactoryBean localContainerFactory,JPAServiceManagerProperties props) throws ServiceException{
		initLocalContainerEntity(localContainerFactory, props, null);
	}
	public JPAServiceManager(LocalContainerEntityManagerFactoryBean localContainerFactory,ServiceManagerProperties serviceManagerProperties,Logger alog) throws ServiceException{
		initLocalContainerEntity(localContainerFactory, new JPAServiceManagerProperties(serviceManagerProperties), alog);
	}
	public JPAServiceManager(LocalContainerEntityManagerFactoryBean localContainerFactory,JPAServiceManagerProperties props,Logger alog) throws ServiceException{
		initLocalContainerEntity(localContainerFactory, props, alog);
	}
	private void initLocalContainerEntity(LocalContainerEntityManagerFactoryBean localContainerFactory,JPAServiceManagerProperties props,Logger alog) throws ServiceException{
		if(alog==null){
			this.log = Logger.getLogger(JPAServiceManager.class);
		}else
			this.log = alog;
		
		try{
			
			if(localContainerFactory==null){
				throw new Exception("Local Container Entity Manager Factory is not initialized");
			}
			
			this.persistenceUnitName = localContainerFactory.getPersistenceUnitName();
			this.jpaProperties = props;
			
			this.datasource = localContainerFactory.getDataSource();
			if(this.datasource == null){
				throw new Exception("Datasource is null");
			}
			
			this.emf = localContainerFactory.getObject();
			if(this.emf==null){
				throw new Exception("Entity Manager Factory is not initialized");
			}
			this.em = this.emf.createEntityManager();
			if(this.em==null){
				throw new Exception("Entity Manager is not initialized");
			}
						
		} catch(Exception e) {
			this.log.error("Initialization failure: "+e.getMessage(),e);
			throw new ServiceException("Initialization failure: "+e.getMessage(),e);
		}
	}
	
	public JPAServiceManager(EntityManagerFactory entityManagerFactory,ServiceManagerProperties serviceManagerProperties) throws ServiceException{
		this(entityManagerFactory,new JPAServiceManagerProperties(serviceManagerProperties),null);
	}
	public JPAServiceManager(EntityManagerFactory entityManagerFactory,JPAServiceManagerProperties props) throws ServiceException{
		this(entityManagerFactory,props,null);
	}
	public JPAServiceManager(EntityManagerFactory entityManagerFactory,ServiceManagerProperties serviceManagerProperties,Logger alog) throws ServiceException{
		this(entityManagerFactory,new JPAServiceManagerProperties(serviceManagerProperties),alog);
	}
	public JPAServiceManager(EntityManagerFactory entityManagerFactory,JPAServiceManagerProperties props,Logger alog) throws ServiceException{
		
		if(alog==null){
			this.log = Logger.getLogger(JPAServiceManager.class);
		}else
			this.log = alog;
		
		try{
			
			if(entityManagerFactory==null){
				throw new Exception("Entity Manager Factory is not initialized");
			}
			
			this.jpaProperties = props;
			
			this.emf = entityManagerFactory;
			if(this.emf==null){
				throw new Exception("Entity Manager Factory is not initialized");
			}
			this.em = this.emf.createEntityManager();
			if(this.em==null){
				throw new Exception("Entity Manager is not initialized");
			}
						
		} catch(Exception e) {
			this.log.error("Initialization failure: "+e.getMessage(),e);
			throw new ServiceException("Initialization failure: "+e.getMessage(),e);
		}
	}
	
	public Logger getLog() {
		return this.log;
	}
	public EntityManager getEntityManager() {
		return this.em;
	}
	public JPAServiceManagerProperties getJpaProperties() {
		return this.jpaProperties;
	}

	/* Logger */
	public static void configureDefaultLog4jProperties(IProjectInfo project) throws ServiceException{
		JPALoggerProperties loggerProperties = new JPALoggerProperties(project);
		loggerProperties.configureLog4j();
	}
	public static void configureLog4jProperties(File log4jProperties) throws ServiceException{
		JPALoggerProperties loggerProperties = new JPALoggerProperties(null,log4jProperties);
		loggerProperties.configureLog4j();
	}
}
