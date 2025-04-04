/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it).
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 3, as published by
 * the Free Software Foundation.
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


package org.openspcoop2.core.tracciamento.ws.client.traccia.search;

/**
 * Please modify this class to meet your needs
 * This class is not complete
 */

import java.io.File;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;

import org.openspcoop2.core.tracciamento.DominioIdTraccia;

/**
 * This class was generated by Apache CXF 2.7.4
 * 2015-02-18T16:20:28.623+01:00
 * Generated source version: 2.7.4
 * 
 */
public final class Traccia_TracciaPortSoap11_Client {

	private static final QName SERVICE_NAME = new QName("http://www.openspcoop2.org/core/tracciamento/management", "TracciaSoap11Service");

	private Traccia_TracciaPortSoap11_Client() {
	}

	public static void main(String args[]) throws java.lang.Exception {
		URL wsdlURL = TracciaSoap11Service.WSDL_LOCATION;
		if (args.length > 0 && args[0] != null && !"".equals(args[0])) { 
			File wsdlFile = new File(args[0]);
			try {
				if (wsdlFile.exists()) {
					wsdlURL = wsdlFile.toURI().toURL();
				} else {
					wsdlURL = new URL(args[0]);
				}
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
		}

		TracciaSoap11Service ss = new TracciaSoap11Service(wsdlURL, Traccia_TracciaPortSoap11_Client.SERVICE_NAME);
		Traccia port = ss.getTracciaPortSoap11();

		new org.openspcoop2.core.tracciamento.ws.client.utils.RequestContextUtils("traccia.soap11").addRequestContextParameters((javax.xml.ws.BindingProvider)port);  

		long _count__return = 0;
		{
			System.out.println("Invoking count...");
			org.openspcoop2.core.tracciamento.ws.client.traccia.search.SearchFilterTraccia _count_filter = new SearchFilterTraccia();
			try {
				_count__return = port.count(_count_filter);
				System.out.println("count.result=" + _count__return);

			} catch (TracciamentoNotImplementedException_Exception e) { 
				System.out.println("Expected exception: tracciamento-not-implemented-exception has occurred.");
				System.out.println(e.toString());
			} catch (TracciamentoNotAuthorizedException_Exception e) { 
				System.out.println("Expected exception: tracciamento-not-authorized-exception has occurred.");
				System.out.println(e.toString());
			} catch (TracciamentoServiceException_Exception e) { 
				System.out.println("Expected exception: tracciamento-service-exception has occurred.");
				System.out.println(e.toString());
			}
		}

		if(_count__return>0){


			org.openspcoop2.core.tracciamento.Traccia tracciaCampione = null;
			{
				System.out.println("Invoking findAll...");
				org.openspcoop2.core.tracciamento.ws.client.traccia.search.SearchFilterTraccia _findAll_filter = new SearchFilterTraccia();
				try {
					_findAll_filter.setLimit(new BigInteger("10"));
					java.util.List<org.openspcoop2.core.tracciamento.Traccia> _findAll__return = port.findAll(_findAll_filter);
					System.out.println("findAll.result=" + _findAll__return.size());

					for (org.openspcoop2.core.tracciamento.Traccia traccia : _findAll__return) {
						if(tracciaCampione==null){
							tracciaCampione = traccia;
						}
						System.out.println("["+traccia.getOraRegistrazione()+"]["+traccia.getTipo()+"]["+traccia.getBusta().getIdentificativo()+"]["+traccia.getDominio().getIdentificativoPorta()+"]");
					}

				} catch (TracciamentoNotImplementedException_Exception e) { 
					System.out.println("Expected exception: tracciamento-not-implemented-exception has occurred.");
					System.out.println(e.toString());
				} catch (TracciamentoNotAuthorizedException_Exception e) { 
					System.out.println("Expected exception: tracciamento-not-authorized-exception has occurred.");
					System.out.println(e.toString());
				} catch (TracciamentoServiceException_Exception e) { 
					System.out.println("Expected exception: tracciamento-service-exception has occurred.");
					System.out.println(e.toString());
				}
			}


			{
				System.out.println("Invoking get...");
				org.openspcoop2.core.tracciamento.IdTraccia _get_idTraccia = new org.openspcoop2.core.tracciamento.IdTraccia();
				try {
					_get_idTraccia.setIdentificativo(tracciaCampione.getBusta().getIdentificativo());
					DominioIdTraccia idDominioTraccia = new DominioIdTraccia();
					idDominioTraccia.setIdentificativoPorta(tracciaCampione.getDominio().getIdentificativoPorta());
					_get_idTraccia.setDominio(idDominioTraccia);
					org.openspcoop2.core.tracciamento.Traccia _get__return = port.get(_get_idTraccia);
					if(_get__return.equals(tracciaCampione)){
						System.out.println("get.result=OK");
					}
					else{
						StringBuilder bf = new StringBuilder();
						_get__return.diff(tracciaCampione, bf);
						System.out.println("get.result=KO "+bf.toString());
					}

				} catch (TracciamentoMultipleResultException_Exception e) { 
					System.out.println("Expected exception: tracciamento-multiple-result-exception has occurred.");
					System.out.println(e.toString());
				} catch (TracciamentoNotImplementedException_Exception e) { 
					System.out.println("Expected exception: tracciamento-not-implemented-exception has occurred.");
					System.out.println(e.toString());
				} catch (TracciamentoServiceException_Exception e) { 
					System.out.println("Expected exception: tracciamento-service-exception has occurred.");
					System.out.println(e.toString());
				} catch (TracciamentoNotFoundException_Exception e) { 
					System.out.println("Expected exception: tracciamento-not-found-exception has occurred.");
					System.out.println(e.toString());
				} catch (TracciamentoNotAuthorizedException_Exception e) { 
					System.out.println("Expected exception: tracciamento-not-authorized-exception has occurred.");
					System.out.println(e.toString());
				}
			}

			{
				System.out.println("Invoking get NotExists Expected...");
				org.openspcoop2.core.tracciamento.IdTraccia _get_idTraccia = new org.openspcoop2.core.tracciamento.IdTraccia();
				try {
					_get_idTraccia.setIdentificativo(tracciaCampione.getBusta().getIdentificativo()+"_INESISTENTE");
					DominioIdTraccia idDominioTraccia = new DominioIdTraccia();
					idDominioTraccia.setIdentificativoPorta(tracciaCampione.getDominio().getIdentificativoPorta());
					_get_idTraccia.setDominio(idDominioTraccia);
					port.get(_get_idTraccia);
					throw new Exception("Attesa not found exception"); 

				} catch (TracciamentoMultipleResultException_Exception e) { 
					System.out.println("Expected exception: tracciamento-multiple-result-exception has occurred.");
					System.out.println(e.toString());
				} catch (TracciamentoNotImplementedException_Exception e) { 
					System.out.println("Expected exception: tracciamento-not-implemented-exception has occurred.");
					System.out.println(e.toString());
				} catch (TracciamentoServiceException_Exception e) { 
					System.out.println("Expected exception: tracciamento-service-exception has occurred.");
					System.out.println(e.toString());
				} catch (TracciamentoNotFoundException_Exception e) { 
					System.out.println("Invoking get NotExists Expected OK");
				} catch (TracciamentoNotAuthorizedException_Exception e) { 
					System.out.println("Expected exception: tracciamento-not-authorized-exception has occurred.");
					System.out.println(e.toString());
				}
			}

			{
				System.out.println("Invoking exists...");
				org.openspcoop2.core.tracciamento.IdTraccia _exists_idTraccia = new org.openspcoop2.core.tracciamento.IdTraccia();
				try {
					_exists_idTraccia.setIdentificativo(tracciaCampione.getBusta().getIdentificativo());
					DominioIdTraccia idDominioTraccia = new DominioIdTraccia();
					idDominioTraccia.setIdentificativoPorta(tracciaCampione.getDominio().getIdentificativoPorta());
					_exists_idTraccia.setDominio(idDominioTraccia);
					boolean _exists__return = port.exists(_exists_idTraccia);
					System.out.println("exists.result (ok)=" + _exists__return);
					if(!_exists__return){
						throw new Exception("Atteso true");
					}

					_exists_idTraccia.setIdentificativo(tracciaCampione.getBusta().getIdentificativo()+"ERRATO");
					_exists__return = port.exists(_exists_idTraccia);
					System.out.println("exists.result (ko)=" + _exists__return);
					if(_exists__return){
						throw new Exception("Atteso false");
					}

				} catch (TracciamentoMultipleResultException_Exception e) { 
					System.out.println("Expected exception: tracciamento-multiple-result-exception has occurred.");
					System.out.println(e.toString());
				} catch (TracciamentoNotImplementedException_Exception e) { 
					System.out.println("Expected exception: tracciamento-not-implemented-exception has occurred.");
					System.out.println(e.toString());
				} catch (TracciamentoNotAuthorizedException_Exception e) { 
					System.out.println("Expected exception: tracciamento-not-authorized-exception has occurred.");
					System.out.println(e.toString());
				} catch (TracciamentoServiceException_Exception e) { 
					System.out.println("Expected exception: tracciamento-service-exception has occurred.");
					System.out.println(e.toString());
				}
			}

			
			{
				System.out.println("Invoking find...");
				org.openspcoop2.core.tracciamento.ws.client.traccia.search.SearchFilterTraccia _find_filter = new SearchFilterTraccia();
				try {
					_find_filter.setBusta(new Busta());
					_find_filter.getBusta().setIdentificativo(tracciaCampione.getBusta().getIdentificativo());
					_find_filter.setDominio(new Dominio());
					_find_filter.getDominio().setIdentificativoPorta(tracciaCampione.getDominio().getIdentificativoPorta());
					
					org.openspcoop2.core.tracciamento.Traccia _find__return = port.find(_find_filter);
					if(_find__return.equals(tracciaCampione)){
						System.out.println("find.result=OK");
					}
					else{
						StringBuilder bf = new StringBuilder();
						_find__return.diff(tracciaCampione, bf);
						System.out.println("find.result=KO "+bf.toString());
					}

				} catch (TracciamentoMultipleResultException_Exception e) { 
					System.out.println("Expected exception: tracciamento-multiple-result-exception has occurred.");
					System.out.println(e.toString());
				} catch (TracciamentoNotImplementedException_Exception e) { 
					System.out.println("Expected exception: tracciamento-not-implemented-exception has occurred.");
					System.out.println(e.toString());
				} catch (TracciamentoServiceException_Exception e) { 
					System.out.println("Expected exception: tracciamento-service-exception has occurred.");
					System.out.println(e.toString());
				} catch (TracciamentoNotFoundException_Exception e) { 
					System.out.println("Expected exception: tracciamento-not-found-exception has occurred.");
					System.out.println(e.toString());
				} catch (TracciamentoNotAuthorizedException_Exception e) { 
					System.out.println("Expected exception: tracciamento-not-authorized-exception has occurred.");
					System.out.println(e.toString());
				}
			}
			
			{
				System.out.println("Invoking find NotExists...");
				org.openspcoop2.core.tracciamento.ws.client.traccia.search.SearchFilterTraccia _find_filter = new SearchFilterTraccia();
				try {
					_find_filter.setBusta(new Busta());
					_find_filter.getBusta().setIdentificativo(tracciaCampione.getBusta().getIdentificativo()+"Errato");
					_find_filter.setDominio(new Dominio());
					_find_filter.getDominio().setIdentificativoPorta(tracciaCampione.getDominio().getIdentificativoPorta());
					
					port.find(_find_filter);
					throw new Exception("Attesa not found exception"); 

				} catch (TracciamentoMultipleResultException_Exception e) { 
					System.out.println("Expected exception: tracciamento-multiple-result-exception has occurred.");
					System.out.println(e.toString());
				} catch (TracciamentoNotImplementedException_Exception e) { 
					System.out.println("Expected exception: tracciamento-not-implemented-exception has occurred.");
					System.out.println(e.toString());
				} catch (TracciamentoServiceException_Exception e) { 
					System.out.println("Expected exception: tracciamento-service-exception has occurred.");
					System.out.println(e.toString());
				} catch (TracciamentoNotFoundException_Exception e) { 
					System.out.println("Invoking get NotExists Expected OK");
				} catch (TracciamentoNotAuthorizedException_Exception e) { 
					System.out.println("Expected exception: tracciamento-not-authorized-exception has occurred.");
					System.out.println(e.toString());
				}
			}

			{
				System.out.println("Invoking findAllIds...");
				org.openspcoop2.core.tracciamento.ws.client.traccia.search.SearchFilterTraccia _findAllIds_filter = new SearchFilterTraccia();
				try {
					_findAllIds_filter.setLimit(new BigInteger("10"));
					_findAllIds_filter.setDescOrder(true);
					java.util.List<org.openspcoop2.core.tracciamento.IdTraccia> _findAll__return = port.findAllIds(_findAllIds_filter);
					System.out.println("findAll.result=" + _findAll__return.size());

					for (org.openspcoop2.core.tracciamento.IdTraccia traccia : _findAll__return) {
						System.out.println("["+traccia.getIdentificativo()+"]["+traccia.getDominio().getIdentificativoPorta()+"]");
					}
					
				} catch (TracciamentoNotImplementedException_Exception e) { 
					System.out.println("Expected exception: tracciamento-not-implemented-exception has occurred.");
					System.out.println(e.toString());
				} catch (TracciamentoNotAuthorizedException_Exception e) { 
					System.out.println("Expected exception: tracciamento-not-authorized-exception has occurred.");
					System.out.println(e.toString());
				} catch (TracciamentoServiceException_Exception e) { 
					System.out.println("Expected exception: tracciamento-service-exception has occurred.");
					System.out.println(e.toString());
				}
			}
			
			
			{
				System.out.println("Invoking findAll (extended search)...");
				org.openspcoop2.core.tracciamento.ws.client.traccia.search.SearchFilterTraccia _findAll_filter = new SearchFilterTraccia();
				try {
					_findAll_filter.setLimit(new BigInteger("15"));
					_findAll_filter.setOffset(new BigInteger("0"));
					
					Busta busta = new Busta();
					Soggetto mittente = new Soggetto();
					mittente.setIdentificativoPorta(tracciaCampione.getBusta().getMittente().getIdentificativoPorta());
					mittente.setIdentificativo(new SoggettoIdentificativo());
					mittente.getIdentificativo().setTipo(tracciaCampione.getBusta().getMittente().getIdentificativo().getTipo());
					mittente.getIdentificativo().setBase(tracciaCampione.getBusta().getMittente().getIdentificativo().getBase());
					busta.setMittente(mittente);
					Soggetto destinatario = new Soggetto();
					destinatario.setIdentificativoPorta(tracciaCampione.getBusta().getDestinatario().getIdentificativoPorta());
					destinatario.setIdentificativo(new SoggettoIdentificativo());
					destinatario.getIdentificativo().setTipo(tracciaCampione.getBusta().getDestinatario().getIdentificativo().getTipo());
					destinatario.getIdentificativo().setBase(tracciaCampione.getBusta().getDestinatario().getIdentificativo().getBase());
					busta.setDestinatario(destinatario);
					Servizio servizio = new Servizio();
					servizio.setBase(tracciaCampione.getBusta().getServizio().getBase());
					servizio.setTipo(tracciaCampione.getBusta().getServizio().getTipo());
					servizio.setVersione(new BigInteger(""+tracciaCampione.getBusta().getServizio().getVersione()));
					busta.setServizio(servizio);
					busta.setAzione(tracciaCampione.getBusta().getAzione());
					_findAll_filter.setBusta(busta);
					
					_findAll_filter.setCorrelazioneApplicativaAndMatch(true);
					_findAll_filter.setIdentificativoCorrelazioneRichiesta(tracciaCampione.getIdentificativoCorrelazioneRichiesta());
					_findAll_filter.setIdentificativoCorrelazioneRisposta(tracciaCampione.getIdentificativoCorrelazioneRisposta());
					
					_findAll_filter.setDescOrder(true);
					
					_findAll_filter.setDominio(new Dominio());
					_findAll_filter.getDominio().setFunzione(tracciaCampione.getDominio().getFunzione());
					_findAll_filter.getDominio().setIdentificativoPorta(tracciaCampione.getDominio().getIdentificativoPorta());
					_findAll_filter.getDominio().setSoggetto(new DominioSoggetto());
					_findAll_filter.getDominio().getSoggetto().setBase(tracciaCampione.getDominio().getSoggetto().getBase());
					_findAll_filter.getDominio().getSoggetto().setTipo(tracciaCampione.getDominio().getSoggetto().getTipo());
					
					GregorianCalendar cMax = (GregorianCalendar) Calendar.getInstance();
					cMax.setTime(new Date(tracciaCampione.getOraRegistrazione().getTime()+1000));
					XMLGregorianCalendar gcMax = DatatypeFactory.newInstance().newXMLGregorianCalendar(cMax);
					_findAll_filter.setOraRegistrazioneMax(gcMax);
					
					GregorianCalendar cMin = (GregorianCalendar) Calendar.getInstance();
					cMin.setTime(new Date(tracciaCampione.getOraRegistrazione().getTime()-1000));
					XMLGregorianCalendar gcMin = DatatypeFactory.newInstance().newXMLGregorianCalendar(cMin);
					_findAll_filter.setOraRegistrazioneMin(gcMin);
					
					_findAll_filter.setRicercaSoloBusteErrore(tracciaCampione.getBusta().getEccezioni()!=null && tracciaCampione.getBusta().getEccezioni().sizeEccezioneList()>0); 
					
					_findAll_filter.setTipo(tracciaCampione.getTipo());
					
					java.util.List<org.openspcoop2.core.tracciamento.Traccia> _findAll__return = port.findAll(_findAll_filter);
					System.out.println("findAll.result=" + _findAll__return.size());

					for (org.openspcoop2.core.tracciamento.Traccia traccia : _findAll__return) {
						System.out.println("["+traccia.getOraRegistrazione()+"]["+traccia.getTipo()+"]["+traccia.getBusta().getIdentificativo()+"]["+traccia.getDominio().getIdentificativoPorta()+"]");
					}

				} catch (TracciamentoNotImplementedException_Exception e) { 
					System.out.println("Expected exception: tracciamento-not-implemented-exception has occurred.");
					System.out.println(e.toString());
				} catch (TracciamentoNotAuthorizedException_Exception e) { 
					System.out.println("Expected exception: tracciamento-not-authorized-exception has occurred.");
					System.out.println(e.toString());
				} catch (TracciamentoServiceException_Exception e) { 
					System.out.println("Expected exception: tracciamento-service-exception has occurred.");
					System.out.println(e.toString());
				}
			}
			
		}


		System.exit(0);
	}

}
