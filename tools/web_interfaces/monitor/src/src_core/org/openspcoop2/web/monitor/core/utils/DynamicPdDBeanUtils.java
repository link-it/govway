/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it).
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
package org.openspcoop2.web.monitor.core.utils;

import java.awt.Font;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.model.SelectItem;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.core.commons.search.AccordoServizioParteComune;
import org.openspcoop2.core.commons.search.AccordoServizioParteSpecifica;
import org.openspcoop2.core.commons.search.IdAccordoServizioParteComune;
import org.openspcoop2.core.commons.search.IdAccordoServizioParteComuneGruppo;
import org.openspcoop2.core.commons.search.PortaApplicativa;
import org.openspcoop2.core.commons.search.PortaDelegata;
import org.openspcoop2.core.commons.search.Soggetto;
import org.openspcoop2.core.commons.search.constants.TipoPdD;
import org.openspcoop2.core.config.driver.db.DriverConfigurazioneDB;
import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDGruppo;
import org.openspcoop2.core.id.IDPortaApplicativa;
import org.openspcoop2.core.id.IDPortaDelegata;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.mapping.MappingErogazionePortaApplicativa;
import org.openspcoop2.core.mapping.MappingFruizionePortaDelegata;
import org.openspcoop2.core.plugins.IdPlugin;
import org.openspcoop2.core.plugins.Plugin;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziException;
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
import org.openspcoop2.core.registry.driver.IDServizioFactory;
import org.openspcoop2.core.registry.driver.db.DriverRegistroServiziDB;
import org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.engine.utils.NamingUtils;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.web.monitor.core.bean.UserDetailsBean;
import org.openspcoop2.web.monitor.core.core.PddMonitorProperties;
import org.openspcoop2.web.monitor.core.core.PermessiUtenteOperatore;
import org.openspcoop2.web.monitor.core.core.Utility;
import org.openspcoop2.web.monitor.core.dao.DynamicUtilsService;
import org.openspcoop2.web.monitor.core.dao.IDynamicUtilsService;
import org.slf4j.Logger;

/**
 * DynamicPdDBeanUtils
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class DynamicPdDBeanUtils implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L; 

	private transient Logger log = null;

	private transient IDynamicUtilsService dynamicUtilsService = null;

	private static DynamicPdDBeanUtils instance = null;

	public static Integer maxSelectItemsWidth = 700;

	public static Integer defaultSelectItemsWidth = 412;

	private transient AffineTransform affineTransform = null;
	private transient FontRenderContext fontRenderContext = null;

	private transient Font defaultFont = null;

	public static DynamicPdDBeanUtils getInstance(Logger log) throws Exception{
		if(DynamicPdDBeanUtils.instance == null)
			init(log);

		return DynamicPdDBeanUtils.instance;
	}

	public static synchronized void init(Logger log) throws Exception{
		if(DynamicPdDBeanUtils.instance == null)
			DynamicPdDBeanUtils.instance = new DynamicPdDBeanUtils(log);
	}

	public DynamicPdDBeanUtils(Logger log) throws Exception{
		this(null, null, 
				null, null,
				log);
	}
	public DynamicPdDBeanUtils(org.openspcoop2.core.commons.search.dao.IServiceManager serviceManager, org.openspcoop2.core.plugins.dao.IServiceManager pluginsServiceManager, 
			DriverRegistroServiziDB driverRegistroServiziDB, DriverConfigurazioneDB driverConfigurazioneDB,
			Logger log) throws Exception{
		this.log = log;
		try{
			this.log.debug("Init Dynamic Utils in corso...");
			if(serviceManager!=null) {
				this.dynamicUtilsService = new DynamicUtilsService(serviceManager, pluginsServiceManager,
						driverRegistroServiziDB, driverConfigurazioneDB);
			}
			else {
				this.dynamicUtilsService = new DynamicUtilsService();
			}
			String fontName = PddMonitorProperties.getInstance(log).getConsoleFontFamilyName();
			int fontStyle = PddMonitorProperties.getInstance(log).getConsoleFontStyle();
			this.defaultFont = new Font(fontName,fontStyle, 14);
			this.log.debug("Init Dynamic Utils in completato.");
		}catch(Exception e){
			this.log.error("Si e' verificato un errore durante la init: " + e.getMessage(),e);
		}
	}

	public boolean isTipoSoggettoCompatibile(String tipo1, String tipo2)  throws Exception{

		// se uno dei due non e' impostato allora sono compatibili
		if(StringUtils.isBlank(tipo1) || StringUtils.isBlank(tipo2))
			return true;

		List<String> tipiDisponibili1 = new ArrayList<String>();
		List<String> tipiDisponibili2 = new ArrayList<String>();

		if(!tipo1.equals("*") ){
			IProtocolFactory<?> protocolFactory = ProtocolFactoryManager.getInstance().getProtocolFactoryByOrganizationType(tipo1);
			tipiDisponibili1.add(protocolFactory.getProtocol());
		}

		if(!tipo2.equals("*")){
			IProtocolFactory<?> protocolFactory = ProtocolFactoryManager.getInstance().getProtocolFactoryByOrganizationType(tipo2);
			tipiDisponibili2.add(protocolFactory.getProtocol());
		}

		Map<String, String> mappaTipi = new HashMap<String, String>();
		if(tipiDisponibili1 != null && tipiDisponibili1.size() > 0)
			for (String tipo : tipiDisponibili1) {
				if(!mappaTipi.containsKey(tipo))
					mappaTipi.put(tipo, tipo);
			}

		if(tipiDisponibili2 != null && tipiDisponibili2.size() > 0)
			for (String tipo : tipiDisponibili2) {
				if(!mappaTipi.containsKey(tipo))
					mappaTipi.put(tipo, tipo);
			}

		// controllo di validita  i tipi dei soggetti che gestisce il protocollo devono essere gli stessi
		if(tipiDisponibili1.size() > 0 && tipiDisponibili1.size() != mappaTipi.keySet().size())
			return false;

		if(tipiDisponibili2.size() > 0 && tipiDisponibili2.size() != mappaTipi.keySet().size())
			return false;


		return true;
	}

	public boolean isTipoSoggettoCompatibileConProtocollo(String tipoSoggetto, String tipoProtocollo)  throws Exception{

		// se uno dei due non e' impostato allora sono compatibili
		if(StringUtils.isBlank(tipoSoggetto) || StringUtils.isBlank(tipoProtocollo))
			return true;

		if(!tipoSoggetto.equals("*") ){
			IProtocolFactory<?> protocolFactory = ProtocolFactoryManager.getInstance().getProtocolFactoryByOrganizationType(tipoSoggetto);

			if(protocolFactory == null || !protocolFactory.getProtocol().equals(tipoProtocollo))
				return false;
		}

		return true;
	}

	public boolean isTipoServizioCompatibileConProtocollo(String tipoServizio, String tipoProtocollo)  throws Exception{

		// se uno dei due non e' impostato allora sono compatibili
		if(StringUtils.isBlank(tipoServizio) || StringUtils.isBlank(tipoProtocollo))
			return true;

		if(!tipoServizio.equals("*") ){
			IProtocolFactory<?> protocolFactory = ProtocolFactoryManager.getInstance().getProtocolFactoryByServiceType(tipoServizio);

			if(protocolFactory == null || !protocolFactory.getProtocol().equals(tipoProtocollo))
				return false;
		}

		return true;
	}


	public boolean isAccordoCompatibileConProtocollo(String tipoErogatore, String nomeErogatore,String accordo, String tipoProtocollo)  throws Exception{

		// se uno dei due non e' impostato allora sono compatibili
		if(StringUtils.isBlank(accordo) || StringUtils.isBlank(tipoProtocollo))
			return true;

		List<String> tipiDisponibili1 = new ArrayList<String>();

		if(!accordo.equals("*") ){
			List<AccordoServizioParteSpecifica> servizi2 = this.dynamicUtilsService.getServizi(tipoProtocollo,accordo, tipoErogatore, nomeErogatore);

			if(servizi2 != null && servizi2.size() > 0){
				for (AccordoServizioParteSpecifica accordoServizioParteSpecifica : servizi2) {
					String tipo = accordoServizioParteSpecifica.getIdErogatore() != null ? accordoServizioParteSpecifica.getIdErogatore().getTipo() : null;
					if(tipo != null){
						IProtocolFactory<?> protocolFactory = ProtocolFactoryManager.getInstance().getProtocolFactoryByOrganizationType(tipo);

						if(!tipiDisponibili1 .contains(protocolFactory.getProtocol()))
							tipiDisponibili1.add(protocolFactory.getProtocol());
					}
				}
			}
		}

		if(!tipoProtocollo.equals("*")){
			if(tipiDisponibili1.size() > 0 && !tipiDisponibili1 .contains(tipoProtocollo))
				return false;
		}


		return true;
	}

	public boolean isSupportataAutenticazioneApplicativiEsterniErogazione(String tipoProtocollo)  throws Exception{

		// se uno dei due non e' impostato allora sono compatibili
		if(tipoProtocollo==null || "".equals(tipoProtocollo) || StringUtils.isBlank(tipoProtocollo))
			return false;

		IProtocolFactory<?> protocolFactory = ProtocolFactoryManager.getInstance().getProtocolFactoryByName(tipoProtocollo);

		if(protocolFactory == null || !protocolFactory.getProtocol().equals(tipoProtocollo)) {
			return false;
		}

		return protocolFactory.createProtocolConfiguration().isSupportoAutenticazioneApplicativiEsterniErogazioni();
	}
	
	public List<Object> findElencoServiziApplicativiFromSoggettoLocale(String tipoProtocollo,String tipoSoggetto,String nomeSoggetto, boolean trasporto, boolean token){
		List<Object> list = new ArrayList<Object>();
		Soggetto erogatore = this.dynamicUtilsService.findSoggettoByTipoNome(tipoSoggetto, nomeSoggetto);

		if(erogatore != null)
			list = this.dynamicUtilsService.findElencoServiziApplicativi(tipoProtocollo,erogatore, trasporto, token);


		return list;
	}

	public List<SelectItem> getListaSelectItemsServiziApplicativiFromSoggettoLocale(String tipoProtocollo,String tipoSoggetto,String nomeSoggetto, boolean trasporto, boolean token){
		List<SelectItem> sa = new ArrayList<SelectItem>();

		List<Object> list = this.findElencoServiziApplicativiFromSoggettoLocale(tipoProtocollo,tipoSoggetto, nomeSoggetto, trasporto, token);

		for (Object res : list) {
			sa.add(new SelectItem((String) res));
		}

		return sa;
	}



	public List<SelectItem> getSoggetti() {
		List<SelectItem> soggetti = new ArrayList<SelectItem>();
		String tipoProtocollo = null;
		String idPorta = null;
		List<Soggetto> list = this.dynamicUtilsService.findElencoSoggetti(tipoProtocollo ,idPorta);

		for (Soggetto soggetto : list) {
			soggetti.add(new SelectItem(soggetto.getTipoSoggetto() + "/" + soggetto.getNomeSoggetto()));
		}
		return soggetti;
	}

	public List<SelectItem> getIdPorte() {
		List<SelectItem> idPorte = new ArrayList<SelectItem>();

		String tipoProtocollo = null;
		String idPorta = null;
		List<Soggetto> list = this.dynamicUtilsService.findElencoSoggetti(tipoProtocollo ,idPorta);

		List<String> checkContains = new ArrayList<String>();
		for (Soggetto soggetto : list) {
			if(checkContains.contains(soggetto.getIdentificativoPorta())==false){
				idPorte.add(new SelectItem(soggetto.getIdentificativoPorta()));
				checkContains.add(soggetto.getIdentificativoPorta());
			}
		}
		return idPorte;
	}

	public String getIdPortaFromSoggetto(String tipoSoggetto,String nomeSoggetto) {
		String idPorta =null;
		Soggetto soggetto = this.dynamicUtilsService.findSoggettoByTipoNome(tipoSoggetto, nomeSoggetto);

		if(soggetto != null)
			idPorta = soggetto.getIdentificativoPorta();

		return idPorta;
	}

	public List<SelectItem> getSoggettiFromIdPorta(String idPorta) {
		List<SelectItem> soggetti = new ArrayList<SelectItem>();
		String tipoProtocollo = null;
		List<Soggetto> list = this.dynamicUtilsService.findElencoSoggetti(tipoProtocollo ,idPorta);

		for (Soggetto soggetto : list) {
			soggetti.add(new SelectItem(soggetto.getTipoSoggetto() + "/" + soggetto.getNomeSoggetto()));
		}
		return soggetti;
	}

	public String getServerFromSoggetto(String tipoSoggetto,String nomeSoggetto) {
		String idPorta =null;
		Soggetto soggetto = this.dynamicUtilsService.findSoggettoByTipoNome(tipoSoggetto, nomeSoggetto);

		if(soggetto != null)
			idPorta = soggetto.getServer();

		return idPorta;
	}

	public boolean checkTipoPdd(String nome,TipoPdD tipoPdD) {
		return this.dynamicUtilsService.checkTipoPdd(nome, tipoPdD);
	}

	public List<SelectItem> getListaGruppi(String tipoProtocollo){
		List<SelectItem> gruppi = new ArrayList<SelectItem>();

		try{
			List<IDGruppo>  lista = this.dynamicUtilsService.getGruppi(tipoProtocollo);
			if(lista!=null && !lista.isEmpty()) {
				for (IDGruppo id : lista) {
					SelectItem item = new SelectItem(id.getNome(),id.getNome());
					gruppi.add(item);
				}
			}
		}catch(Exception e){}

		return gruppi;
	}
	
	public List<String> getListaNomiGruppi(){
		return getListaNomiGruppi(null);
	}
	public List<String> getListaNomiGruppi(String tipoProtocollo){
		List<String> gruppi = new ArrayList<String>();

		try{
			List<IDGruppo>  lista = this.dynamicUtilsService.getGruppi(tipoProtocollo);
			if(lista!=null && !lista.isEmpty()) {
				for (IDGruppo id : lista) {
					gruppi.add(id.getNome());
				}
			}
		}catch(Exception e){}

		return gruppi;
	}
	
	public boolean existsGruppi(String tipoProtocollo) {
		return this.dynamicUtilsService.countGruppi(tipoProtocollo)>0;
	}
		
	
	public Map<String, String> findAzioniFromServizio(String tipoProtocollo,IDServizio idServizio, String val){
		String nomeServizio = idServizio.getNome();
		String tipoServizio = idServizio.getTipo();
		String nomeErogatore = idServizio.getSoggettoErogatore().getNome();
		String tipoErogatore = idServizio.getSoggettoErogatore().getTipo();
		Integer versioneServizio = idServizio.getVersione();
		
		return findAzioniFromServizio(tipoProtocollo, tipoServizio, nomeServizio, tipoErogatore, nomeErogatore, versioneServizio, val);
	}
	
	/***
	 * 
	 * Restituisce l'elenco delle Azioni dell'Accordo di servizio passato come parametro
	 * 
	 * La Mappa contiene
	 * Nome Azione
	 * 
	 * @param tipoProtocollo
	 * @param tipoServizio
	 * @param nomeServizio
	 * @param tipoErogatore
	 * @param nomeErogatore
	 * @param versioneServizio
	 * @return Azioni dell'Accordo di servizio
	 */
	public Map<String, String> findAzioniFromServizio(String tipoProtocollo,String tipoServizio,String nomeServizio,  String tipoErogatore, String nomeErogatore,Integer versioneServizio, String val){
		Map<String, String>  map = new HashMap<String, String>();

		//		if(idAccordo != null && nomeServizio != null){
		this.log.debug("Get Lista Azioni from Servizio [nome: " + nomeServizio + "]");
		try{
			map = this.dynamicUtilsService.findAzioniFromServizio(tipoProtocollo,tipoServizio, nomeServizio,tipoErogatore,nomeErogatore,versioneServizio,val);
		}catch(Exception e){
			this.log.error("Si e' verificato un errore durante la ricerca  Azioni per il servizio"+nomeServizio+ "]",e);
		}
		return map;
	}

	/***
	 * 
	 * Restituisce la lista delle select items  per le azioni
	 * 
	 * @param tipoProtocollo
	 * @param tipoServizio
	 * @param nomeServizio
	 * @param tipoErogatore
	 * @param nomeErogatore
	 * @param versioneServizio
	 * @return Lista delle SelectItems per le Azioni trovate
	 */
	public List<SelectItem> getListaSelectItemsAzioniFromServizio(String tipoProtocollo, String tipoServizio,String nomeServizio,  String tipoErogatore , String nomeErogatore,Integer versioneServizio, String val){
		List<SelectItem> azioni = new ArrayList<SelectItem>();

		try{
			Map<String, String>  map = this.findAzioniFromServizio(tipoProtocollo,tipoServizio,nomeServizio,tipoErogatore,nomeErogatore,versioneServizio,val);
			for (String azione : map.keySet()) {
				SelectItem item = new SelectItem(azione,map.get(azione));
				azioni.add(item);
			}
		}catch(Exception e){}

		return azioni;
	}


	/***
	 * 
	 * Restituisce l'elenco dei servizi associati al soggetto erogatore passato come parametro
	 * 
	 */
	public List<Map<String, Object>> findElencoServiziSoggettoErogatore(String tipoProtocollo,Soggetto erogatore){
		List<Map<String, Object>>  list = null;
		this.log.debug("Find Lista Servizi per il Soggetto Erogatore [" + (erogatore != null ? erogatore.getNomeSoggetto() : "Null")+ "]");
		try{
			list = this.dynamicUtilsService.findElencoServizi(tipoProtocollo,erogatore);
		}catch(Exception e){
			this.log.error("Si e' verificato un errore durante la ricerca dei servizi per il Soggetto Erogatore [" + (erogatore != null ? erogatore.getNomeSoggetto() : "Null")+ "]");
		}
		return list;
	}


	public List<SelectItem> getListaSelectItemsElencoServiziSoggettoErogatore(String tipoProtocollo,Soggetto erogatore, boolean showTipoServizio, boolean showUriAccordo){
		List<SelectItem> servizi = new ArrayList<SelectItem>();

		try{
			List<Map<String, Object>> mapServizi = this.findElencoServiziSoggettoErogatore(tipoProtocollo,erogatore);

			if(mapServizi != null && mapServizi.size() > 0){
				for (Map<String, Object> res : mapServizi) {

					String label= null;
					// servizi.add(new
					// SelectItem(servizio.getAccordo().getNome()+"@"+servizio.getNome()));
					StringBuilder uri = new StringBuilder();

					Object obj = res.get(JDBCUtilities.getAlias(AccordoServizioParteSpecifica.model().NOME));

					String nomeAsps = (obj instanceof String) ? (String) obj : null;

					String tipoAsps = null;
					if(showTipoServizio){
						obj = res.get(JDBCUtilities.getAlias(AccordoServizioParteSpecifica.model().TIPO));
						tipoAsps = (obj instanceof String) ? (String) obj : null;
						if(tipoAsps != null)
							uri.append(tipoAsps).append("/");
					}

					uri.append(nomeAsps);

					if(showUriAccordo){
						obj = res.get("idAccordo");

						AccordoServizioParteComune aspc = (obj instanceof AccordoServizioParteComune) ? (AccordoServizioParteComune) obj : null;

						if(aspc != null){
							String nomeAspc = aspc.getNome();

							Integer versioneAspc = aspc.getVersione();

							String nomeReferenteAspc = (aspc.getIdReferente() != null) ? aspc.getIdReferente().getNome() : null;

							String tipoReferenteAspc= (aspc.getIdReferente() != null) ? aspc.getIdReferente().getTipo() : null;

							try {
								uri.append(" (").append(IDAccordoFactory
										.getInstance()
										.getUriFromValues(nomeAspc,tipoReferenteAspc,nomeReferenteAspc,versioneAspc)

										//								res.getIdAccordoServizioParteComune().getNome(),
										//								res.getIdAccordoServizioParteComune()
										//										.getIdSoggetto() != null ? res
										//										.getIdAccordoServizioParteComune()
										//										.getIdSoggetto().getTipo() : null,
										//								res.getIdAccordoServizioParteComune()
										//										.getIdSoggetto() != null ? res
										//										.getIdAccordoServizioParteComune()
										//										.getIdSoggetto().getNome() : null,
										//								res.getIdAccordoServizioParteComune()
										//										.getVersione())
										);

								uri.append(")");
							} catch (DriverRegistroServiziException e) {
								// ignore
								uri = new StringBuilder();
								uri.append("");
							}
						}
					}

					label = uri.toString();
					//compongo la label e la imposto
					servizi.add(new SelectItem(label));
				}
			}
		}catch(Exception e){
			this.log.error("Si e' verificato un errore durante la ricerca dei servizi per il Soggetto Erogatore [" + (erogatore != null ? erogatore.getNomeSoggetto() : "Null")+ "]");
		}
		return servizi;
	}


	/**
	 * Resistuisce l'elenco degli accordi di servizio
	 * 
	 */
	public List<SelectItem> getListaSelectItemsAccordiServizio(String tipoProtocollo, String tipoSoggetto, String nomeSoggetto, boolean isReferente, boolean isErogatore, String tag){
		List<SelectItem> servizi = new ArrayList<SelectItem>();

		try{
			List<AccordoServizioParteComune> listaAccordi = this.dynamicUtilsService.getAccordiServizio(tipoProtocollo,tipoSoggetto, nomeSoggetto, isReferente, isErogatore, tag);

			List<String> lstLabelOrdinate = new ArrayList<>();
			Map<String, String> mapElementi = new HashMap<>();
			
			if(listaAccordi != null && listaAccordi.size() > 0){
				for (AccordoServizioParteComune aspc : listaAccordi) {

					if(aspc != null){
						String nomeAspc = aspc.getNome();

						Integer versioneAspc = aspc.getVersione();

						String nomeReferenteAspc = (aspc.getIdReferente() != null) ? aspc.getIdReferente().getNome() : null;

						String tipoReferenteAspc= (aspc.getIdReferente() != null) ? aspc.getIdReferente().getTipo() : null;

						String label = null;
						String uri = null;
						try {
							uri = IDAccordoFactory.getInstance().getUriFromValues(nomeAspc,tipoReferenteAspc,nomeReferenteAspc,versioneAspc);
							IDAccordo idAccordo =  IDAccordoFactory.getInstance().getIDAccordoFromValues(nomeAspc,tipoReferenteAspc,nomeReferenteAspc,versioneAspc);
							label = NamingUtils.getLabelAccordoServizioParteComune(idAccordo);
						} catch (DriverRegistroServiziException e) {
							// ignore
							uri = "";
						}
						
						lstLabelOrdinate.add(label);
						mapElementi.put(label, uri);
						
					}


				}
			}
			
			if(lstLabelOrdinate.size() > 0) {
				Collections.sort(lstLabelOrdinate);
				
				for (String string : lstLabelOrdinate) {
					servizi.add(new SelectItem(mapElementi.get(string), string));  
				}
			}
			
		}catch(Exception e){
			this.log.error("Si e' verificato un errore durante la ricerca degli Accordi di servizio il Soggetto [" + tipoSoggetto + "/" + nomeSoggetto+ "] Referente ["+isReferente+"], Erogatore ["+isErogatore+"]");
		}
		return servizi;
	}


	public List<SelectItem> getListaSelectItemsElencoServiziFromAccordoAndSoggettoErogatore(String tipoProtocollo,String gruppo, IDAccordo idAccordo,
			String uriAccordoServizio, String tipoSoggetto, String nomeSoggetto, String input){
		return getListaSelectItemsElencoServiziFromAccordoAndSoggettoErogatore(tipoProtocollo, gruppo, idAccordo, uriAccordoServizio, tipoSoggetto, nomeSoggetto, input, false);
	}


	public List<SelectItem> getListaSelectItemsElencoServiziFromAccordoAndSoggettoErogatore(String tipoProtocollo,String gruppo, IDAccordo idAccordo,
			String uriAccordoServizio, String tipoSoggetto , String nomeSoggetto, String input, boolean soloOperativi){
		List<SelectItem> servizi = new ArrayList<SelectItem>();

		try{

			UserDetailsBean user = Utility.getLoggedUser();

			PermessiUtenteOperatore permessiUtenteOperatoreRecheck = null;
			if(user!=null && !user.isAdmin()) {
				permessiUtenteOperatoreRecheck = new PermessiUtenteOperatore();
				if(user.getUtenteSoggettoList()!=null && !user.getUtenteSoggettoList().isEmpty()) {
					permessiUtenteOperatoreRecheck.getListIDSoggetti().addAll(user.getUtenteSoggettoList());
				}
				if(user.getUtenteServizioList()!=null && !user.getUtenteServizioList().isEmpty()) {
					permessiUtenteOperatoreRecheck.getListIDServizi().addAll(user.getUtenteServizioList());
				}
			}
			
			if(permessiUtenteOperatoreRecheck!=null) {
				
				List<IDServizio> listIdServiziErogazione = this.dynamicUtilsService.getServiziErogazione(tipoProtocollo, tipoSoggetto, nomeSoggetto, input, false, permessiUtenteOperatoreRecheck, true);
				
				List<IDServizio> listIdServiziFruizione = this.dynamicUtilsService.getServiziFruizione(tipoProtocollo, tipoSoggetto, nomeSoggetto, input, false, permessiUtenteOperatoreRecheck, true);
				
				List<IDServizio> listIdServizi = new ArrayList<IDServizio>();
				if(listIdServiziErogazione!=null && !listIdServiziErogazione.isEmpty()) {
					for (IDServizio idServizio : listIdServiziErogazione) {
						if(!listIdServizi.contains(idServizio)) {
							listIdServizi.add(idServizio);
						}
					}
				}
				if(listIdServiziFruizione!=null && !listIdServiziFruizione.isEmpty()) {
					for (IDServizio idServizio : listIdServiziFruizione) {
						if(!listIdServizi.contains(idServizio)) {
							listIdServizi.add(idServizio);
						}
					}
				}
				if(listIdServizi!=null && !listIdServizi.isEmpty()) {
					IDAccordoFactory idAccordoFactory = null;
					if(idAccordo!=null) {
						idAccordoFactory = IDAccordoFactory.getInstance();
					}
					
					List<String> lstLabelOrdinate = new ArrayList<>();
					Map<String, String> mapElementi = new HashMap<>();
					for (IDServizio res : listIdServizi) {
						
						
						if( (gruppo!=null && !"".equals(gruppo)) || idAccordo!=null ) {
							
							AccordoServizioParteComune aspc = this.dynamicUtilsService.getAccordoServizio(tipoProtocollo, res.getSoggettoErogatore(), 
									res.getTipo(), res.getNome(), res.getVersione());
							
							if(idAccordo!=null) {
								IDAccordo idAccordoDB = idAccordoFactory.getIDAccordoFromValues(aspc.getNome(), aspc.getIdReferente().getTipo(), aspc.getIdReferente().getNome(), aspc.getVersione());
								if(!idAccordo.equals(idAccordoDB)) {
									continue;
								}
							}
							
							if((gruppo!=null && !"".equals(gruppo))) {
								IdAccordoServizioParteComune idAspc = new IdAccordoServizioParteComune();
								idAspc.setIdSoggetto(aspc.getIdReferente());
								idAspc.setNome(aspc.getNome());
								idAspc.setVersione(aspc.getVersione());
								List<IdAccordoServizioParteComuneGruppo> lGruppi = this.dynamicUtilsService.getAccordoServizioGruppi(idAspc);
								boolean found = false;
								if(lGruppi!=null && !lGruppi.isEmpty()) {
									for (IdAccordoServizioParteComuneGruppo gruppoCheck : lGruppi) {
										if(gruppoCheck.getIdGruppo().getNome().equals(gruppo)) {
											found = true;
											break;
										}
									}
								}
								if(!found) {
									continue;
								}
							}
						}
						
						boolean add= true;
						String value= null;
						// servizi.add(new
						// SelectItem(servizio.getAccordo().getNome()+"@"+servizio.getNome()));
						StringBuilder uri = new StringBuilder();

						String nomeAsps = res.getNome();

						String tipoAsps = res.getTipo();

						if(tipoAsps != null)
							uri.append(tipoAsps).append("/");

						uri.append(nomeAsps).append(":").append(res.getVersione());
						

//							if(showErogatore ){
						uri.append(" (").append(res.getSoggettoErogatore().getTipo()).append("/").append(res.getSoggettoErogatore().getNome()).append(")"); 
//							}
						
						IDServizio idServizio = IDServizioFactory.getInstance().getIDServizioFromValues(tipoAsps, nomeAsps, res.getSoggettoErogatore().getTipo(), res.getSoggettoErogatore().getNome(), res.getVersione());
						String label = NamingUtils.getLabelAccordoServizioParteSpecifica(tipoProtocollo,idServizio);
						
						value = uri.toString();
						//compongo la label e la imposto
						if(soloOperativi){ // controllo se il soggetto e' associato ad una pdd operativa
							String nomePddFromSoggetto = this.getServerFromSoggetto(res.getSoggettoErogatore().getTipo(), res.getSoggettoErogatore().getNome());
							add = this.checkTipoPdd(nomePddFromSoggetto, TipoPdD.OPERATIVO);
						}

						// Controllo spostato nei metodi che ottengono la lista
//							if(add && !user.isAdmin()){
	//
//								// controllo sul soggetto
//								boolean existsPermessoSoggetto = false;
//								if(user.getSizeSoggetti()>0){
//									for (IDSoggetto utenteSoggetto : user.getUtenteSoggettoList()) {
//										if(res.getSoggettoErogatore().getTipo().equals(utenteSoggetto.getTipo()) &&
//												res.getSoggettoErogatore().getNome().equals(utenteSoggetto.getNome())){
//											existsPermessoSoggetto = true;
//											break;
//										}
//									}
//								}
	//
//								boolean existsPermessoServizio = false;
//								if(!existsPermessoSoggetto){
//									if(user.getSizeServizio()>0){
//										for (IDServizio utenteSoggetto : user.getUtenteServizioList()) {
//											if(res.getSoggettoErogatore().getTipo().equals(utenteSoggetto.getSoggettoErogatore().getTipo()) &&
//													res.getSoggettoErogatore().getNome().equals(utenteSoggetto.getSoggettoErogatore().getNome()) &&
//													res.getTipo().equals(utenteSoggetto.getTipo()) &&
//													res.getNome().equals(utenteSoggetto.getNome())){
//												existsPermessoServizio = true;
//												break;
//											}
//										}
//									}
//								}
	//
//								add = (existsPermessoSoggetto || existsPermessoServizio);
//							}

						if(add) {
							lstLabelOrdinate.add(label);
							mapElementi.put(label, value);
						}
					}

					if(lstLabelOrdinate.size() > 0) {
						Collections.sort(lstLabelOrdinate);
						
						for (String string : lstLabelOrdinate) {
							servizi.add(new SelectItem(mapElementi.get(string), string));  
						}
					}
				}
				
			}
			else {
			
				List<AccordoServizioParteSpecifica> servizi2 = this.dynamicUtilsService.getServizi(tipoProtocollo,uriAccordoServizio, tipoSoggetto, nomeSoggetto, input);
	
				IDAccordoFactory idAccordoFactory = null;
				if(idAccordo!=null) {
					idAccordoFactory = IDAccordoFactory.getInstance();
				}
				
				if(servizi2 != null && servizi2.size() > 0){
					for (AccordoServizioParteSpecifica res : servizi2) {
						
						if( (gruppo!=null && !"".equals(gruppo)) || idAccordo!=null ) {
							
							if(idAccordo!=null) {
								IDAccordo idAccordoDB = idAccordoFactory.getIDAccordoFromValues(res.getIdAccordoServizioParteComune().getNome(), 
										res.getIdAccordoServizioParteComune().getIdSoggetto().getTipo(), res.getIdAccordoServizioParteComune().getIdSoggetto().getNome(), 
										res.getIdAccordoServizioParteComune().getVersione());
								if(!idAccordo.equals(idAccordoDB)) {
									continue;
								}
							}
							
							if((gruppo!=null && !"".equals(gruppo))) {
								List<IdAccordoServizioParteComuneGruppo> lGruppi = this.dynamicUtilsService.getAccordoServizioGruppi(res.getIdAccordoServizioParteComune());
								boolean found = false;
								if(lGruppi!=null && !lGruppi.isEmpty()) {
									for (IdAccordoServizioParteComuneGruppo gruppoCheck : lGruppi) {
										if(gruppoCheck.getIdGruppo().getNome().equals(gruppo)) {
											found = true;
											break;
										}
									}
								}
								if(!found) {
									continue;
								}
							}
						}
						
						boolean add= true;
						String value= null;
						// servizi.add(new
						// SelectItem(servizio.getAccordo().getNome()+"@"+servizio.getNome()));
						StringBuilder uri = new StringBuilder();
	
						String nomeAsps = res.getNome();
	
						String tipoAsps = res.getTipo();
	
						if(tipoAsps != null)
							uri.append(tipoAsps).append("/");
	
						uri.append(nomeAsps).append(":").append(res.getVersione());
						
	
	//					if(showErogatore ){
						uri.append(" (").append(res.getIdErogatore().getTipo()).append("/").append(res.getIdErogatore().getNome()).append(")"); 
	//					}
						
						IDServizio idServizio = IDServizioFactory.getInstance().getIDServizioFromValues(tipoAsps, nomeAsps, res.getIdErogatore().getTipo(), res.getIdErogatore().getNome(), res.getVersione());
						String label = StringUtils.isEmpty(nomeSoggetto) ? NamingUtils.getLabelAccordoServizioParteSpecifica(tipoProtocollo,idServizio) 
								: NamingUtils.getLabelAccordoServizioParteSpecificaSenzaErogatore(tipoProtocollo, idServizio.getTipo(), idServizio.getNome(), idServizio.getVersione());
						
						value = uri.toString();
						//compongo la label e la imposto
						if(soloOperativi){ // controllo se il soggetto e' associato ad una pdd operativa
							String nomePddFromSoggetto = this.getServerFromSoggetto(res.getIdErogatore().getTipo(), res.getIdErogatore().getNome());
							add = this.checkTipoPdd(nomePddFromSoggetto, TipoPdD.OPERATIVO);
						}
	
						// Controllo spostato nei metodi che ottengono la lista
	//					if(add && !user.isAdmin()){
	//
	//						// controllo sul soggetto
	//						boolean existsPermessoSoggetto = false;
	//						if(user.getSizeSoggetti()>0){
	//							for (IDSoggetto utenteSoggetto : user.getUtenteSoggettoList()) {
	//								if(res.getIdErogatore().getTipo().equals(utenteSoggetto.getTipo()) &&
	//										res.getIdErogatore().getNome().equals(utenteSoggetto.getNome())){
	//									existsPermessoSoggetto = true;
	//									break;
	//								}
	//							}
	//						}
	//
	//						boolean existsPermessoServizio = false;
	//						if(!existsPermessoSoggetto){
	//							if(user.getSizeServizio()>0){
	//								for (IDServizio utenteSoggetto : user.getUtenteServizioList()) {
	//									if(res.getIdErogatore().getTipo().equals(utenteSoggetto.getSoggettoErogatore().getTipo()) &&
	//											res.getIdErogatore().getNome().equals(utenteSoggetto.getSoggettoErogatore().getNome()) &&
	//											res.getTipo().equals(utenteSoggetto.getTipo()) &&
	//											res.getNome().equals(utenteSoggetto.getNome())){
	//										existsPermessoServizio = true;
	//										break;
	//									}
	//								}
	//							}
	//						}
	//
	//						add = (existsPermessoSoggetto || existsPermessoServizio);
	//					}
	
						if(add)
							servizi.add(new SelectItem(value, label)); 
					}
				}
			}

		}catch(Exception e){
			this.log.error("Si e' verificato un errore durante la ricerca dei servizi per l'accordo ["+uriAccordoServizio+"] erogati dal Soggetto [" + tipoSoggetto + "/" + nomeSoggetto+ "]");
		}
		return servizi;
	}

	public Integer getLunghezzaSelectList(List<SelectItem> listaSelectItem){
		return getLunghezzaSelectList(listaSelectItem, this.getDefaultFont());
	}

	public Integer getLunghezzaSelectList(List<SelectItem> listaSelectItem, Font font){
		int lunghezza = 0;
		Integer lunghezzaToRet = 0;
		try{
			if(listaSelectItem != null && listaSelectItem.size() > 0){
				for (SelectItem selectItem : listaSelectItem) {
					String label = selectItem.getLabel() != null ? selectItem.getLabel() : (String) selectItem.getValue();
					lunghezza = getFontWidth(label,font);
					lunghezzaToRet = Math.max(lunghezza, lunghezzaToRet);
				}
			}
		}catch(Throwable e){
			this.log.error("Si e' verificato un errore durante il calcolo della lunghezza delle select items");
		}
		return lunghezzaToRet;
	}

	/***
	 * 
	 * Controlla se il valore dato sta all'interno dei due estremi, nel caso in cui non lo sia viene riportato 
	 * ad uno dei due estremi.
	 * 
	 * @param minValue
	 * @param maxValue
	 * @param value
	 * @return  restituisce il valore compreso nei limiti impostati
	 */
	public static Integer checkLimits(Integer minValue, Integer maxValue , Integer value){
		// valore deve essere compreso minore del max ma almeno quanto la default
		Integer toRet = Math.max(minValue, value);
		toRet = Math.min(toRet, maxValue);
		return toRet;
	}


	// UTILITIES misurazione dimensione text
	public Integer getFontWidth(String text) throws Throwable{
		return getFontWidth(text, this.getDefaultFont());
	} 

	public Integer getFontWidth(String text, String fontName, int fontStyle, int fontSize) throws Throwable{
		Font fontToCheck = new Font(fontName,  fontStyle , fontSize);
		return getFontWidth(text, fontToCheck);
	} 


	public Integer getFontWidth(String text, Font fontToCheck) throws Throwable{
		if(this.fontRenderContext == null){
			if(this.affineTransform == null)
				this.affineTransform = new AffineTransform();

			this.fontRenderContext = new FontRenderContext(this.affineTransform,true,true);
		}

		Rectangle2D rectangle2d = fontToCheck.getStringBounds(text, this.fontRenderContext);
		return (int) rectangle2d.getWidth(); 
	}	
	//	public Integer getFontWidthWithFontMetrics(String text, String fontName, int fontStyle, int fontSize) throws Throwable{
	//		Font fontToCheck = new Font(fontName,  fontStyle , fontSize);
	//		return this.getFontWidthWithFontMetrics(text,fontToCheck);
	//	} 
	//	public Integer getFontWidthWithFontMetrics(String text, Font fontTocheck ) throws Throwable{
	//		if(this.fm == null){
	//			Canvas c = new Canvas();
	//			this.fm = c.getFontMetrics(fontTocheck);
	//		}
	//		return this.fm.stringWidth(text);
	//	}

	/***
	 * utilizzo Lucida sans come font di dafault poiche' e' generalmente presente nella jdk
	 * 
	 * @return Font di defualt dell'applicazione
	 */
	public Font getDefaultFont() {
		if(this.defaultFont == null)
			this.defaultFont = new Font("Lucida Sans", Font.PLAIN , 14);

		return this.defaultFont;
	}
	public void setDefaultFont(Font defaultFont) {
		this.defaultFont = defaultFont;
	}


	public AccordoServizioParteSpecifica getAspsFromValues(String tipoServizio, String nomeServizio, String tipoErogatore, String nomeErogatore, Integer versioneServizio){
		return this.dynamicUtilsService.getAspsFromValues(tipoServizio, nomeServizio, tipoErogatore, nomeErogatore,versioneServizio);
	}
	
	public List<Soggetto> getListaSoggetti(String tipoProtocollo,TipoPdD tipoPdD){
		return this.dynamicUtilsService.findElencoSoggettiFromTipoPdD(tipoProtocollo, tipoPdD);
	}

	public List<Soggetto> getSoggettiErogatoreAutoComplete(String tipoProtocollo,String uriAccordoServizio, String input, boolean soloOperativi){
		List<Soggetto> list = this.dynamicUtilsService.getSoggettiErogatoreAutoComplete(tipoProtocollo, uriAccordoServizio, input);

		if(soloOperativi && list != null && list.size() >0) {
			List<Soggetto> lstOperativi = new ArrayList<Soggetto>();

			for (Soggetto soggetto : list) {
				if(this.checkTipoPdd(soggetto.getServer(), TipoPdD.OPERATIVO))
					lstOperativi.add(soggetto);
			}

			return lstOperativi;
		}


		return new ArrayList<Soggetto>();
	}

	
	public List<SelectItem> getListaSelectItemsElencoServiziErogazione(String tipoProtocollo, String gruppo, IDAccordo idAccordo, 
			String tipoSoggetto, String nomeSoggetto, String input, boolean distinct){
		return _getListaSelectItemsElencoServiziErogazione(tipoProtocollo, gruppo, idAccordo, 
				tipoSoggetto, nomeSoggetto,
				null, null, null, null, null, null,
				input, false, null, distinct);
	}
	public List<SelectItem> getListaSelectItemsElencoServiziErogazione(String tipoProtocollo, String gruppo, IDAccordo idAccordo, 
			String tipoSoggetto , String nomeSoggetto, String input, boolean soloOperativi, boolean distinct){
		return _getListaSelectItemsElencoServiziErogazione(tipoProtocollo, gruppo, idAccordo, 
				tipoSoggetto, nomeSoggetto, 
				null, null, null, null, null, null,
				input, soloOperativi, null, distinct);
	}

	public List<SelectItem> getListaSelectItemsElencoConfigurazioneServiziErogazione(String tipoProtocollo, String gruppo, IDAccordo idAccordo, 
			String tipoSoggetto , String nomeSoggetto, String input, boolean soloOperativi,  PermessiUtenteOperatore permessiUtenteOperatore, boolean distinct){
		return _getListaSelectItemsElencoServiziErogazione(tipoProtocollo, gruppo, idAccordo, 
				tipoSoggetto, nomeSoggetto, 
				null, null, null, null, null, null,
				input, soloOperativi, permessiUtenteOperatore, distinct);
	}
	public List<SelectItem> getListaSelectItemsElencoConfigurazioneServiziErogazione(String tipoProtocollo, String gruppo, IDAccordo idAccordo, 
			String tipoSoggetto , String nomeSoggetto, 
			String tipoErogatore, String nomeErogatore, String tipoServizio ,String nomeServizio, Integer versioneServizio, String nomeAzione, 
			String input, boolean soloOperativi,  PermessiUtenteOperatore permessiUtenteOperatore, boolean distinct){
		return _getListaSelectItemsElencoServiziErogazione(tipoProtocollo, gruppo, idAccordo, 
				tipoSoggetto, nomeSoggetto, 
				tipoErogatore, nomeErogatore, tipoServizio ,nomeServizio, versioneServizio, nomeAzione, 
				input, soloOperativi, permessiUtenteOperatore, distinct);
	}

	private List<SelectItem> _getListaSelectItemsElencoServiziErogazione(String tipoProtocollo, String gruppo, IDAccordo idAccordo, 
			String tipoSoggetto , String nomeSoggetto, 
			String tipoErogatore, String nomeErogatore, String tipoServizio ,String nomeServizio, Integer versioneServizio, String nomeAzione, 
			String input, boolean soloOperativi, PermessiUtenteOperatore permessiUtenteOperatore, boolean distinct){
		List<SelectItem> servizi = new ArrayList<SelectItem>();

		try{

			UserDetailsBean user = Utility.getLoggedUser();

			PermessiUtenteOperatore permessiUtenteOperatoreRecheck = null;
			if(permessiUtenteOperatore!=null) {
				permessiUtenteOperatoreRecheck = permessiUtenteOperatore;
			}
			else {
				if(user!=null && !user.isAdmin()) {
					permessiUtenteOperatoreRecheck = new PermessiUtenteOperatore();
					if(user.getUtenteSoggettoList()!=null && !user.getUtenteSoggettoList().isEmpty()) {
						permessiUtenteOperatoreRecheck.getListIDSoggetti().addAll(user.getUtenteSoggettoList());
					}
					if(user.getUtenteServizioList()!=null && !user.getUtenteServizioList().isEmpty()) {
						permessiUtenteOperatoreRecheck.getListIDServizi().addAll(user.getUtenteServizioList());
					}
				}
			}
			
			List<IDServizio> servizi2 = null;
			if(permessiUtenteOperatore!=null) {
				// ci si arriva da elenco configurazioni
				if(nomeServizio!=null) {
					servizi2 = this.dynamicUtilsService.getConfigurazioneServiziErogazione(tipoProtocollo, tipoSoggetto, nomeSoggetto, 
							tipoServizio ,nomeServizio, tipoErogatore, nomeErogatore, versioneServizio, nomeAzione, 
							input, false, permessiUtenteOperatoreRecheck, distinct);
				}
				else {
					servizi2 = this.dynamicUtilsService.getConfigurazioneServiziErogazione(tipoProtocollo, tipoSoggetto, nomeSoggetto, 
							input, false, permessiUtenteOperatoreRecheck, distinct);
				}
			}
			else {
				if(nomeServizio!=null) {
					servizi2 = this.dynamicUtilsService.getServiziErogazione(tipoProtocollo, tipoSoggetto, nomeSoggetto, 
							tipoServizio ,nomeServizio, tipoErogatore, nomeErogatore, versioneServizio, nomeAzione, 
							input, false, permessiUtenteOperatoreRecheck, distinct);
				}
				else {
					servizi2 = this.dynamicUtilsService.getServiziErogazione(tipoProtocollo, tipoSoggetto, nomeSoggetto, input, false, permessiUtenteOperatoreRecheck, distinct);
				}
			}

			IDAccordoFactory idAccordoFactory = null;
			if(idAccordo!=null) {
				idAccordoFactory = IDAccordoFactory.getInstance();
			}
			
			List<String> lstLabelOrdinate = new ArrayList<>();
			Map<String, String> mapElementi = new HashMap<>();
			if(servizi2 != null && servizi2.size() > 0){
				for (IDServizio res : servizi2) {
					
					if( (gruppo!=null && !"".equals(gruppo)) || idAccordo!=null ) {
						
						AccordoServizioParteComune aspc = this.dynamicUtilsService.getAccordoServizio(tipoProtocollo, res.getSoggettoErogatore(), 
								res.getTipo(), res.getNome(), res.getVersione());
						
						if(idAccordo!=null) {
							IDAccordo idAccordoDB = idAccordoFactory.getIDAccordoFromValues(aspc.getNome(), aspc.getIdReferente().getTipo(), aspc.getIdReferente().getNome(), aspc.getVersione());
							if(!idAccordo.equals(idAccordoDB)) {
								continue;
							}
						}
						
						if((gruppo!=null && !"".equals(gruppo))) {
							IdAccordoServizioParteComune idAspc = new IdAccordoServizioParteComune();
							idAspc.setIdSoggetto(aspc.getIdReferente());
							idAspc.setNome(aspc.getNome());
							idAspc.setVersione(aspc.getVersione());
							List<IdAccordoServizioParteComuneGruppo> lGruppi = this.dynamicUtilsService.getAccordoServizioGruppi(idAspc);
							boolean found = false;
							if(lGruppi!=null && !lGruppi.isEmpty()) {
								for (IdAccordoServizioParteComuneGruppo gruppoCheck : lGruppi) {
									if(gruppoCheck.getIdGruppo().getNome().equals(gruppo)) {
										found = true;
										break;
									}
								}
							}
							if(!found) {
								continue;
							}
						}
					}
					
					boolean add= true;
					String value= null;
					// servizi.add(new
					// SelectItem(servizio.getAccordo().getNome()+"@"+servizio.getNome()));
					StringBuilder uri = new StringBuilder();

					String nomeAsps = res.getNome();

					String tipoAsps = res.getTipo();

					if(tipoAsps != null)
						uri.append(tipoAsps).append("/");

					uri.append(nomeAsps).append(":").append(res.getVersione());
					

//					if(showErogatore ){
					uri.append(" (").append(res.getSoggettoErogatore().getTipo()).append("/").append(res.getSoggettoErogatore().getNome()).append(")"); 
//					}
					
					IDServizio idServizio = IDServizioFactory.getInstance().getIDServizioFromValues(tipoAsps, nomeAsps, res.getSoggettoErogatore().getTipo(), res.getSoggettoErogatore().getNome(), res.getVersione());
					String label = StringUtils.isEmpty(nomeSoggetto) ? NamingUtils.getLabelAccordoServizioParteSpecifica(tipoProtocollo,idServizio) 
							: NamingUtils.getLabelAccordoServizioParteSpecificaSenzaErogatore(tipoProtocollo, idServizio.getTipo(), idServizio.getNome(), idServizio.getVersione());
					
					value = uri.toString();
					//compongo la label e la imposto
					if(soloOperativi){ // controllo se il soggetto e' associato ad una pdd operativa
						String nomePddFromSoggetto = this.getServerFromSoggetto(res.getSoggettoErogatore().getTipo(), res.getSoggettoErogatore().getNome());
						add = this.checkTipoPdd(nomePddFromSoggetto, TipoPdD.OPERATIVO);
					}

					// Controllo spostato nei metodi che ottengono la lista
//					if(add && !user.isAdmin()){
//
//						// controllo sul soggetto
//						boolean existsPermessoSoggetto = false;
//						if(user.getSizeSoggetti()>0){
//							for (IDSoggetto utenteSoggetto : user.getUtenteSoggettoList()) {
//								if(res.getSoggettoErogatore().getTipo().equals(utenteSoggetto.getTipo()) &&
//										res.getSoggettoErogatore().getNome().equals(utenteSoggetto.getNome())){
//									existsPermessoSoggetto = true;
//									break;
//								}
//							}
//						}
//
//						boolean existsPermessoServizio = false;
//						if(!existsPermessoSoggetto){
//							if(user.getSizeServizio()>0){
//								for (IDServizio utenteSoggetto : user.getUtenteServizioList()) {
//									if(res.getSoggettoErogatore().getTipo().equals(utenteSoggetto.getSoggettoErogatore().getTipo()) &&
//											res.getSoggettoErogatore().getNome().equals(utenteSoggetto.getSoggettoErogatore().getNome()) &&
//											res.getTipo().equals(utenteSoggetto.getTipo()) &&
//											res.getNome().equals(utenteSoggetto.getNome())){
//										existsPermessoServizio = true;
//										break;
//									}
//								}
//							}
//						}
//
//						add = (existsPermessoSoggetto || existsPermessoServizio);
//					}

					if(add) {
						lstLabelOrdinate.add(label);
						mapElementi.put(label, value);
					}
				}
			}

			if(lstLabelOrdinate.size() > 0) {
				Collections.sort(lstLabelOrdinate);
				
				for (String string : lstLabelOrdinate) {
					servizi.add(new SelectItem(mapElementi.get(string), string));  
				}
			}
		}catch(Exception e){
			this.log.error("Si e' verificato un errore durante la ricerca dei servizi erogati dal Soggetto [" + tipoSoggetto + "/" + nomeSoggetto+ "]");
		}
		return servizi;
	}
	
	public List<SelectItem> getListaSelectItemsElencoServiziFruizione(String tipoProtocollo, String gruppo, IDAccordo idAccordo,
			String tipoSoggettoErogatore, String nomeSoggettoErogatore, String input, boolean distinct){
		return _getListaSelectItemsElencoServiziFruizione(tipoProtocollo, gruppo, idAccordo, null, null, tipoSoggettoErogatore, nomeSoggettoErogatore, 
				null ,null, null, null, 
				input, false, null, distinct);
	}

	public List<SelectItem> getListaSelectItemsElencoServiziFruizione(String tipoProtocollo, String gruppo, IDAccordo idAccordo, 
			String tipoSoggettoErogatore , String nomeSoggettoErogatore, String input, boolean soloOperativi, boolean distinct){
		return _getListaSelectItemsElencoServiziFruizione(tipoProtocollo, gruppo, idAccordo, null, null, tipoSoggettoErogatore, nomeSoggettoErogatore, 
				null ,null, null, null, 
				input, soloOperativi, null, distinct);
	}
	
	public List<SelectItem> getListaSelectItemsElencoConfigurazioneServiziFruizione(String tipoProtocollo, String gruppo, IDAccordo idAccordo, 
			String tipoSoggetto, String nomeSoggetto, String tipoSoggettoErogatore , String nomeSoggettoErogatore, 
			String input, boolean soloOperativi, PermessiUtenteOperatore permessiUtenteOperatore, boolean distinct){
		return _getListaSelectItemsElencoServiziFruizione(tipoProtocollo, gruppo, idAccordo, tipoSoggetto, nomeSoggetto, tipoSoggettoErogatore, nomeSoggettoErogatore, 
				null ,null, null, null, 
				input, soloOperativi, permessiUtenteOperatore, distinct);
	}
	
	public List<SelectItem> getListaSelectItemsElencoConfigurazioneServiziFruizione(String tipoProtocollo, String gruppo, IDAccordo idAccordo, 
			String tipoSoggetto, String nomeSoggetto, String tipoSoggettoErogatore , String nomeSoggettoErogatore, 
			String tipoServizio ,String nomeServizio, Integer versioneServizio, String nomeAzione, 
			String input, boolean soloOperativi, PermessiUtenteOperatore permessiUtenteOperatore, boolean distinct){
		return _getListaSelectItemsElencoServiziFruizione(tipoProtocollo, gruppo, idAccordo, tipoSoggetto, nomeSoggetto, tipoSoggettoErogatore, nomeSoggettoErogatore, 
				tipoServizio ,nomeServizio, versioneServizio, nomeAzione, 
				input, soloOperativi, permessiUtenteOperatore, distinct);
	}
	
	private List<SelectItem> _getListaSelectItemsElencoServiziFruizione(String tipoProtocollo, String gruppo, IDAccordo idAccordo, 
			String tipoSoggetto, String nomeSoggetto, String tipoSoggettoErogatore , String nomeSoggettoErogatore, 
			String tipoServizio ,String nomeServizio, Integer versioneServizio, String nomeAzione, 
			String input, boolean soloOperativi, PermessiUtenteOperatore permessiUtenteOperatore, boolean distinct){
		List<SelectItem> servizi = new ArrayList<SelectItem>();

		try{

			UserDetailsBean user = Utility.getLoggedUser();

			PermessiUtenteOperatore permessiUtenteOperatoreRecheck = null;
			if(permessiUtenteOperatore!=null) {
				permessiUtenteOperatoreRecheck = permessiUtenteOperatore;
			}
			else {
				if(user!=null && !user.isAdmin()) {
					permessiUtenteOperatoreRecheck = new PermessiUtenteOperatore();
					if(user.getUtenteSoggettoList()!=null && !user.getUtenteSoggettoList().isEmpty()) {
						permessiUtenteOperatoreRecheck.getListIDSoggetti().addAll(user.getUtenteSoggettoList());
					}
					if(user.getUtenteServizioList()!=null && !user.getUtenteServizioList().isEmpty()) {
						permessiUtenteOperatoreRecheck.getListIDServizi().addAll(user.getUtenteServizioList());
					}
				}
			}
			
			List<IDServizio> servizi2 = null;
			if(tipoSoggetto!=null && nomeSoggetto!=null) {
				// ci si arriva da elenco configurazioni
				if(nomeServizio!=null) {
					servizi2 = this.dynamicUtilsService.getConfigurazioneServiziFruizione(tipoProtocollo,tipoSoggetto,nomeSoggetto,
							tipoServizio,nomeServizio, tipoSoggettoErogatore, nomeSoggettoErogatore, versioneServizio,nomeAzione, 
							input, false, permessiUtenteOperatoreRecheck, distinct);
				}
				else {
					servizi2 = this.dynamicUtilsService.getConfigurazioneServiziFruizione(tipoProtocollo,tipoSoggetto,nomeSoggetto,
							null,null, tipoSoggettoErogatore, nomeSoggettoErogatore, null,null,
							input, false, permessiUtenteOperatoreRecheck, distinct);
				}
			}else {
								
				if(nomeServizio!=null) {
					servizi2 = this.dynamicUtilsService.getServiziFruizione(tipoProtocollo, 
							tipoSoggetto, nomeSoggetto, 
							tipoSoggettoErogatore , nomeSoggettoErogatore, 
							tipoServizio , nomeServizio, versioneServizio, nomeAzione, 
							input, false, permessiUtenteOperatoreRecheck, distinct);
				}
				else {
					servizi2 = this.dynamicUtilsService.getServiziFruizione(tipoProtocollo, tipoSoggettoErogatore, nomeSoggettoErogatore, input, false, permessiUtenteOperatoreRecheck, distinct);
				}
			}

			IDAccordoFactory idAccordoFactory = null;
			if(idAccordo!=null) {
				idAccordoFactory = IDAccordoFactory.getInstance();
			}
			
			List<String> lstLabelOrdinate = new ArrayList<>();
			Map<String, String> mapElementi = new HashMap<>();
			if(servizi2 != null && servizi2.size() > 0){
				for (IDServizio res : servizi2) {
					
					
					if( (gruppo!=null && !"".equals(gruppo)) || idAccordo!=null ) {
						
						AccordoServizioParteComune aspc = this.dynamicUtilsService.getAccordoServizio(tipoProtocollo, res.getSoggettoErogatore(), 
								res.getTipo(), res.getNome(), res.getVersione());
						
						if(idAccordo!=null) {
							IDAccordo idAccordoDB = idAccordoFactory.getIDAccordoFromValues(aspc.getNome(), aspc.getIdReferente().getTipo(), aspc.getIdReferente().getNome(), aspc.getVersione());
							if(!idAccordo.equals(idAccordoDB)) {
								continue;
							}
						}
						
						if((gruppo!=null && !"".equals(gruppo))) {
							IdAccordoServizioParteComune idAspc = new IdAccordoServizioParteComune();
							idAspc.setIdSoggetto(aspc.getIdReferente());
							idAspc.setNome(aspc.getNome());
							idAspc.setVersione(aspc.getVersione());
							List<IdAccordoServizioParteComuneGruppo> lGruppi = this.dynamicUtilsService.getAccordoServizioGruppi(idAspc);
							boolean found = false;
							if(lGruppi!=null && !lGruppi.isEmpty()) {
								for (IdAccordoServizioParteComuneGruppo gruppoCheck : lGruppi) {
									if(gruppoCheck.getIdGruppo().getNome().equals(gruppo)) {
										found = true;
										break;
									}
								}
							}
							if(!found) {
								continue;
							}
						}
					}
					
					boolean add= true;
					String value= null;
					// servizi.add(new
					// SelectItem(servizio.getAccordo().getNome()+"@"+servizio.getNome()));
					StringBuilder uri = new StringBuilder();

					String nomeAsps = res.getNome();

					String tipoAsps = res.getTipo();

					if(tipoAsps != null)
						uri.append(tipoAsps).append("/");

					uri.append(nomeAsps).append(":").append(res.getVersione());
					

//					if(showErogatore ){
					uri.append(" (").append(res.getSoggettoErogatore().getTipo()).append("/").append(res.getSoggettoErogatore().getNome()).append(")"); 
//					}
					
					IDServizio idServizio = IDServizioFactory.getInstance().getIDServizioFromValues(tipoAsps, nomeAsps, res.getSoggettoErogatore().getTipo(), res.getSoggettoErogatore().getNome(), res.getVersione());
					String label = StringUtils.isEmpty(nomeSoggettoErogatore) ? NamingUtils.getLabelAccordoServizioParteSpecifica(tipoProtocollo,idServizio) 
							: NamingUtils.getLabelAccordoServizioParteSpecificaSenzaErogatore(tipoProtocollo, idServizio.getTipo(), idServizio.getNome(), idServizio.getVersione());
					
					value = uri.toString();
					//compongo la label e la imposto
					if(soloOperativi){ // controllo se il soggetto e' associato ad una pdd operativa
						String nomePddFromSoggetto = this.getServerFromSoggetto(res.getSoggettoErogatore().getTipo(), res.getSoggettoErogatore().getNome());
						add = this.checkTipoPdd(nomePddFromSoggetto, TipoPdD.OPERATIVO);
					}

					// Controllo spostato nei metodi che ottengono la lista
//					if(add && !user.isAdmin()){
//
//						// controllo sul soggetto
//						boolean existsPermessoSoggetto = false;
//						if(user.getSizeSoggetti()>0){
//							for (IDSoggetto utenteSoggetto : user.getUtenteSoggettoList()) {
//								if(res.getSoggettoErogatore().getTipo().equals(utenteSoggetto.getTipo()) &&
//										res.getSoggettoErogatore().getNome().equals(utenteSoggetto.getNome())){
//									existsPermessoSoggetto = true;
//									break;
//								}
//							}
//						}
//
//						boolean existsPermessoServizio = false;
//						if(!existsPermessoSoggetto){
//							if(user.getSizeServizio()>0){
//								for (IDServizio utenteSoggetto : user.getUtenteServizioList()) {
//									if(res.getSoggettoErogatore().getTipo().equals(utenteSoggetto.getSoggettoErogatore().getTipo()) &&
//											res.getSoggettoErogatore().getNome().equals(utenteSoggetto.getSoggettoErogatore().getNome()) &&
//											res.getTipo().equals(utenteSoggetto.getTipo()) &&
//											res.getNome().equals(utenteSoggetto.getNome())){
//										existsPermessoServizio = true;
//										break;
//									}
//								}
//							}
//						}
//
//						add = (existsPermessoSoggetto || existsPermessoServizio);
//					}

					if(add) {
						lstLabelOrdinate.add(label);
						mapElementi.put(label, value);
					}
				}
			}

			if(lstLabelOrdinate.size() > 0) {
				Collections.sort(lstLabelOrdinate);
				
				for (String string : lstLabelOrdinate) {
					servizi.add(new SelectItem(mapElementi.get(string), string));  
				}
			}

		}catch(Exception e){
			this.log.error("Si e' verificato un errore durante la ricerca dei servizi erogati dal Soggetto [" + tipoSoggettoErogatore + "/" + nomeSoggettoErogatore+ "]");
		}
		return servizi;
	}
	
	public PortaDelegata getPortaDelegata(String nomePorta) {
		return this.dynamicUtilsService.getPortaDelegata(nomePorta);
	}
	
	public PortaApplicativa getPortaApplicativa(String nomePorta) {
		return this.dynamicUtilsService.getPortaApplicativa(nomePorta);
	}
	
	public MappingFruizionePortaDelegata getMappingFruizione(IDServizio idServizio, IDSoggetto idSoggetto, IDPortaDelegata idPortaDelegata) {
		return this.dynamicUtilsService.getMappingFruizione(idServizio, idSoggetto, idPortaDelegata);
	}
	public MappingErogazionePortaApplicativa getMappingErogazione(IDServizio idServizio, IDPortaApplicativa idPortaApplicativa) {
		return this.dynamicUtilsService.getMappingErogazione(idServizio, idPortaApplicativa);
	}
	
	public Plugin getPlugin(IdPlugin idPlugin) {
		return this.dynamicUtilsService.getPlugin(idPlugin);
	}
}
