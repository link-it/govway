/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2016 Link.it srl (http://link.it). 
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


package org.openspcoop2.pdd.core.jmx;


import java.util.Iterator;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.AttributeNotFoundException;
import javax.management.DynamicMBean;
import javax.management.InvalidAttributeValueException;
import javax.management.JMException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanConstructorInfo;
import javax.management.MBeanException;
import javax.management.MBeanInfo;
import javax.management.MBeanOperationInfo;
import javax.management.MBeanParameterInfo;
import javax.management.NotificationBroadcasterSupport;
import javax.management.ReflectionException;

import org.openspcoop2.core.config.TipoFiltroAbilitazioneServizi;
import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import org.openspcoop2.pdd.core.StatoServiziPdD;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;


/**
 * Implementazione JMX per la gestione dell'autorizzazione
 *   
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class StatoServiziJMXResource extends NotificationBroadcasterSupport implements DynamicMBean {

	/** Nomi proprieta' */
	public final static String COMPONENTE_PD = "Servizio PortaDelegata";
	public final static String COMPONENTE_PD_ABILITAZIONI = "Servizio PortaDelegata (Abilitazioni Puntuali)";
	public final static String COMPONENTE_PD_DISABILITAZIONI = "Servizio PortaDelegata (Disabilitazioni Puntuali)";
	public final static String COMPONENTE_PA = "Servizio PortaApplicativa";
	public final static String COMPONENTE_PA_ABILITAZIONI = "Servizio PortaApplicativa (Abilitazioni Puntuali)";
	public final static String COMPONENTE_PA_DISABILITAZIONI = "Servizio PortaApplicativa (Disabilitazioni Puntuali)";
	public final static String COMPONENTE_IM = "Servizio IntegrationManager";

	/** Nomi metodi */
	public final static String ABILITA_COMPONENTE_PD = "abilitaServizioPortaDelegata"; 
	public final static String ABILITA_COMPONENTE_PA = "abilitaServizioPortaApplicativa"; 
	public final static String ABILITA_COMPONENTE_IM = "abilitaServizioIntegrationManager"; 
	public final static String ABILITA_SERVIZI = "abilitazioneCompletaServizi";

	public final static String DISABILITA_COMPONENTE_PD = "disabilitaServizioPortaDelegata"; 
	public final static String DISABILITA_COMPONENTE_PA = "disabilitaServizioPortaApplicativa"; 
	public final static String DISABILITA_COMPONENTE_IM = "disabilitaServizioIntegrationManager"; 
	public final static String DISABILITA_SERVIZI = "disabilitazioneCompletaServizi";

	public final static String ABILITA_ADD_FILTRO_ABILITAZIONE_PD = "addFiltroAbilitazioneServizioPortaDelegata";
	public final static String ABILITA_ADD_FILTRO_DISABILITAZIONE_PD = "addFiltroDisabilitazioneServizioPortaDelegata";
	public final static String ABILITA_ADD_FILTRO_ABILITAZIONE_PA = "addFiltroAbilitazioneServizioPortaApplicativa";
	public final static String ABILITA_ADD_FILTRO_DISABILITAZIONE_PA = "addFiltroDisabilitazioneServizioPortaApplicativa";

	public final static String ABILITA_REMOVE_FILTRO_ABILITAZIONE_PD = "removeFiltroAbilitazioneServizioPortaDelegata";
	public final static String ABILITA_REMOVE_FILTRO_DISABILITAZIONE_PD = "removeFiltroDisabilitazioneServizioPortaDelegata";
	public final static String ABILITA_REMOVE_FILTRO_ABILITAZIONE_PA = "removeFiltroAbilitazioneServizioPortaApplicativa";
	public final static String ABILITA_REMOVE_FILTRO_DISABILITAZIONE_PA = "removeFiltroDisabilitazioneServizioPortaApplicativa";



	/** Attributi */
	private StatoFunzionalita componentePD = CostantiConfigurazione.ABILITATO;
	private String componentePD_abilitazioniPuntuali = "";
	private String componentePD_disabilitazioniPuntuali = "";

	private StatoFunzionalita componentePA = CostantiConfigurazione.ABILITATO;
	private String componentePA_abilitazioniPuntuali = "";
	private String componentePA_disabilitazioniPuntuali = "";

	private StatoFunzionalita componenteIM = CostantiConfigurazione.ABILITATO;

	/** getAttribute */
	@Override
	public Object getAttribute(String attributeName) throws AttributeNotFoundException,MBeanException,ReflectionException{

		if( (attributeName==null) || (attributeName.equals("")) )
			throw new IllegalArgumentException("Il nome dell'attributo e' nullo o vuoto");

		if(attributeName.equals(StatoServiziJMXResource.COMPONENTE_PD))
			return this.componentePD;
		else if(attributeName.equals(StatoServiziJMXResource.COMPONENTE_PD_ABILITAZIONI))
			return this.componentePD_abilitazioniPuntuali;
		else if(attributeName.equals(StatoServiziJMXResource.COMPONENTE_PD_DISABILITAZIONI))
			return this.componentePD_disabilitazioniPuntuali;

		else if(attributeName.equals(StatoServiziJMXResource.COMPONENTE_PA))
			return this.componentePA;
		else if(attributeName.equals(StatoServiziJMXResource.COMPONENTE_PA_ABILITAZIONI))
			return this.componentePA_abilitazioniPuntuali;
		else if(attributeName.equals(StatoServiziJMXResource.COMPONENTE_PA_DISABILITAZIONI))
			return this.componentePA_disabilitazioniPuntuali;

		else if(attributeName.equals(StatoServiziJMXResource.COMPONENTE_IM))
			return this.componenteIM;

		throw new AttributeNotFoundException("Attributo "+attributeName+" non trovato");
	}

	/** getAttributes */
	@Override
	public AttributeList getAttributes(String [] attributesNames){

		if(attributesNames==null)
			throw new IllegalArgumentException("Array nullo");

		AttributeList list = new AttributeList();
		for (int i=0; i<attributesNames.length; i++){
			try{
				list.add(new Attribute(attributesNames[i],getAttribute(attributesNames[i])));
			}catch(JMException ex){}
		}
		return list;
	}

	/** setAttribute */
	@Override
	public void setAttribute(Attribute attribute) throws AttributeNotFoundException, InvalidAttributeValueException, MBeanException, ReflectionException{

		if( attribute==null )
			throw new IllegalArgumentException("Il nome dell'attributo e' nullo");

		try{

			if(attribute.getName().equals(StatoServiziJMXResource.COMPONENTE_PD)){
				String v = (String) attribute.getValue();
				if(CostantiConfigurazione.ABILITATO.equals(v)){
					this.abilitaServizioPortaDelegata();
				}else{
					this.disabilitaServizioPortaDelegata();
				}
			}			
			else if(attribute.getName().equals(StatoServiziJMXResource.COMPONENTE_PA)){
				String v = (String) attribute.getValue();
				if(CostantiConfigurazione.ABILITATO.equals(v)){
					this.abilitaServizioPortaApplicativa();
				}else{
					this.disabilitaServizioPortaApplicativa();
				}
			}

			else if(attribute.getName().equals(StatoServiziJMXResource.COMPONENTE_IM)){
				String v = (String) attribute.getValue();
				if(CostantiConfigurazione.ABILITATO.equals(v)){
					this.abilitaServizioIntegrationManager();
				}else{
					this.disabilitaServizioIntegrationManager();
				}
			}

			else if(attribute.getName().equals(StatoServiziJMXResource.COMPONENTE_PD_ABILITAZIONI)){
				// nothing DO
			}
			else if(attribute.getName().equals(StatoServiziJMXResource.COMPONENTE_PD_DISABILITAZIONI)){
				// nothing DO
			}
			else if(attribute.getName().equals(StatoServiziJMXResource.COMPONENTE_PA_ABILITAZIONI)){
				// nothing DO
			}
			else if(attribute.getName().equals(StatoServiziJMXResource.COMPONENTE_PA_DISABILITAZIONI)){
				// nothing DO
			}

			else
				throw new AttributeNotFoundException("Attributo "+attribute.getName()+" non trovato");

		}catch(ClassCastException ce){
			throw new InvalidAttributeValueException("il tipo "+attribute.getValue().getClass()+" dell'attributo "+attribute.getName()+" non e' valido");
		}catch(JMException j){
			throw new MBeanException(j);
		}

	}

	/** setAttributes */
	@Override
	public AttributeList setAttributes(AttributeList list){

		if(list==null)
			throw new IllegalArgumentException("Lista degli attributi e' nulla");

		AttributeList ret = new AttributeList();
		Iterator<?> it = ret.iterator();

		while(it.hasNext()){
			try{
				Attribute attribute = (Attribute) it.next();
				setAttribute(attribute);
				ret.add(attribute);
			}catch(JMException ex){}
		}

		return ret;

	}

	/** invoke */
	@Override
	public Object invoke(String actionName, Object[]params, String[]signature) throws MBeanException,ReflectionException{

		if( (actionName==null) || (actionName.equals("")) )
			throw new IllegalArgumentException("Nessuna operazione definita");

		if(actionName.equals(StatoServiziJMXResource.ABILITA_COMPONENTE_PD)){
			return this.abilitaServizioPortaDelegata();
		}
		else if(actionName.equals(StatoServiziJMXResource.ABILITA_COMPONENTE_PA)){
			return this.abilitaServizioPortaApplicativa();
		}
		else if(actionName.equals(StatoServiziJMXResource.ABILITA_COMPONENTE_IM)){
			return this.abilitaServizioIntegrationManager();
		}
		else if(actionName.equals(StatoServiziJMXResource.ABILITA_SERVIZI)){
			return this.abilitazioneCompletaServizi();
		}

		else if(actionName.equals(StatoServiziJMXResource.DISABILITA_COMPONENTE_PD)){
			return this.disabilitaServizioPortaDelegata();
		}
		else if(actionName.equals(StatoServiziJMXResource.DISABILITA_COMPONENTE_PA)){
			return this.disabilitaServizioPortaApplicativa();
		}
		else if(actionName.equals(StatoServiziJMXResource.DISABILITA_COMPONENTE_IM)){
			return this.disabilitaServizioIntegrationManager();
		}
		else if(actionName.equals(StatoServiziJMXResource.DISABILITA_SERVIZI)){
			return this.disabilitazioneCompletaServizi();
		}

		else if(actionName.equals(StatoServiziJMXResource.ABILITA_ADD_FILTRO_ABILITAZIONE_PD)){
			if(params.length != 9)
				throw new MBeanException(new Exception("["+StatoServiziJMXResource.ABILITA_ADD_FILTRO_ABILITAZIONE_PD+"] Lunghezza parametri non corretta: "+params.length));
			return addFiltroAbilitazioneServizioPortaDelegata(getTipoFiltroAbilitazioneServizi(params));
		}
		else if(actionName.equals(StatoServiziJMXResource.ABILITA_ADD_FILTRO_DISABILITAZIONE_PD)){
			if(params.length != 9)
				throw new MBeanException(new Exception("["+StatoServiziJMXResource.ABILITA_ADD_FILTRO_DISABILITAZIONE_PD+"] Lunghezza parametri non corretta: "+params.length));
			return addFiltroDisabilitazioneServizioPortaDelegata(getTipoFiltroAbilitazioneServizi(params));
		}
		else if(actionName.equals(StatoServiziJMXResource.ABILITA_REMOVE_FILTRO_ABILITAZIONE_PD)){
			if(params.length != 9)
				throw new MBeanException(new Exception("["+StatoServiziJMXResource.ABILITA_REMOVE_FILTRO_ABILITAZIONE_PD+"] Lunghezza parametri non corretta: "+params.length));
			return removeFiltroAbilitazioneServizioPortaDelegata(getTipoFiltroAbilitazioneServizi(params));
		}
		else if(actionName.equals(StatoServiziJMXResource.ABILITA_REMOVE_FILTRO_DISABILITAZIONE_PD)){
			if(params.length != 9)
				throw new MBeanException(new Exception("["+StatoServiziJMXResource.ABILITA_REMOVE_FILTRO_DISABILITAZIONE_PD+"] Lunghezza parametri non corretta: "+params.length));
			return removeFiltroDisabilitazioneServizioPortaDelegata(getTipoFiltroAbilitazioneServizi(params));
		}

		else if(actionName.equals(StatoServiziJMXResource.ABILITA_ADD_FILTRO_ABILITAZIONE_PA)){
			if(params.length != 9)
				throw new MBeanException(new Exception("["+StatoServiziJMXResource.ABILITA_ADD_FILTRO_ABILITAZIONE_PA+"] Lunghezza parametri non corretta: "+params.length));
			return addFiltroAbilitazioneServizioPortaApplicativa(getTipoFiltroAbilitazioneServizi(params));
		}
		else if(actionName.equals(StatoServiziJMXResource.ABILITA_ADD_FILTRO_DISABILITAZIONE_PA)){
			if(params.length != 9)
				throw new MBeanException(new Exception("["+StatoServiziJMXResource.ABILITA_ADD_FILTRO_DISABILITAZIONE_PA+"] Lunghezza parametri non corretta: "+params.length));
			return addFiltroDisabilitazioneServizioPortaApplicativa(getTipoFiltroAbilitazioneServizi(params));
		}
		else if(actionName.equals(StatoServiziJMXResource.ABILITA_REMOVE_FILTRO_ABILITAZIONE_PA)){
			if(params.length != 9)
				throw new MBeanException(new Exception("["+StatoServiziJMXResource.ABILITA_REMOVE_FILTRO_ABILITAZIONE_PA+"] Lunghezza parametri non corretta: "+params.length));
			return removeFiltroAbilitazioneServizioPortaApplicativa(getTipoFiltroAbilitazioneServizi(params));
		}
		else if(actionName.equals(StatoServiziJMXResource.ABILITA_REMOVE_FILTRO_DISABILITAZIONE_PA)){
			if(params.length != 9)
				throw new MBeanException(new Exception("["+StatoServiziJMXResource.ABILITA_REMOVE_FILTRO_DISABILITAZIONE_PA+"] Lunghezza parametri non corretta: "+params.length));
			return removeFiltroDisabilitazioneServizioPortaApplicativa(getTipoFiltroAbilitazioneServizi(params));
		}

		throw new UnsupportedOperationException("Operazione "+actionName+" sconosciuta");
	}

	private TipoFiltroAbilitazioneServizi getTipoFiltroAbilitazioneServizi(Object [] params){
		TipoFiltroAbilitazioneServizi tipo = new TipoFiltroAbilitazioneServizi();

		int index = 0;

		String tmp = (String) params[index];
		if(tmp!=null && !"".equals(tmp)){
			tipo.setTipoSoggettoFruitore(tmp);
		}
		index++;
		tmp = (String) params[index];
		if(tmp!=null && !"".equals(tmp)){
			tipo.setSoggettoFruitore(tmp);
		}
		index++;
		tmp = (String) params[index];
		if(tmp!=null && !"".equals(tmp)){
			tipo.setIdentificativoPortaFruitore(tmp);
		}

		index++;
		tmp = (String) params[index];
		if(tmp!=null && !"".equals(tmp)){
			tipo.setTipoSoggettoErogatore(tmp);
		}
		index++;
		tmp = (String) params[index];
		if(tmp!=null && !"".equals(tmp)){
			tipo.setSoggettoErogatore(tmp);
		}
		index++;
		tmp = (String) params[index];
		if(tmp!=null && !"".equals(tmp)){
			tipo.setIdentificativoPortaErogatore(tmp);
		}

		index++;
		tmp = (String) params[index];
		if(tmp!=null && !"".equals(tmp)){
			tipo.setTipoServizio(tmp);
		}
		index++;
		tmp = (String) params[index];
		if(tmp!=null && !"".equals(tmp)){
			tipo.setServizio(tmp);
		}

		index++;
		tmp = (String) params[index];
		if(tmp!=null && !"".equals(tmp)){
			tipo.setAzione(tmp);
		}

		return tipo;
	}

	/* MBean info */
	@Override
	public MBeanInfo getMBeanInfo(){

		// Descrizione della classe nel MBean
		String className = this.getClass().getName();
		String description = "Stato dei servizi attivi sulla Porta di Dominio";

		// MetaData per gli attributi
		MBeanAttributeInfo componentePD_VAR 
		= new MBeanAttributeInfo(StatoServiziJMXResource.COMPONENTE_PD,String.class.getName(),
				"Indicazione se e' il servizio PortaDelegata risulta abilitato",
				JMXUtils.JMX_ATTRIBUTE_READABLE,JMXUtils.JMX_ATTRIBUTE_WRITABLE,!JMXUtils.JMX_ATTRIBUTE_IS_GETTER);
		MBeanAttributeInfo componentePD_abilitazioni_VAR 
		= new MBeanAttributeInfo(StatoServiziJMXResource.COMPONENTE_PD_ABILITAZIONI,String.class.getName(),
				"Filtri puntuali che abilitano l'utilizzo del servizio PortaDelegata",
				JMXUtils.JMX_ATTRIBUTE_READABLE,!JMXUtils.JMX_ATTRIBUTE_WRITABLE,!JMXUtils.JMX_ATTRIBUTE_IS_GETTER);
		MBeanAttributeInfo componentePD_disabilitazioni_VAR 
		= new MBeanAttributeInfo(StatoServiziJMXResource.COMPONENTE_PD_DISABILITAZIONI,String.class.getName(),
				"Filtri puntuali che disabilitano l'utilizzo del servizio PortaDelegata",
				JMXUtils.JMX_ATTRIBUTE_READABLE,!JMXUtils.JMX_ATTRIBUTE_WRITABLE,!JMXUtils.JMX_ATTRIBUTE_IS_GETTER);

		MBeanAttributeInfo componentePA_VAR 
		= new MBeanAttributeInfo(StatoServiziJMXResource.COMPONENTE_PA,String.class.getName(),
				"Indicazione se e' il servizio PortaApplicativa risulta abilitato",
				JMXUtils.JMX_ATTRIBUTE_READABLE,JMXUtils.JMX_ATTRIBUTE_WRITABLE,!JMXUtils.JMX_ATTRIBUTE_IS_GETTER);
		MBeanAttributeInfo componentePA_abilitazioni_VAR 
		= new MBeanAttributeInfo(StatoServiziJMXResource.COMPONENTE_PA_ABILITAZIONI,String.class.getName(),
				"Filtri puntuali che abilitano l'utilizzo del servizio PortaApplicativa",
				JMXUtils.JMX_ATTRIBUTE_READABLE,!JMXUtils.JMX_ATTRIBUTE_WRITABLE,!JMXUtils.JMX_ATTRIBUTE_IS_GETTER);
		MBeanAttributeInfo componentePA_disabilitazioni_VAR 
		= new MBeanAttributeInfo(StatoServiziJMXResource.COMPONENTE_PA_DISABILITAZIONI,String.class.getName(),
				"Filtri puntuali che disabilitano l'utilizzo del servizio PortaApplicativa",
				JMXUtils.JMX_ATTRIBUTE_READABLE,!JMXUtils.JMX_ATTRIBUTE_WRITABLE,!JMXUtils.JMX_ATTRIBUTE_IS_GETTER);

		MBeanAttributeInfo componenteIM_VAR 
		= new MBeanAttributeInfo(StatoServiziJMXResource.COMPONENTE_IM,String.class.getName(),
				"Indicazione se e' il servizio IntegrationManager risulta abilitato",
				JMXUtils.JMX_ATTRIBUTE_READABLE,JMXUtils.JMX_ATTRIBUTE_WRITABLE,!JMXUtils.JMX_ATTRIBUTE_IS_GETTER);


		// MetaData per le operazioni
		MBeanOperationInfo abilitaPD_OP 
		= new MBeanOperationInfo(StatoServiziJMXResource.ABILITA_COMPONENTE_PD,"Abilita il servizio PortaDelegata",
				null,
				String.class.getName(),
				MBeanOperationInfo.ACTION);
		MBeanOperationInfo abilitaPA_OP 
		= new MBeanOperationInfo(StatoServiziJMXResource.ABILITA_COMPONENTE_PA,"Abilita il servizio PortaApplicativa",
				null,
				String.class.getName(),
				MBeanOperationInfo.ACTION);
		MBeanOperationInfo abilitaIM_OP 
		= new MBeanOperationInfo(StatoServiziJMXResource.ABILITA_COMPONENTE_IM,"Abilita il servizio IntegrationManager",
				null,
				String.class.getName(),
				MBeanOperationInfo.ACTION);
		MBeanOperationInfo abilitazioneCompleta_OP 
		= new MBeanOperationInfo(StatoServiziJMXResource.ABILITA_SERVIZI,"Abilitazione completa di tutti i servizi della Porta di Dominio",
				null,
				String.class.getName(),
				MBeanOperationInfo.ACTION);

		MBeanOperationInfo disabilitaPD_OP 
		= new MBeanOperationInfo(StatoServiziJMXResource.DISABILITA_COMPONENTE_PD,"Disabilita il servizio PortaDelegata",
				null,
				String.class.getName(),
				MBeanOperationInfo.ACTION);
		MBeanOperationInfo disabilitaPA_OP 
		= new MBeanOperationInfo(StatoServiziJMXResource.DISABILITA_COMPONENTE_PA,"Disabilita il servizio PortaApplicativa",
				null,
				String.class.getName(),
				MBeanOperationInfo.ACTION);
		MBeanOperationInfo disabilitaIM_OP 
		= new MBeanOperationInfo(StatoServiziJMXResource.DISABILITA_COMPONENTE_IM,"Disabilita il servizio IntegrationManager",
				null,
				String.class.getName(),
				MBeanOperationInfo.ACTION);
		MBeanOperationInfo disabilitazioneCompleta_OP 
		= new MBeanOperationInfo(StatoServiziJMXResource.DISABILITA_SERVIZI,"Disabilitazione completa di tutti i servizi della Porta di Dominio",
				null,
				String.class.getName(),
				MBeanOperationInfo.ACTION);

		MBeanOperationInfo componentePD_addFiltroAbilitazione_VAR 
		= new MBeanOperationInfo(StatoServiziJMXResource.ABILITA_ADD_FILTRO_ABILITAZIONE_PD,"Aggiunge un filtro puntuale che abilita l'utilizzo del servizio PortaDelegata",
				getMBeanParameterInfoFiltroAbilitazioni(),
				String.class.getName(),
				MBeanOperationInfo.ACTION);		
		MBeanOperationInfo componentePD_addFiltroDisabilitazione_VAR 
		= new MBeanOperationInfo(StatoServiziJMXResource.ABILITA_ADD_FILTRO_DISABILITAZIONE_PD,"Aggiunge un filtro puntuale che disabilita l'utilizzo del servizio PortaDelegata",
				getMBeanParameterInfoFiltroAbilitazioni(),
				String.class.getName(),
				MBeanOperationInfo.ACTION);	
		MBeanOperationInfo componentePD_removeFiltroAbilitazione_VAR 
		= new MBeanOperationInfo(StatoServiziJMXResource.ABILITA_REMOVE_FILTRO_ABILITAZIONE_PD,"Rimuove un filtro puntuale che abilita l'utilizzo del servizio PortaDelegata",
				getMBeanParameterInfoFiltroAbilitazioni(),
				String.class.getName(),
				MBeanOperationInfo.ACTION);		
		MBeanOperationInfo componentePD_removeFiltroDisabilitazione_VAR 
		= new MBeanOperationInfo(StatoServiziJMXResource.ABILITA_REMOVE_FILTRO_DISABILITAZIONE_PD,"Rimuove un filtro puntuale che disabilita l'utilizzo del servizio PortaDelegata",
				getMBeanParameterInfoFiltroAbilitazioni(),
				String.class.getName(),
				MBeanOperationInfo.ACTION);	

		MBeanOperationInfo componentePA_addFiltroAbilitazione_VAR 
		= new MBeanOperationInfo(StatoServiziJMXResource.ABILITA_ADD_FILTRO_ABILITAZIONE_PA,"Aggiunge un filtro puntuale che abilita l'utilizzo del servizio PortaApplicativa",
				getMBeanParameterInfoFiltroAbilitazioni(),
				String.class.getName(),
				MBeanOperationInfo.ACTION);		
		MBeanOperationInfo componentePA_addFiltroDisabilitazione_VAR 
		= new MBeanOperationInfo(StatoServiziJMXResource.ABILITA_ADD_FILTRO_DISABILITAZIONE_PA,"Aggiunge un filtro puntuale che disabilita l'utilizzo del servizio PortaApplicativa",
				getMBeanParameterInfoFiltroAbilitazioni(),
				String.class.getName(),
				MBeanOperationInfo.ACTION);	
		MBeanOperationInfo componentePA_removeFiltroAbilitazione_VAR 
		= new MBeanOperationInfo(StatoServiziJMXResource.ABILITA_REMOVE_FILTRO_ABILITAZIONE_PA,"Rimuove un filtro puntuale che abilita l'utilizzo del servizio PortaApplicativa",
				getMBeanParameterInfoFiltroAbilitazioni(),
				String.class.getName(),
				MBeanOperationInfo.ACTION);		
		MBeanOperationInfo componentePA_removeFiltroDisabilitazione_VAR 
		= new MBeanOperationInfo(StatoServiziJMXResource.ABILITA_REMOVE_FILTRO_DISABILITAZIONE_PA,"Rimuove un filtro puntuale che disabilita l'utilizzo del servizio PortaApplicativa",
				getMBeanParameterInfoFiltroAbilitazioni(),
				String.class.getName(),
				MBeanOperationInfo.ACTION);	


		// Mbean costruttore
		MBeanConstructorInfo defaultConstructor = new MBeanConstructorInfo("Default Constructor","Crea e inizializza una nuova istanza del MBean",null);

		// Lista attributi
		MBeanAttributeInfo[] attributes = new MBeanAttributeInfo[]{componentePD_VAR,componentePD_abilitazioni_VAR,componentePD_disabilitazioni_VAR,
				componentePA_VAR,componentePA_abilitazioni_VAR,componentePA_disabilitazioni_VAR,
				componenteIM_VAR};

		// Lista Costruttori
		MBeanConstructorInfo[] constructors = new MBeanConstructorInfo[]{defaultConstructor};

		// Lista operazioni
		MBeanOperationInfo[] operations = new MBeanOperationInfo[]{abilitaPD_OP,abilitaPA_OP,abilitaIM_OP,abilitazioneCompleta_OP,
				disabilitaPD_OP,disabilitaPA_OP,disabilitaIM_OP,disabilitazioneCompleta_OP,
				componentePD_addFiltroAbilitazione_VAR,componentePD_addFiltroDisabilitazione_VAR,
				componentePD_removeFiltroAbilitazione_VAR,componentePD_removeFiltroDisabilitazione_VAR,
				componentePA_addFiltroAbilitazione_VAR,componentePA_addFiltroDisabilitazione_VAR,
				componentePA_removeFiltroAbilitazione_VAR,componentePA_removeFiltroDisabilitazione_VAR};

		return new MBeanInfo(className,description,attributes,constructors,operations,null);
	}


	private MBeanParameterInfo[] getMBeanParameterInfoFiltroAbilitazioni(){
		MBeanParameterInfo[] m = new MBeanParameterInfo[]{

				new MBeanParameterInfo("tipoSoggettoFruitore",String.class.getName(),"Tipo del soggetto fruitore"),
				new MBeanParameterInfo("nomeSoggettoFruitore",String.class.getName(),"Nome del soggetto fruitore"),
				new MBeanParameterInfo("identificativoPortaSoggettoFruitore",String.class.getName(),"Identificativo della PdD del soggetto fruitore"),

				new MBeanParameterInfo("tipoSoggettoErogatore",String.class.getName(),"Tipo del soggetto erogatore"),
				new MBeanParameterInfo("nomeSoggettoErogatore",String.class.getName(),"Nome del soggetto erogatore"),
				new MBeanParameterInfo("identificativoPortaSoggettoErogatore",String.class.getName(),"Identificativo della PdD del soggetto erogatore"),

				new MBeanParameterInfo("tipoServizio",String.class.getName(),"Tipo del servizio"),
				new MBeanParameterInfo("nomeServizio",String.class.getName(),"Nome del servizio"),

				new MBeanParameterInfo("azione",String.class.getName(),"azione")
		};
		return m;
	}


	/* Costruttore */
	public StatoServiziJMXResource() throws Exception{

		// Configurazione
		this.componentePD = StatoServiziPdD.isPDServiceActive() ? CostantiConfigurazione.ABILITATO : CostantiConfigurazione.DISABILITATO;
		this.componentePA = StatoServiziPdD.isPAServiceActive() ? CostantiConfigurazione.ABILITATO : CostantiConfigurazione.DISABILITATO;
		this.componenteIM = StatoServiziPdD.isIMServiceActive() ? CostantiConfigurazione.ABILITATO : CostantiConfigurazione.DISABILITATO;
		
		this.componentePD_abilitazioniPuntuali = visualizzaFiltriAbilitazioniServizioPortaDelegata();
		this.componentePD_disabilitazioniPuntuali = visualizzaFiltriDisabilitazioniServizioPortaDelegata();
		
		this.componentePA_abilitazioniPuntuali = visualizzaFiltriAbilitazioniServizioPortaApplicativa();
		this.componentePA_disabilitazioniPuntuali = visualizzaFiltriDisabilitazioniServizioPortaApplicativa();
		
	}

	/* Metodi di management JMX */

	public String abilitaServizioPortaDelegata(){
		if(CostantiConfigurazione.ABILITATO.equals(this.componentePD)){
			return JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+" il servizio PortaDelegata risulta già abilitato";
		}else{
			if(StatoServiziPdD.isPDServiceActive()){
				return JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+" il servizio PortaDelegata risulta già abilitato";
			}else{
				try{
					StatoServiziPdD.setPDServiceActive(true);
					this.componentePD = CostantiConfigurazione.ABILITATO;
					return "Abilitazione del servizio PortaDelegata effettuata con successo";
				}catch(Throwable e){
					OpenSPCoop2Logger.getLoggerOpenSPCoopCore().error("Abilitazione del servizio PortaDelegata non riuscita: "+e.getMessage(),e);
					return JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+" sistema non disponibile";
				}
			}
		}
	}
	public String abilitaServizioPortaApplicativa(){
		if(CostantiConfigurazione.ABILITATO.equals(this.componentePA)){
			return JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+" il servizio PortaApplicativa risulta già abilitato";
		}else{
			if(StatoServiziPdD.isPAServiceActive()){
				return JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+" il servizio PortaApplicativa risulta già abilitato";
			}else{
				try{
					StatoServiziPdD.setPAServiceActive(true);
					this.componentePA = CostantiConfigurazione.ABILITATO;
					return "Abilitazione del servizio PortaApplicativa effettuata con successo";
				}catch(Throwable e){
					OpenSPCoop2Logger.getLoggerOpenSPCoopCore().error("Abilitazione del servizio PortaApplicativa non riuscita: "+e.getMessage(),e);
					return JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+" sistema non disponibile";
				}
			}
		}
	}
	public String abilitaServizioIntegrationManager(){
		if(CostantiConfigurazione.ABILITATO.equals(this.componenteIM)){
			return JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+" il servizio IntegrationManager risulta già abilitato";
		}else{
			if(StatoServiziPdD.isIMServiceActive()){
				return JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+" il servizio IntegrationManager risulta già abilitato";
			}else{
				try{
					StatoServiziPdD.setIMServiceActive(true);
					this.componenteIM = CostantiConfigurazione.ABILITATO;
					return "Abilitazione del servizio IntegrationManager effettuata con successo";
				}catch(Throwable e){
					OpenSPCoop2Logger.getLoggerOpenSPCoopCore().error("Abilitazione del servizio IntegrationManager non riuscita: "+e.getMessage(),e);
					return JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+" sistema non disponibile";
				}
			}
		}
	}
	public String abilitazioneCompletaServizi(){
		StringBuffer bf = new StringBuffer();	
		bf.append(this.abilitaServizioPortaDelegata());	
		bf.append("; ");
		bf.append(this.abilitaServizioPortaApplicativa());	
		bf.append("; ");
		bf.append(this.abilitaServizioIntegrationManager());	
		return bf.toString();
	}




	public String disabilitaServizioPortaDelegata(){
		if(CostantiConfigurazione.DISABILITATO.equals(this.componentePD)){
			return JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+" il servizio PortaDelegata risulta già disabilitato";
		}else{
			if(!StatoServiziPdD.isPDServiceActive()){
				return JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+" il servizio PortaDelegata risulta già disabilitato";
			}else{
				try{
					StatoServiziPdD.setPDServiceActive(false);
					this.componentePD = CostantiConfigurazione.DISABILITATO;
					return "Disabilitazione del servizio PortaDelegata effettuata con successo";
				}catch(Throwable e){
					OpenSPCoop2Logger.getLoggerOpenSPCoopCore().error("Disabilitazione del servizio PortaDelegata non riuscita: "+e.getMessage(),e);
					return JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+" sistema non disponibile";
				}
			}
		}
	}
	public String disabilitaServizioPortaApplicativa(){
		if(CostantiConfigurazione.DISABILITATO.equals(this.componentePA)){
			return JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+" il servizio PortaApplicativa risulta già disabilitato";
		}else{
			if(!StatoServiziPdD.isPAServiceActive()){
				return JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+" il servizio PortaApplicativa risulta già disabilitato";
			}else{
				try{
					StatoServiziPdD.setPAServiceActive(false);
					this.componentePA = CostantiConfigurazione.DISABILITATO;
					return "Disabilitazione del servizio PortaApplicativa effettuata con successo";
				}catch(Throwable e){
					OpenSPCoop2Logger.getLoggerOpenSPCoopCore().error("Disabilitazione del servizio PortaApplicativa non riuscita: "+e.getMessage(),e);
					return JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+" sistema non disponibile";
				}
			}
		}
	}
	public String disabilitaServizioIntegrationManager(){
		if(CostantiConfigurazione.DISABILITATO.equals(this.componenteIM)){
			return JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+" il servizio IntegrationManager risulta già disabilitato";
		}else{
			if(!StatoServiziPdD.isIMServiceActive()){
				return JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+" il servizio IntegrationManager risulta già disabilitato";
			}else{
				try{
					StatoServiziPdD.setIMServiceActive(false);
					this.componenteIM = CostantiConfigurazione.DISABILITATO;
					return "Disabilitazione del servizio IntegrationManager effettuata con successo";
				}catch(Throwable e){
					OpenSPCoop2Logger.getLoggerOpenSPCoopCore().error("Disabilitazione del servizio IntegrationManager non riuscita: "+e.getMessage(),e);
					return JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+" sistema non disponibile";
				}
			}
		}
	}
	public String disabilitazioneCompletaServizi(){
		StringBuffer bf = new StringBuffer();	
		bf.append(this.disabilitaServizioPortaDelegata());	
		bf.append("; ");
		bf.append(this.disabilitaServizioPortaApplicativa());	
		bf.append("; ");
		bf.append(this.disabilitaServizioIntegrationManager());	
		return bf.toString();
	}





	public String visualizzaFiltriAbilitazioniServizioPortaDelegata(){
		try{
			return StatoServiziPdD.getPDServiceFiltriAbilitazioneAttivi();
		}catch(Throwable e){
			OpenSPCoop2Logger.getLoggerOpenSPCoopCore().error("Operazione visualizzaFiltriAbilitazioniServizioPortaDelegata non riuscita: "+e.getMessage(),e);
			return JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+" sistema non disponibile";
		}
	}
	public String visualizzaFiltriDisabilitazioniServizioPortaDelegata(){
		try{
			return StatoServiziPdD.getPDServiceFiltriDisabilitazioneAttivi();
		}catch(Throwable e){
			OpenSPCoop2Logger.getLoggerOpenSPCoopCore().error("Operazione visualizzaFiltriDisabilitazioniServizioPortaDelegata non riuscita: "+e.getMessage(),e);
			return JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+" sistema non disponibile";
		}
	}
	public String addFiltroAbilitazioneServizioPortaDelegata(TipoFiltroAbilitazioneServizi tipo){
		try{
			StatoServiziPdD.addFiltroAbilitazionePD(tipo);
			this.componentePD_abilitazioniPuntuali = visualizzaFiltriAbilitazioniServizioPortaDelegata();
			return "Operazione effettuata con successo";
		}catch(Throwable e){
			OpenSPCoop2Logger.getLoggerOpenSPCoopCore().error("Operazione addFiltroAbilitazioneServizioPortaDelegata non riuscita: "+e.getMessage(),e);
			return JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+" sistema non disponibile";
		}
	}
	public String addFiltroDisabilitazioneServizioPortaDelegata(TipoFiltroAbilitazioneServizi tipo){
		try{
			StatoServiziPdD.addFiltroDisabilitazionePD(tipo);
			this.componentePD_disabilitazioniPuntuali = visualizzaFiltriDisabilitazioniServizioPortaDelegata();
			return "Operazione effettuata con successo";
		}catch(Throwable e){
			OpenSPCoop2Logger.getLoggerOpenSPCoopCore().error("Operazione addFiltroDisabilitazioneServizioPortaDelegata non riuscita: "+e.getMessage(),e);
			return JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+" sistema non disponibile";
		}
	}
	public String removeFiltroAbilitazioneServizioPortaDelegata(TipoFiltroAbilitazioneServizi tipo){
		try{
			StatoServiziPdD.removeFiltroAbilitazionePD(tipo);
			this.componentePD_abilitazioniPuntuali = visualizzaFiltriAbilitazioniServizioPortaDelegata();
			return "Operazione effettuata con successo";
		}catch(Throwable e){
			OpenSPCoop2Logger.getLoggerOpenSPCoopCore().error("Operazione removeFiltroAbilitazioneServizioPortaDelegata non riuscita: "+e.getMessage(),e);
			return JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+" sistema non disponibile";
		}
	}
	public String removeFiltroDisabilitazioneServizioPortaDelegata(TipoFiltroAbilitazioneServizi tipo){
		try{
			StatoServiziPdD.removeFiltroDisabilitazionePD(tipo);
			this.componentePD_disabilitazioniPuntuali = visualizzaFiltriDisabilitazioniServizioPortaDelegata();
			return "Operazione effettuata con successo";
		}catch(Throwable e){
			OpenSPCoop2Logger.getLoggerOpenSPCoopCore().error("Operazione removeFiltroDisabilitazioneServizioPortaDelegata non riuscita: "+e.getMessage(),e);
			return JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+" sistema non disponibile";
		}
	}

	public String visualizzaFiltriAbilitazioniServizioPortaApplicativa(){
		try{
			return StatoServiziPdD.getPAServiceFiltriAbilitazioneAttivi();
		}catch(Throwable e){
			OpenSPCoop2Logger.getLoggerOpenSPCoopCore().error("Operazione visualizzaFiltriAbilitazioniServizioPortaApplicativa non riuscita: "+e.getMessage(),e);
			return JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+" sistema non disponibile";
		}
	}
	public String visualizzaFiltriDisabilitazioniServizioPortaApplicativa(){
		try{
			return StatoServiziPdD.getPAServiceFiltriDisabilitazioneAttivi();
		}catch(Throwable e){
			OpenSPCoop2Logger.getLoggerOpenSPCoopCore().error("Operazione visualizzaFiltriDisabilitazioniServizioPortaApplicativa non riuscita: "+e.getMessage(),e);
			return JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+" sistema non disponibile";
		}
	}
	public String addFiltroAbilitazioneServizioPortaApplicativa(TipoFiltroAbilitazioneServizi tipo){
		try{
			StatoServiziPdD.addFiltroAbilitazionePA(tipo);
			this.componentePA_abilitazioniPuntuali = visualizzaFiltriAbilitazioniServizioPortaApplicativa();
			return "Operazione effettuata con successo";
		}catch(Throwable e){
			OpenSPCoop2Logger.getLoggerOpenSPCoopCore().error("Operazione addFiltroAbilitazioneServizioPortaApplicativa non riuscita: "+e.getMessage(),e);
			return JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+" sistema non disponibile";
		}
	}
	public String addFiltroDisabilitazioneServizioPortaApplicativa(TipoFiltroAbilitazioneServizi tipo){
		try{
			StatoServiziPdD.addFiltroDisabilitazionePA(tipo);
			this.componentePA_disabilitazioniPuntuali = visualizzaFiltriDisabilitazioniServizioPortaApplicativa();
			return "Operazione effettuata con successo";
		}catch(Throwable e){
			OpenSPCoop2Logger.getLoggerOpenSPCoopCore().error("Operazione addFiltroDisabilitazioneServizioPortaApplicativa non riuscita: "+e.getMessage(),e);
			return JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+" sistema non disponibile";
		}
	}
	public String removeFiltroAbilitazioneServizioPortaApplicativa(TipoFiltroAbilitazioneServizi tipo){
		try{
			StatoServiziPdD.removeFiltroAbilitazionePA(tipo);
			this.componentePA_abilitazioniPuntuali = visualizzaFiltriAbilitazioniServizioPortaApplicativa();
			return "Operazione effettuata con successo";
		}catch(Throwable e){
			OpenSPCoop2Logger.getLoggerOpenSPCoopCore().error("Operazione removeFiltroAbilitazioneServizioPortaApplicativa non riuscita: "+e.getMessage(),e);
			return JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+" sistema non disponibile";
		}
	}
	public String removeFiltroDisabilitazioneServizioPortaApplicativa(TipoFiltroAbilitazioneServizi tipo){
		try{
			StatoServiziPdD.removeFiltroDisabilitazionePA(tipo);
			this.componentePA_disabilitazioniPuntuali = visualizzaFiltriDisabilitazioniServizioPortaApplicativa();
			return "Operazione effettuata con successo";
		}catch(Throwable e){
			OpenSPCoop2Logger.getLoggerOpenSPCoopCore().error("Operazione removeFiltroDisabilitazioneServizioPortaApplicativa non riuscita: "+e.getMessage(),e);
			return JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+" sistema non disponibile";
		}
	}
}
