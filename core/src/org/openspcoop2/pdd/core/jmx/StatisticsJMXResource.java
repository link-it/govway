/*
 * OpenSPCoop - Customizable API Gateway 
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


import java.text.SimpleDateFormat;
import java.util.Date;
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
import javax.management.NotificationBroadcasterSupport;
import javax.management.ReflectionException;

import org.openspcoop2.pdd.core.handlers.statistics.StatisticsCollection;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.date.DateManager;


/**
 * Implementazione JMX per la gestione dell'autorizzazione
 *   
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class StatisticsJMXResource extends NotificationBroadcasterSupport implements DynamicMBean {

	/** Nomi proprieta' */
	public final static String INTERVALLO_STATISTICO = "Intervallo Temporale dei dati Statistici";
	
	public final static String NUMERO_TRANSAZIONI = "Numero transazioni PdD";
	public final static String TXT_NUMERO_TRANSAZIONI = "Numero di transazioni (totale/ok/inErrore) gestite dalla PdD";
	
	public final static String NUMERO_TRANSAZIONI_PD = "Numero transazioni PortaDelegata";
	public final static String TXT_NUMERO_TRANSAZIONI_PD = "Numero di transazioni (totale/ok/inErrore) gestite sulla PortaDelegata";
	
	public final static String NUMERO_TRANSAZIONI_PA = "Numero transazioni PortaApplicativa";
	public final static String TXT_NUMERO_TRANSAZIONI_PA = "Numero di transazioni (totale/ok/inErrore) gestite sulla PortaApplicativa";
	
	public final static String LATENZA_ATTRAVERSAMENTO = "Tempo attraversamento PdD";
	public final static String TXT_LATENZA_ATTRAVERSAMENTO = "Tempo di attraversamento (min/avg/max) della PdD";
	public final static String LATENZA_ATTRAVERSAMENTO_REQUEST = 
		"Tempo attraversamento richieste";
	public final static String TXT_LATENZA_ATTRAVERSAMENTO_REQUEST = 
		"Tempo di attraversamento (min/avg/max) della PdD per i messaggi di richiesta";
	public final static String LATENZA_ATTRAVERSAMENTO_RESPONSE = 
		"Tempo attraversamento risposte";
	public final static String TXT_LATENZA_ATTRAVERSAMENTO_RESPONSE = 
		"Tempo di attraversamento (min/avg/max) della PdD per i messaggi di risposta";
	
	public final static String LATENZA_ATTRAVERSAMENTO_PD = "Tempo attraversamento PortaDelegata";
	public final static String TXT_LATENZA_ATTRAVERSAMENTO_PD = "Tempo di attraversamento (min/avg/max) della PortaDelegata";
	public final static String LATENZA_ATTRAVERSAMENTO_PD_REQUEST = 
		"Tempo attraversamento richieste sulla PortaDelegata";
	public final static String TXT_LATENZA_ATTRAVERSAMENTO_PD_REQUEST = 
		"Tempo di attraversamento (min/avg/max) della PortaDelegata per i messaggi di richiesta";
	public final static String LATENZA_ATTRAVERSAMENTO_PD_RESPONSE = 
		"Tempo attraversamento risposte sulla PortaDelegata";
	public final static String TXT_LATENZA_ATTRAVERSAMENTO_PD_RESPONSE = 
		"Tempo di attraversamento (min/avg/max) della PortaDelegata per i messaggi di risposta";
	
	public final static String LATENZA_ATTRAVERSAMENTO_PA = "Tempo attraversamento PortaApplicativa";
	public final static String TXT_LATENZA_ATTRAVERSAMENTO_PA = "Tempo di attraversamento (min/avg/max) della PortaApplicativa";
	public final static String LATENZA_ATTRAVERSAMENTO_PA_REQUEST = 
		"Tempo attraversamento richieste sulla PortaApplicativa";
	public final static String TXT_LATENZA_ATTRAVERSAMENTO_PA_REQUEST = 
		"Tempo di attraversamento (min/avg/max) della PortaApplicativa per i messaggi di richiesta";
	public final static String LATENZA_ATTRAVERSAMENTO_PA_RESPONSE = 
		"Tempo attraversamento risposte sulla PortaApplicativa";
	public final static String TXT_LATENZA_ATTRAVERSAMENTO_PA_RESPONSE = 
		"Tempo di attraversamento (min/avg/max) della PortaApplicativa per i messaggi di risposta";
	
	public final static String DIMENSIONE_MESSAGGI = "Dimensione messaggi PdD";
	public final static String DIMENSIONE_MESSAGGI_IN_REQUEST = "Dimensione richieste in ingresso sulla PdD";
	public final static String DIMENSIONE_MESSAGGI_OUT_REQUEST = "Dimensione richieste in uscita dalla PdD";
	public final static String DIMENSIONE_MESSAGGI_IN_RESPONSE = "Dimensione risposte in ingresso sulla PdD";
	public final static String DIMENSIONE_MESSAGGI_OUT_RESPONSE = "Dimensione risposte in uscita dalla PdD";
	public final static String TXT_DIMENSIONE_MESSAGGI = "Dimensione dei messaggi (min/avg/max) gestiti dalla PdD";
	public final static String TXT_DIMENSIONE_MESSAGGI_IN_REQUEST = "Dimensione dei messaggi di richiesta (min/avg/max) in ingresso sulla PdD";
	public final static String TXT_DIMENSIONE_MESSAGGI_OUT_REQUEST = "Dimensione dei messaggi di richiesta (min/avg/max) in uscita dalla PdD";
	public final static String TXT_DIMENSIONE_MESSAGGI_IN_RESPONSE = "Dimensione dei messaggi di risposta (min/avg/max) in ingresso sulla PdD";
	public final static String TXT_DIMENSIONE_MESSAGGI_OUT_RESPONSE = "Dimensione dei messaggi di risposta (min/avg/max) in uscita dalla PdD";
	
	public final static String DIMENSIONE_MESSAGGI_PD = "Dimensione messaggi PortaDelegata";
	public final static String DIMENSIONE_MESSAGGI_PD_IN_REQUEST = "Dimensione richieste in ingresso sulla PortaDelegata";
	public final static String DIMENSIONE_MESSAGGI_PD_OUT_REQUEST = "Dimensione richieste in uscita dalla PortaDelegata";
	public final static String DIMENSIONE_MESSAGGI_PD_IN_RESPONSE = "Dimensione risposte in ingresso sulla PortaDelegata";
	public final static String DIMENSIONE_MESSAGGI_PD_OUT_RESPONSE = "Dimensione risposte in uscita dalla PortaDelegata";
	public final static String TXT_DIMENSIONE_MESSAGGI_PD = "Dimensione dei messaggi (min/avg/max) gestiti dalla PortaDelegata";
	public final static String TXT_DIMENSIONE_MESSAGGI_PD_IN_REQUEST = "Dimensione dei messaggi di richiesta (min/avg/max) in ingresso sulla PortaDelegata";
	public final static String TXT_DIMENSIONE_MESSAGGI_PD_OUT_REQUEST = "Dimensione dei messaggi di richiesta (min/avg/max) in uscita dalla PortaDelegata";
	public final static String TXT_DIMENSIONE_MESSAGGI_PD_IN_RESPONSE = "Dimensione dei messaggi di risposta (min/avg/max) in ingresso sulla PortaDelegata";
	public final static String TXT_DIMENSIONE_MESSAGGI_PD_OUT_RESPONSE = "Dimensione dei messaggi di risposta (min/avg/max) in uscita dalla PortaDelegata";
		
	public final static String DIMENSIONE_MESSAGGI_PA = "Dimensione messaggi PortaApplicativa";
	public final static String DIMENSIONE_MESSAGGI_PA_IN_REQUEST = "Dimensione richieste in ingresso sulla PortaApplicativa";
	public final static String DIMENSIONE_MESSAGGI_PA_OUT_REQUEST = "Dimensione richieste in uscita dalla PortaApplicativa";
	public final static String DIMENSIONE_MESSAGGI_PA_IN_RESPONSE = "Dimensione risposte in ingresso sulla PortaApplicativa";
	public final static String DIMENSIONE_MESSAGGI_PA_OUT_RESPONSE = "Dimensione risposte in uscita dalla PortaApplicativa";
	public final static String TXT_DIMENSIONE_MESSAGGI_PA = "Dimensione dei messaggi (min/avg/max) gestiti dalla PortaApplicativa";
	public final static String TXT_DIMENSIONE_MESSAGGI_PA_IN_REQUEST = "Dimensione dei messaggi di richiesta (min/avg/max) in ingresso sulla PortaApplicativa";
	public final static String TXT_DIMENSIONE_MESSAGGI_PA_OUT_REQUEST = "Dimensione dei messaggi di richiesta (min/avg/max) in uscita dalla PortaApplicativa";
	public final static String TXT_DIMENSIONE_MESSAGGI_PA_IN_RESPONSE = "Dimensione dei messaggi di risposta (min/avg/max) in ingresso sulla PortaApplicativa";
	public final static String TXT_DIMENSIONE_MESSAGGI_PA_OUT_RESPONSE = "Dimensione dei messaggi di risposta (min/avg/max) in uscita dalla PortaApplicativa";
	
	
	/** Nomi metodi */
	public final static String REFRESH = "refresh"; 
	public final static String RESET = "reset"; 
	


	/** Attributi */
	private String intervallo_statistico = "";
	private String numero_transazioni = "";
	private String numero_transazioni_pd = "";
	private String numero_transazioni_pa = "";
	private String latenza_attraversamento = "";
	private String latenza_attraversamento_request = "";
	private String latenza_attraversamento_response = "";
	private String latenza_attraversamento_pd = "";
	private String latenza_attraversamento_pd_request = "";
	private String latenza_attraversamento_pd_response = "";
	private String latenza_attraversamento_pa = "";
	private String latenza_attraversamento_pa_request = "";
	private String latenza_attraversamento_pa_response = "";
	private String dimensione_messaggi = "";
	private String dimensione_messaggi_in_request = "";
	private String dimensione_messaggi_out_request = "";
	private String dimensione_messaggi_in_response = "";
	private String dimensione_messaggi_out_response = "";
	private String dimensione_messaggi_pd = "";
	private String dimensione_messaggi_pd_in_request = "";
	private String dimensione_messaggi_pd_out_request = "";
	private String dimensione_messaggi_pd_in_response = "";
	private String dimensione_messaggi_pd_out_response = "";	
	private String dimensione_messaggi_pa = "";
	private String dimensione_messaggi_pa_in_request = "";
	private String dimensione_messaggi_pa_out_request = "";
	private String dimensione_messaggi_pa_in_response = "";
	private String dimensione_messaggi_pa_out_response = "";
	
	
	/** getAttribute */
	@Override
	public Object getAttribute(String attributeName) throws AttributeNotFoundException,MBeanException,ReflectionException{

		if( (attributeName==null) || (attributeName.equals("")) )
			throw new IllegalArgumentException("Il nome dell'attributo e' nullo o vuoto");

		if(attributeName.equals(StatisticsJMXResource.INTERVALLO_STATISTICO))
			return this.intervallo_statistico;
		
		else if(attributeName.equals(StatisticsJMXResource.NUMERO_TRANSAZIONI))
			return this.numero_transazioni;
		else if(attributeName.equals(StatisticsJMXResource.NUMERO_TRANSAZIONI_PD))
			return this.numero_transazioni_pd;
		else if(attributeName.equals(StatisticsJMXResource.NUMERO_TRANSAZIONI_PA))
			return this.numero_transazioni_pa;
		
		else if(attributeName.equals(StatisticsJMXResource.LATENZA_ATTRAVERSAMENTO))
			return this.latenza_attraversamento;
		else if(attributeName.equals(StatisticsJMXResource.LATENZA_ATTRAVERSAMENTO_REQUEST))
			return this.latenza_attraversamento_request;
		else if(attributeName.equals(StatisticsJMXResource.LATENZA_ATTRAVERSAMENTO_RESPONSE))
			return this.latenza_attraversamento_response;
		else if(attributeName.equals(StatisticsJMXResource.LATENZA_ATTRAVERSAMENTO_PD))
			return this.latenza_attraversamento_pd;
		else if(attributeName.equals(StatisticsJMXResource.LATENZA_ATTRAVERSAMENTO_PD_REQUEST))
			return this.latenza_attraversamento_pd_request;
		else if(attributeName.equals(StatisticsJMXResource.LATENZA_ATTRAVERSAMENTO_PD_RESPONSE))
			return this.latenza_attraversamento_pd_response;
		else if(attributeName.equals(StatisticsJMXResource.LATENZA_ATTRAVERSAMENTO_PA))
			return this.latenza_attraversamento_pa;
		else if(attributeName.equals(StatisticsJMXResource.LATENZA_ATTRAVERSAMENTO_PA_REQUEST))
			return this.latenza_attraversamento_pa_request;
		else if(attributeName.equals(StatisticsJMXResource.LATENZA_ATTRAVERSAMENTO_PA_RESPONSE))
			return this.latenza_attraversamento_pa_response;

		else if(attributeName.equals(StatisticsJMXResource.DIMENSIONE_MESSAGGI))
			return this.dimensione_messaggi;
		else if(attributeName.equals(StatisticsJMXResource.DIMENSIONE_MESSAGGI_IN_REQUEST))
			return this.dimensione_messaggi_in_request;
		else if(attributeName.equals(StatisticsJMXResource.DIMENSIONE_MESSAGGI_OUT_REQUEST))
			return this.dimensione_messaggi_out_request;
		else if(attributeName.equals(StatisticsJMXResource.DIMENSIONE_MESSAGGI_IN_RESPONSE))
			return this.dimensione_messaggi_in_response;
		else if(attributeName.equals(StatisticsJMXResource.DIMENSIONE_MESSAGGI_OUT_RESPONSE))
			return this.dimensione_messaggi_out_response;
		else if(attributeName.equals(StatisticsJMXResource.DIMENSIONE_MESSAGGI_PD))
			return this.dimensione_messaggi_pd;
		else if(attributeName.equals(StatisticsJMXResource.DIMENSIONE_MESSAGGI_PD_IN_REQUEST))
			return this.dimensione_messaggi_pd_in_request;
		else if(attributeName.equals(StatisticsJMXResource.DIMENSIONE_MESSAGGI_PD_OUT_REQUEST))
			return this.dimensione_messaggi_pd_out_request;
		else if(attributeName.equals(StatisticsJMXResource.DIMENSIONE_MESSAGGI_PD_IN_RESPONSE))
			return this.dimensione_messaggi_pd_in_response;
		else if(attributeName.equals(StatisticsJMXResource.DIMENSIONE_MESSAGGI_PD_OUT_RESPONSE))
			return this.dimensione_messaggi_pd_out_response;
		else if(attributeName.equals(StatisticsJMXResource.DIMENSIONE_MESSAGGI_PA))
			return this.dimensione_messaggi_pa;
		else if(attributeName.equals(StatisticsJMXResource.DIMENSIONE_MESSAGGI_PA_IN_REQUEST))
			return this.dimensione_messaggi_pa_in_request;
		else if(attributeName.equals(StatisticsJMXResource.DIMENSIONE_MESSAGGI_PA_OUT_REQUEST))
			return this.dimensione_messaggi_pa_out_request;
		else if(attributeName.equals(StatisticsJMXResource.DIMENSIONE_MESSAGGI_PA_IN_RESPONSE))
			return this.dimensione_messaggi_pa_in_response;
		else if(attributeName.equals(StatisticsJMXResource.DIMENSIONE_MESSAGGI_PA_OUT_RESPONSE))
			return this.dimensione_messaggi_pa_out_response;
		
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

			if(attribute.getName().equals(StatisticsJMXResource.INTERVALLO_STATISTICO)){
				// nothing DO
			}
			
			else if(attribute.getName().equals(StatisticsJMXResource.NUMERO_TRANSAZIONI)){
				// nothing DO
			}
			else if(attribute.getName().equals(StatisticsJMXResource.NUMERO_TRANSAZIONI_PD)){
				// nothing DO
			}
			else if(attribute.getName().equals(StatisticsJMXResource.NUMERO_TRANSAZIONI_PA)){
				// nothing DO
			}
			
			else if(attribute.getName().equals(StatisticsJMXResource.LATENZA_ATTRAVERSAMENTO)){
				// nothing DO
			}
			else if(attribute.getName().equals(StatisticsJMXResource.LATENZA_ATTRAVERSAMENTO_REQUEST)){
				// nothing DO
			}
			else if(attribute.getName().equals(StatisticsJMXResource.LATENZA_ATTRAVERSAMENTO_RESPONSE)){
				// nothing DO
			}
			else if(attribute.getName().equals(StatisticsJMXResource.LATENZA_ATTRAVERSAMENTO_PD)){
				// nothing DO
			}
			else if(attribute.getName().equals(StatisticsJMXResource.LATENZA_ATTRAVERSAMENTO_PD_REQUEST)){
				// nothing DO
			}
			else if(attribute.getName().equals(StatisticsJMXResource.LATENZA_ATTRAVERSAMENTO_PD_RESPONSE)){
				// nothing DO
			}
			else if(attribute.getName().equals(StatisticsJMXResource.LATENZA_ATTRAVERSAMENTO_PA)){
				// nothing DO
			}
			else if(attribute.getName().equals(StatisticsJMXResource.LATENZA_ATTRAVERSAMENTO_PA_REQUEST)){
				// nothing DO
			}
			else if(attribute.getName().equals(StatisticsJMXResource.LATENZA_ATTRAVERSAMENTO_PA_RESPONSE)){
				// nothing DO
			}

			else if(attribute.getName().equals(StatisticsJMXResource.DIMENSIONE_MESSAGGI)){
				// nothing DO
			}
			else if(attribute.getName().equals(StatisticsJMXResource.DIMENSIONE_MESSAGGI_IN_REQUEST)){
				// nothing DO
			}
			else if(attribute.getName().equals(StatisticsJMXResource.DIMENSIONE_MESSAGGI_OUT_REQUEST)){
				// nothing DO
			}
			else if(attribute.getName().equals(StatisticsJMXResource.DIMENSIONE_MESSAGGI_IN_RESPONSE)){
				// nothing DO
			}
			else if(attribute.getName().equals(StatisticsJMXResource.DIMENSIONE_MESSAGGI_OUT_RESPONSE)){
				// nothing DO
			}
			else if(attribute.getName().equals(StatisticsJMXResource.DIMENSIONE_MESSAGGI_PD)){
				// nothing DO
			}
			else if(attribute.getName().equals(StatisticsJMXResource.DIMENSIONE_MESSAGGI_PD_IN_REQUEST)){
				// nothing DO
			}
			else if(attribute.getName().equals(StatisticsJMXResource.DIMENSIONE_MESSAGGI_PD_OUT_REQUEST)){
				// nothing DO
			}
			else if(attribute.getName().equals(StatisticsJMXResource.DIMENSIONE_MESSAGGI_PD_IN_RESPONSE)){
				// nothing DO
			}
			else if(attribute.getName().equals(StatisticsJMXResource.DIMENSIONE_MESSAGGI_PD_OUT_RESPONSE)){
				// nothing DO
			}
			else if(attribute.getName().equals(StatisticsJMXResource.DIMENSIONE_MESSAGGI_PA)){
				// nothing DO
			}
			else if(attribute.getName().equals(StatisticsJMXResource.DIMENSIONE_MESSAGGI_PA_IN_REQUEST)){
				// nothing DO
			}
			else if(attribute.getName().equals(StatisticsJMXResource.DIMENSIONE_MESSAGGI_PA_OUT_REQUEST)){
				// nothing DO
			}
			else if(attribute.getName().equals(StatisticsJMXResource.DIMENSIONE_MESSAGGI_PA_IN_RESPONSE)){
				// nothing DO
			}
			else if(attribute.getName().equals(StatisticsJMXResource.DIMENSIONE_MESSAGGI_PA_OUT_RESPONSE)){
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

		if(actionName.equals(StatisticsJMXResource.REFRESH)){
			return this.refresh();
		}
		else if(actionName.equals(StatisticsJMXResource.RESET)){
			return this.reset();
		}
		
		throw new UnsupportedOperationException("Operazione "+actionName+" sconosciuta");
	}

	
	/* MBean info */
	@Override
	public MBeanInfo getMBeanInfo(){

		// Descrizione della classe nel MBean
		String className = this.getClass().getName();
		String description = "Stato dei servizi attivi sulla Porta di Dominio";

		// MetaData per gli attributi
		MBeanAttributeInfo intervalloStatistico_VAR 
			= new MBeanAttributeInfo(StatisticsJMXResource.INTERVALLO_STATISTICO,String.class.getName(),
				"Intervallo temporale a cui si riferisce il rilevamento statistico",
				JMXUtils.JMX_ATTRIBUTE_READABLE,!JMXUtils.JMX_ATTRIBUTE_WRITABLE,!JMXUtils.JMX_ATTRIBUTE_IS_GETTER);		
		
		MBeanAttributeInfo numeroTransazioni_VAR 
			= new MBeanAttributeInfo(StatisticsJMXResource.NUMERO_TRANSAZIONI,String.class.getName(),
				StatisticsJMXResource.TXT_NUMERO_TRANSAZIONI,
				JMXUtils.JMX_ATTRIBUTE_READABLE,!JMXUtils.JMX_ATTRIBUTE_WRITABLE,!JMXUtils.JMX_ATTRIBUTE_IS_GETTER);	
		MBeanAttributeInfo numeroTransazioniPD_VAR 
			= new MBeanAttributeInfo(StatisticsJMXResource.NUMERO_TRANSAZIONI_PD,String.class.getName(),
					StatisticsJMXResource.TXT_NUMERO_TRANSAZIONI_PD,
					JMXUtils.JMX_ATTRIBUTE_READABLE,!JMXUtils.JMX_ATTRIBUTE_WRITABLE,!JMXUtils.JMX_ATTRIBUTE_IS_GETTER);	
		MBeanAttributeInfo numeroTransazioniPA_VAR 
			= new MBeanAttributeInfo(StatisticsJMXResource.NUMERO_TRANSAZIONI_PA,String.class.getName(),
					StatisticsJMXResource.TXT_NUMERO_TRANSAZIONI_PA,
					JMXUtils.JMX_ATTRIBUTE_READABLE,!JMXUtils.JMX_ATTRIBUTE_WRITABLE,!JMXUtils.JMX_ATTRIBUTE_IS_GETTER);	
		
		MBeanAttributeInfo latenzaAttraversamento_VAR 
			= new MBeanAttributeInfo(StatisticsJMXResource.LATENZA_ATTRAVERSAMENTO,String.class.getName(),
					StatisticsJMXResource.TXT_LATENZA_ATTRAVERSAMENTO,
					JMXUtils.JMX_ATTRIBUTE_READABLE,!JMXUtils.JMX_ATTRIBUTE_WRITABLE,!JMXUtils.JMX_ATTRIBUTE_IS_GETTER);	
		MBeanAttributeInfo latenzaAttraversamentoRequest_VAR 
			= new MBeanAttributeInfo(StatisticsJMXResource.LATENZA_ATTRAVERSAMENTO_REQUEST,String.class.getName(),
					StatisticsJMXResource.TXT_LATENZA_ATTRAVERSAMENTO_REQUEST,
					JMXUtils.JMX_ATTRIBUTE_READABLE,!JMXUtils.JMX_ATTRIBUTE_WRITABLE,!JMXUtils.JMX_ATTRIBUTE_IS_GETTER);	
		MBeanAttributeInfo latenzaAttraversamentoResponse_VAR 
			= new MBeanAttributeInfo(StatisticsJMXResource.LATENZA_ATTRAVERSAMENTO_RESPONSE,String.class.getName(),
					StatisticsJMXResource.TXT_LATENZA_ATTRAVERSAMENTO_RESPONSE,
					JMXUtils.JMX_ATTRIBUTE_READABLE,!JMXUtils.JMX_ATTRIBUTE_WRITABLE,!JMXUtils.JMX_ATTRIBUTE_IS_GETTER);	

		MBeanAttributeInfo latenzaAttraversamentoPD_VAR 
			= new MBeanAttributeInfo(StatisticsJMXResource.LATENZA_ATTRAVERSAMENTO_PD,String.class.getName(),
					StatisticsJMXResource.TXT_LATENZA_ATTRAVERSAMENTO_PD,
					JMXUtils.JMX_ATTRIBUTE_READABLE,!JMXUtils.JMX_ATTRIBUTE_WRITABLE,!JMXUtils.JMX_ATTRIBUTE_IS_GETTER);	
		MBeanAttributeInfo latenzaAttraversamentoPDRequest_VAR 
			= new MBeanAttributeInfo(StatisticsJMXResource.LATENZA_ATTRAVERSAMENTO_PD_REQUEST,String.class.getName(),
					StatisticsJMXResource.TXT_LATENZA_ATTRAVERSAMENTO_PD_REQUEST,
					JMXUtils.JMX_ATTRIBUTE_READABLE,!JMXUtils.JMX_ATTRIBUTE_WRITABLE,!JMXUtils.JMX_ATTRIBUTE_IS_GETTER);	
		MBeanAttributeInfo latenzaAttraversamentoPDResponse_VAR 
			= new MBeanAttributeInfo(StatisticsJMXResource.LATENZA_ATTRAVERSAMENTO_PD_RESPONSE,String.class.getName(),
					StatisticsJMXResource.TXT_LATENZA_ATTRAVERSAMENTO_PD_RESPONSE,
					JMXUtils.JMX_ATTRIBUTE_READABLE,!JMXUtils.JMX_ATTRIBUTE_WRITABLE,!JMXUtils.JMX_ATTRIBUTE_IS_GETTER);	
		
		MBeanAttributeInfo latenzaAttraversamentoPA_VAR 
			= new MBeanAttributeInfo(StatisticsJMXResource.LATENZA_ATTRAVERSAMENTO_PA,String.class.getName(),
					StatisticsJMXResource.TXT_LATENZA_ATTRAVERSAMENTO_PA,
					JMXUtils.JMX_ATTRIBUTE_READABLE,!JMXUtils.JMX_ATTRIBUTE_WRITABLE,!JMXUtils.JMX_ATTRIBUTE_IS_GETTER);	
		MBeanAttributeInfo latenzaAttraversamentoPARequest_VAR 
			= new MBeanAttributeInfo(StatisticsJMXResource.LATENZA_ATTRAVERSAMENTO_PA_REQUEST,String.class.getName(),
					StatisticsJMXResource.TXT_LATENZA_ATTRAVERSAMENTO_PA_REQUEST,
					JMXUtils.JMX_ATTRIBUTE_READABLE,!JMXUtils.JMX_ATTRIBUTE_WRITABLE,!JMXUtils.JMX_ATTRIBUTE_IS_GETTER);	
		MBeanAttributeInfo latenzaAttraversamentoPAResponse_VAR 
			= new MBeanAttributeInfo(StatisticsJMXResource.LATENZA_ATTRAVERSAMENTO_PA_RESPONSE,String.class.getName(),
					StatisticsJMXResource.TXT_LATENZA_ATTRAVERSAMENTO_PA_RESPONSE,
					JMXUtils.JMX_ATTRIBUTE_READABLE,!JMXUtils.JMX_ATTRIBUTE_WRITABLE,!JMXUtils.JMX_ATTRIBUTE_IS_GETTER);	
		
		MBeanAttributeInfo dimensioneMessaggi_VAR 
			= new MBeanAttributeInfo(StatisticsJMXResource.DIMENSIONE_MESSAGGI,String.class.getName(),
					StatisticsJMXResource.TXT_DIMENSIONE_MESSAGGI,
					JMXUtils.JMX_ATTRIBUTE_READABLE,!JMXUtils.JMX_ATTRIBUTE_WRITABLE,!JMXUtils.JMX_ATTRIBUTE_IS_GETTER);	
		MBeanAttributeInfo dimensioneMessaggiInRequest_VAR 
			= new MBeanAttributeInfo(StatisticsJMXResource.DIMENSIONE_MESSAGGI_IN_REQUEST,String.class.getName(),
					StatisticsJMXResource.TXT_DIMENSIONE_MESSAGGI_IN_REQUEST,
					JMXUtils.JMX_ATTRIBUTE_READABLE,!JMXUtils.JMX_ATTRIBUTE_WRITABLE,!JMXUtils.JMX_ATTRIBUTE_IS_GETTER);	
		MBeanAttributeInfo dimensioneMessaggiOutRequest_VAR 
			= new MBeanAttributeInfo(StatisticsJMXResource.DIMENSIONE_MESSAGGI_OUT_REQUEST,String.class.getName(),
					StatisticsJMXResource.TXT_DIMENSIONE_MESSAGGI_OUT_REQUEST,
					JMXUtils.JMX_ATTRIBUTE_READABLE,!JMXUtils.JMX_ATTRIBUTE_WRITABLE,!JMXUtils.JMX_ATTRIBUTE_IS_GETTER);	
		MBeanAttributeInfo dimensioneMessaggiInResponse_VAR 
			= new MBeanAttributeInfo(StatisticsJMXResource.DIMENSIONE_MESSAGGI_IN_RESPONSE,String.class.getName(),
					StatisticsJMXResource.TXT_DIMENSIONE_MESSAGGI_IN_RESPONSE,
					JMXUtils.JMX_ATTRIBUTE_READABLE,!JMXUtils.JMX_ATTRIBUTE_WRITABLE,!JMXUtils.JMX_ATTRIBUTE_IS_GETTER);	
		MBeanAttributeInfo dimensioneMessaggiOutResponse_VAR 
			= new MBeanAttributeInfo(StatisticsJMXResource.DIMENSIONE_MESSAGGI_OUT_RESPONSE,String.class.getName(),
					StatisticsJMXResource.TXT_DIMENSIONE_MESSAGGI_OUT_RESPONSE,
					JMXUtils.JMX_ATTRIBUTE_READABLE,!JMXUtils.JMX_ATTRIBUTE_WRITABLE,!JMXUtils.JMX_ATTRIBUTE_IS_GETTER);	
		
		MBeanAttributeInfo dimensioneMessaggiPD_VAR 
			= new MBeanAttributeInfo(StatisticsJMXResource.DIMENSIONE_MESSAGGI_PD,String.class.getName(),
					StatisticsJMXResource.TXT_DIMENSIONE_MESSAGGI_PD,
					JMXUtils.JMX_ATTRIBUTE_READABLE,!JMXUtils.JMX_ATTRIBUTE_WRITABLE,!JMXUtils.JMX_ATTRIBUTE_IS_GETTER);	
		MBeanAttributeInfo dimensioneMessaggiPDInRequest_VAR 
			= new MBeanAttributeInfo(StatisticsJMXResource.DIMENSIONE_MESSAGGI_PD_IN_REQUEST,String.class.getName(),
					StatisticsJMXResource.TXT_DIMENSIONE_MESSAGGI_PD_IN_REQUEST,
					JMXUtils.JMX_ATTRIBUTE_READABLE,!JMXUtils.JMX_ATTRIBUTE_WRITABLE,!JMXUtils.JMX_ATTRIBUTE_IS_GETTER);	
		MBeanAttributeInfo dimensioneMessaggiPDOutRequest_VAR 
			= new MBeanAttributeInfo(StatisticsJMXResource.DIMENSIONE_MESSAGGI_PD_OUT_REQUEST,String.class.getName(),
					StatisticsJMXResource.TXT_DIMENSIONE_MESSAGGI_PD_OUT_REQUEST,
					JMXUtils.JMX_ATTRIBUTE_READABLE,!JMXUtils.JMX_ATTRIBUTE_WRITABLE,!JMXUtils.JMX_ATTRIBUTE_IS_GETTER);	
		MBeanAttributeInfo dimensioneMessaggiPDInResponse_VAR 
			= new MBeanAttributeInfo(StatisticsJMXResource.DIMENSIONE_MESSAGGI_PD_IN_RESPONSE,String.class.getName(),
					StatisticsJMXResource.TXT_DIMENSIONE_MESSAGGI_PD_IN_RESPONSE,
					JMXUtils.JMX_ATTRIBUTE_READABLE,!JMXUtils.JMX_ATTRIBUTE_WRITABLE,!JMXUtils.JMX_ATTRIBUTE_IS_GETTER);	
		MBeanAttributeInfo dimensioneMessaggiPDOutResponse_VAR 
			= new MBeanAttributeInfo(StatisticsJMXResource.DIMENSIONE_MESSAGGI_PD_OUT_RESPONSE,String.class.getName(),
					StatisticsJMXResource.TXT_DIMENSIONE_MESSAGGI_PD_OUT_RESPONSE,
					JMXUtils.JMX_ATTRIBUTE_READABLE,!JMXUtils.JMX_ATTRIBUTE_WRITABLE,!JMXUtils.JMX_ATTRIBUTE_IS_GETTER);
		
		MBeanAttributeInfo dimensioneMessaggiPA_VAR 
			= new MBeanAttributeInfo(StatisticsJMXResource.DIMENSIONE_MESSAGGI_PA,String.class.getName(),
					StatisticsJMXResource.TXT_DIMENSIONE_MESSAGGI_PA,
					JMXUtils.JMX_ATTRIBUTE_READABLE,!JMXUtils.JMX_ATTRIBUTE_WRITABLE,!JMXUtils.JMX_ATTRIBUTE_IS_GETTER);	
		MBeanAttributeInfo dimensioneMessaggiPAInRequest_VAR 
			= new MBeanAttributeInfo(StatisticsJMXResource.DIMENSIONE_MESSAGGI_PA_IN_REQUEST,String.class.getName(),
					StatisticsJMXResource.TXT_DIMENSIONE_MESSAGGI_PA_IN_REQUEST,
					JMXUtils.JMX_ATTRIBUTE_READABLE,!JMXUtils.JMX_ATTRIBUTE_WRITABLE,!JMXUtils.JMX_ATTRIBUTE_IS_GETTER);	
		MBeanAttributeInfo dimensioneMessaggiPAOutRequest_VAR 
			= new MBeanAttributeInfo(StatisticsJMXResource.DIMENSIONE_MESSAGGI_PA_OUT_REQUEST,String.class.getName(),
					StatisticsJMXResource.TXT_DIMENSIONE_MESSAGGI_PA_OUT_REQUEST,
					JMXUtils.JMX_ATTRIBUTE_READABLE,!JMXUtils.JMX_ATTRIBUTE_WRITABLE,!JMXUtils.JMX_ATTRIBUTE_IS_GETTER);	
		MBeanAttributeInfo dimensioneMessaggiPAInResponse_VAR 
			= new MBeanAttributeInfo(StatisticsJMXResource.DIMENSIONE_MESSAGGI_PA_IN_RESPONSE,String.class.getName(),
					StatisticsJMXResource.TXT_DIMENSIONE_MESSAGGI_PA_IN_RESPONSE,
					JMXUtils.JMX_ATTRIBUTE_READABLE,!JMXUtils.JMX_ATTRIBUTE_WRITABLE,!JMXUtils.JMX_ATTRIBUTE_IS_GETTER);	
		MBeanAttributeInfo dimensioneMessaggiPAOutResponse_VAR 
			= new MBeanAttributeInfo(StatisticsJMXResource.DIMENSIONE_MESSAGGI_PA_OUT_RESPONSE,String.class.getName(),
					StatisticsJMXResource.TXT_DIMENSIONE_MESSAGGI_PA_OUT_RESPONSE,
					JMXUtils.JMX_ATTRIBUTE_READABLE,!JMXUtils.JMX_ATTRIBUTE_WRITABLE,!JMXUtils.JMX_ATTRIBUTE_IS_GETTER);
		
		// MetaData per le operazioni
		MBeanOperationInfo refresh_OP 
		= new MBeanOperationInfo(StatisticsJMXResource.REFRESH,"Aggiorna le statistiche",
				null,
				String.class.getName(),
				MBeanOperationInfo.ACTION);
		MBeanOperationInfo reset_OP 
		= new MBeanOperationInfo(StatisticsJMXResource.RESET,"Reset delle informazioni statistiche",
				null,
				String.class.getName(),
				MBeanOperationInfo.ACTION);


		// Mbean costruttore
		MBeanConstructorInfo defaultConstructor = 
			new MBeanConstructorInfo("Default Constructor","Crea e inizializza una nuova istanza del MBean",null);

		// Lista attributi
		MBeanAttributeInfo[] attributes = 
			new MBeanAttributeInfo[]{intervalloStatistico_VAR,
				numeroTransazioni_VAR,numeroTransazioniPD_VAR,numeroTransazioniPA_VAR,
				latenzaAttraversamento_VAR,latenzaAttraversamentoRequest_VAR,latenzaAttraversamentoResponse_VAR,
				latenzaAttraversamentoPD_VAR,latenzaAttraversamentoPDRequest_VAR,latenzaAttraversamentoPDResponse_VAR,
				latenzaAttraversamentoPA_VAR,latenzaAttraversamentoPARequest_VAR,latenzaAttraversamentoPAResponse_VAR,
				dimensioneMessaggi_VAR,dimensioneMessaggiInRequest_VAR,dimensioneMessaggiOutRequest_VAR,
				dimensioneMessaggiInResponse_VAR,dimensioneMessaggiOutResponse_VAR,
				dimensioneMessaggiPD_VAR,dimensioneMessaggiPDInRequest_VAR,dimensioneMessaggiPDOutRequest_VAR,
				dimensioneMessaggiPDInResponse_VAR,dimensioneMessaggiPDOutResponse_VAR,
				dimensioneMessaggiPA_VAR,dimensioneMessaggiPAInRequest_VAR,dimensioneMessaggiPAOutRequest_VAR,
				dimensioneMessaggiPAInResponse_VAR,dimensioneMessaggiPAOutResponse_VAR};

		// Lista Costruttori
		MBeanConstructorInfo[] constructors = new MBeanConstructorInfo[]{defaultConstructor};

		// Lista operazioni
		MBeanOperationInfo[] operations = new MBeanOperationInfo[]{refresh_OP,reset_OP};

		return new MBeanInfo(className,description,attributes,constructors,operations,null);
	}


	


	/* Costruttore */
	public StatisticsJMXResource() throws Exception{

		
		
	}

	/* Metodi di management JMX */

	public String refresh(){
		StatisticsCollection stat = StatisticsCollection.getStatisticsCollection();
		
		if(stat.getDataUltimoRefresh()>0) {
			// Altrimenti non sono ancora passati messaggi.
			Date rightBound = DateManager.getDate();
			Date leftBound = new Date(stat.getDataUltimoRefresh());
		
			SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); // SimpleDateFormat non e' thread-safe
			this.intervallo_statistico = "["+ dateformat.format(leftBound) 
				+ "] - ["+dateformat.format(rightBound)+"]";
		}else{
			this.intervallo_statistico = "";
		}
		
		this.numero_transazioni = stat.getStatNumeroTransazioni().numeroTransazioni + "/" +
			stat.getStatNumeroTransazioni().numeroTransazioni_esitoOK + "/" +
			stat.getStatNumeroTransazioni().numeroTransazioni_esitoErrore;
		this.numero_transazioni_pd = stat.getStatNumeroTransazioni_PD().numeroTransazioni + "/" +
			stat.getStatNumeroTransazioni_PD().numeroTransazioni_esitoOK + "/" +
			stat.getStatNumeroTransazioni_PD().numeroTransazioni_esitoErrore;
		this.numero_transazioni_pa = stat.getStatNumeroTransazioni_PA().numeroTransazioni + "/" +
			stat.getStatNumeroTransazioni_PA().numeroTransazioni_esitoOK + "/" +
			stat.getStatNumeroTransazioni_PA().numeroTransazioni_esitoErrore;
		
		this.latenza_attraversamento = convertTime(stat.getStatLatenzaAttraversamento().latenzaMinimaAttraversamento) + "/" +
			convertTime(stat.getStatLatenzaAttraversamento().latenzaMediaAttraversamento) + "/" +
			convertTime(stat.getStatLatenzaAttraversamento().latenzaMassimaAttraversamento);
		this.latenza_attraversamento_request = convertTime(stat.getStatLatenzaAttraversamento_request().latenzaMinimaAttraversamento) + "/" +
			convertTime(stat.getStatLatenzaAttraversamento_request().latenzaMediaAttraversamento) + "/" +
			convertTime(stat.getStatLatenzaAttraversamento_request().latenzaMassimaAttraversamento);
		this.latenza_attraversamento_response = convertTime(stat.getStatLatenzaAttraversamento_response().latenzaMinimaAttraversamento) + "/" +
			convertTime(stat.getStatLatenzaAttraversamento_response().latenzaMediaAttraversamento) + "/" +
			convertTime(stat.getStatLatenzaAttraversamento_response().latenzaMassimaAttraversamento);
		
		this.latenza_attraversamento_pd = convertTime(stat.getStatLatenzaAttraversamento_PD().latenzaMinimaAttraversamento) + "/" +
			convertTime(stat.getStatLatenzaAttraversamento_PD().latenzaMediaAttraversamento) + "/" +
			convertTime(stat.getStatLatenzaAttraversamento_PD().latenzaMassimaAttraversamento);
		this.latenza_attraversamento_pd_request = convertTime(stat.getStatLatenzaAttraversamento_PD_request().latenzaMinimaAttraversamento) + "/" +
			convertTime(stat.getStatLatenzaAttraversamento_PD_request().latenzaMediaAttraversamento) + "/" +
			convertTime(stat.getStatLatenzaAttraversamento_PD_request().latenzaMassimaAttraversamento);
		this.latenza_attraversamento_pd_response = convertTime(stat.getStatLatenzaAttraversamento_PD_response().latenzaMinimaAttraversamento) + "/" +
			convertTime(stat.getStatLatenzaAttraversamento_PD_response().latenzaMediaAttraversamento) + "/" +
			convertTime(stat.getStatLatenzaAttraversamento_PD_response().latenzaMassimaAttraversamento);
		
		this.latenza_attraversamento_pa = convertTime(stat.getStatLatenzaAttraversamento_PA().latenzaMinimaAttraversamento) + "/" +
			convertTime(stat.getStatLatenzaAttraversamento_PA().latenzaMediaAttraversamento) + "/" +
			convertTime(stat.getStatLatenzaAttraversamento_PA().latenzaMassimaAttraversamento);
		this.latenza_attraversamento_pa_request = convertTime(stat.getStatLatenzaAttraversamento_PA_request().latenzaMinimaAttraversamento) + "/" +
			convertTime(stat.getStatLatenzaAttraversamento_PA_request().latenzaMediaAttraversamento) + "/" +
			convertTime(stat.getStatLatenzaAttraversamento_PA_request().latenzaMassimaAttraversamento);
		this.latenza_attraversamento_pa_response = convertTime(stat.getStatLatenzaAttraversamento_PA_response().latenzaMinimaAttraversamento) + "/" +
			convertTime(stat.getStatLatenzaAttraversamento_PA_response().latenzaMediaAttraversamento) + "/" +
			convertTime(stat.getStatLatenzaAttraversamento_PA_response().latenzaMassimaAttraversamento);
		
		this.dimensione_messaggi = convertSize(stat.getStatDimensioneMessaggio().dimensioneMinimaMessaggio) + "/" +
			convertSize(stat.getStatDimensioneMessaggio().dimensioneMediaMessaggio) + "/" +
			convertSize(stat.getStatDimensioneMessaggio().dimensioneMassimaMessaggio);
		this.dimensione_messaggi_in_request = convertSize(stat.getStatDimensioneMessaggio_in_request().dimensioneMinimaMessaggio) + "/" +
			convertSize(stat.getStatDimensioneMessaggio_in_request().dimensioneMediaMessaggio) + "/" +
			convertSize(stat.getStatDimensioneMessaggio_in_request().dimensioneMassimaMessaggio);
		this.dimensione_messaggi_out_request = convertSize(stat.getStatDimensioneMessaggio_out_request().dimensioneMinimaMessaggio) + "/" +
			convertSize(stat.getStatDimensioneMessaggio_out_request().dimensioneMediaMessaggio) + "/" +
			convertSize(stat.getStatDimensioneMessaggio_out_request().dimensioneMassimaMessaggio);
		this.dimensione_messaggi_in_response = convertSize(stat.getStatDimensioneMessaggio_in_response().dimensioneMinimaMessaggio) + "/" +
			convertSize(stat.getStatDimensioneMessaggio_in_response().dimensioneMediaMessaggio) + "/" +
			convertSize(stat.getStatDimensioneMessaggio_in_response().dimensioneMassimaMessaggio);
		this.dimensione_messaggi_out_response = convertSize(stat.getStatDimensioneMessaggio_out_response().dimensioneMinimaMessaggio) + "/" +
			convertSize(stat.getStatDimensioneMessaggio_out_response().dimensioneMediaMessaggio) + "/" +
			convertSize(stat.getStatDimensioneMessaggio_out_response().dimensioneMassimaMessaggio);
		
		this.dimensione_messaggi_pd = convertSize(stat.getStatDimensioneMessaggio_PD().dimensioneMinimaMessaggio) + "/" +
			convertSize(stat.getStatDimensioneMessaggio_PD().dimensioneMediaMessaggio) + "/" +
			convertSize(stat.getStatDimensioneMessaggio_PD().dimensioneMassimaMessaggio);
		this.dimensione_messaggi_pd_in_request = convertSize(stat.getStatDimensioneMessaggio_PD_in_request().dimensioneMinimaMessaggio) + "/" +
			convertSize(stat.getStatDimensioneMessaggio_PD_in_request().dimensioneMediaMessaggio) + "/" +
			convertSize(stat.getStatDimensioneMessaggio_PD_in_request().dimensioneMassimaMessaggio);
		this.dimensione_messaggi_pd_out_request = convertSize(stat.getStatDimensioneMessaggio_PD_out_request().dimensioneMinimaMessaggio) + "/" +
			convertSize(stat.getStatDimensioneMessaggio_PD_out_request().dimensioneMediaMessaggio) + "/" +
			convertSize(stat.getStatDimensioneMessaggio_PD_out_request().dimensioneMassimaMessaggio);
		this.dimensione_messaggi_pd_in_response = convertSize(stat.getStatDimensioneMessaggio_PD_in_response().dimensioneMinimaMessaggio) + "/" +
			convertSize(stat.getStatDimensioneMessaggio_PD_in_response().dimensioneMediaMessaggio) + "/" +
			convertSize(stat.getStatDimensioneMessaggio_PD_in_response().dimensioneMassimaMessaggio);
		this.dimensione_messaggi_pd_out_response = convertSize(stat.getStatDimensioneMessaggio_PD_out_response().dimensioneMinimaMessaggio) + "/" +
			convertSize(stat.getStatDimensioneMessaggio_PD_out_response().dimensioneMediaMessaggio) + "/" +
			convertSize(stat.getStatDimensioneMessaggio_PD_out_response().dimensioneMassimaMessaggio);
		
		this.dimensione_messaggi_pa = convertSize(stat.getStatDimensioneMessaggio_PA().dimensioneMinimaMessaggio) + "/" +
			convertSize(stat.getStatDimensioneMessaggio_PA().dimensioneMediaMessaggio) + "/" +
			convertSize(stat.getStatDimensioneMessaggio_PA().dimensioneMassimaMessaggio);
		this.dimensione_messaggi_pa_in_request = convertSize(stat.getStatDimensioneMessaggio_PA_in_request().dimensioneMinimaMessaggio) + "/" +
			convertSize(stat.getStatDimensioneMessaggio_PA_in_request().dimensioneMediaMessaggio) + "/" +
			convertSize(stat.getStatDimensioneMessaggio_PA_in_request().dimensioneMassimaMessaggio);
		this.dimensione_messaggi_pa_out_request = convertSize(stat.getStatDimensioneMessaggio_PA_out_request().dimensioneMinimaMessaggio) + "/" +
			convertSize(stat.getStatDimensioneMessaggio_PA_out_request().dimensioneMediaMessaggio) + "/" +
			convertSize(stat.getStatDimensioneMessaggio_PA_out_request().dimensioneMassimaMessaggio);
		this.dimensione_messaggi_pa_in_response = convertSize(stat.getStatDimensioneMessaggio_PA_in_response().dimensioneMinimaMessaggio) + "/" +
			convertSize(stat.getStatDimensioneMessaggio_PA_in_response().dimensioneMediaMessaggio) + "/" +
			convertSize(stat.getStatDimensioneMessaggio_PA_in_response().dimensioneMassimaMessaggio);
		this.dimensione_messaggi_pa_out_response = convertSize(stat.getStatDimensioneMessaggio_PA_out_response().dimensioneMinimaMessaggio) + "/" +
			convertSize(stat.getStatDimensioneMessaggio_PA_out_response().dimensioneMediaMessaggio) + "/" +
			convertSize(stat.getStatDimensioneMessaggio_PA_out_response().dimensioneMassimaMessaggio);
		
		return "Refresh dati statistici effettuato";
	}
	
	private String convertTime(long time){
		return Utilities.convertSystemTimeIntoString_millisecondi(time, true);
	}
	private String convertSize(long size){
		return Utilities.convertBytesToFormatString(size);
	}
	
	public String reset(){
		StatisticsCollection.reset();
		refresh();
		return "Reset dati statistici effettuato";
	}

}
