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



package org.openspcoop2.core.registry.driver.uddi;

import java.util.Vector;

import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDAccordoCooperazione;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziException;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziNotFound;
import org.openspcoop2.core.registry.driver.IDAccordoCooperazioneFactory;
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
import org.uddi4j.UDDIException;
import org.uddi4j.client.UDDIProxy;
import org.uddi4j.datatype.Name;
import org.uddi4j.datatype.OverviewDoc;
import org.uddi4j.datatype.OverviewURL;
import org.uddi4j.datatype.binding.AccessPoint;
import org.uddi4j.datatype.binding.BindingTemplate;
import org.uddi4j.datatype.binding.BindingTemplates;
import org.uddi4j.datatype.binding.TModelInstanceDetails;
import org.uddi4j.datatype.binding.TModelInstanceInfo;
import org.uddi4j.datatype.business.BusinessEntity;
import org.uddi4j.datatype.service.BusinessService;
import org.uddi4j.datatype.service.BusinessServices;
import org.uddi4j.datatype.tmodel.TModel;
import org.uddi4j.response.AuthToken;
import org.uddi4j.response.BindingDetail;
import org.uddi4j.response.BusinessDetail;
import org.uddi4j.response.BusinessInfo;
import org.uddi4j.response.BusinessList;
import org.uddi4j.response.ServiceDetail;
import org.uddi4j.response.ServiceInfo;
import org.uddi4j.response.ServiceList;
import org.uddi4j.response.TModelDetail;
import org.uddi4j.response.TModelInfo;
import org.uddi4j.response.TModelList;
import org.uddi4j.transport.TransportException;
import org.uddi4j.util.CategoryBag;
import org.uddi4j.util.FindQualifier;
import org.uddi4j.util.FindQualifiers;
import org.uddi4j.util.IdentifierBag;
import org.uddi4j.util.KeyedReference;
import org.uddi4j.util.TModelBag;
import org.uddi4j.util.TModelKey;


/**
 * Classe utilizzata per interagire con un registro UDDI che implementa le specifiche SICA.
 *
 * @author Anedda Valentino
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class UDDILib
{
	/* ********  F I E L D S  P R I V A T I  ******** */

	/** Dichiarazione delle chiavi di tassonomia*/	
	protected String tmkID="uuid:b6ba2f0c-b4e5-40c4-a4f4-6212aa8d389f";//chiave di tassonomia per identificazione

	/** Numero massimo di oggetti da ritornare */
	private static int MAX_SEARCH = Integer.MAX_VALUE;
	
	/** UDDI Proxy */
	protected UDDIProxy proxy=null;
	/** Username */
	protected String username;
	/** Password */
	protected String password;


	/** Indicazione di una corretta creazione */
	public boolean create = false;
	/** Motivo di errore di una scorretta creazione */
	protected String error;

	/** TModel di OpenSPCoop */
	public static final String TMODEL_OPENSPCOOP = "OpenSPCoop:SPCoopIdentifier";
	
	public static final String ACCORDO_SERVIZIO = "AccordoServizio";
	public static final String PORTA_DOMINIO = "PortaDominio";
	public static final String PORTA_DOMINIO_PREFIX = "PdD@";
	public static final String ACCORDO_COOPERAZIONE = "AccordoCooperazione";
	public static final String ACCORDO_COOPERAZIONE_PREFIX = "AccordoCooperazione@";
	
	// Factory
	private IDAccordoFactory idAccordoFactory = IDAccordoFactory.getInstance();
	private IDAccordoCooperazioneFactory idAccordoCooperazioneFactory = IDAccordoCooperazioneFactory.getInstance();

	
	/**
	 * In caso di avvenuto errore nel costruttore, questo metodo ritorna il motivo dell'errore.
	 *
	 * @return motivo dell'errore (se avvenuto in fase di consegna).
	 * 
	 */
	public String getErrore(){
		return this.error;
	}


	/* ********  C O S T R U T T O R E  ******** */

	/**
	 * Costruttore.
	 *
	 * @param inquiry URL utilizzata per interrogare il registro UDDI.
	 * @param publish URL utilizzata per pubblicare sul registro UDDI.
	 * @param user UserID necessaria per l'autorizzazione della pubblicazione
	 * @param password Password necessaria per l'autorizzazione della pubblicazione
	 */    
	public UDDILib(String inquiry, String publish, String user, String password){
		try{
			this.proxy = new UDDIProxy();
			if(inquiry != null)
				this.proxy.setInquiryURL(inquiry);
			if(publish != null)
				this.proxy.setPublishURL(publish);

			this.username = user;
			this.password = password;

			// per verificare il corretto funzionamento
			//this.create = checkProxy();
			this.create = true;
		}
		catch (java.net.MalformedURLException e){
			this.error = e.getMessage();
			this.create = false;
		}
		catch (Exception e){
			this.error = e.getMessage();
			this.create = false;
		}
	}	
	/**
	 * Costruttore.
	 *
	 * @param inquiry URL utilizzata per interrogare il registro UDDI.
	 * @param user UserID necessaria per l'autorizzazione della pubblicazione
	 * @param password Password necessaria per l'autorizzazione della pubblicazione
	 */    
	public UDDILib(String inquiry, String user, String password){
		this(inquiry,null,user,password);
	}	
	/**
	 * Costruttore.
	 *
	 * @param inquiry URL utilizzata per interrogare il registro UDDI.
	 */
	public UDDILib(String inquiry){
		this(inquiry,null,null,null);
	}	







	
	
	
	
	
	
	
	
	/* ******** UTILITY GENERICHE   ******** */
	
	protected TModel getTModel(String tipoOggetto,String nome) throws DriverRegistroServiziException,DriverRegistroServiziNotFound{
		if ( nome==null )
			throw new DriverRegistroServiziException("[UDDILib.getTModel("+tipoOggetto+")]: Alcuni parametri non definiti");
		
		TModel tm=null;
		try{
			// Filtro per nome esatto della TModel
			FindQualifiers findQualifiers = new FindQualifiers();
			Vector<FindQualifier> qualifier = new Vector<FindQualifier>();
			qualifier.add(new FindQualifier(FindQualifier.exactNameMatch));
			qualifier.add(new FindQualifier(FindQualifier.caseSensitiveMatch));
			findQualifiers.setFindQualifierVector(qualifier);
			
			TModelList tmodelList = this.proxy.find_tModel(nome,null,null,findQualifiers,1);
			if(tmodelList.getTModelInfos()==null || tmodelList.getTModelInfos().size()==0)
				throw new DriverRegistroServiziNotFound("TModel("+tipoOggetto+") non trovata");
			Vector<?> tmodelInfoVector = tmodelList.getTModelInfos().getTModelInfoVector();
			TModelInfo tmodelInfo=(TModelInfo) tmodelInfoVector.elementAt(0);
			TModelDetail tmd = this.proxy.get_tModelDetail(tmodelInfo.getTModelKey());
			Vector<?> tmv = tmd.getTModelVector();
			tm = (TModel) tmv.elementAt(0);
		}
		catch (DriverRegistroServiziNotFound e){
			throw e;
		}
		catch (UDDIException e){
			throw new DriverRegistroServiziException("[UDDILib.getTModel("+tipoOggetto+")]: "+e.getMessage(),e);
		}
		catch (TransportException e){
			throw new DriverRegistroServiziException("[UDDILib.getTModel("+tipoOggetto+")]: "+e.getMessage(),e);
		}
		catch (Exception e){
			throw new DriverRegistroServiziException("[UDDILib.getTModel("+tipoOggetto+")]: "+e.getMessage(),e);
		}
		return tm;
	}
	
	private TModel[] getTModelByFilter(String tipoOggetto,String searchNome,String urlRepository,boolean ricercaEsatta) throws DriverRegistroServiziException,DriverRegistroServiziNotFound{
		try{
			String nomeRicerca = null;
			if(searchNome!=null)
				nomeRicerca = searchNome;
			else
				nomeRicerca = "%";
			
			// Filtro per nome esatto della TModel
			FindQualifiers findQualifiers = null;
			if( ricercaEsatta && nomeRicerca.equals("%")==false){
				findQualifiers = new FindQualifiers();
				Vector<FindQualifier> qualifier = new Vector<FindQualifier>();
				qualifier.add(new FindQualifier(FindQualifier.exactNameMatch));
				qualifier.add(new FindQualifier(FindQualifier.caseSensitiveMatch));
				findQualifiers.setFindQualifierVector(qualifier);
			}
			
			TModelList tmodelList = this.proxy.find_tModel(nomeRicerca,null,null,findQualifiers,UDDILib.MAX_SEARCH);
			Vector<TModel> tmodels = new Vector<TModel>();
			// TModel vector lista.
			if(tmodelList.getTModelInfos()!=null){
				Vector<?> tmodelInfoVector = tmodelList.getTModelInfos().getTModelInfoVector();
				for(int i=0; i<tmodelInfoVector.size(); i++){
					TModelInfo tmodelInfo=(TModelInfo) tmodelInfoVector.elementAt(i);
					TModelDetail tmd = this.proxy.get_tModelDetail(tmodelInfo.getTModelKey());
					Vector<?> tmv = tmd.getTModelVector();
					TModel t = (TModel) tmv.elementAt(0);
					// Filtro le tmodel per il repository
					if(urlRepository!=null){
						if(t.getOverviewDoc()!=null  && t.getOverviewDoc().getOverviewURLString()!=null &&
								t.getOverviewDoc().getOverviewURLString().startsWith(urlRepository)	){
							if(UDDILib.ACCORDO_SERVIZIO.equals(tipoOggetto)){
								if(t.getNameString().startsWith(UDDILib.ACCORDO_COOPERAZIONE_PREFIX)==false  &&  t.getNameString().startsWith(UDDILib.PORTA_DOMINIO_PREFIX)==false){
									tmodels.add(t);
								}
							}else{
								tmodels.add(t);
							}
						}
					}else{
						if(UDDILib.ACCORDO_SERVIZIO.equals(tipoOggetto)){
							if(t.getNameString().startsWith(UDDILib.ACCORDO_COOPERAZIONE_PREFIX)==false  &&  t.getNameString().startsWith(UDDILib.PORTA_DOMINIO_PREFIX)==false){
								tmodels.add(t);
							}
						}else{
							tmodels.add(t);
						}
					}
				}
			}
			if(tmodels.size()>0){
				TModel[] tm = new TModel[1];
				tm = tmodels.toArray(tm);
				return tm;
			}else{
				throw new DriverRegistroServiziNotFound("Non sono state trovate TModelByFilter("+tipoOggetto+") nel registro UDDI con nome: "+searchNome);
			}
		}
		catch (UDDIException e){
			throw new DriverRegistroServiziException("[UDDILib.getTModelByFilter("+tipoOggetto+")]: "+e.getMessage(),e);
		}
		catch (TransportException e){
			throw new DriverRegistroServiziException("[UDDILib.getTModelByFilter("+tipoOggetto+")]: "+e.getMessage(),e);
		}
		catch (DriverRegistroServiziNotFound e){
			throw e;
		}
		catch (Exception e){
			throw new DriverRegistroServiziException("[UDDILib.getTModelByFilter("+tipoOggetto+")]: "+e.getMessage(),e);
		}
	}

	private void createTModel(String tipoOggetto,String nome, String url) throws DriverRegistroServiziException{

		if ( (nome==null) || (url==null) )
			throw new DriverRegistroServiziException("[UDDILib.createTModel("+tipoOggetto+")]: Alcuni parametri non definiti");
		try{
			AuthToken token = this.proxy.get_authToken(this.username,this.password);

			Vector<TModel> tms = new Vector<TModel>();
			TModel tm = new TModel("", nome);
			
			OverviewDoc od = new OverviewDoc();
			OverviewURL ou = new OverviewURL();
			ou.setText(url);
			od.setOverviewURL(ou);
			tm.setOverviewDoc(od);

			tms.add(tm);
			this.proxy.save_tModel(token.getAuthInfoString(),tms);
		}
		catch (UDDIException e){
			throw new DriverRegistroServiziException("[UDDILib.createTModel("+tipoOggetto+")]: "+e.getMessage(),e);
		}
		catch (TransportException e){
			throw new DriverRegistroServiziException("[UDDILib.createTModel("+tipoOggetto+")]: "+e.getMessage(),e);
		}
		catch (Exception e){
			throw new DriverRegistroServiziException("[UDDILib.createTModel("+tipoOggetto+")]: "+e.getMessage(),e);
		}
	}
	
	private void updateTModel(String tipoOggetto,String nomeOLD,String nomeNEW,String url) throws DriverRegistroServiziException,DriverRegistroServiziNotFound{
		if ( (nomeOLD==null) || (url==null) || (nomeNEW==null))
			throw new DriverRegistroServiziException("[UDDILib.updateTModel("+tipoOggetto+")]: Alcuni parametri non definiti");
	
		TModel tm=null;
		try{
			AuthToken token = this.proxy.get_authToken(this.username,this.password);

			tm = getTModel(tipoOggetto, nomeOLD);

			OverviewDoc od = tm.getOverviewDoc();
			OverviewURL ou = od.getOverviewURL();
			ou.setText(url);
			od.setOverviewURL(ou);
			tm.setOverviewDoc(od);

			tm.setName(nomeNEW);
			Vector<TModel> tms = new Vector<TModel>();
			tms.add(tm);
			this.proxy.save_tModel(token.getAuthInfoString(),tms);
		}
		catch(DriverRegistroServiziNotFound e){
			throw e;
		}
		catch (UDDIException e){
			throw new DriverRegistroServiziException("[UDDILib.updateTModel("+tipoOggetto+")]: "+e.getMessage(),e);
		}
		catch (TransportException e){
			throw new DriverRegistroServiziException("[UDDILib.updateTModel("+tipoOggetto+")]: "+e.getMessage(),e);
		}
		catch (Exception e){
			throw new DriverRegistroServiziException("[UDDILib.updateTModel("+tipoOggetto+")]: "+e.getMessage(),e);
		}
	}
	
	private void deleteTModel(String tipoOggetto,String nome) throws DriverRegistroServiziException{

		if ( nome==null )
			throw new DriverRegistroServiziException("[UDDILib.deleteTModel("+tipoOggetto+")]: Alcuni parametri non definiti");
		
		try{
			AuthToken token = this.proxy.get_authToken(this.username,this.password);
			
			// Filtro per nome esatto della TModel
			FindQualifiers findQualifiers = new FindQualifiers();
			Vector<FindQualifier> qualifier = new Vector<FindQualifier>();
			qualifier.add(new FindQualifier(FindQualifier.exactNameMatch));
			qualifier.add(new FindQualifier(FindQualifier.caseSensitiveMatch));
			findQualifiers.setFindQualifierVector(qualifier);
			
			TModelList tmodelList = this.proxy.find_tModel(nome,null,null,findQualifiers,1);
			Vector<?> tmodelInfoVector = tmodelList.getTModelInfos().getTModelInfoVector();
			TModelInfo tmodelInfo=(TModelInfo) tmodelInfoVector.elementAt(0);
			this.proxy.delete_tModel(token.getAuthInfoString(),tmodelInfo.getTModelKey());
		
		}catch(Exception e){
			throw new DriverRegistroServiziException("[UDDILib.deleteTModel("+tipoOggetto+")]: "+e.getMessage(),e);
		}
	}
	
	private void updateUrlXmlTModel(String tipoOggetto,String nome, String url) throws DriverRegistroServiziException,DriverRegistroServiziNotFound{
		
		if ( (nome==null) || (url==null) )
			throw new DriverRegistroServiziException("[UDDILib.updateUrlXmlTModel("+tipoOggetto+")]: Alcuni parametri non definiti");
	
		try{
			AuthToken token = this.proxy.get_authToken(this.username,this.password);

			TModel tm = getTModel(tipoOggetto,nome);
			Vector<TModel> tms = new Vector<TModel>();
			OverviewDoc od = tm.getOverviewDoc();
			OverviewURL ou = od.getOverviewURL();
			ou.setText(url);
			od.setOverviewURL(ou);
			tm.setOverviewDoc(od);
			tms.add(tm);
			this.proxy.save_tModel(token.getAuthInfoString(),tms);
		}catch(DriverRegistroServiziNotFound e){
			throw e;
		}catch(Exception e){
			throw new DriverRegistroServiziException("[UDDILib.updateUrlXmlTModel("+tipoOggetto+")]: "+e.getMessage(),e);
		}
	}
	
	



	
	
	
	
	
	
	
	/* ******** M E T O D I   A C C O R D O    D I    C O O P E R A Z I O N E   ******** */


	
	/**
	 * Si occupa di recuperare il TModel che registra l'accordo di cooperazione con nome <var>nome</var>
	 *
	 * @param idAccordo ID che identifica l'accordo di cooperazione
	 * @return la TModel che registra l'accordo di cooperazione
	 */
	public TModel getAccordoCooperazione(IDAccordoCooperazione idAccordo) throws DriverRegistroServiziException,DriverRegistroServiziNotFound{
		String uriAccordo = this.idAccordoCooperazioneFactory.getUriFromIDAccordo(idAccordo);
		return this.getTModel(UDDILib.ACCORDO_COOPERAZIONE, UDDILib.ACCORDO_COOPERAZIONE_PREFIX+uriAccordo);
	}
	
	/**
	 * Si occupa di recuperare le TModels che soddisfano il filtro
	 *
	 * @param urlRepository Url del Repository
	 * @return una lista di TModel che registra l'accordo di cooperazione
	 */
	public TModel[] getAccordiCooperazione(String urlRepository) throws DriverRegistroServiziException,DriverRegistroServiziNotFound{
		return this.getTModelByFilter(UDDILib.ACCORDO_COOPERAZIONE, UDDILib.ACCORDO_COOPERAZIONE_PREFIX, urlRepository,false);
	}
	
	/**
	 * Si occupa di registrare come TModel un'accordo di cooperazione con nome <var>nome</var>
	 *
	 * @param idAccordo ID che identifica l'accordo di cooperazione
	 * @param url url del file XML associato all'accordo di cooperazione
	 */	
	public void createAccordoCooperazione(IDAccordoCooperazione idAccordo, String url) throws DriverRegistroServiziException{
		String uriAccordo = this.idAccordoCooperazioneFactory.getUriFromIDAccordo(idAccordo);
		this.createTModel(UDDILib.ACCORDO_COOPERAZIONE, UDDILib.ACCORDO_COOPERAZIONE_PREFIX+uriAccordo, url);
	}

	/**
	 * Il metodo si occupa di verificare se nel registro e' regitrata una TModel identificata dal parametro
	 *
	 * @param idAccordo ID dell'accordo di cooperazione
	 * @return true se la TModel risulta registrata, false altrimenti
	 * 
	 */
	public boolean existsAccordoCooperazione(IDAccordoCooperazione idAccordo) throws DriverRegistroServiziException{
		
		if ( idAccordo==null )
			throw new DriverRegistroServiziException("[UDDILib.existsAccordoCooperazione]: Alcuni parametri non definiti");
		String uriAccordo = this.idAccordoCooperazioneFactory.getUriFromIDAccordo(idAccordo);
		try{
			TModel t = getTModel(UDDILib.ACCORDO_COOPERAZIONE,UDDILib.ACCORDO_COOPERAZIONE_PREFIX+uriAccordo);
			if(t == null)
				throw new Exception("TModel is null");
		}catch(DriverRegistroServiziNotFound e){
			return false;
		}catch(Exception e){
			throw new DriverRegistroServiziException(e.getMessage(),e);
		}
		return true;	
	}

	/**
	 * Si occupa di modificare il nome della TModel che registra l'accordo di cooperazione con nome <var>nomeOLD</var>
	 *
	 * @param idAccordoOLD vecchio id che identifica l'accordo di cooperazione
	 * @param idAccordoNEW nuovo id che identifica l'accordo di cooperazione
	 * @param url nuova url del file XML associato all'accordo di cooperazione
	 */
	public void updateAccordoCooperazione(IDAccordoCooperazione idAccordoOLD,IDAccordoCooperazione idAccordoNEW,String url) throws DriverRegistroServiziException,DriverRegistroServiziNotFound{
		String uriAccordoOLD = this.idAccordoCooperazioneFactory.getUriFromIDAccordo(idAccordoOLD);
		String uriAccordoNEW = this.idAccordoCooperazioneFactory.getUriFromIDAccordo(idAccordoNEW);
		this.updateTModel(UDDILib.ACCORDO_COOPERAZIONE, UDDILib.ACCORDO_COOPERAZIONE_PREFIX+uriAccordoOLD, UDDILib.ACCORDO_COOPERAZIONE_PREFIX+uriAccordoNEW, url);
	}

	/**
	 * Il metodo si occupa di cancellare la TModel identificata dal parametro
	 *
	 * @param idAccordo ID che identifica l'accordo di cooperazione
	 */
	public void deleteAccordoCooperazione(IDAccordoCooperazione idAccordo) throws DriverRegistroServiziException{
		String uriAccordo = this.idAccordoCooperazioneFactory.getUriFromIDAccordo(idAccordo);
		this.deleteTModel(UDDILib.ACCORDO_COOPERAZIONE, UDDILib.ACCORDO_COOPERAZIONE_PREFIX+uriAccordo);
	}

	/**
	 * Il metodo si occupa di impostare la url del file XML della TModel
	 *
	 * @param idAccordo ID che identifica l'accordo di cooperazione
	 * @param url url del file XML associato all'accordo di cooperazione
	 */
	public void updateUrlXmlAccordoCooperazione(IDAccordoCooperazione idAccordo, String url) throws DriverRegistroServiziException,DriverRegistroServiziNotFound{
		String uriAccordo = this.idAccordoCooperazioneFactory.getUriFromIDAccordo(idAccordo);
		this.updateUrlXmlTModel(UDDILib.ACCORDO_COOPERAZIONE, UDDILib.ACCORDO_COOPERAZIONE_PREFIX+uriAccordo, url);
	}
	/**
	 * Si occupa di recuperare la URL dell'XML associato all'accordo di cooperazione
	 * registrato con il nome <var>nome</var>
	 *
	 * @param idAccordo ID che identifica l'accordo di cooperazione
	 * @return la url dell'XML associato all'accordo di cooperazione
	 * 
	 */
	public String getUrlXmlAccordoCooperazione(IDAccordoCooperazione idAccordo) throws DriverRegistroServiziException,DriverRegistroServiziNotFound{
		if ( idAccordo==null )
			throw new DriverRegistroServiziException("[UDDILib.getUrlXmlAccordoCooperazione]: Alcuni parametri non definiti");
		try{
			String uriAccordo = this.idAccordoCooperazioneFactory.getUriFromIDAccordo(idAccordo);
			TModel tm = getTModel(UDDILib.ACCORDO_COOPERAZIONE, UDDILib.ACCORDO_COOPERAZIONE_PREFIX+uriAccordo);
			return tm.getOverviewDoc().getOverviewURLString();
		}catch(DriverRegistroServiziNotFound e){
			throw e;
		}catch(Exception e){
			throw new DriverRegistroServiziException("[UDDILib.getUrlXmlAccordoCooperazione]: "+e.getMessage(),e);
		}
	}
	
	/**
	 * Si occupa di recuperare la URL dell'XML associato all'accordo di cooperazione
	 * registrato con il nome <var>nome</var>
	 *
	 * @param idAccordo ID che identifica l'accordo di cooperazione
	 * @return la url dell'XML associato all'accordo di cooperazione
	 * 
	 */
	public String[] getUrlXmlAccordiCooperazione(IDAccordoCooperazione idAccordo,String urlPrefix) throws DriverRegistroServiziException,DriverRegistroServiziNotFound{
		try{
			TModel[] tm = null;
			if(idAccordo!=null) {
				String uriAccordo = this.idAccordoCooperazioneFactory.getUriFromIDAccordo(idAccordo);
				tm = getTModelByFilter(UDDILib.ACCORDO_COOPERAZIONE, UDDILib.ACCORDO_COOPERAZIONE_PREFIX+uriAccordo,urlPrefix,true);
			}else{
				tm = getTModelByFilter(UDDILib.ACCORDO_COOPERAZIONE, UDDILib.ACCORDO_COOPERAZIONE_PREFIX,urlPrefix,false);
			}
			if(tm!=null){
				String[] url = new String[tm.length];
				for(int i=0; i<tm.length; i++){
					url[i] = tm[i].getOverviewDoc().getOverviewURLString();
				}
				return url;
			}else{
				throw new DriverRegistroServiziNotFound("Accordi di Cooperazione (definizione XML) non trovati");
			}
		}
		catch (DriverRegistroServiziNotFound e){
			throw e;
		}
		catch(Exception e){
			throw new DriverRegistroServiziException("[UDDILib.getUrlXmlAccordiCooperazione]: "+e.getMessage(),e);
		}
	}
	
	
	
	
	









	/* ******** M E T O D I   A C C O R D O    D I    S E R V I Z I O   ******** */


	
	/**
	 * Si occupa di recuperare il TModel che registra l'accordo di servizio con nome <var>nome</var>
	 *
	 * @param idAccordo ID che identifica l'accordo di servizio
	 * @return la TModel che registra l'accordo di servizio
	 */
	public TModel getAccordoServizio(IDAccordo idAccordo) throws DriverRegistroServiziException,DriverRegistroServiziNotFound{
		String uriAccordo = this.idAccordoFactory.getUriFromIDAccordo(idAccordo);
		return this.getTModel(UDDILib.ACCORDO_SERVIZIO, uriAccordo);
	}
	
	/**
	 * Si occupa di recuperare le TModels che soddisfano il filtro
	 *
	 * @param urlRepository Url del Repository
	 * @return una lista di TModel che registra l'accordo di servizio
	 */
	public TModel[] getAccordiServizio(String urlRepository) throws DriverRegistroServiziException,DriverRegistroServiziNotFound{
		return this.getTModelByFilter(UDDILib.ACCORDO_SERVIZIO, null, urlRepository,false);
	}
	
	/**
	 * Si occupa di registrare come TModel un'accordo di servizio con nome <var>nome</var>
	 *
	 * @param idAccordo ID che identifica l'accordo di servizio
	 * @param url url del file XML associato all'accordo di servizio
	 */	
	public void createAccordoServizio(IDAccordo idAccordo, String url) throws DriverRegistroServiziException{
		String uriAccordo = this.idAccordoFactory.getUriFromIDAccordo(idAccordo);
		this.createTModel(UDDILib.ACCORDO_SERVIZIO, uriAccordo, url);
	}

	/**
	 * Il metodo si occupa di verificare se nel registro e' regitrata una TModel identificata dal parametro
	 *
	 * @param idAccordo ID dell'accordo di servizio
	 * @return true se la TModel risulta registrata, false altrimenti
	 * 
	 */
	public boolean existsAccordoServizio(IDAccordo idAccordo) throws DriverRegistroServiziException{
		
		if ( idAccordo==null )
			throw new DriverRegistroServiziException("[UDDILib.existsAccordoServizio]: Alcuni parametri non definiti");
		String uriAccordo = this.idAccordoFactory.getUriFromIDAccordo(idAccordo);
		try{
			TModel t = getTModel(UDDILib.ACCORDO_SERVIZIO,uriAccordo);
			if(t == null)
				throw new Exception("TModel is null");
		}catch(DriverRegistroServiziNotFound e){
			return false;
		}catch(Exception e){
			throw new DriverRegistroServiziException(e.getMessage(),e);
		}
		return true;	
	}

	/**
	 * Si occupa di modificare il nome della TModel che registra l'accordo di servizio con nome <var>nomeOLD</var>
	 *
	 * @param idAccordoOLD vecchio id che identifica l'accordo di servizio
	 * @param idAccordoNEW nuovo id che identifica l'accordo di servizio
	 * @param url nuova url del file XML associato all'accordo di servizio
	 */
	public void updateAccordoServizio(IDAccordo idAccordoOLD,IDAccordo idAccordoNEW,String url) throws DriverRegistroServiziException,DriverRegistroServiziNotFound{
		String uriAccordoOLD = this.idAccordoFactory.getUriFromIDAccordo(idAccordoOLD);
		String uriAccordoNEW = this.idAccordoFactory.getUriFromIDAccordo(idAccordoNEW);
		this.updateTModel(UDDILib.ACCORDO_SERVIZIO, uriAccordoOLD, uriAccordoNEW, url);
	}

	/**
	 * Il metodo si occupa di cancellare la TModel identificata dal parametro
	 *
	 * @param idAccordo ID che identifica l'accordo di servizio
	 */
	public void deleteAccordoServizio(IDAccordo idAccordo) throws DriverRegistroServiziException{
		String uriAccordo = this.idAccordoFactory.getUriFromIDAccordo(idAccordo);
		this.deleteTModel(UDDILib.ACCORDO_SERVIZIO, uriAccordo);
	}

	/**
	 * Il metodo si occupa di impostare la url del file XML della TModel
	 *
	 * @param idAccordo ID che identifica l'accordo di servizio
	 * @param url url del file XML associato all'accordo di servizio
	 */
	public void updateUrlXmlAccordoServizio(IDAccordo idAccordo, String url) throws DriverRegistroServiziException,DriverRegistroServiziNotFound{
		String uriAccordo = this.idAccordoFactory.getUriFromIDAccordo(idAccordo);
		this.updateUrlXmlTModel(UDDILib.ACCORDO_SERVIZIO, uriAccordo, url);
	}
	/**
	 * Si occupa di recuperare la URL dell'XML associato all'accordo di servizio
	 * registrato con il nome <var>nome</var>
	 *
	 * @param idAccordo ID che identifica l'accordo di servizio
	 * @return la url dell'XML associato all'accordo di servizio
	 * 
	 */
	public String getUrlXmlAccordoServizio(IDAccordo idAccordo) throws DriverRegistroServiziException,DriverRegistroServiziNotFound{
		if ( idAccordo==null )
			throw new DriverRegistroServiziException("[UDDILib.getUrlXmlAccordoServizio]: Alcuni parametri non definiti");
		try{
			String uriAccordo = this.idAccordoFactory.getUriFromIDAccordo(idAccordo);
			TModel tm = getTModel(UDDILib.ACCORDO_SERVIZIO,uriAccordo);
			return tm.getOverviewDoc().getOverviewURLString();
		}catch(DriverRegistroServiziNotFound e){
			throw e;
		}catch(Exception e){
			throw new DriverRegistroServiziException("[UDDILib.getUrlXmlAccordoServizio]: "+e.getMessage(),e);
		}
	}
	
	/**
	 * Si occupa di recuperare la URL dell'XML associato all'accordo di servizio
	 * registrato con il nome <var>nome</var>
	 *
	 * @param idAccordo ID che identifica l'accordo di servizio
	 * @return la url dell'XML associato all'accordo di servizio
	 * 
	 */
	public String[] getUrlXmlAccordiServizio(IDAccordo idAccordo,String urlRepository) throws DriverRegistroServiziException,DriverRegistroServiziNotFound{
		try{
			TModel[] tm = null;
			if(idAccordo!=null) {
				String uriAccordo = this.idAccordoFactory.getUriFromIDAccordo(idAccordo);
				tm = getTModelByFilter(UDDILib.ACCORDO_SERVIZIO, uriAccordo,urlRepository,true);
			}else{
				tm = getTModelByFilter(UDDILib.ACCORDO_SERVIZIO, null,urlRepository,false);
			}
			if(tm!=null){
				String[] url = new String[tm.length];
				for(int i=0; i<tm.length; i++){
					url[i] = tm[i].getOverviewDoc().getOverviewURLString();
				}
				return url;
			}else{
				throw new DriverRegistroServiziNotFound("Accordi di Servizio (definizione XML) non trovati");
			}
		}
		catch (DriverRegistroServiziNotFound e){
			throw e;
		}
		catch(Exception e){
			throw new DriverRegistroServiziException("[UDDILib.getUrlXmlAccordoServizio]: "+e.getMessage(),e);
		}
	}

	







	
	
	
	
	
	
	
	
	/* ******** M E T O D I   A C C O R D O    D I    S E R V I Z I O   ******** */


	
	/**
	 * Si occupa di recuperare il TModel che registra la porta di dominio con nome <var>nome</var>
	 *
	 * @param nome nome che identifica la porta di dominio
	 * @return la TModel che registra la porta di dominio
	 */
	public TModel getPortaDominio(String nome) throws DriverRegistroServiziException,DriverRegistroServiziNotFound{
		return this.getTModel(UDDILib.PORTA_DOMINIO, UDDILib.PORTA_DOMINIO_PREFIX+nome);
	}
	
	/**
	 * Si occupa di recuperare le TModels che soddisfano il filtro
	 *
	 * @param urlRepository Url del Repository
	 * @return una lista di TModel che registra la porta di dominio
	 */
	public TModel[] getPorteDominio(String urlRepository) throws DriverRegistroServiziException,DriverRegistroServiziNotFound{
		return this.getTModelByFilter(UDDILib.PORTA_DOMINIO, UDDILib.PORTA_DOMINIO_PREFIX, urlRepository, false);
	}
	
	/**
	 * Si occupa di registrare come TModel una porta di dominio con nome <var>nome</var>
	 *
	 * @param nome Nome che identifica l'accordo di servizio
	 * @param url url del file XML associato all'accordo di servizio
	 */	
	public void createPortaDominio(String nome, String url) throws DriverRegistroServiziException{
		this.createTModel(UDDILib.PORTA_DOMINIO, UDDILib.PORTA_DOMINIO_PREFIX+ nome, url);
	}

	/**
	 * Il metodo si occupa di verificare se nel registro e' regitrata una TModel identificata dal parametro
	 *
	 * @param nome Nome della porta di dominio
	 * @return true se la TModel risulta registrata, false altrimenti
	 * 
	 */
	public boolean existsPortaDominio(String nome) throws DriverRegistroServiziException{
		
		if ( nome==null )
			throw new DriverRegistroServiziException("[UDDILib.existsPortaDominio]: Alcuni parametri non definiti");
		try{
			TModel t = getTModel(UDDILib.PORTA_DOMINIO, UDDILib.PORTA_DOMINIO_PREFIX+nome);
			if(t == null)
				throw new Exception("TModel is null");
		}catch(DriverRegistroServiziNotFound e){
			return false;
		}catch(Exception e){
			throw new DriverRegistroServiziException(e.getMessage(),e);
		}
		return true;	
	}

	/**
	 * Si occupa di modificare il nome della TModel che registra la porta di dominio con nome <var>nomeOLD</var>
	 *
	 * @param nomeOLD vecchio nome che identifica la porta di dominio
	 * @param nomeNEW nuovo nome che identifica la porta di dominio
	 * @param url nuova url del file XML associato all'accordo di servizio
	 */
	public void updatePortaDominio(String nomeOLD,String nomeNEW,String url) throws DriverRegistroServiziException,DriverRegistroServiziNotFound{
		this.updateTModel(UDDILib.PORTA_DOMINIO, UDDILib.PORTA_DOMINIO_PREFIX+nomeOLD, UDDILib.PORTA_DOMINIO_PREFIX+nomeNEW, url);
	}

	/**
	 * Il metodo si occupa di cancellare la TModel identificata dal parametro
	 *
	 * @param nome nome che identifica la porta di dominio
	 */
	public void deletePortaDominio(String nome) throws DriverRegistroServiziException{
		this.deleteTModel(UDDILib.PORTA_DOMINIO, UDDILib.PORTA_DOMINIO_PREFIX+nome);
	}

	/**
	 * Il metodo si occupa di impostare la url del file XML della TModel
	 *
	 * @param nome Nome che identifica la porta di dominio
	 * @param url url del file XML associato alla porta di dominio
	 */
	public void updateUrlXmlPortaDominio(String nome, String url) throws DriverRegistroServiziException,DriverRegistroServiziNotFound{
		this.updateUrlXmlTModel(UDDILib.PORTA_DOMINIO, UDDILib.PORTA_DOMINIO_PREFIX+nome, url);
	}
	/**
	 * Si occupa di recuperare la URL dell'XML associato alla porta di dominio
	 * registrato con il nome <var>nome</var>
	 *
	 * @param nome Nome che identifica la porta di dominio
	 * @return la url dell'XML associato alla porta di dominio
	 * 
	 */
	public String getUrlXmlPortaDominio(String nome) throws DriverRegistroServiziException,DriverRegistroServiziNotFound{
		if ( nome==null )
			throw new DriverRegistroServiziException("[UDDILib.getUrlXmlPortaDominio]: Alcuni parametri non definiti");
		try{
			TModel tm = getTModel(UDDILib.PORTA_DOMINIO, UDDILib.PORTA_DOMINIO_PREFIX+nome);
			return tm.getOverviewDoc().getOverviewURLString();
		}catch(DriverRegistroServiziNotFound e){
			throw e;
		}catch(Exception e){
			throw new DriverRegistroServiziException("[UDDILib.getUrlXmlPortaDominio]: "+e.getMessage(),e);
		}
	}
	
	/**
	 * Si occupa di recuperare la URL dell'XML associato alla porta di dominio
	 * registrata con il nome <var>nome</var>
	 *
	 * @param searchNome Nome che identifica la porta di dominio
	 * @return la url dell'XML associato alla porta di dominio
	 * 
	 */
	public String[] getUrlXmlPortaDominio(String searchNome,String urlRepository) throws DriverRegistroServiziException,DriverRegistroServiziNotFound{
		try{
			TModel[] tm = null;
			if(searchNome!=null){
				tm = getTModelByFilter(UDDILib.PORTA_DOMINIO, UDDILib.PORTA_DOMINIO_PREFIX+searchNome,urlRepository,true);
			}else{
				tm = getTModelByFilter(UDDILib.PORTA_DOMINIO, UDDILib.PORTA_DOMINIO_PREFIX,urlRepository,false);
			}
			
			/*Vector<TModel> tmFiltratePerPdd = new Vector<TModel>(); 
			for(int i=0; i<tm.length; i++){
				if(tm[i].getNameString().startsWith(PORTA_DOMINIO_PREFIX)){
					tmFiltratePerPdd.add(tm[i]);
				}
			}
			if(tmFiltratePerPdd.size()<=0){
				throw new DriverRegistroServiziNotFound("Porte di dominio non trovate, dopo il filtro per pdd");
			}
			
			String[] url = new String[tmFiltratePerPdd.size()];
			int i=0;
			while(tmFiltratePerPdd.size()>0){
				TModel t = tmFiltratePerPdd.remove(0);
				url[i] = t.getOverviewDoc().getOverviewURLString();
				i++;
			}
			return url;
			*/
			
			if(tm!=null){
				String[] url = new String[tm.length];
				for(int i=0; i<tm.length; i++){
					url[i] = tm[i].getOverviewDoc().getOverviewURLString();
				}
				return url;
			}else{
				throw new DriverRegistroServiziNotFound("Porte di Dominio (definizione XML) non trovate");
			}
		}
		catch (DriverRegistroServiziNotFound e){
			throw e;
		}
		catch(Exception e){
			throw new DriverRegistroServiziException("[UDDILib.getUrlXmlPortaDominio]: "+e.getMessage(),e);
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	




	/* ------------------- (Business Entity)  --------------------- */

	/**
	 * Il metodo si occupa di cercare all'interno del registro le businessEntity che hanno un match con
	 * i valori passati per parametro.
	 *
	 * @param idSogg del Soggetto.
	 * @return un oggetto BusinessEntity se la ricerca ha successo, null altrimenti.
	 */
	protected BusinessEntity getBusinessEntity(IDSoggetto idSogg) throws DriverRegistroServiziException,DriverRegistroServiziNotFound{
		
		if ( (idSogg==null) || (idSogg.getNome()==null) || (idSogg.getTipo()==null))
			throw new DriverRegistroServiziException("[UDDILib.getBusinessEntity]: Alcuni parametri non definiti");
		
		String keyBE = idSogg.getTipo() + "/" + idSogg.getNome();

		BusinessEntity be=null;
		try{
			Vector<Name> names = new Vector<Name>();
			names.add(new Name("%"));
			IdentifierBag identifierBag= new IdentifierBag();
			KeyedReference k1 = new KeyedReference();
			k1.setKeyValue(keyBE);
			k1.setTModelKey(this.tmkID);
			Vector<KeyedReference> keyedReferenceVector = new Vector<KeyedReference>();
			keyedReferenceVector.add(k1);
			identifierBag.setKeyedReferenceVector(keyedReferenceVector);
			BusinessList businessList = this.proxy.find_business(names, null, identifierBag, null, null, null, 1);
			if(businessList.getBusinessInfos()==null || businessList.getBusinessInfos().size()==0)
				throw new DriverRegistroServiziNotFound("BusinessEntity non trovata");
			Vector<?> businessInfoVector  = businessList.getBusinessInfos().getBusinessInfoVector();
			BusinessInfo bi = (BusinessInfo) businessInfoVector.elementAt(0);
			BusinessDetail bd = this.proxy.get_businessDetail(bi.getBusinessKey());
			Vector<?> v = bd.getBusinessEntityVector();
			be = (BusinessEntity) v.elementAt(0);
		}
		catch (UDDIException e){
			throw new DriverRegistroServiziException("[UDDILib.getBusinessEntity]: "+e.getMessage(),e);
		}
		catch (TransportException e){
			throw new DriverRegistroServiziException("[UDDILib.getBusinessEntity]: "+e.getMessage(),e);
		}
		catch (DriverRegistroServiziNotFound e){
			throw e;
		}
		catch (Exception e){
			throw new DriverRegistroServiziException("[UDDILib.getBusinessEntity]: "+e.getMessage(),e);
		}
		
		return be;
	}
	
	/**
	 * Il metodo si occupa di cercare all'interno del registro le businessEntity che hanno un match con
	 * i valori passati per parametro.
	 *
	 * @return un oggetto BusinessEntity se la ricerca ha successo, null altrimenti.
	 */
	protected BusinessEntity[] getBusinessEntities() throws DriverRegistroServiziException,DriverRegistroServiziNotFound{

		try{
			Vector<Name> names = new Vector<Name>();
			names.add(new Name("%"));
			
			Vector<BusinessEntity> bus = new Vector<BusinessEntity>();
			BusinessList businessList = this.proxy.find_business(names, null, null, null, null, null, UDDILib.MAX_SEARCH);
			if(businessList!=null && businessList.getBusinessInfos()!=null){
				Vector<?> businessInfoVector  = businessList.getBusinessInfos().getBusinessInfoVector();
				for(int i=0; i<businessInfoVector.size(); i++){
					BusinessInfo bi = (BusinessInfo) businessInfoVector.elementAt(i);
					BusinessDetail bd = this.proxy.get_businessDetail(bi.getBusinessKey());
					Vector<?> v = bd.getBusinessEntityVector();
					BusinessEntity be = (BusinessEntity) v.elementAt(0);
					bus.add(be);
				}
			}
			
			if(bus.size()>0){
				BusinessEntity[] be = new BusinessEntity[1];
				be = bus.toArray(be);
				return be;
			}else{
				throw new DriverRegistroServiziNotFound("Non sono state trovate BusinessEntity nel registro UDDI");
			}
		}
		catch (UDDIException e){
			throw new DriverRegistroServiziException("[UDDILib.getBusinessEntity]: "+e.getMessage(),e);
		}
		catch (TransportException e){
			throw new DriverRegistroServiziException("[UDDILib.getBusinessEntity]: "+e.getMessage(),e);
		}
		catch (DriverRegistroServiziNotFound e){
			throw e;
		}
		catch (Exception e){
			throw new DriverRegistroServiziException("[UDDILib.getBusinessEntity]: "+e.getMessage(),e);
		}
	}

	/**
	 * Il metodo si occupa di aggiungere nel registro una nuova BusinessEntity 
	 * con tipo di identificativo <var>tipo</var> e codice identificativo <var>codice</>.
	 * 	
	 * @param idSogg del Soggetto.
	 * @return la BusinessEntity inserita, se l'inserimento ha successo
	 */
	protected BusinessEntity addBusinessEntity(IDSoggetto idSogg)throws DriverRegistroServiziException{
		
		if ( (idSogg==null) || (idSogg.getNome()==null) || (idSogg.getTipo()==null) || (idSogg.getCodicePorta()==null))
			throw new DriverRegistroServiziException("[UDDILib.addBusinessEntity]: Alcuni parametri non definiti");

		String keyBE =  idSogg.getTipo() + "/" + idSogg.getNome();

		try{
			AuthToken token = this.proxy.get_authToken(this.username,this.password);

			KeyedReference k1 = new KeyedReference();
			k1.setKeyName(idSogg.getCodicePorta());
			k1.setKeyValue(keyBE);	
			k1.setTModelKey(this.tmkID);

			Vector<KeyedReference> keyedReferenceVector1 = new Vector<KeyedReference>();
			keyedReferenceVector1.add(k1);

			BusinessEntity be = new BusinessEntity("",keyBE);

			IdentifierBag ib = new IdentifierBag();
			ib.setKeyedReferenceVector(keyedReferenceVector1);
			be.setIdentifierBag(ib);

			CategoryBag cb = new CategoryBag();
			be.setCategoryBag(cb);

			Vector<BusinessEntity> entities = new Vector<BusinessEntity>();
			entities.addElement(be);

			BusinessDetail bd = this.proxy.save_business(token.getAuthInfoString(),entities);

			Vector<?> businessEntities = bd.getBusinessEntityVector();
			be = (BusinessEntity) businessEntities.elementAt(0);

			return be;
		}
		catch (UDDIException e){
			throw new DriverRegistroServiziException("[UDDILib.addBusinessEntity]: "+e.getMessage(),e);
		}
		catch (TransportException e){
			throw new DriverRegistroServiziException("[UDDILib.addBusinessEntity]: "+e.getMessage(),e);
		}
		catch (Exception e){
			throw new DriverRegistroServiziException("[UDDILib.addBusinessEntity]: "+e.getMessage(),e);
		}

	}
	
	/**
	 * Si occupa di cancellare la BusinessEntity identificata dai parametri
	 *
	 *@param idSogg Identificativo del soggetto
	 */	
	protected void deleteBusinessEntity(IDSoggetto idSogg) throws DriverRegistroServiziException,DriverRegistroServiziNotFound{
		if ( (idSogg==null) || (idSogg.getNome()==null) || (idSogg.getTipo()==null) )
			throw new DriverRegistroServiziException("[UDDILib.deleteBusinessEntity]: Alcuni parametri non definiti");

		try{
			AuthToken token = this.proxy.get_authToken(this.username,this.password);

			BusinessEntity be = getBusinessEntity(idSogg);
			this.proxy.delete_business(token.getAuthInfoString(), be.getBusinessKey());
		}catch (DriverRegistroServiziNotFound e){
			throw e;
		}catch(Exception e){
			throw new DriverRegistroServiziException("[UDDILib.deleteBusinessEntity]: "+e.getMessage(),e);
		}
	}
	
	/**
	 * Il metodo assegna alla BusinessEntity <var>be</var> la descrizione <var>descrizione</var>
	 * 
	 * @param be Business Entity
	 * @param nome Nome da assegnare alla Business Entity
	 * @return la BusinessEntity modificata, se la modifica ha successo
	 */
	protected BusinessEntity updateNomeBusinessEntity(BusinessEntity be, String nome)throws DriverRegistroServiziException{
		if ( (be==null) || (nome==null) )
			throw new DriverRegistroServiziException("[UDDILib.updateNomeBusinessEntity]: Alcuni parametri non definiti");

		try{
			be.setDefaultName(new Name(nome));    
			Vector<BusinessEntity> entities = new Vector<BusinessEntity>();
			entities.addElement(be);
			
			AuthToken token = this.proxy.get_authToken(this.username,this.password);
			this.proxy.save_business(token.getAuthInfoString(),entities);
		}
		catch (UDDIException e){
			throw new DriverRegistroServiziException("[UDDILib.updateDescrizioneBusinessEntity]: "+e.getMessage(),e);
		}
		catch (TransportException e){
			throw new DriverRegistroServiziException("[UDDILib.updateDescrizioneBusinessEntity]: "+e.getMessage(),e);
		}
		catch (Exception e){
			throw new DriverRegistroServiziException("[UDDILib.updateDescrizioneBusinessEntity]: "+e.getMessage(),e);
		}
		return be;
	}
	
	/**
	 * Il metodo si recuperare il nome della BusinessEntity <var>be</var>.
	 *
	 * @param be Business Entity
	 * @return il nome della BusinessEntity.
	 */
	protected String getNomeBusinessEntity(BusinessEntity be)throws DriverRegistroServiziException{
		if (be==null)
			throw new DriverRegistroServiziException("[UDDILib.getNomeBusinessEntity]: Alcuni parametri non definiti");
		try{
			return be.getDefaultName().getText();
		}catch (Exception e){
			throw new DriverRegistroServiziException("[UDDILib.getNomeBusinessEntity]: "+e.getMessage(),e);
		}
	}
	
	/**
	 * Il metodo si occupa di aggiungere alla BusinessEntity <var>be</var> una maggiore descrizione 
	 * passata nel parametro <var>descrizione</var>.
	 * @param be Business Entity
	 * @param descrizione Descrizione associata al Soggetto
	 * @return la BusinessEntity modificata se tutto e' andato bene.
	 */	
	protected BusinessEntity updateDescrizioneBusinessEntity(BusinessEntity be, String descrizione)throws DriverRegistroServiziException{
		if ( (be==null) || (descrizione==null) )
			throw new DriverRegistroServiziException("[UDDILib.updateDescrizioneBusinessEntity]: Alcuni parametri non definiti");

		try{
			AuthToken token = this.proxy.get_authToken(this.username,this.password);

			be.setDefaultDescriptionString(descrizione);

			Vector<BusinessEntity> entities = new Vector<BusinessEntity>();
			entities.addElement(be);

			this.proxy.save_business(token.getAuthInfoString(),entities);
		}
		catch (UDDIException e){
			throw new DriverRegistroServiziException("[UDDILib.updateDescrizioneBusinessEntity]: "+e.getMessage(),e);
		}
		catch (TransportException e){
			throw new DriverRegistroServiziException("[UDDILib.updateDescrizioneBusinessEntity]: "+e.getMessage(),e);
		}
		catch (Exception e){
			throw new DriverRegistroServiziException("[UDDILib.updateDescrizioneBusinessEntity]: "+e.getMessage(),e);
		}
		return be;
	}
	
	/**
	 * Il metodo si occupa di settare nella BusinessEntity <var>be</var> una category bag
	 * contenente come valore la url dell'xml associato al Soggetto.
	 *
	 * @param be Business Entity
	 * @param url url del file XML associato al soggetto.
	 * @return la BusinessEntity modificata in caso di successo, null altrimenti.
	 */
	protected BusinessEntity updateUrlXmlBusinessEntity(BusinessEntity be, String url)throws DriverRegistroServiziException{
		if ( (be==null) || (url==null))
			throw new DriverRegistroServiziException("[UDDILib.updateUrlXmlBusinessEntity]: Alcuni parametri non definiti");

		try{
			AuthToken token = this.proxy.get_authToken(this.username,this.password);

			KeyedReference k1 = new KeyedReference();
			k1.setKeyName(org.openspcoop2.utils.Costanti.OPENSPCOOP2);
			k1.setKeyValue(url);
			Vector<BusinessEntity> entities = new Vector<BusinessEntity>();
			CategoryBag ib = be.getCategoryBag();
			if(ib == null)
				ib = new CategoryBag();
			ib.add(k1); 
			be.setCategoryBag(ib);
			entities.addElement(be);
			this.proxy.save_business(token.getAuthInfoString(),entities);	    
		}
		catch (UDDIException e){
			throw new DriverRegistroServiziException("[UDDILib.updateUrlXmlBusinessEntity]: "+e.getMessage(),e);
		}
		catch (TransportException e){
			throw new DriverRegistroServiziException("[UDDILib.updateUrlXmlBusinessEntity]: "+e.getMessage(),e);
		}
		catch (Exception e){
			throw new DriverRegistroServiziException("[UDDILib.updateUrlXmlBusinessEntity]: "+e.getMessage(),e);
		}

		return be;
	}
	
	/**
	 * Il metodo si occupa di impostare il keyName della category bag del soggetto
	 *
	 * @param idSogg Identificativo del Soggetto
	 * @param categoryBagName
	 */
	protected void updateCategoryBagKeyNameBusinessEntity(IDSoggetto idSogg,String categoryBagName) throws DriverRegistroServiziException,DriverRegistroServiziNotFound{
		if ( (idSogg==null) || (idSogg.getNome()==null) || (idSogg.getTipo()==null) || (categoryBagName==null))
			throw new DriverRegistroServiziException("[UDDILib.updateCategoryBagKeyNameBusinessEntity]: Alcuni parametri non definiti");

		try{
			AuthToken token = this.proxy.get_authToken(this.username,this.password);

			BusinessEntity be = getBusinessEntity(idSogg);	

			CategoryBag cb = be.getCategoryBag();
			Vector<?> krv = cb.getKeyedReferenceVector();
			KeyedReference kr = (KeyedReference) krv.elementAt(0);
			kr.setKeyName(categoryBagName);
			cb.setKeyedReferenceVector(krv);
			be.setCategoryBag(cb);
			Vector<BusinessEntity> entities = new Vector<BusinessEntity>();
			entities.addElement(be);

			this.proxy.save_business(token.getAuthInfoString(),entities);

		}catch (DriverRegistroServiziNotFound e){
			throw e;
		}
		catch (Exception e){
			throw new DriverRegistroServiziException("[UDDILib.updateCategoryBagKeyNameBusinessEntity]: "+e.getMessage(),e);
		}
	}
	
	
	
	
	
	
	
	
	
	
	/* --------------- Soggetto -------------------*/
	
	/**
	 * Il metodo si occupa di aggiungere nel registro un nuovo Soggetto. 
	 * 
	 * @param idSogg Identificativo del soggetto
	 * @param descrizione Descrizione da assegnare al Soggetto
	 * @param urlXML url dell'XML associato al Soggetto.
	 */
	public void createSoggetto(IDSoggetto idSogg, 
			String descrizione, String urlXML)throws DriverRegistroServiziException{

		if ( (idSogg==null) || (idSogg.getNome()==null) || (idSogg.getTipo()==null)||
				(descrizione==null) || 	(idSogg.getCodicePorta()==null) || (urlXML==null)){
			throw new DriverRegistroServiziException("[UDDILib.createSoggetto]: Alcuni parametri non definiti");
		}

		try{
			// add BusinessEntity
			BusinessEntity be = addBusinessEntity(idSogg);
			
			// set Descrizione in BusinessEntity
			try{
				be=updateNomeBusinessEntity(be,descrizione);
			}catch(Exception e){
				try{
					//rollback quanto fatto prima
					deleteBusinessEntity(idSogg);
				}catch(Exception rollbackE){}
				throw e; // rilancio l'eccezione
			}

			// set url XML nella BusinessEntity
			try{
				updateUrlXmlBusinessEntity(be,urlXML);
			}catch(Exception e){
				try{
					//rollback quanto fatto prima
					deleteBusinessEntity(idSogg);
				}catch(Exception rollbackE){}
				throw e; // rilancio l'eccezione
			}

		}catch(Exception e){
			throw new DriverRegistroServiziException("[UDDILib.createSoggetto]: "+e.getMessage(),e);
					}
	}
	
	/**
	 * Il metodo si occupa di verificare se nel registro e' regitrato un Soggetto identificato dai parametri
	 *
	 * @param idSogg del Soggetto
	 * @return true se il Soggetto risulta registrato, false altrimenti
	 */
	public boolean existsSoggetto(IDSoggetto idSogg)throws DriverRegistroServiziException,DriverRegistroServiziNotFound{
		if ( idSogg==null )
			throw new DriverRegistroServiziException("[UDDILib.existsSoggetto]: Alcuni parametri non definiti");
		BusinessEntity be = null;
		try{
			be = getBusinessEntity(idSogg);
			if(be == null)
				throw new Exception("BusinessEntity is null");
		}catch (DriverRegistroServiziNotFound e){
			return false;
		}
		catch(Exception e){
			throw new DriverRegistroServiziException(e.getMessage(),e);
		}
		return true;
	}

	/**
	 * Il metodo si occupa di recuperare l'url del file XML del Soggetto.
	 *
	 * @param idSogg Identificativo del Soggetto
	 */
	public String getUrlXmlSoggetto(IDSoggetto idSogg)throws DriverRegistroServiziException,DriverRegistroServiziNotFound{
		if ( idSogg==null )
			throw new DriverRegistroServiziException("[UDDILib.getUrlXmlSoggetto]: Alcuni parametri non definiti");
		try
		{
			BusinessEntity be =  getBusinessEntity(idSogg);
			return be.getCategoryBag().get(0).getKeyValue();
		}
		catch (DriverRegistroServiziNotFound e){
			throw e;
		}
		catch (Exception e){
			throw new DriverRegistroServiziException("[UDDILib.getUrlXmlSoggetto]: "+e.getMessage(),e);
		}
	}		
	
	/**
	 * Il metodo si occupa di recuperare l'url del file XML del Soggetto.
	 *
	 */
	public String[] getUrlXmlSoggetti()throws DriverRegistroServiziException,DriverRegistroServiziNotFound{
		try
		{
			BusinessEntity[] be =  getBusinessEntities();
			String [] url = new String[be.length];
			for(int i=0; i<be.length; i++){
				url[i] = be[i].getCategoryBag().get(0).getKeyValue();
			}
			return url;
		}
		catch (DriverRegistroServiziNotFound e){
			throw e;
		}
		catch (Exception e){
			throw new DriverRegistroServiziException("[UDDILib.getUrlXmlSoggetto]: "+e.getMessage(),e);
		}
	}		

	/**
	 * Il metodo si occupa di impostare la url del file XML associato al Soggetto
	 *
	 * @param idSogg Identificativo del Soggetto
	 * @param url Url da impostare
	 */
	public void updateUrlXmlSoggetto(IDSoggetto idSogg,String url)throws DriverRegistroServiziException,DriverRegistroServiziNotFound{
		if ( idSogg==null  || url==null)
			throw new DriverRegistroServiziException("[UDDILib.updateUrlXmlSoggetto]: Alcuni parametri non definiti");
	
		try{
			AuthToken token = this.proxy.get_authToken(this.username,this.password);

			BusinessEntity be = getBusinessEntity(idSogg);	

			CategoryBag cb = be.getCategoryBag();
			Vector<?> krv = cb.getKeyedReferenceVector();
			KeyedReference kr = (KeyedReference) krv.elementAt(0);
			kr.setKeyValue(url);
			cb.setKeyedReferenceVector(krv);
			be.setCategoryBag(cb);
			Vector<BusinessEntity> entities = new Vector<BusinessEntity>();
			entities.addElement(be);

			this.proxy.save_business(token.getAuthInfoString(),entities);

		}catch (DriverRegistroServiziNotFound e){
			throw e;
		}
		catch (Exception e){
			throw new DriverRegistroServiziException("[UDDILib.updateUrlXmlSoggetto]: "+e.getMessage(),e);
		}
	}

	/**
	 * Il metodo si occupa di impostare la descrizione associata al Soggetto
	 *
	 * @param idSogg Identificativo del Soggetto
	 * @param descrizione Descrizione da associare al soggetto
	 */
	public void updateDescrizioneSoggetto(IDSoggetto idSogg,String descrizione) throws DriverRegistroServiziException,DriverRegistroServiziNotFound{
		if ( idSogg==null  || descrizione==null)
			throw new DriverRegistroServiziException("[UDDILib.updateDescrizioneSoggetto]: Alcuni parametri non definiti");

		try{
			BusinessEntity be = getBusinessEntity(idSogg);
			updateNomeBusinessEntity(be,descrizione);
		}
		catch (DriverRegistroServiziNotFound e){
			throw e;
		}
		catch (Exception e){
			throw new DriverRegistroServiziException("[UDDILib.updateDescrizioneSoggetto]: "+e.getMessage(),e);
		}
	}

	/**
	 * Il metodo si occupa di modificare il codice tipo/valore associato al Soggetto
	 *
	 * @param idSoggOLD Vecchio identificativo del Soggetto
	 * @param idSoggNEW Nuovo identificativo del Soggetto
	 */
	public void updateIdSoggetto(IDSoggetto idSoggOLD,IDSoggetto idSoggNEW)throws DriverRegistroServiziException,DriverRegistroServiziNotFound{
		if ( (idSoggOLD==null) || (idSoggOLD.getNome()==null) || (idSoggOLD.getTipo()==null) || 
			 (idSoggNEW==null) || (idSoggNEW.getNome()==null) || (idSoggNEW.getTipo()==null))
			throw new DriverRegistroServiziException("[UDDILib.updateIdSoggetto]: Alcuni parametri non definiti");

		try{
			AuthToken token = this.proxy.get_authToken(this.username,this.password);

			BusinessEntity be = getBusinessEntity(idSoggOLD);
	
			IdentifierBag ib = be.getIdentifierBag();
			Vector<?> v = ib.getKeyedReferenceVector();
			KeyedReference kr = (KeyedReference) v.elementAt(0);

			String newValue = idSoggNEW.getTipo() + "/" + idSoggNEW.getNome();

			kr.setKeyValue(newValue);
			ib.setKeyedReferenceVector(v);
			be.setIdentifierBag(ib);

			Vector<BusinessEntity> entities = new Vector<BusinessEntity>();
			entities.addElement (be);

			BusinessDetail bd = this.proxy.save_business(token.getAuthInfoString(),entities);

			Vector<?> businessEntities = bd.getBusinessEntityVector();
			be = (BusinessEntity) businessEntities.elementAt (0);
		}
		catch (DriverRegistroServiziNotFound e){
			throw e;
		}
		catch (Exception e){
			throw new DriverRegistroServiziException("[UDDILib.updateIdSoggetto]: "+e.getMessage(),e);
		}
	}


	/**
	 * Si occupa di cancellare il Soggetto identificata dai parametri
	 *
	 * @param idSogg Identificativo del soggetto
	 */	
	public void deleteSoggetto(IDSoggetto idSogg)throws DriverRegistroServiziException{
		if ( idSogg==null )
			throw new DriverRegistroServiziException("[UDDILib.deleteSoggetto]: Alcuni parametri non definiti");
		try{
			deleteBusinessEntity(idSogg);
		}catch(Exception e){
			throw new DriverRegistroServiziException("[UDDILib.deleteSoggetto]: "+e.getMessage(),e);
		}
	}

	/**
	 * Il metodo si occupa di impostare il codice della porta di dominio associato al Soggetto
	 *
	 * @param idSogg Identificativo del Soggetto
	 * @param newIdentificativoPorta Nuovo codice di dominio associare al soggetto
	 */
	public void updateIdentificativoPortaSoggetto(IDSoggetto idSogg,String newIdentificativoPorta)throws DriverRegistroServiziException,DriverRegistroServiziNotFound{
		if ( idSogg==null || newIdentificativoPorta==null)
			throw new DriverRegistroServiziException("[UDDILib.updateIdentificativoPortaSoggetto]: Alcuni parametri non definiti");
	
		try{
			AuthToken token = this.proxy.get_authToken(this.username,this.password);

			BusinessEntity be = getBusinessEntity(idSogg);

			IdentifierBag ib = be.getIdentifierBag();
			Vector<?> v = ib.getKeyedReferenceVector();
			KeyedReference kr = (KeyedReference) v.elementAt(0);
			kr.setKeyName(newIdentificativoPorta);
			ib.setKeyedReferenceVector(v);
			be.setIdentifierBag(ib);

			Vector<BusinessEntity> entities = new Vector<BusinessEntity>();
			entities.addElement (be);

			BusinessDetail bd = this.proxy.save_business(token.getAuthInfoString(),entities);

			Vector<?> businessEntities = bd.getBusinessEntityVector();
			be = (BusinessEntity) businessEntities.elementAt (0);
		}
		catch (DriverRegistroServiziNotFound e){
			throw e;
		}
		catch (Exception e){
			throw new DriverRegistroServiziException("[UDDILib.updateIdentificativoPortaSoggetto]: Alcuni parametri non definiti");
		}
	}
























	/* ********  M E T O D I   PRIVATI    ******** */

	/**
	 * Il metodo si occupa di cercare all'interno del registro il servizio associato
	 * ad una BusinessEntity di nome <var>serviceName</var> 
	 * Ritorna null in caso il servizio risulti non registrato.
	 *
	 * @param be BusinessEntity.
	 * @param idServ Identificativo del Servizio
	 * @return un oggetto BusinessService se la ricerca ha successo
	 */
	protected BusinessService getBusinessService(BusinessEntity be, IDServizio idServ) throws DriverRegistroServiziException,DriverRegistroServiziNotFound{
		if ( be==null || idServ==null || idServ.getTipoServizio()==null || idServ.getServizio()==null)
			throw new DriverRegistroServiziException("[UDDILib.getBusinessService]: Alcuni parametri non definiti");
	
		try{    

			String keyService = idServ.getTipoServizio() + "/" + idServ.getServizio();

			BusinessService bs=null;

			Name nome = new Name(keyService);
			Vector<Name> nomi = new Vector<Name>();
			nomi.add(nome);
			
			FindQualifiers findQualifiers = new FindQualifiers();
			Vector<FindQualifier> qualifier = new Vector<FindQualifier>();
			qualifier.add(new FindQualifier(FindQualifier.exactNameMatch));
			qualifier.add(new FindQualifier(FindQualifier.caseSensitiveMatch));
			findQualifiers.setFindQualifierVector(qualifier);

			ServiceList serviceList = this.proxy.find_service(be.getBusinessKey(), nomi, null, null, findQualifiers, 1);
			if(serviceList.getServiceInfos()==null || serviceList.getServiceInfos().size()==0)
				throw new DriverRegistroServiziNotFound("BusinessService non trovata");
			
			Vector<?> serviceInfoVector = serviceList.getServiceInfos().getServiceInfoVector();
			ServiceInfo serviceInfo = (ServiceInfo) serviceInfoVector.elementAt(0);
			ServiceDetail sd = this.proxy.get_serviceDetail(serviceInfo.getServiceKey());
			Vector<?> v = sd.getBusinessServiceVector();
			bs = (BusinessService) v.elementAt(0);

			return bs;
		}
		catch (DriverRegistroServiziNotFound e){
			throw e;
		}
		catch (UDDIException e){
			throw new DriverRegistroServiziException("[UDDILib.getBusinessService]: "+e.getMessage(),e);
		}
		catch (TransportException e){
			throw new DriverRegistroServiziException("[UDDILib.getBusinessService]: "+e.getMessage(),e);
		}
		catch (Exception e){
			throw new DriverRegistroServiziException("[UDDILib.getBusinessService]: "+e.getMessage(),e);
		}

	}
	
	

	/**
	 * Il metodo si occupa di aggiungere nel registro una nuovo servizio con nome <var>serviceName</var> 
	 * associandolo alla businessEntity <var>be</var>
	 * In caso un servizio con lo stesso nome sia gia' registrato non non fa niente e ritorna null.
	 * 
	 * @param be BusinessEntity.
	 * @param idServ Identificativo del Servizio
	 * @return il BusinessService inserito, se l'inserimento ha successo, null altrimenti.
	 */
	protected BusinessService createBusinessService(BusinessEntity be, IDServizio idServ) throws DriverRegistroServiziException{
		if ( be==null || idServ==null || idServ.getTipoServizio()==null || idServ.getServizio()==null)
			throw new DriverRegistroServiziException("[UDDILib.createBusinessService]: Alcuni parametri non definiti");
	
		BusinessService bs = null;
		try{
			AuthToken token = this.proxy.get_authToken(this.username,this.password);

			String keyService = idServ.getTipoServizio() + "/" + idServ.getServizio();

			String businessKey = be.getBusinessKey();   
			
			// verifico che non esista gia' un servizio associato a be
			try{
				bs = getBusinessService(be,idServ);
			}catch(DriverRegistroServiziNotFound e){}
			
			/*entro solo se il servizio non e' gia stato registrato*/
			if(bs==null){
				bs = new BusinessService("");
				bs.setDefaultNameString(keyService,null);
				bs.setBusinessKey(businessKey);
				Vector<BusinessService> services = new Vector<BusinessService>();
				services.addElement(bs);

				ServiceDetail sd = this.proxy.save_service(token.getAuthInfoString(),services);

				Vector<?> businessServices = sd.getBusinessServiceVector();
				bs = (BusinessService) businessServices.elementAt(0);

				BusinessServices bds =  be.getBusinessServices();
				bds.add(bs); 

				be.setBusinessServices(bds);
				Vector<BusinessEntity> entities = new Vector<BusinessEntity>();
				entities.addElement(be);
				this.proxy.save_business(token.getAuthInfoString(),entities);
			}
		}
		catch (UDDIException e){
			throw new DriverRegistroServiziException("[UDDILib.createBusinessService]: "+e.getMessage(),e);
		}
		catch (TransportException e){
			throw new DriverRegistroServiziException("[UDDILib.createBusinessService]: "+e.getMessage(),e);
		}
		catch (Exception e){
			throw new DriverRegistroServiziException("[UDDILib.createBusinessService]: "+e.getMessage(),e);
		}
		return bs;
	}
	
	/**
	 * Si occupa di cancellare il servizio <var>servizio</var> registrato nella BusinessEntity
	 * identificata dai parametri <var>tipo</var> e <var>cod</var>.
	 *
	 * @param idServ Identificativo del Servizio
	 */	
	protected void deleteBusinessService(IDServizio idServ) throws DriverRegistroServiziException{
		if ( idServ==null || idServ.getTipoServizio()==null || idServ.getServizio()==null ||
				idServ.getSoggettoErogatore()==null || idServ.getSoggettoErogatore().getTipo()==null || idServ.getSoggettoErogatore().getNome()==null)
			throw new DriverRegistroServiziException("[UDDILib.deleteBusinessService]: Alcuni parametri non definiti");
	
		try{
			AuthToken token = this.proxy.get_authToken(this.username,this.password);

			FindQualifiers findQualifiers = new FindQualifiers();
			Vector<FindQualifier> qualifier = new Vector<FindQualifier>();
			qualifier.add(new FindQualifier(FindQualifier.exactNameMatch));
			qualifier.add(new FindQualifier(FindQualifier.caseSensitiveMatch));
			findQualifiers.setFindQualifierVector(qualifier);

			IdentifierBag ib= new IdentifierBag();
			KeyedReference k1 = new KeyedReference();
			String keyBE =  idServ.getSoggettoErogatore().getTipo()+ "/" + idServ.getSoggettoErogatore().getNome();
			k1.setKeyValue(keyBE);
			k1.setTModelKey(this.tmkID);

			Vector<KeyedReference> keyedReferenceVector = new Vector<KeyedReference>();
			keyedReferenceVector.add(k1);
			ib.setKeyedReferenceVector(keyedReferenceVector);

			Vector<Name> names = new Vector<Name>();

			String keyService = idServ.getTipoServizio() + "/" + idServ.getServizio();

			names.add(new Name(keyService));

			BusinessList businessList = this.proxy.find_business(null, null, ib, null, null, null,1);
			Vector<?> businessInfoVector  = businessList.getBusinessInfos().getBusinessInfoVector();
			BusinessInfo businessInfo = (BusinessInfo) businessInfoVector.elementAt(0);
			ServiceList serviceList = this.proxy.find_service(businessInfo.getBusinessKey(),names,null,null,findQualifiers,1);
			Vector<?> serviceInfoVector = serviceList.getServiceInfos().getServiceInfoVector();
			ServiceInfo serviceInfo = (ServiceInfo)serviceInfoVector.elementAt(0);

			this.proxy.delete_service(token.getAuthInfoString(), serviceInfo.getServiceKey());

		}catch(Exception e){
			throw new DriverRegistroServiziException("[UDDILib.deleteBusinessService]: "+e.getMessage(),e);
		}
	}
	
	/**
	 * Il metodo si occupa di ritornare il nome del BusinessService <var>bs</var>.
	 * @param bs BusinessService
	 * @return il nome del servizio se la ricerca ha successo.
	 */	
	protected String getNomeBusinessService(BusinessService bs)throws DriverRegistroServiziException{
		if ( bs==null )
			throw new DriverRegistroServiziException("[UDDILib.getNomeBusinessService]: Alcuni parametri non definiti");
	
		try{
			return bs.getDefaultName().getText();
		}catch(Exception e){
			throw new DriverRegistroServiziException("[UDDILib.getNomeBusinessService]: "+e.getMessage(),e);
		}
	}
	
	/**
	 * Il metodo si occupa di assegnare al servizio <var>bs</var> il nome <var>nome</var>
	 *
	 * @param bs BusinessService
	 * @param idServ Identificativo del Servizio
	 * @return la BusinessService modificata, se la modifica ha successo, null altrimenti.
	 */
	protected BusinessService updateNomeBusinessService(BusinessService bs, IDServizio idServ)throws DriverRegistroServiziException{
		if (bs==null || idServ==null || idServ.getTipoServizio()==null || idServ.getServizio()==null){
			throw new DriverRegistroServiziException("[UDDILib.updateNomeBusinessService]: Alcuni parametri non definiti");
		}

		try{
			AuthToken token = this.proxy.get_authToken(this.username,this.password);

			String keyService = idServ.getTipoServizio() + "/" + idServ.getServizio();


			bs.setDefaultName(new Name(keyService));

			Vector<BusinessService> services = new Vector<BusinessService>();
			services.addElement(bs);
			this.proxy.save_service(token.getAuthInfoString(),services);
		}
		catch (UDDIException e){
			throw new DriverRegistroServiziException("[UDDILib.updateNomeBusinessService]: "+e.getMessage(),e);
		}
		catch (TransportException e){
			throw new DriverRegistroServiziException("[UDDILib.updateNomeBusinessService]: "+e.getMessage(),e);
		}
		catch (Exception e){
			throw new DriverRegistroServiziException("[UDDILib.updateNomeBusinessService]: "+e.getMessage(),e);
		}

		return bs;
	}
	
	/**
	 * Il metodo si occupa di cercare all'interno del registro il bindingTemplate che definisce il servizio <var>bs</var>.
	 * 
	 * @param bs BusinessService.
	 * @return un BindingTemplate
	 */
	protected BindingTemplate getBindingTemplate(BusinessService bs)throws DriverRegistroServiziException{
		if ( bs==null )
			throw new DriverRegistroServiziException("[UDDILib.getBindingTemplate]: Alcuni parametri non definiti");
	
		BindingTemplate bt=null;
		try{
			
			BindingDetail bindingDetailReturned = this.proxy.find_binding(null, bs.getServiceKey(), null, 1);
			Vector<?> bindingTemplatesFound = bindingDetailReturned.getBindingTemplateVector();
			bt = (BindingTemplate)(bindingTemplatesFound.elementAt(0));
		}catch (TransportException e){
			throw new DriverRegistroServiziException("[UDDILib.getBindingTemplate]: "+e.getMessage(),e);
		}
		catch (Exception e){
			throw new DriverRegistroServiziException("[UDDILib.getBindingTemplate]: "+e.getMessage(),e);
		}
		return bt;
	}
	
	/**
	 * Il metodo si occupa di aggiungere nel registro una nuovo bindingTemplate associato al servizio <var>bs</var>      
	 * 
	 * @param bs BusinessService.
	 * @return il BindingTemplate inserito, se l'inserimento ha successo
	 */
	protected BindingTemplate createBindingTemplate(BusinessService bs)throws DriverRegistroServiziException{
		if ( bs==null )
			throw new DriverRegistroServiziException("[UDDILib.createBindingTemplate]: Alcuni parametri non definiti");

		try{
			AuthToken token = this.proxy.get_authToken(this.username,this.password);

			BindingTemplate bt = null;
			//BindingTemplates bts = bs.getBindingTemplates();
			BindingTemplates bts = new BindingTemplates();
			String serviceKey = bs.getServiceKey();
			bt = new BindingTemplate();
			bt.setServiceKey(serviceKey);
			AccessPoint ap = new AccessPoint();
			ap.setText("url di default");
			bt.setAccessPoint(ap);

			Vector<BindingTemplate> templates = new Vector<BindingTemplate>();
			templates.addElement(bt);

			BindingDetail bindingDetail = this.proxy.save_binding(token.getAuthInfoString(),templates);
			Vector<?> bd = bindingDetail.getBindingTemplateVector();
			bt = (BindingTemplate) bd.elementAt(bd.size()-1);
			bts.add(bt);
			bs.setBindingTemplates(bts);

			Vector<BusinessService> services = new Vector<BusinessService>();
			services.addElement(bs);
			this.proxy.save_service(token.getAuthInfoString(),services);

			return bt;
		}
		catch (UDDIException e){
			throw new DriverRegistroServiziException("[UDDILib.createBindingTemplate]: "+e.getMessage(),e);
		}
		catch (TransportException e){
			throw new DriverRegistroServiziException("[UDDILib.createBindingTemplate]: "+e.getMessage(),e);
		}
		catch (Exception e){
			throw new DriverRegistroServiziException("[UDDILib.createBindingTemplate]: "+e.getMessage(),e);
		}
	}
	
	/**
	 * Il metodo si occupa di cercare all'interno del registro il TModel che definisce il servizio a cui e' associato<var>bt</var>
	 * @param bt BindingTemplate.
	 * @return un oggetto Tmodel se la ricerca ha successo
	 */
	protected TModel getTModel(BindingTemplate bt)throws DriverRegistroServiziException{
		if ( bt==null )
			throw new DriverRegistroServiziException("[UDDILib.getTModel]: Alcuni parametri non definiti");
	
		TModel tm = null;
		try{
			TModelInstanceDetails tmid = bt.getTModelInstanceDetails();
			TModelInstanceInfo tmii = tmid.get(0);
			TModelDetail tmd = this.proxy.get_tModelDetail(tmii.getTModelKey());
			Vector<?> v = tmd.getTModelVector();
			tm = (TModel) v.elementAt(0);
		}
		catch (UDDIException e){
			throw new DriverRegistroServiziException("[UDDILib.getTModel]: "+e.getMessage(),e);
		}
		catch (TransportException e){
			throw new DriverRegistroServiziException("[UDDILib.getTModel]: "+e.getMessage(),e);
		}
		catch (Exception e){
			throw new DriverRegistroServiziException("[UDDILib.getTModel]: "+e.getMessage(),e);
		}
		return tm;
	}
	
	/**
	 * Il metodo si occupa di aggiungere nel registro un TModel associato al bindingTemplate <var>bt</var>
	 * 
	 * @param bt BindingTemplate.
	 * @param idAccordo ID dell'accordo di Servizio
	 */
	protected void createTModelServizio(BindingTemplate bt,IDAccordo idAccordo)throws DriverRegistroServiziException,DriverRegistroServiziNotFound{
		if ( bt==null || (idAccordo==null))
			throw new DriverRegistroServiziException("[UDDILib.createTModelServizio]: Alcuni parametri non definiti");
	
		TModel tm = null;
		try{
			AuthToken token = this.proxy.get_authToken(this.username,this.password);

			tm = getAccordoServizio(idAccordo);

			TModelInstanceInfo tmii = new TModelInstanceInfo(tm.getTModelKey());
			TModelInstanceDetails tmids = new TModelInstanceDetails();
			tmids.add(tmii);

			bt.setTModelInstanceDetails(tmids);
			Vector<BindingTemplate> templates = new Vector<BindingTemplate>();
			templates.addElement(bt);

			this.proxy.save_binding(token.getAuthInfoString(),templates);
		}
		catch(DriverRegistroServiziNotFound e){
			throw e;
		}
		catch (UDDIException e){
			throw new DriverRegistroServiziException("[UDDILib.createTModelServizio]: "+e.getMessage(),e);
		}
		catch (TransportException e){
			throw new DriverRegistroServiziException("[UDDILib.createTModelServizio]: "+e.getMessage(),e);
		}
		catch (Exception e){
			throw new DriverRegistroServiziException("[UDDILib.createTModelServizio]: "+e.getMessage(),e);
		}
	}
	
	/**
	 * Il metodo si occupa di recuperarie l'access point dal BindingTemplate <var>bt</var>.
	 * @param bt BindingTemplate 
	 * @return una stringa contenente l'url all'XML del Servizio.
	 */	      
	protected String getAccessPoint(BindingTemplate bt)throws DriverRegistroServiziException{
		if ( bt==null  )
			throw new DriverRegistroServiziException("[UDDILib.getAccessPoint]: Alcuni parametri non definiti");
	
		try{
			return bt.getAccessPoint().getText();
		}catch(Exception e){
			throw new DriverRegistroServiziException("[UDDILib.getAccessPoint]: "+e.getMessage(),e);
		}
	}   
	
	/**
	 * Il metodo si occupa di settare l'access point specificato nel parametro<var>URL</var>  
	 * nel BindingTemplate <var>bt</var>.
	 * @param bt BindingTemplate
	 * @param URL URL dell'XML associato al Servizio
	 * @return l'oggetto bindingTemplate modificato se tutto e' andato bene.
	 */	   
	protected BindingTemplate updateAccessPoint(BindingTemplate bt, String URL)throws DriverRegistroServiziException{
		if ( bt==null || (URL==null) )
			throw new DriverRegistroServiziException("[UDDILib.updateAccessPoint]: Alcuni parametri non definiti");
	
		try{
			AuthToken token = this.proxy.get_authToken(this.username,this.password);

			AccessPoint ap = bt.getAccessPoint();

			ap.setText(URL);
			bt.setAccessPoint(ap);

			Vector<BindingTemplate> templates = new Vector<BindingTemplate>();
			templates.addElement(bt);
			this.proxy.save_binding(token.getAuthInfoString(),templates);

		}
		catch (UDDIException e){
			throw new DriverRegistroServiziException("[UDDILib.updateAccessPoint]: "+e.getMessage(),e);
		}
		catch (TransportException e){
			throw new DriverRegistroServiziException("[UDDILib.updateAccessPoint]: "+e.getMessage(),e);
		}
		catch (Exception e){
			throw new DriverRegistroServiziException("[UDDILib.updateAccessPoint]: "+e.getMessage(),e);
		}

		return bt;
	}
	
	/**
	 * Il metodo si occupa di rimuovere l'accessoPoint dal BindingTemplate <var>bt</var> passato per parametro.
	 * @param bt BindingTemplate 
	 * @return il BindingTemplate modificato
	 */	   
	protected BindingTemplate deleteAccessPoint(BindingTemplate bt)throws DriverRegistroServiziException{
		if ( bt==null  )
			throw new DriverRegistroServiziException("[UDDILib.deleteAccessPoint]: Alcuni parametri non definiti");

		try{
			bt = updateAccessPoint(bt,"");
			return bt;
		}catch(Exception e){
			throw new DriverRegistroServiziException("[UDDILib.deleteAccessPoint]: "+e.getMessage(),e);
		}
	}
	
	
	
	
	
	
	
	
	
	/* ********  M E T O D I    S E R V I Z I    ******** */

	/**
	 * Il metodo si occupa di verificare se alla BusinessEntity identificata dai parametri
	 * e' stato registrato il servizio <var>servizio</var>.
	 *
	 * @param idServ Identificativo del servizio
	 * @return true se il servizio risulta registrato, false altrimenti
	 */
	public boolean existsServizio(IDServizio idServ) throws DriverRegistroServiziException{
		if ( idServ==null || idServ.getSoggettoErogatore()==null)
			throw new DriverRegistroServiziException("[UDDILib.existsServizio]: Alcuni parametri non definiti");
		
		BusinessService bs = null;
		try{
			BusinessEntity be=getBusinessEntity(idServ.getSoggettoErogatore());
			if(be==null)
				throw new Exception("BusinessEntity is null");
			bs=getBusinessService(be,idServ);
			if(bs==null)
				throw new Exception("BusinessService is null");
		}catch (DriverRegistroServiziNotFound e){
			return false;
		}
		catch(Exception e){
			throw new DriverRegistroServiziException(e.getMessage(),e);
		}
		return true;
			
	}

	/**
	 * Il metodo si occupa di aggiungere nel registro un nuovo Servizio 
	 * associato al soggetto identificato dai parametri <var>tipo</var> e <var>codice</var>. 
	 * 
	 * @param idServ Identificativo del servizio
	 * @param urlXML url dell'XML associato al Servizio.
	 * @param idAccordo ID dell'accordo di Servizio
	 */
	public void createServizio(IDServizio idServ, 
			String urlXML,IDAccordo idAccordo)throws DriverRegistroServiziException,DriverRegistroServiziNotFound{
		if ( idServ==null || idServ.getSoggettoErogatore()==null || (urlXML==null) || (idAccordo==null))
			throw new DriverRegistroServiziException("[UDDILib.createServizio]: Alcuni parametri non definiti");
		
		try{

			// get Business Enity
			BusinessEntity be = getBusinessEntity(idServ.getSoggettoErogatore());

			// Add Business Service
			BusinessService bs = createBusinessService(be,idServ);

			// Add template (url servizio.xml)
			BindingTemplate bt = null;
			try{
				bt = createBindingTemplate(bs);
			}catch(Exception e){
				// rollback
				try{
					deleteBusinessService(idServ);
				}catch(Exception eRollback){}
				throw e; // rilancio
			}

			// set Template (url servizio.xml)
			try{
				bt=updateAccessPoint(bt,urlXML);
			}catch(Exception e){
				// rollback
				try{
					deleteBusinessService(idServ);
				}catch(Exception eRollback){}
				throw e; // rilancio
			}
			
			// Add TModel per Accordo di Servizio
			try{
				createTModelServizio(bt,idAccordo);
			}catch(Exception e){
				// rollback
				try{
					deleteBusinessService(idServ);
				}catch(Exception eRollback){}
				throw e; // rilancio
			}

		}catch (DriverRegistroServiziNotFound e){
			throw e;
		}catch(Exception e){
			throw new DriverRegistroServiziException("[UDDILib.createServizio]: "+e.getMessage(),e);
		}
	}

	/**
	 * Si occupa di recuperare la URL dell'XML associato alla porta di dominio 
	 * registrata con il codice <var>codice</var>
	 *
	 * @param idServ Identificativo del servizio
	 * @return la url dell'XML associato alla porta di dominio
	 */
	public String getUrlXmlServizio(IDServizio idServ) throws DriverRegistroServiziException,DriverRegistroServiziNotFound{
		if ( idServ==null || idServ.getSoggettoErogatore()==null )
			throw new DriverRegistroServiziException("[UDDILib.getUrlXmlServizio]: Alcuni parametri non definiti");
		
		try{
		
			BusinessEntity be = getBusinessEntity(idServ.getSoggettoErogatore());
			BusinessService bs= getBusinessService(be,idServ);
			BindingTemplate bt = getBindingTemplate(bs);
			return getAccessPoint(bt);
		}catch (DriverRegistroServiziNotFound e){
			throw e;
		}catch(Exception e){
			throw new DriverRegistroServiziException("[UDDILib.getUrlXmlServizio]: "+e.getMessage(),e);
		}
	}

	/**
	 * Il metodo si occupa di impostare la url del file XML associato al Servizio
	 *
	 * @param idServ Identificativo del servizio
	 * @param url Url da impostare
	 */
	public void updateUrlXmlServizio(IDServizio idServ, String url)throws DriverRegistroServiziException,DriverRegistroServiziNotFound{
		if ( url==null || idServ==null || idServ.getSoggettoErogatore()==null )
			throw new DriverRegistroServiziException("[UDDILib.updateUrlXmlServizio]: Alcuni parametri non definiti");
		try{
			BusinessEntity be = getBusinessEntity(idServ.getSoggettoErogatore());
			BusinessService bs = getBusinessService(be,idServ);
			BindingTemplate bt = getBindingTemplate(bs);
			bt=updateAccessPoint(bt,url);
		}
		catch (DriverRegistroServiziNotFound e){
			throw e;
		}catch (Exception e){
			throw new DriverRegistroServiziException("[UDDILib.updateUrlXmlServizio]: "+e.getMessage(),e);
		}
	}


	/**
	 * Il metodo si occupa di modificare il nome di un Servizio
	 *
	 * @param idServOLD Vecchio Identificativo del Servizio
	 * @param idServNEW Nuovo Identificativo del Servizio
	 */
	public void updateIdServizio(IDServizio idServOLD,IDServizio idServNEW)throws DriverRegistroServiziException,DriverRegistroServiziNotFound{
		if ( idServOLD==null || idServOLD.getTipoServizio()==null || idServOLD.getServizio()==null ||
				idServOLD.getSoggettoErogatore()==null || idServOLD.getSoggettoErogatore().getTipo()==null || idServOLD.getSoggettoErogatore().getNome()==null)
			throw new DriverRegistroServiziException("[UDDILib.modificaNomeServizio]: Alcuni parametri non definiti");
	
		if ( idServNEW==null || idServNEW.getTipoServizio()==null || idServNEW.getServizio()==null ||
				idServNEW.getSoggettoErogatore()==null || idServNEW.getSoggettoErogatore().getTipo()==null || idServNEW.getSoggettoErogatore().getNome()==null)
			throw new DriverRegistroServiziException("[UDDILib.modificaNomeServizio]: Alcuni parametri non definiti");
	
		try{
			BusinessEntity be = getBusinessEntity(idServOLD.getSoggettoErogatore());
			BusinessService bs = getBusinessService(be, idServOLD);
			updateNomeBusinessService(bs,idServNEW);
		}	   
		catch (DriverRegistroServiziNotFound e){
			throw e;
		}catch (Exception e){
			throw new DriverRegistroServiziException("[UDDILib.modificaNomeServizio]: "+e.getMessage(),e);
		}

	}

	/**
	 * Si occupa di cancellare il servizio <var>servizio</var> registrato nel registro, 
	 * identificato dai parametri.
	 *
	 * @param idServ Identificativo del servizio
	 */	
	public void deleteServizio(IDServizio idServ) throws DriverRegistroServiziException{
		if ( idServ==null || idServ.getSoggettoErogatore()==null )
			throw new DriverRegistroServiziException("[UDDILib.deleteServizio]: Alcuni parametri non definiti");
		try{	
			deleteBusinessService(idServ);
		}
		catch (Exception e){
			throw new DriverRegistroServiziException("[UDDILib.deleteServizio]: "+e.getMessage(),e);
		}
	}


	
	
	
	
	
	
	/**
	 * Il metodo si occupa di modificare la TModel associata al servizio
	 *
	 * @param idServ Identificativo del servizio
	 * @param newIDAccordoServizio Nuovo accordo di servizio
	 */
	public void updateAccordoServizio(IDServizio idServ, IDAccordo newIDAccordoServizio)throws DriverRegistroServiziException,DriverRegistroServiziNotFound{
		if ( idServ==null || idServ.getSoggettoErogatore()==null || newIDAccordoServizio==null)
			throw new DriverRegistroServiziException("[UDDILib.updateAccordoServizio]: Alcuni parametri non definiti");
		try{

			BusinessEntity be = getBusinessEntity(idServ.getSoggettoErogatore());
			BusinessService bs = getBusinessService(be, idServ);
			BindingTemplate bt = getBindingTemplate(bs);
			createTModelServizio(bt,newIDAccordoServizio);
		}	   
		catch (DriverRegistroServiziNotFound e){
			throw e;
		}catch (Exception e){
			throw new DriverRegistroServiziException("[UDDILib.updateAccordoServizio]: "+e.getMessage(),e);
		}
	}

	/**
	 * Si occupa di recuperare un array di URL che puntano a XML di servizi presente 
	 * nel soggetto identificato dai parametri, che possiedeno l'accordo di servizio <var>accordo</var>.
	 *
	 * @param idSogg Identificativo del Soggetto erogatore
	 * @param idAccordo Accordo di Servizio
	 * @return Array di url di XML dei servizi
	 */
	public String[] getUrlXmlServizi(IDSoggetto idSogg, IDAccordo idAccordo)throws DriverRegistroServiziException,DriverRegistroServiziNotFound{
		if(idSogg==null || idAccordo==null)
			throw new DriverRegistroServiziException("[UDDILib.getUrlXmlServizi]: Alcuni parametri non definiti");
		
		try{
			// Soggetto
			BusinessEntity be = getBusinessEntity(idSogg);
	
			// Filtro per Accordi
			TModelBag bagAccordo = new TModelBag();
			TModel accordoS = getAccordoServizio(idAccordo);
			bagAccordo.add(new TModelKey(accordoS.getTModelKey()));
		
			FindQualifiers findQualifiers = new FindQualifiers();
			Vector<FindQualifier> qualifier = new Vector<FindQualifier>();
			qualifier.add(new FindQualifier(FindQualifier.exactNameMatch));
			qualifier.add(new FindQualifier(FindQualifier.caseSensitiveMatch));
			findQualifiers.setFindQualifierVector(qualifier);
			
			// l'ultimo parametro lo lascerei indefinito. 
			// Richiede un intero che stabilisce il numero di risultati che la ricerca find_service deve produrre. 
			// Ma noi a priori non lo possiamo sapere (anche se la ricerca dovrebbe essere sempre 1 o 2). 
			// Effettuo il filtro per bagAccordo
			ServiceList serviceList = this.proxy.find_service(be.getBusinessKey(), null, null, bagAccordo, findQualifiers, -1);
			if(serviceList.getServiceInfos()==null || serviceList.getServiceInfos().size()==0)
				throw new DriverRegistroServiziNotFound("BusinessServices non trovate");
			
			
			//questo vettore contiene tutti i servizi della businessEntity
			Vector<?> serviceInfoVector = serviceList.getServiceInfos().getServiceInfoVector();
		 
			if(serviceInfoVector.size()==0)
				throw new Exception("Servizi non trovati");
			
			//e ora vado ad analizzare un servizio alla volta
			String[]url = new String[serviceInfoVector.size()];
			for (int i=0; i<serviceInfoVector.size(); i++){
				ServiceInfo serviceInfo = (ServiceInfo) serviceInfoVector.elementAt(i);
				//System.out.println("SERVIZIO ["+serviceInfo.getServiceKey()+"]");
				ServiceDetail sd = this.proxy.get_serviceDetail(serviceInfo.getServiceKey());
			    Vector<?> v = sd.getBusinessServiceVector();
				BusinessService bs = (BusinessService) v.elementAt(0);			
				BindingTemplate bt=getBindingTemplate(bs);
				url[i] = getAccessPoint(bt);
				if(url[i]==null)
					throw new Exception("access point non presente?");
			}
			return url;
		}
		catch(DriverRegistroServiziNotFound e){
			throw e;
		}
		catch(TransportException e){
			throw new DriverRegistroServiziException("[UDDILib.getUrlXmlServizi]: "+e.getMessage(),e);
		}catch(Exception e){
			throw new DriverRegistroServiziException("[UDDILib.getUrlXmlServizi]: "+e.getMessage(),e);
		}
		

		
	}


	/**
	 * @param idAccordo
	 * @param soggettoErogatore
	 * @param tipoServizio
	 * @param nomeServizio
	 * @return URL XML dei servizi che rispettano la ricerca
	 * @throws DriverRegistroServiziException
	 * @throws DriverRegistroServiziNotFound
	 */
	protected String[] getUrlXMLServiziBySearch(IDAccordo idAccordo,IDSoggetto soggettoErogatore,String tipoServizio,String nomeServizio) throws DriverRegistroServiziException,DriverRegistroServiziNotFound{
		
		try{    

			// Filtro Soggetto
			String businessKey = null;
			if(soggettoErogatore!=null && soggettoErogatore.getTipo()!=null && soggettoErogatore.getNome()!=null){
				try{
					BusinessEntity be = this.getBusinessEntity(soggettoErogatore);
					if(be!=null)
						businessKey = be.getBusinessKey();
				}catch(DriverRegistroServiziNotFound e){
					throw new DriverRegistroServiziNotFound("[getBusinessServices] Soggetto ["+soggettoErogatore+"] usato come filtro non presente",e);
				}
			}
				
			// Filtro accordo
			TModelBag bagAccordo = null;
			if(idAccordo!=null){
				try{
					bagAccordo = new TModelBag();
					TModel accordoS = getAccordoServizio(idAccordo);
					bagAccordo.add(new TModelKey(accordoS.getTModelKey()));
				}catch(DriverRegistroServiziNotFound e){
					throw new DriverRegistroServiziNotFound("[getBusinessServices] Accordo ["+idAccordo.toString()+"] usato come filtro non presente",e);
				}	
			}
			
			// Filtro Servizio
			String keyService = null;
			if(tipoServizio!=null && nomeServizio!=null){
				keyService = tipoServizio+ "/" + nomeServizio;
			}else if(tipoServizio!=null){
				keyService = tipoServizio+ "/%";
			}else if(nomeServizio!=null){
				keyService = "%/"+nomeServizio;
			}else{
				keyService = "%/%";
			}
			Name nome = new Name(keyService);
			Vector<Name> nomi = new Vector<Name>();
			nomi.add(nome);
			
			// FindQualifiers
			FindQualifiers findQualifiers = null;
			if("%/%".equals(keyService)==false){
				findQualifiers = new FindQualifiers();
				Vector<FindQualifier> qualifier = new Vector<FindQualifier>();
				qualifier.add(new FindQualifier(FindQualifier.exactNameMatch));
				qualifier.add(new FindQualifier(FindQualifier.caseSensitiveMatch));
				findQualifiers.setFindQualifierVector(qualifier);
			}
			
			// l'ultimo parametro lo lascerei indefinito. 
			// Richiede un intero che stabilisce il numero di risultati che la ricerca find_service deve produrre. 
			// Ma noi a priori non lo possiamo sapere (anche se la ricerca dovrebbe essere sempre 1 o 2). 
			// Effettuo il filtro per bagAccordo
			ServiceList serviceList = this.proxy.find_service(businessKey, nomi, null, bagAccordo, findQualifiers, UDDILib.MAX_SEARCH);
		 
			//questo vettore contiene tutti i servizi della businessEntity
			Vector<?> serviceInfoVector = serviceList.getServiceInfos().getServiceInfoVector();
		 
			if(serviceInfoVector.size()==0){
				throw new DriverRegistroServiziNotFound("Non esistono BusinessService che rispettano il filtro selezionato accordo["+idAccordo+"] soggetto["+soggettoErogatore+"] tipo_servizio["+tipoServizio+"] nome_servizio["+nomeServizio+"]");
			}
			
			//e ora vado ad analizzare un servizio alla volta
			String[]url = new String[serviceInfoVector.size()];
			for (int i=0; i<serviceInfoVector.size(); i++){
				ServiceInfo serviceInfo = (ServiceInfo) serviceInfoVector.elementAt(i);
				//System.out.println("SERVIZIO ["+serviceInfo.getServiceKey()+"]");
				ServiceDetail sd = this.proxy.get_serviceDetail(serviceInfo.getServiceKey());
			    Vector<?> v = sd.getBusinessServiceVector();
				BusinessService bs = (BusinessService) v.elementAt(0);			
				BindingTemplate bt=getBindingTemplate(bs);
				url[i] = getAccessPoint(bt);
				if(url[i]==null)
					throw new Exception("access point non presente?");
			}
			return url;
		}
		catch (UDDIException e){
			throw new DriverRegistroServiziException("[UDDILib.getBusinessService]: "+e.getMessage(),e);
		}
		catch (TransportException e){
			throw new DriverRegistroServiziException("[UDDILib.getBusinessService]: "+e.getMessage(),e);
		}
		catch( DriverRegistroServiziNotFound de){
			throw de;
		}
		catch (Exception e){
			throw new DriverRegistroServiziException("[UDDILib.getBusinessService]: "+e.getMessage(),e);
		}

	}
	
}
