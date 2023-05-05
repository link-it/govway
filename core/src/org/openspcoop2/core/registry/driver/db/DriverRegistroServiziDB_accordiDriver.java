/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it). 
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



package org.openspcoop2.core.registry.driver.db;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.core.commons.DBUtils;
import org.openspcoop2.core.commons.Filtri;
import org.openspcoop2.core.commons.ISearch;
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.core.commons.SearchUtils;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.constants.ProprietariProtocolProperty;
import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.AccordoCooperazione;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.Documento;
import org.openspcoop2.core.registry.IdSoggetto;
import org.openspcoop2.core.registry.Operation;
import org.openspcoop2.core.registry.PortType;
import org.openspcoop2.core.registry.ProtocolProperty;
import org.openspcoop2.core.registry.Soggetto;
import org.openspcoop2.core.registry.constants.CostantiRegistroServizi;
import org.openspcoop2.core.registry.constants.ProfiloCollaborazione;
import org.openspcoop2.core.registry.constants.ProprietariDocumento;
import org.openspcoop2.core.registry.constants.RuoliDocumento;
import org.openspcoop2.core.registry.constants.ServiceBinding;
import org.openspcoop2.core.registry.constants.StatiAccordo;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziException;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziNotFound;
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
import org.openspcoop2.core.registry.driver.ValidazioneStatoPackageException;
import org.openspcoop2.utils.jdbc.JDBCUtilities;
import org.openspcoop2.utils.sql.ISQLQueryObject;
import org.openspcoop2.utils.sql.SQLObjectFactory;

/**
 * DriverRegistroServiziDB_accordiDriver
 * 
 * 
 * @author Sandra Giangrandi (sandra@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DriverRegistroServiziDB_accordiDriver {

	private DriverRegistroServiziDB driver = null;
	private DriverRegistroServiziDB_accordiSoapDriver driverSoap = null;
	private DriverRegistroServiziDB_accordiRestDriver driverRest = null;
	private DriverRegistroServiziDB_accordiGruppiDriver driverGruppi = null;
	private DriverRegistroServiziDB_accordiServiziCompostiDriver driverAccordiServiziComposti = null;
	
	protected DriverRegistroServiziDB_accordiDriver(DriverRegistroServiziDB driver) {
		this.driver = driver;
		this.driverSoap = new DriverRegistroServiziDB_accordiSoapDriver(driver);
		this.driverRest = new DriverRegistroServiziDB_accordiRestDriver(driver);
		this.driverGruppi = new DriverRegistroServiziDB_accordiGruppiDriver(driver);
		this.driverAccordiServiziComposti = new DriverRegistroServiziDB_accordiServiziCompostiDriver(driver);
	}
	

	protected int getAccordoServizioParteComuneNextVersion(IDAccordo idAccordo) throws DriverRegistroServiziException,DriverRegistroServiziNotFound {
		// conrollo consistenza
		if (idAccordo == null)
			throw new DriverRegistroServiziException("[getAccordoServizioParteComuneNextVersion] Parametro idAccordo is null");
		if (idAccordo.getNome() == null)
			throw new DriverRegistroServiziException("[getAccordoServizioParteComuneNextVersion] Parametro idAccordo.getNome is null");
		if (idAccordo.getNome().trim().equals(""))
			throw new DriverRegistroServiziException("[getAccordoServizioParteComuneNextVersion] Parametro idAccordo.getNome non e' definito");

		this.driver.logDebug("richiesto getAccordoServizioParteComuneNextVersion: " + idAccordo.toString());

		int nextVersion = -1;
		Connection con = null;
		try {
			this.driver.logDebug("operazione atomica = " + this.driver.atomica);
			// prendo la connessione dal pool
			if (this.driver.atomica)
				con = this.driver.getConnectionFromDatasource("getAccordoServizioParteComuneNextVersion(idAccordo)");
			else
				con = this.driver.globalConnection;
			
			nextVersion = DBUtils.getAccordoServizioParteComuneNextVersion(idAccordo, con, this.driver.tipoDB);
		}catch (Exception se) {
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::getAccordoServizioParteComuneNextVersion] Exception :" + se.getMessage(),se);
		} finally {
			this.driver.closeConnection(con);
		}
		return nextVersion;
	}
	
	protected org.openspcoop2.core.registry.AccordoServizioParteComune getAccordoServizioParteComune(IDAccordo idAccordo) throws DriverRegistroServiziException,DriverRegistroServiziNotFound {
		return getAccordoServizioParteComune(idAccordo,false,false);
	}
	protected org.openspcoop2.core.registry.AccordoServizioParteComune getAccordoServizioParteComune(IDAccordo idAccordo,boolean readContenutoAllegati,boolean readDatiRegistro) throws DriverRegistroServiziException,DriverRegistroServiziNotFound {
		// conrollo consistenza
		if (idAccordo == null)
			throw new DriverRegistroServiziException("[getAccordoServizioParteComune] Parametro idAccordo is null");
		if (idAccordo.getNome() == null)
			throw new DriverRegistroServiziException("[getAccordoServizioParteComune] Parametro idAccordo.getNome is null");
		if (idAccordo.getNome().trim().equals(""))
			throw new DriverRegistroServiziException("[getAccordoServizioParteComune] Parametro idAccordo.getNome non e' definito");

		this.driver.logDebug("richiesto getAccordoServizioParteComune: " + idAccordo.toString());

		org.openspcoop2.core.registry.AccordoServizioParteComune accordoServizio = null;
		Connection con = null;
		PreparedStatement stm = null;
		ResultSet rs = null;

		try {

			this.driver.logDebug("operazione atomica = " + this.driver.atomica);
			// prendo la connessione dal pool
			if (this.driver.atomica)
				con = this.driver.getConnectionFromDatasource("getAccordoServizioParteComune(idAccordo)");
			else
				con = this.driver.globalConnection;

			long idAccordoLong = DBUtils.getIdAccordoServizioParteComune(idAccordo, con, this.driver.tipoDB);
			if(idAccordoLong<=0){
				throw new DriverRegistroServiziNotFound("[DriverRegistroServiziDB::getAccordoServizioParteComune] Accordo non trovato (id:"+idAccordo+")");
			}


			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.ACCORDI);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition("id = ?");
			sqlQueryObject.setANDLogicOperator(true);
			String sqlQuery = sqlQueryObject.createSQLQuery();



			stm = con.prepareStatement(sqlQuery);

			stm.setLong(1, idAccordoLong);


			this.driver.logDebug("eseguo query : " + DriverRegistroServiziDB_LIB.formatSQLString(sqlQuery, idAccordoLong));
			rs = stm.executeQuery();

			if (rs.next()) {
				accordoServizio = new org.openspcoop2.core.registry.AccordoServizioParteComune();

				accordoServizio.setId(rs.getLong("id"));

				String tmp = rs.getString("nome");
				// se tmp==null oppure tmp=="" then setNome(null) else
				// setNome(tmp)
				accordoServizio.setNome(((tmp == null || tmp.equals("")) ? null : tmp));

				tmp = rs.getString("descrizione");
				accordoServizio.setDescrizione(((tmp == null || tmp.equals("")) ? null : tmp));

				tmp = rs.getString("service_binding");
				accordoServizio.setServiceBinding(DriverRegistroServiziDB_LIB.getEnumServiceBinding((tmp == null || tmp.equals("")) ? null : tmp));
				
				tmp = rs.getString("message_type");
				accordoServizio.setMessageType(DriverRegistroServiziDB_LIB.getEnumMessageType((tmp == null || tmp.equals("")) ? null : tmp));
				
				// controllare i vari casi di profcoll (one-way....)
				tmp = rs.getString("profilo_collaborazione");
				accordoServizio.setProfiloCollaborazione(DriverRegistroServiziDB_LIB.getEnumProfiloCollaborazione((tmp == null || tmp.equals("")) ? null : tmp));
				if(accordoServizio.getProfiloCollaborazione()==null){
					// puo' essere null se e' stato ridefinito nei port type e nelle operation
					// inserisco comunque un default (usato anche nelle interfacce)
					accordoServizio.setProfiloCollaborazione(ProfiloCollaborazione.ONEWAY);
				}

				//		if (tmp == null || tmp.equals(""))
				//		    accordoServizio.setProfiloCollaborazione(null);
				//		if (tmp.equals("oneway"))
				//		    accordoServizio
				//			    .setProfiloCollaborazione(CostantiRegistroServizi.ONEWAY);
				//		if (tmp.equals("sincrono"))
				//		    accordoServizio
				//			    .setProfiloCollaborazione(CostantiRegistroServizi.SINCRONO);
				//		if (tmp.equals("asincrono-simmetrico"))
				//		    accordoServizio
				//			    .setProfiloCollaborazione(CostantiRegistroServizi.ASINCRONO_SIMMETRICO);
				//		if (tmp.equals("asincrono-asimmetrico"))
				//		    accordoServizio
				//			    .setProfiloCollaborazione(CostantiRegistroServizi.ASINCRONO_ASIMMETRICO);

				tmp = rs.getString("formato_specifica");
				accordoServizio.setFormatoSpecifica(DriverRegistroServiziDB_LIB.getEnumFormatoSpecifica((tmp == null || tmp.equals("")) ? null : tmp));
				
				tmp = rs.getString("wsdl_definitorio");
				accordoServizio.setByteWsdlDefinitorio(((tmp == null || tmp.trim().equals("")) ? null : tmp.getBytes()));

				tmp = rs.getString("wsdl_concettuale");
				accordoServizio.setByteWsdlConcettuale(((tmp == null || tmp.trim().equals("")) ? null : tmp.getBytes()));

				tmp = rs.getString("wsdl_logico_erogatore");
				accordoServizio.setByteWsdlLogicoErogatore(((tmp == null || tmp.trim().equals("")) ? null : tmp.getBytes()));

				tmp = rs.getString("wsdl_logico_fruitore");
				accordoServizio.setByteWsdlLogicoFruitore(((tmp == null || tmp.trim().equals("")) ? null : tmp.getBytes()));

				tmp = rs.getString("spec_conv_concettuale");
				accordoServizio.setByteSpecificaConversazioneConcettuale(((tmp == null || tmp.trim().equals("")) ? null : tmp.getBytes()));

				tmp = rs.getString("spec_conv_erogatore");
				accordoServizio.setByteSpecificaConversazioneErogatore(((tmp == null || tmp.trim().equals("")) ? null : tmp.getBytes()));

				tmp = rs.getString("spec_conv_fruitore");
				accordoServizio.setByteSpecificaConversazioneFruitore(((tmp == null || tmp.trim().equals("")) ? null : tmp.getBytes()));

				accordoServizio.setUtilizzoSenzaAzione(rs.getInt("utilizzo_senza_azione") == CostantiDB.TRUE ? true : false);

				tmp = rs.getString("filtro_duplicati");
				accordoServizio.setFiltroDuplicati(DriverRegistroServiziDB_LIB.getEnumStatoFunzionalita(((tmp == null || tmp.equals("")) ? null : tmp)));

				tmp = rs.getString("conferma_ricezione");
				accordoServizio.setConfermaRicezione(DriverRegistroServiziDB_LIB.getEnumStatoFunzionalita(((tmp == null || tmp.equals("")) ? null : tmp)));

				tmp = rs.getString("identificativo_collaborazione");
				accordoServizio.setIdCollaborazione(DriverRegistroServiziDB_LIB.getEnumStatoFunzionalita(((tmp == null || tmp.equals("")) ? null : tmp)));
				
				tmp = rs.getString("id_riferimento_richiesta");
				accordoServizio.setIdRiferimentoRichiesta(DriverRegistroServiziDB_LIB.getEnumStatoFunzionalita(((tmp == null || tmp.equals("")) ? null : tmp)));
				
				tmp = rs.getString("consegna_in_ordine");
				accordoServizio.setConsegnaInOrdine(DriverRegistroServiziDB_LIB.getEnumStatoFunzionalita(((tmp == null || tmp.equals("")) ? null : tmp)));
				
				tmp = rs.getString("scadenza");
				accordoServizio.setScadenza(((tmp == null || tmp.equals("")) ? null : tmp));

				tmp = rs.getString("superuser");
				accordoServizio.setSuperUser(((tmp == null || tmp.equals("")) ? null : tmp));

				if(rs.getInt("privato")==1)
					accordoServizio.setPrivato(true);
				else
					accordoServizio.setPrivato(false);

				// Ora Registrazione
				if(rs.getTimestamp("ora_registrazione")!=null){
					accordoServizio.setOraRegistrazione(new Date(rs.getTimestamp("ora_registrazione").getTime()));
				}

				// Soggetto referente
				long id_referente = rs.getLong("id_referente");
				if(id_referente>0) {
					IDSoggetto soggettoReferente = null;
					try {
						soggettoReferente = this.driver.getIdSoggetto(id_referente,con);
						if(soggettoReferente==null){
							throw new DriverRegistroServiziNotFound ("non esiste");
						}
					}catch(DriverRegistroServiziNotFound notFound) {
						throw new Exception ("Soggetto referente ["+id_referente+"] dell'accordo non esiste");
					}
					IdSoggetto assr = new IdSoggetto();
					assr.setTipo(soggettoReferente.getTipo());
					assr.setNome(soggettoReferente.getNome());
					assr.setId(id_referente);
					accordoServizio.setSoggettoReferente(assr);
				}

				//Versione
				if(rs.getString("versione")!=null && !"".equals(rs.getString("versione")))
					accordoServizio.setVersione(rs.getInt("versione"));

				// Stato
				tmp = rs.getString("stato");
				accordoServizio.setStatoPackage(((tmp == null || tmp.equals("")) ? null : tmp));

				// Canali
				String canale = rs.getString("canale");
				accordoServizio.setCanale(canale);
				
				rs.close();
				stm.close();


				// Aggiungo azione

				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.ACCORDI_AZIONI);
				sqlQueryObject.addSelectField("*");
				sqlQueryObject.addWhereCondition("id_accordo = ?");
				sqlQueryObject.setANDLogicOperator(true);
				sqlQueryObject.addOrderBy("nome");
				sqlQueryObject.setSortType(true);
				sqlQuery = sqlQueryObject.createSQLQuery();

				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idAccordoLong);

				this.driver.logDebug("eseguo query : " + DriverRegistroServiziDB_LIB.formatSQLString(sqlQuery, idAccordoLong));

				rs = stm.executeQuery();

				org.openspcoop2.core.registry.Azione azione = null;
				while (rs.next()) {

					azione = new org.openspcoop2.core.registry.Azione();

					tmp = rs.getString("conferma_ricezione");
					azione.setConfermaRicezione(DriverRegistroServiziDB_LIB.getEnumStatoFunzionalita(((tmp == null || tmp.equals("")) ? null : tmp)));

					tmp = rs.getString("consegna_in_ordine");
					azione.setConsegnaInOrdine(DriverRegistroServiziDB_LIB.getEnumStatoFunzionalita(((tmp == null || tmp.equals("")) ? null : tmp)));

					tmp = rs.getString("filtro_duplicati");
					azione.setFiltroDuplicati(DriverRegistroServiziDB_LIB.getEnumStatoFunzionalita(((tmp == null || tmp.equals("")) ? null : tmp)));

					tmp = rs.getString("identificativo_collaborazione");
					azione.setIdCollaborazione(DriverRegistroServiziDB_LIB.getEnumStatoFunzionalita(((tmp == null || tmp.equals("")) ? null : tmp)));

					tmp = rs.getString("id_riferimento_richiesta");
					azione.setIdRiferimentoRichiesta(DriverRegistroServiziDB_LIB.getEnumStatoFunzionalita(((tmp == null || tmp.equals("")) ? null : tmp)));
					
					tmp = rs.getString("nome");
					azione.setNome(((tmp == null || tmp.equals("")) ? null : tmp));

					tmp = rs.getString("scadenza");
					azione.setScadenza(((tmp == null || tmp.equals("")) ? null : tmp));

					tmp = rs.getString("profilo_collaborazione");
					azione.setProfiloCollaborazione(DriverRegistroServiziDB_LIB.getEnumProfiloCollaborazione(((tmp == null || tmp.equals("")) ? null : tmp)));

					tmp = rs.getString("correlata");
					azione.setCorrelata(((tmp == null || tmp.equals("")) ? null : tmp));

					//		    if (tmp == null || tmp.equals("")){
					//			azione.setProfiloCollaborazione(null);
					//		    }
					//		    else if (tmp.equals("oneway")){
					//			azione.setProfiloCollaborazione(CostantiRegistroServizi.ONEWAY);
					//		    }else if (tmp.equals("sincrono")){
					//			azione.setProfiloCollaborazione(CostantiRegistroServizi.SINCRONO);
					//		    }else if (tmp.equals("asincrono-simmetrico")){
					//			azione.setProfiloCollaborazione(CostantiRegistroServizi.ASINCRONO_SIMMETRICO);
					//		    }else if (tmp.equals("asincrono-asimmetrico")){
					//			azione.setProfiloCollaborazione(CostantiRegistroServizi.ASINCRONO_ASIMMETRICO);
					//		    }

					tmp = rs.getString("profilo_azione");
					if (tmp == null || tmp.equals(""))
						azione.setProfAzione(CostantiRegistroServizi.PROFILO_AZIONE_DEFAULT);
					else
						azione.setProfAzione(tmp);

					long idAzione = rs.getLong("id");
					azione.setId(idAzione);

					
					// Protocol Properties
					try{
						List<ProtocolProperty> listPP = DriverRegistroServiziDB_LIB.getListaProtocolProperty(idAzione, ProprietariProtocolProperty.AZIONE_ACCORDO, con, this.driver.tipoDB);
						if(listPP!=null && listPP.size()>0){
							for (ProtocolProperty protocolProperty : listPP) {
								azione.addProtocolProperty(protocolProperty);
							}
						}
					}catch(DriverRegistroServiziNotFound dNotFound){}
					
					accordoServizio.addAzione(azione);

				}
				rs.close();
				stm.close();


				// read port type
				this.driverSoap.readPortTypes(accordoServizio,con);
				
				// read resources
				this.driverRest.readResources(accordoServizio,con,readDatiRegistro);
				
				// read gruppi
				this.driverGruppi.readAccordiGruppi(accordoServizio,con);

				// read AccordoServizioComposto
				this.driverAccordiServiziComposti.readAccordoServizioComposto(accordoServizio , con);


				// read Documenti generici: i bytes non vengono ritornati se readContenutoAllegati==false, utilizzare il metodo apposta per averli: 
				//                                               DriverRegistroServiziDB_LIB.getDocumento(id, readBytes, connection);
				try{
					List<?> allegati = DriverRegistroServiziDB_documentiLIB.getListaDocumenti(RuoliDocumento.allegato.toString(), idAccordoLong, 
							ProprietariDocumento.accordoServizio,readContenutoAllegati, con, this.driver.tipoDB);
					for(int i=0; i<allegati.size();i++){
						accordoServizio.addAllegato((Documento) allegati.get(i));
					}
				}catch(DriverRegistroServiziNotFound dNotFound){}
				try{
					List<?> specificheSemiformali = DriverRegistroServiziDB_documentiLIB.getListaDocumenti(RuoliDocumento.specificaSemiformale.toString(), 
							idAccordoLong, ProprietariDocumento.accordoServizio,readContenutoAllegati, con, this.driver.tipoDB);
					for(int i=0; i<specificheSemiformali.size();i++){	
						accordoServizio.addSpecificaSemiformale((Documento) specificheSemiformali.get(i));
					}
				}catch(DriverRegistroServiziNotFound dNotFound){}
				if(accordoServizio.getServizioComposto()!=null){
					// read Documenti generici: i bytes non vengono ritornati se readContenutoAllegati==false, utilizzare il metodo apposta per averli: 
					//                                               DriverRegistroServiziDB_LIB.getDocumento(id, readBytes, connection);
					try{
						List<?> specificheCoordinamento = DriverRegistroServiziDB_documentiLIB.getListaDocumenti(RuoliDocumento.specificaCoordinamento.toString(), 
								idAccordoLong, ProprietariDocumento.accordoServizio,readContenutoAllegati, con, this.driver.tipoDB);
						for(int i=0; i<specificheCoordinamento.size();i++){	
							accordoServizio.getServizioComposto().addSpecificaCoordinamento((Documento) specificheCoordinamento.get(i));
						}
					}catch(DriverRegistroServiziNotFound dNotFound){}
				}

				
				// Protocol Properties
				try{
					List<ProtocolProperty> listPP = DriverRegistroServiziDB_LIB.getListaProtocolProperty(idAccordoLong, ProprietariProtocolProperty.ACCORDO_SERVIZIO_PARTE_COMUNE, con, this.driver.tipoDB);
					if(listPP!=null && listPP.size()>0){
						for (ProtocolProperty protocolProperty : listPP) {
							accordoServizio.addProtocolProperty(protocolProperty);
						}
					}
				}catch(DriverRegistroServiziNotFound dNotFound){}
				
				
			} else {
				throw new DriverRegistroServiziNotFound("[DriverRegistroServiziDB::getAccordoServizioParteComune] rs.next non ha restituito valori con la seguente interrogazione :\n" + DriverRegistroServiziDB_LIB.formatSQLString(sqlQuery, idAccordoLong));
			}


//			if(accordoServizio!=null){
//				// nomiAzione setting 
//				accordoServizio.setNomiAzione(accordoServizio.readNomiAzione());
//			}
			return accordoServizio;

		}catch (DriverRegistroServiziNotFound e) {
			throw e;
		}catch (Exception se) {
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::getAccordoServizioParteComune] Exception :" + se.getMessage(),se);
		} finally {

			JDBCUtilities.closeResources(rs, stm);

			this.driver.closeConnection(con);

		}
	}
	
	
	
	
	protected AccordoServizioParteComune getAccordoServizioParteComune(long id) throws DriverRegistroServiziException,DriverRegistroServiziNotFound {
		return getAccordoServizioParteComune(id,false,false,null);
	}
	protected AccordoServizioParteComune getAccordoServizioParteComune(long id, boolean readContenutoAllegati,boolean readDatiRegistro) throws DriverRegistroServiziException,DriverRegistroServiziNotFound {
		return getAccordoServizioParteComune(id,readContenutoAllegati,readDatiRegistro,null);
	}
	protected AccordoServizioParteComune getAccordoServizioParteComune(long id,Connection conParam) throws DriverRegistroServiziException,DriverRegistroServiziNotFound {
		return getAccordoServizioParteComune(id,false,false,conParam);
	}
	protected AccordoServizioParteComune getAccordoServizioParteComune(long id, boolean readContenutoAllegati,boolean readDatiRegistro,Connection conParam) throws DriverRegistroServiziException,DriverRegistroServiziNotFound {
		this.driver.logDebug("richiesto getAccordoServizio: " + id);
		// conrollo consistenza
		if (id <= 0)
			return null;

		Connection con = null;
		PreparedStatement stm = null;
		ResultSet rs = null;

		IDAccordo idAccordo = null;
		try {
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.ACCORDI);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition("id = ?");
			String sqlQuery = sqlQueryObject.createSQLQuery();

			this.driver.logDebug("operazione atomica = " + this.driver.atomica);
			// prendo la connessione dal pool
			if(conParam!=null){
				con = conParam;
			}
			else if (this.driver.atomica)
				con = this.driver.getConnectionFromDatasource("getAccordoServizioParteComune(longId)");
			else
				con = this.driver.globalConnection;

			stm = con.prepareStatement(sqlQuery);

			stm.setLong(1, id);

			this.driver.logDebug("eseguo query : " + DriverRegistroServiziDB_LIB.formatSQLString(sqlQuery, id));
			rs = stm.executeQuery();


			if (rs.next()) {

				IDSoggetto referente = null;
				long idReferente = rs.getLong("id_referente");
				if(idReferente>0){
					try {
						referente = this.driver.getIdSoggetto(idReferente,con);
						if(referente==null){
							throw new DriverRegistroServiziNotFound ("non esiste");
						}
					}catch(DriverRegistroServiziNotFound notFound) {
						throw new Exception ("Soggetto referente ["+idReferente+"] dell'accordo non esiste");
					}
				}

				idAccordo = this.driver.idAccordoFactory.getIDAccordoFromValues(rs.getString("nome"),referente,rs.getInt("versione"));

			} else {
				rs.close();
				stm.close();
				throw new DriverRegistroServiziNotFound("[DriverRegistroServiziDB::getAccordoServizio] rs.next non ha restituito valori con la seguente interrogazione :\n" + DriverRegistroServiziDB_LIB.formatSQLString(sqlQuery, id));
			}

			rs.close();
			stm.close();

		} catch (SQLException se) {
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::getAccordoServizio] SQLException :" + se.getMessage(),se);
		}catch (DriverRegistroServiziNotFound e) {
			throw e;
		}catch (Exception se) {
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::getAccordoServizio] Exception :" + se.getMessage(),se);
		} finally {

			//Chiudo statement and resultset
			JDBCUtilities.closeResources(rs, stm);

			this.driver.closeConnection(conParam, con);

		}

		return this.getAccordoServizioParteComune(idAccordo, readContenutoAllegati, readDatiRegistro);
	}
	
	
	
	
	
	protected List<AccordoServizioParteComune> accordiCompatibiliList(ISearch ricerca) throws DriverRegistroServiziException {
		String nomeMetodo = "accordiCompatibiliList";
		int idLista = Liste.ACCORDI;
		int offset;
		int limit;
		String search;
		String queryString;

		limit = ricerca.getPageSize(idLista);
		offset = ricerca.getIndexIniziale(idLista);
		search = (org.openspcoop2.core.constants.Costanti.SESSION_ATTRIBUTE_VALUE_RICERCA_UNDEFINED.equals(ricerca.getSearchString(idLista)) ? "" : ricerca.getSearchString(idLista));
		ricerca.getSearchString(idLista);

		this.driver.logDebug("search : " + search);

		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet risultato = null;

		ArrayList<AccordoServizioParteComune> lista = new ArrayList<AccordoServizioParteComune>();

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("accordiCompatibiliList");

			} catch (Exception e) {
				throw new DriverRegistroServiziException("[DriverRegistroServiziDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.logDebug("operazione this.driver.atomica = " + this.driver.atomica);

		try {

			ISQLQueryObject sqlquery = DriverRegistroServiziDB_accordiLIB.getSQLRicercaAccordiValidi();
			sqlquery.setANDLogicOperator(true);

			ISQLQueryObject sqlQueryObjectSoggetti = null;
			if (!search.equals("")) {
				sqlQueryObjectSoggetti = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObjectSoggetti.addFromTable(CostantiDB.SOGGETTI);
				sqlQueryObjectSoggetti.addSelectField(CostantiDB.SOGGETTI, "nome_soggetto");
				sqlQueryObjectSoggetti.addWhereCondition(true,
						sqlQueryObjectSoggetti.getWhereLikeCondition("nome_soggetto", search, true, true),
						CostantiDB.ACCORDI+".id_referente="+CostantiDB.SOGGETTI+".id");
			}


			if (!search.equals("")) {
				//query con search
				sqlquery.addSelectCountField("*", "cont");
				sqlquery.addWhereCondition(false, 
						sqlquery.getWhereLikeCondition("nome", search, true, true),
						sqlquery.getWhereLikeCondition("versione", search, true, true),
						sqlquery.getWhereExistsCondition(false, sqlQueryObjectSoggetti));
				queryString = sqlquery.createSQLQuery();
			} else {
				sqlquery.addSelectCountField("*", "cont");
				queryString = sqlquery.createSQLQuery();
			}

			stmt = con.prepareStatement(queryString);
			risultato = stmt.executeQuery();
			if (risultato.next())
				ricerca.setNumEntries(idLista,risultato.getInt(1));
			risultato.close();
			stmt.close();

			//resetto query
			sqlquery = DriverRegistroServiziDB_accordiLIB.getSQLRicercaAccordiValidi();
			sqlquery.setANDLogicOperator(true);
			// ricavo le entries
			if (limit == 0) // con limit
				limit = ISQLQueryObject.LIMIT_DEFAULT_VALUE;
			if (!search.equals("")) { // con search

				sqlquery.addSelectField("id");
				sqlquery.addSelectField("nome");
				sqlquery.addSelectField("descrizione");
				sqlquery.addSelectField("id_referente");
				sqlquery.addSelectField("versione");
				sqlquery.addSelectField("stato");
				sqlquery.addWhereCondition(false, 
						sqlquery.getWhereLikeCondition("nome", search, true, true),
						sqlquery.getWhereLikeCondition("versione", search, true, true),
						sqlquery.getWhereExistsCondition(false, sqlQueryObjectSoggetti));
				sqlquery.addOrderBy("nome");
				sqlquery.addOrderBy("versione");
				sqlquery.addOrderBy("id_referente");
				sqlquery.setSortType(true);
				sqlquery.setLimit(limit);
				sqlquery.setOffset(offset);
				queryString = sqlquery.createSQLQuery();
			} else {
				// senza search
				sqlquery.addSelectField("id");
				sqlquery.addSelectField("nome");
				sqlquery.addSelectField("descrizione");
				sqlquery.addSelectField("id_referente");
				sqlquery.addSelectField("versione");
				sqlquery.addSelectField("stato");
				sqlquery.addOrderBy("nome");
				sqlquery.addOrderBy("versione");
				sqlquery.addOrderBy("id_referente");
				sqlquery.setSortType(true);
				sqlquery.setLimit(limit);
				sqlquery.setOffset(offset);
				queryString = sqlquery.createSQLQuery();
			}


			this.driver.logDebug("eseguo query : " + DriverRegistroServiziDB_LIB.formatSQLString(queryString));

			stmt = con.prepareStatement(queryString);
			risultato = stmt.executeQuery();

			AccordoServizioParteComune accordo = null;

			while (risultato.next()) {

				accordo = new AccordoServizioParteComune();

				accordo.setId(risultato.getLong("id"));
				accordo.setNome(risultato.getString("nome"));
				accordo.setDescrizione(risultato.getString("descrizione"));
				accordo.setStatoPackage(risultato.getString("stato"));
				accordo.setVersione(risultato.getInt("versione"));

				// Soggetto referente
				long id_referente = risultato.getLong("id_referente");
				IDSoggetto soggettoReferente = null;
				if(id_referente>0){
					try {
						soggettoReferente = this.driver.getIdSoggetto(id_referente,con);
						if(soggettoReferente==null){
							throw new DriverRegistroServiziNotFound ("non esiste");
						}
					}catch(DriverRegistroServiziNotFound notFound) {
						throw new Exception ("Soggetto referente ["+id_referente+"] dell'accordo non esiste");
					}
					IdSoggetto assr = new IdSoggetto();
					assr.setTipo(soggettoReferente.getTipo());
					assr.setNome(soggettoReferente.getNome());
					accordo.setSoggettoReferente(assr);
				}
				
				lista.add(accordo);

				this.driverAccordiServiziComposti.readAccordoServizioComposto(accordo, con);

			}


		}catch (Exception se) {

			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::" + nomeMetodo + "] Exception: " + se.getMessage(),se);
		} finally {

			//Chiudo statement and resultset
			JDBCUtilities.closeResources(risultato, stmt);
			
			this.driver.closeConnection(con);
		}
		return lista;
	}
	
	
	protected void createAccordoServizioParteComune(org.openspcoop2.core.registry.AccordoServizioParteComune accordoServizio) throws DriverRegistroServiziException {

		Connection connection = null;
		boolean error = false;

		if (this.driver.atomica) {
			try {
				connection = this.driver.getConnectionFromDatasource("createAccordoServizioParteComune");
				connection.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverRegistroServiziException("DriverRegistroServiziDB::createAccordoServizioParteComune] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			connection = this.driver.globalConnection;

		this.driver.logDebug("operazione atomica = " + this.driver.atomica);

		try {
			DriverRegistroServiziDB_accordiLIB.createAccordoServizioParteComune(accordoServizio, connection, this.driver.tabellaSoggetti, this.driver.log, this.driver.idAccordoFactory);
		} catch (DriverRegistroServiziException e) {
			error = true;
			throw e;
		}catch (Exception e) {
			error = true;
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::createAccordoServizioParteComune] Exception [" + e.getMessage() + "].",e);
		}finally {
			this.driver.closeConnection(error, connection);
		}

	}
	
	protected void updateAccordoServizioParteComune(org.openspcoop2.core.registry.AccordoServizioParteComune accordoServizio) throws DriverRegistroServiziException {

		Connection connection = null;
		boolean error = false;

		if (this.driver.atomica) {
			try {
				connection = this.driver.getConnectionFromDatasource("updateAccordoServizioParteComune");
				connection.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverRegistroServiziException("DriverRegistroServiziDB::createAccordoServizio] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			connection = this.driver.globalConnection;

		this.driver.logDebug("operazione atomica = " + this.driver.atomica);

		try {
			DriverRegistroServiziDB_accordiLIB.updateAccordoServizioParteComune(accordoServizio, connection, this.driver.tabellaSoggetti, this.driver.log, this.driver.idAccordoFactory);
		}catch (DriverRegistroServiziException se) {
			this.driver.log.error(se.getMessage(),se);
			error = true;
			throw se;
		} 
		catch (Exception se) {
			this.driver.log.error(se.getMessage(),se);
			error = true;
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::updateAccordoServizio] Exception [" + se.getMessage() + "].",se);
		}finally {
			this.driver.closeConnection(error, connection);
		}

	}
	

	protected void deleteAccordoServizioParteComune(org.openspcoop2.core.registry.AccordoServizioParteComune accordoServizio) throws DriverRegistroServiziException {

		Connection connection = null;
		
		boolean error = false;

		if (this.driver.atomica) {
			try {
				connection = this.driver.getConnectionFromDatasource("deleteAccordoServizioParteComune");
				connection.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverRegistroServiziException("DriverRegistroServiziDB::deleteAccordoServizioParteComune] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			connection = this.driver.globalConnection;

		this.driver.logDebug("operazione atomica = " + this.driver.atomica);

		try {

			DriverRegistroServiziDB_accordiLIB.deleteAccordoServizioParteComune(accordoServizio, connection, this.driver.tabellaSoggetti, this.driver.log, this.driver.idAccordoFactory);

		} catch (DriverRegistroServiziException se) {

			error = true;
			throw se;
		} catch (Exception se) {

			error = true;
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::deleteAccordoServizio] Exception [" + se.getMessage() + "].",se);
		}finally {
			this.driver.closeConnection(error, connection);
		}
	}
	
	
	protected List<IDAccordoDB> idAccordiList(String superuser, ISearch ricerca, 
			boolean soloAccordiConsistentiRest, boolean soloAccordiConsistentiSoap) throws DriverRegistroServiziException {
		return idAccordiListEngine(superuser,ricerca,false,false,
				soloAccordiConsistentiRest, soloAccordiConsistentiSoap);
	}
	protected List<IDAccordoDB> idAccordiServizioParteComuneList(String superuser, ISearch ricerca, 
			boolean soloAccordiConsistentiRest, boolean soloAccordiConsistentiSoap) throws DriverRegistroServiziException {
		return idAccordiListEngine(superuser,ricerca,false,true,
				soloAccordiConsistentiRest, soloAccordiConsistentiSoap);
	}
	protected List<IDAccordoDB> idAccordiServizioCompostiList(String superuser, ISearch ricerca, 
			boolean soloAccordiConsistentiRest, boolean soloAccordiConsistentiSoap) throws DriverRegistroServiziException {
		return idAccordiListEngine(superuser,ricerca,true,false,
				soloAccordiConsistentiRest, soloAccordiConsistentiSoap);
	}
	private List<IDAccordoDB> idAccordiListEngine(String superuser, ISearch ricerca,boolean excludeASParteComune,boolean excludeASComposti, 
			boolean soloAccordiConsistentiRest, boolean soloAccordiConsistentiSoap) throws DriverRegistroServiziException {
		String nomeMetodo = "idAccordiList";
		int idLista = Liste.ACCORDI;
		int offset;
		int limit;
		String search;
		String queryString;

		limit = ricerca.getPageSize(idLista);
		offset = ricerca.getIndexIniziale(idLista);
		search = (org.openspcoop2.core.constants.Costanti.SESSION_ATTRIBUTE_VALUE_RICERCA_UNDEFINED.equals(ricerca.getSearchString(idLista)) ? "" : ricerca.getSearchString(idLista));
		
		String filterProtocollo = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_PROTOCOLLO);
		String filterProtocolli = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_PROTOCOLLI);
		List<String> tipoSoggettiProtocollo = null;
		try {
			tipoSoggettiProtocollo = Filtri.convertToTipiSoggetti(filterProtocollo, filterProtocolli);
		}catch(Exception e) {
			throw new DriverRegistroServiziException(e.getMessage(),e);
		}
		boolean searchByTipoSoggetto = (tipoSoggettiProtocollo!=null && tipoSoggettiProtocollo.size()>0);
		
		String filterTipoAPI = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_SERVICE_BINDING);
		
		String filterStatoAccordo = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_STATO_ACCORDO);
		
		String filterGruppo = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_GRUPPO);

		String filterCanale = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_CANALE);
		boolean searchCanale = false;
		boolean canaleDefault = false;
		if(filterCanale!=null && !filterCanale.equals("")) {
			searchCanale = true;
			if(filterCanale.startsWith(Filtri.PREFIX_VALUE_CANALE_DEFAULT)) {
				filterCanale = filterCanale.substring(Filtri.PREFIX_VALUE_CANALE_DEFAULT.length());
				canaleDefault = true;
			}
		}
		
		this.driver.logDebug("search : " + search);
		this.driver.logDebug("filterProtocollo : " + filterProtocollo);
		this.driver.logDebug("filterProtocolli : " + filterProtocolli);
		this.driver.logDebug("filterTipoAPI : " + filterTipoAPI);
		this.driver.logDebug("filterStatoAccordo : " + filterStatoAccordo);
		this.driver.logDebug("filterGruppo : " + filterGruppo);
		this.driver.logDebug("filterCanale : " + filterCanale);

		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet risultato = null;

		ArrayList<IDAccordoDB> lista = new ArrayList<IDAccordoDB>();

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("accordiListEngine");

			} catch (Exception e) {
				throw new DriverRegistroServiziException("[DriverRegistroServiziDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.logDebug("operazione this.driver.atomica = " + this.driver.atomica);

		try {

			if(excludeASComposti && excludeASParteComune){
				throw new Exception("Non e' possibile invocare il metodo accordiListEngine con entrambi i parametri excludeASParteComune,excludeASComposti impostati al valore true");
			}


			ISQLQueryObject sqlQueryObjectExclude = null;
			if(excludeASComposti || excludeASParteComune){
				sqlQueryObjectExclude = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObjectExclude.addFromTable(CostantiDB.ACCORDI_SERVIZI_COMPOSTO);
				sqlQueryObjectExclude.addSelectField(CostantiDB.ACCORDI_SERVIZI_COMPOSTO, "id_accordo");
				sqlQueryObjectExclude.addWhereCondition(CostantiDB.ACCORDI_SERVIZI_COMPOSTO+".id_accordo="+CostantiDB.ACCORDI+".id");
			}

			ISQLQueryObject sqlQueryObjectExistsResource = null;
			if(soloAccordiConsistentiRest) {
				sqlQueryObjectExistsResource = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObjectExistsResource.addFromTable(CostantiDB.API_RESOURCES);
				sqlQueryObjectExistsResource.addSelectField(CostantiDB.API_RESOURCES, "id_accordo");
				sqlQueryObjectExistsResource.addWhereCondition(CostantiDB.API_RESOURCES+".id_accordo="+CostantiDB.ACCORDI+".id");
				sqlQueryObjectExistsResource.setANDLogicOperator(true);
			}
			
			ISQLQueryObject sqlQueryObjectExistsPortTypeConAzioni = null;
			if(soloAccordiConsistentiSoap){
				ISQLQueryObject sqlQueryObjectExistsPortTypeCheckAzioni = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObjectExistsPortTypeCheckAzioni.addFromTable(CostantiDB.PORT_TYPE_AZIONI);
				sqlQueryObjectExistsPortTypeCheckAzioni.addSelectField(CostantiDB.PORT_TYPE_AZIONI, "id_port_type");
				sqlQueryObjectExistsPortTypeCheckAzioni.addWhereCondition(CostantiDB.PORT_TYPE_AZIONI+".id_port_type="+CostantiDB.PORT_TYPE+".id");
				sqlQueryObjectExistsPortTypeCheckAzioni.setANDLogicOperator(true);
				
				sqlQueryObjectExistsPortTypeConAzioni = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObjectExistsPortTypeConAzioni.addFromTable(CostantiDB.PORT_TYPE);
				sqlQueryObjectExistsPortTypeConAzioni.addSelectField(CostantiDB.PORT_TYPE, "id_accordo");
				sqlQueryObjectExistsPortTypeConAzioni.addWhereCondition(CostantiDB.PORT_TYPE+".id_accordo="+CostantiDB.ACCORDI+".id");
				sqlQueryObjectExistsPortTypeConAzioni.addWhereExistsCondition(false, sqlQueryObjectExistsPortTypeCheckAzioni);
				sqlQueryObjectExistsPortTypeConAzioni.setANDLogicOperator(true);
			}
			
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.ACCORDI);
			sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
			sqlQueryObject.addWhereCondition(CostantiDB.ACCORDI+".id_referente="+CostantiDB.SOGGETTI+".id");
			sqlQueryObject.addSelectCountField("*", "cont");
			if(this.driver.useSuperUser && superuser!=null && (!superuser.equals("")))
				sqlQueryObject.addWhereCondition(CostantiDB.ACCORDI+".superuser = ?");
			
			//query con search
			if (!search.equals("")) {
				sqlQueryObject.addWhereCondition(false, 
						sqlQueryObject.getWhereLikeCondition(CostantiDB.ACCORDI+".nome", search, true, true));
						//sqlQueryObject.getWhereLikeCondition("versione", search, true, true), // la versione e' troppo, tutte hanno 1 ....
						//sqlQueryObject.getWhereLikeCondition(CostantiDB.SOGGETTI+".nome_soggetto", search, true, true));  // fa confusone nei protocolli che non supportano il referente
			}
			if (searchByTipoSoggetto) {
				sqlQueryObject.addWhereINCondition("tipo_soggetto", true, tipoSoggettiProtocollo.toArray(new String[1]));		
			}
			
			if(excludeASParteComune){
				sqlQueryObject.addWhereExistsCondition(false, sqlQueryObjectExclude);
			}
			if(excludeASComposti){
				sqlQueryObject.addWhereExistsCondition(true, sqlQueryObjectExclude);
			}
			
			if(filterTipoAPI!=null && !filterTipoAPI.equals("")) {
				sqlQueryObject.addWhereCondition(CostantiDB.ACCORDI+".service_binding = ?");
			}
			if(filterStatoAccordo!=null && !filterStatoAccordo.equals("")) {
				sqlQueryObject.addWhereCondition(CostantiDB.ACCORDI+".stato = ?");
			}
			if(filterGruppo!=null && !filterGruppo.equals("")) {
				sqlQueryObject.addFromTable(CostantiDB.ACCORDI_GRUPPI);
				sqlQueryObject.addFromTable(CostantiDB.GRUPPI);
				sqlQueryObject.addWhereCondition(CostantiDB.ACCORDI_GRUPPI+".id_accordo="+CostantiDB.ACCORDI+".id");
				sqlQueryObject.addWhereCondition(CostantiDB.ACCORDI_GRUPPI+".id_gruppo="+CostantiDB.GRUPPI+".id");
				sqlQueryObject.addWhereCondition(CostantiDB.GRUPPI+".nome = ?");
			}
			
			if(soloAccordiConsistentiRest && soloAccordiConsistentiSoap) {
				sqlQueryObject.addWhereCondition(false, 
						sqlQueryObjectExistsResource.getWhereExistsCondition(false, sqlQueryObjectExistsResource),
						sqlQueryObjectExistsPortTypeConAzioni.getWhereExistsCondition(false, sqlQueryObjectExistsPortTypeConAzioni));
			}
			else if(soloAccordiConsistentiRest) {
				sqlQueryObject.addWhereExistsCondition(false, sqlQueryObjectExistsResource);
			}
			else if(soloAccordiConsistentiSoap) {
				sqlQueryObject.addWhereExistsCondition(false, sqlQueryObjectExistsPortTypeConAzioni);
			}
			
			if(searchCanale) {
				if(canaleDefault) {
					sqlQueryObject.addWhereCondition(false, CostantiDB.ACCORDI+".canale = ?", CostantiDB.ACCORDI+".canale is null");
				}
				else {
					sqlQueryObject.addWhereCondition(CostantiDB.ACCORDI+".canale = ?");
				}
			}
			
			sqlQueryObject.setANDLogicOperator(true);
			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			int index = 1;
			if(this.driver.useSuperUser && superuser!=null && (!superuser.equals("")))
				stmt.setString(index++, superuser);
			if(filterTipoAPI!=null && !filterTipoAPI.equals("")) {
				stmt.setString(index++, filterTipoAPI);
			}
			if(filterStatoAccordo!=null && !filterStatoAccordo.equals("")) {
				stmt.setString(index++, filterStatoAccordo);
			}
			if(filterGruppo!=null && !filterGruppo.equals("")) {
				stmt.setString(index++, filterGruppo);
			}
			if(searchCanale) {
				stmt.setString(index++, filterCanale);
			}
			
			risultato = stmt.executeQuery();
			if (risultato.next())
				ricerca.setNumEntries(idLista,risultato.getInt(1));
			risultato.close();
			stmt.close();

			// ricavo le entries
			if (limit == 0) // con limit
				limit = ISQLQueryObject.LIMIT_DEFAULT_VALUE;
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.ACCORDI);
			sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
			sqlQueryObject.addSelectAliasField(CostantiDB.ACCORDI, "id", "idAccordo");
			sqlQueryObject.addSelectAliasField(CostantiDB.ACCORDI, "nome", "nomeAccordo");
			sqlQueryObject.addSelectField(CostantiDB.ACCORDI, "versione");
			sqlQueryObject.addSelectField(CostantiDB.SOGGETTI, "tipo_soggetto");
			sqlQueryObject.addSelectField(CostantiDB.SOGGETTI, "nome_soggetto");
			sqlQueryObject.addSelectAliasField(CostantiDB.SOGGETTI, "id", "idSoggetto");
			sqlQueryObject.addWhereCondition(CostantiDB.ACCORDI+".id_referente="+CostantiDB.SOGGETTI+".id");
			//sqlQueryObject.addSelectField("id_referente");
			if(this.driver.useSuperUser && superuser!=null && (!superuser.equals("")))
				sqlQueryObject.addWhereCondition(CostantiDB.ACCORDI+".superuser = ?");
			
			if (!search.equals("")) { // con search
				sqlQueryObject.addWhereCondition(false, 
						sqlQueryObject.getWhereLikeCondition(CostantiDB.ACCORDI+".nome", search, true, true));
				//sqlQueryObject.getWhereLikeCondition("versione", search, true, true), // la versione e' troppo, tutte hanno 1 ....
				//sqlQueryObject.getWhereLikeCondition(CostantiDB.SOGGETTI+".nome_soggetto", search, true, true));  // fa confusone nei protocolli che non supportano il referente
			}
			if (searchByTipoSoggetto) {
				sqlQueryObject.addWhereINCondition("tipo_soggetto", true, tipoSoggettiProtocollo.toArray(new String[1]));		
			}

			if(excludeASParteComune){
				sqlQueryObject.addWhereExistsCondition(false, sqlQueryObjectExclude);
			}
			if(excludeASComposti){
				sqlQueryObject.addWhereExistsCondition(true, sqlQueryObjectExclude);
			}
			
			if(filterTipoAPI!=null && !filterTipoAPI.equals("")) { // con filter
				sqlQueryObject.addWhereCondition(CostantiDB.ACCORDI+".service_binding = ?");
			}
			if(filterStatoAccordo!=null && !filterStatoAccordo.equals("")) {
				sqlQueryObject.addWhereCondition(CostantiDB.ACCORDI+".stato = ?");
			}
			if(filterGruppo!=null && !filterGruppo.equals("")) {
				sqlQueryObject.addFromTable(CostantiDB.ACCORDI_GRUPPI);
				sqlQueryObject.addFromTable(CostantiDB.GRUPPI);
				sqlQueryObject.addWhereCondition(CostantiDB.ACCORDI_GRUPPI+".id_accordo="+CostantiDB.ACCORDI+".id");
				sqlQueryObject.addWhereCondition(CostantiDB.ACCORDI_GRUPPI+".id_gruppo="+CostantiDB.GRUPPI+".id");
				sqlQueryObject.addWhereCondition(CostantiDB.GRUPPI+".nome = ?");
			}
						
			if(soloAccordiConsistentiRest && soloAccordiConsistentiSoap) {
				sqlQueryObject.addWhereCondition(false, 
						sqlQueryObjectExistsResource.getWhereExistsCondition(false, sqlQueryObjectExistsResource),
						sqlQueryObjectExistsPortTypeConAzioni.getWhereExistsCondition(false, sqlQueryObjectExistsPortTypeConAzioni));
			}
			else if(soloAccordiConsistentiRest) {
				sqlQueryObject.addWhereExistsCondition(false, sqlQueryObjectExistsResource);
			}
			else if(soloAccordiConsistentiSoap) {
				sqlQueryObject.addWhereExistsCondition(false, sqlQueryObjectExistsPortTypeConAzioni);
			}
			
			if(searchCanale) {
				if(canaleDefault) {
					sqlQueryObject.addWhereCondition(false, CostantiDB.ACCORDI+".canale = ?", CostantiDB.ACCORDI+".canale is null");
				}
				else {
					sqlQueryObject.addWhereCondition(CostantiDB.ACCORDI+".canale = ?");
				}
			}
			
			sqlQueryObject.setANDLogicOperator(true);
			sqlQueryObject.addOrderBy("nomeAccordo");
			sqlQueryObject.addOrderBy("versione");
			sqlQueryObject.addOrderBy("nome_soggetto");
			sqlQueryObject.addOrderBy("tipo_soggetto");
			sqlQueryObject.setSortType(true);
			sqlQueryObject.setLimit(limit);
			sqlQueryObject.setOffset(offset);
			queryString = sqlQueryObject.createSQLQuery();

			this.driver.logDebug("eseguo query : " + DriverRegistroServiziDB_LIB.formatSQLString(queryString));

			stmt = con.prepareStatement(queryString);
			index = 1;
			if(this.driver.useSuperUser && superuser!=null && (!superuser.equals("")))
				stmt.setString(index++, superuser);
			if(filterTipoAPI!=null && !filterTipoAPI.equals("")) {
				stmt.setString(index++, filterTipoAPI);
			}
			if(filterStatoAccordo!=null && !filterStatoAccordo.equals("")) {
				stmt.setString(index++, filterStatoAccordo);
			}
			if(filterGruppo!=null && !filterGruppo.equals("")) {
				stmt.setString(index++, filterGruppo);
			}
			if(searchCanale) {
				stmt.setString(index++, filterCanale);
			}
			risultato = stmt.executeQuery();

			while (risultato.next()) {

				IDSoggettoDB idSoggettoDB = new IDSoggettoDB(risultato.getString("tipo_soggetto"), risultato.getString("nome_soggetto"));
				idSoggettoDB.setId(risultato.getLong("idSoggetto"));
				
				IDAccordoDB idAccordoDB = new IDAccordoDB(risultato.getString("nomeAccordo"), idSoggettoDB, risultato.getInt("versione"));
				idAccordoDB.setId(risultato.getLong("idAccordo"));
				
				lista.add(idAccordoDB);

			}

			this.driver.logDebug("size lista :" + ((lista == null) ? null : lista.size()));

			return lista;

		} catch (Exception se) {

			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::" + nomeMetodo + "] Exception: " + se.getMessage(),se);
		} finally {

			//Chiudo statement and resultset
			JDBCUtilities.closeResources(risultato, stmt);

			this.driver.closeConnection(con);
		}
	}
	
	protected AccordoServizioParteComune[] getAllIdAccordiWithSoggettoReferente(IDSoggetto idsoggetto) throws DriverRegistroServiziException,DriverRegistroServiziNotFound{

		Connection con = null;
		PreparedStatement stm = null;
		ResultSet rs = null;

		this.driver.logDebug("getAllIdAccordiWithSoggettoReferente...");

		try {
			this.driver.logDebug("operazione atomica = " + this.driver.atomica);
			// prendo la connessione dal pool
			if (this.driver.atomica)
				con = this.driver.getConnectionFromDatasource("getAllIdAccordiWithSoggettoReferente");
			else
				con = this.driver.globalConnection;

			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.ACCORDI);
			sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
			sqlQueryObject.addSelectAliasField(CostantiDB.ACCORDI,"id","idAccordo");
			sqlQueryObject.addWhereCondition(CostantiDB.ACCORDI+".id_referente="+CostantiDB.SOGGETTI+".id");
			sqlQueryObject.addWhereCondition(CostantiDB.SOGGETTI+".tipo_soggetto=?");
			sqlQueryObject.addWhereCondition(CostantiDB.SOGGETTI+".nome_soggetto=?");
			sqlQueryObject.setANDLogicOperator(true);
			String sqlQuery = sqlQueryObject.createSQLQuery();
			this.driver.logDebug("eseguo query : " + sqlQuery );
			stm = con.prepareStatement(sqlQuery);
			stm.setString(1, idsoggetto.getTipo());
			stm.setString(2, idsoggetto.getNome());
			rs = stm.executeQuery();
			List<AccordoServizioParteComune> accordi = new ArrayList<AccordoServizioParteComune>();
			while (rs.next()) {
				accordi.add(this.getAccordoServizioParteComune(rs.getLong("idAccordo")));
			}
			if(accordi.size()==0){
				throw new DriverRegistroServiziNotFound("Accordi non trovati con soggetto referente ["+idsoggetto+"]");
			}else{
				AccordoServizioParteComune [] res = new AccordoServizioParteComune[1];
				return accordi.toArray(res);
			}
		}catch(DriverRegistroServiziNotFound de){
			throw de;
		}
		catch(Exception e){
			throw new DriverRegistroServiziException("getAllIdAccordiWithSoggettoReferente error",e);
		} finally {

			//Chiudo statement and resultset
			JDBCUtilities.closeResources(rs, stm);

			this.driver.closeConnection(con);

		}
	}
	
	
	protected IDAccordo getIdAccordoServizioParteComune(long id) throws DriverRegistroServiziException,DriverRegistroServiziNotFound {
		return getIdAccordoServizioParteComune(id,null);
	}
	protected IDAccordo getIdAccordoServizioParteComune(long id,Connection conParam) throws DriverRegistroServiziException,DriverRegistroServiziNotFound {
		this.driver.logDebug("richiesto getIdAccordoServizioParteComune: " + id);
		// conrollo consistenza
		if (id <= 0)
			return null;

		Connection con = null;
		PreparedStatement stm = null;
		ResultSet rs = null;

		IDAccordo idAccordo = null;
		try {
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.ACCORDI);
			sqlQueryObject.addSelectField("nome");
			sqlQueryObject.addSelectField("id_referente");
			sqlQueryObject.addSelectField("versione");
			sqlQueryObject.addWhereCondition("id = ?");
			String sqlQuery = sqlQueryObject.createSQLQuery();

			this.driver.logDebug("operazione atomica = " + this.driver.atomica);
			// prendo la connessione dal pool
			if(conParam!=null){
				con = conParam;
			}
			else if (this.driver.atomica)
				con = this.driver.getConnectionFromDatasource("getIdAccordoServizioParteComune(longId)");
			else
				con = this.driver.globalConnection;

			stm = con.prepareStatement(sqlQuery);

			stm.setLong(1, id);

			this.driver.logDebug("eseguo query : " + DriverRegistroServiziDB_LIB.formatSQLString(sqlQuery, id));
			rs = stm.executeQuery();


			if (rs.next()) {

				IDSoggetto referente = null;
				long idReferente = rs.getLong("id_referente");
				if(idReferente>0){
					referente = this.driver.getIdSoggetto(idReferente,con);
					if(referente==null){
						throw new Exception("Soggetto referente non presente?");
					}
				}

				idAccordo = this.driver.idAccordoFactory.getIDAccordoFromValues(rs.getString("nome"),referente,rs.getInt("versione"));

			} else {
				rs.close();
				stm.close();
				throw new DriverRegistroServiziNotFound("[DriverRegistroServiziDB::getIdAccordoServizioParteComune] rs.next non ha restituito valori con la seguente interrogazione :\n" + DriverRegistroServiziDB_LIB.formatSQLString(sqlQuery, id));
			}

			rs.close();
			stm.close();

		} catch (SQLException se) {
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::getIdAccordoServizioParteComune] SQLException :" + se.getMessage(),se);
		}catch (DriverRegistroServiziNotFound e) {
			throw e;
		}catch (Exception se) {
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::getIdAccordoServizioParteComune] Exception :" + se.getMessage(),se);
		} finally {

			//Chiudo statement and resultset
			JDBCUtilities.closeResources(rs, stm);

			this.driver.closeConnection(conParam, con);

		}

		return idAccordo;
	}
	
	protected List<Documento> accordiAllegatiList(long idAccordo, ISearch ricerca) throws DriverRegistroServiziException {
		String nomeMetodo = "accordiAllegatiList";
		int idLista = Liste.ACCORDI_ALLEGATI;
		int offset;
		int limit;
		String search;
		String queryString;

		limit = ricerca.getPageSize(idLista);
		offset = ricerca.getIndexIniziale(idLista);
		search = (org.openspcoop2.core.constants.Costanti.SESSION_ATTRIBUTE_VALUE_RICERCA_UNDEFINED.equals(ricerca.getSearchString(idLista)) ? "" : ricerca.getSearchString(idLista));
		ricerca.getSearchString(idLista);

		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet risultato = null;

		ArrayList<Documento> lista = new ArrayList<Documento>();

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("accordiAllegatiList");

			} catch (Exception e) {
				throw new DriverRegistroServiziException("[DriverRegistroServiziDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		try {

			if (!search.equals("")) {
				//query con search
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.DOCUMENTI);
				sqlQueryObject.addSelectCountField("*", "cont");
				sqlQueryObject.addWhereCondition("id_proprietario = ?");
				sqlQueryObject.addWhereCondition("tipo_proprietario = ?");
				sqlQueryObject.addWhereCondition(false, 
						//sqlQueryObject.getWhereLikeCondition("ruolo",search,true,true),
						sqlQueryObject.getWhereLikeCondition("nome",search,true,true));
				sqlQueryObject.setANDLogicOperator(true);
				queryString = sqlQueryObject.createSQLQuery();
			} else {
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.DOCUMENTI);
				sqlQueryObject.addSelectCountField("*", "cont");
				sqlQueryObject.addWhereCondition("id_proprietario = ?");
				sqlQueryObject.addWhereCondition("tipo_proprietario = ?");
				sqlQueryObject.setANDLogicOperator(true);
				queryString = sqlQueryObject.createSQLQuery();
			}

			stmt = con.prepareStatement(queryString);
			stmt.setLong(1,idAccordo);
			stmt.setString(2, ProprietariDocumento.accordoServizio.toString());
			risultato = stmt.executeQuery();
			if (risultato.next())
				ricerca.setNumEntries(idLista,risultato.getInt("cont"));
			risultato.close();
			stmt.close();

			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.DOCUMENTI);
			sqlQueryObject.addSelectField("id");
			sqlQueryObject.addSelectField("nome");
			sqlQueryObject.addSelectField("ruolo");
			sqlQueryObject.addSelectField("id_proprietario");
			sqlQueryObject.addSelectField("tipo_proprietario");
			//where
			sqlQueryObject.addWhereCondition("id_proprietario = ?");
			sqlQueryObject.addWhereCondition("tipo_proprietario = ?");

			// ricavo le entries
			if (limit == 0) // con limit
				limit = ISQLQueryObject.LIMIT_DEFAULT_VALUE;

			if (!search.equals("")) { // con search
				sqlQueryObject.addWhereCondition(false, 
						//sqlQueryObject.getWhereLikeCondition("ruolo",search,true,true),
						sqlQueryObject.getWhereLikeCondition("nome",search,true,true));
			} 

			sqlQueryObject.setANDLogicOperator(true);
			sqlQueryObject.addOrderBy("nome");
			sqlQueryObject.setSortType(true);
			sqlQueryObject.setLimit(limit);
			sqlQueryObject.setOffset(offset);
			queryString = sqlQueryObject.createSQLQuery();

			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idAccordo);
			stmt.setString(2, ProprietariDocumento.accordoServizio.toString());
			risultato = stmt.executeQuery();

			while(risultato.next()){
				Documento doc = DriverRegistroServiziDB_documentiLIB.getDocumento(risultato.getLong("id"),false, con, this.driver.tipoDB); 
				lista.add(doc);
			}

			return lista;

		} catch (Exception se) {

			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::" + nomeMetodo + "] Exception: " + se.getMessage(),se);
		} finally {
			//Chiudo statement and resultset
			JDBCUtilities.closeResources(risultato, stmt);
			
			this.driver.closeConnection(con);
		}
	}
	
	protected List<IDServizio> getIdServiziWithAccordo(IDAccordo idAccordo,boolean checkPTisNull) throws DriverRegistroServiziException,DriverRegistroServiziNotFound {
		String nomeMetodo = "getIdServiziWithPortType";

		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String queryString;

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("getIdServiziWithAccordo");

			} catch (Exception e) {
				throw new DriverRegistroServiziException("[DriverRegistroServiziDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(), e);

			}

		} else {
			con = this.driver.globalConnection;
		}

		this.driver.logDebug("operazione this.atomica = " + this.driver.atomica);

		List<IDServizio> idServizi = new ArrayList<IDServizio>(); 
		try {

			//recupero idAccordo
			long idAccordoLong = DBUtils.getIdAccordoServizioParteComune(idAccordo, con, this.driver.tipoDB);
			// Controllo che il pdd non sia in uso
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.SERVIZI);
			sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
			sqlQueryObject.addSelectField("nome_servizio");
			sqlQueryObject.addSelectField("tipo_servizio");
			sqlQueryObject.addSelectField("versione_servizio");
			sqlQueryObject.addSelectField("nome_soggetto");
			sqlQueryObject.addSelectField("tipo_soggetto");
			sqlQueryObject.setANDLogicOperator(true);
			sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI+".id_accordo=?");
			sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI+".id_soggetto="+CostantiDB.SOGGETTI+".id");
			if(checkPTisNull){
				sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI+".port_type is null");
			}
			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idAccordoLong);

			rs = stmt.executeQuery();
			while (rs.next()) {
				IDSoggetto soggettoErogatore = new IDSoggetto(rs.getString("tipo_soggetto"), rs.getString("nome_soggetto"));
				IDServizio idServizio = 
						this.driver.idServizioFactory.getIDServizioFromValues(rs.getString("tipo_servizio"), 
								rs.getString("nome_servizio"), soggettoErogatore, rs.getInt("versione_servizio"));
				idServizi.add(idServizio);
			}

			if(idServizi.size()<=0){
				throw new DriverRegistroServiziNotFound("Servizi non trovato che implementano l'accordo di servizio "+idAccordo.toString());
			}
			else{
				return idServizi;
			}

		}catch(DriverRegistroServiziNotFound dNot){
			throw dNot;
		}catch (Exception se) {
			throw new DriverRegistroServiziException("[DriverRegistroServiziException::" + nomeMetodo + "] Exception: " + se.getMessage(),se);
		} finally {
			// Chiudo statement and resultset
			JDBCUtilities.closeResources(rs, stmt);
			
			this.driver.closeConnection(con);
		}
	}
	
	
	protected void validaStatoAccordoServizio(AccordoServizioParteComune as,boolean utilizzoAzioniDiretteInAccordoAbilitato) throws ValidazioneStatoPackageException{

		ValidazioneStatoPackageException erroreValidazione =
				new ValidazioneStatoPackageException("AccordoServizio",as.getStatoPackage(),null);

		try{

			// Controlli di visibilita
			if(as.getPrivato()==null || as.getPrivato()==false){
				if(as.getSoggettoReferente()!=null){
					IDSoggetto idS = new IDSoggetto(as.getSoggettoReferente().getTipo(),as.getSoggettoReferente().getNome());
					try{
						Soggetto s = this.driver.getSoggetto(idS);
						if(s.getPrivato()!=null && s.getPrivato()){
							erroreValidazione.addErroreValidazione("soggetto referente ["+idS+"] con visibilita' privata, in un accordo di servizio con visibilita' pubblica");
						}
					}catch(DriverRegistroServiziNotFound dNot){}
				}
			}
			if(as.getServizioComposto()!=null){
				if(as.getServizioComposto().getIdAccordoCooperazione()>0){
					try{
						AccordoCooperazione ac = this.driver.getAccordoCooperazione(as.getServizioComposto().getIdAccordoCooperazione());
						if(as.getPrivato()==null || as.getPrivato()==false){
							if(ac.getPrivato()!=null && ac.getPrivato()){
								erroreValidazione.addErroreValidazione("accordo di cooperazione ["+this.driver.idAccordoCooperazioneFactory.getUriFromAccordo(ac)+"] con visibilita' privata, in un accordo di servizio con visibilita' pubblica");
							}
						}
					}catch(DriverRegistroServiziNotFound dNot){}
				}
				if(as.getServizioComposto().sizeServizioComponenteList()>=2){
					for(int i=0; i<as.getServizioComposto().sizeServizioComponenteList(); i++){
						if(as.getServizioComposto().getServizioComponente(i).getId()>0){
							try{
								AccordoServizioParteSpecifica sc = this.driver.getAccordoServizioParteSpecifica(as.getServizioComposto().getServizioComponente(i).getId());
								if(as.getPrivato()==null || as.getPrivato()==false){
									if(sc.getPrivato()!=null && sc.getPrivato()){
										String uriServizioComponente = this.driver.idServizioFactory.getUriFromAccordo(sc);
										erroreValidazione.addErroreValidazione("servizio componente ["+uriServizioComponente+"] con visibilita' privata, in un accordo di servizio con visibilita' pubblica");
									}
								}
							}catch(DriverRegistroServiziNotFound dNot){}
						}
					}
				}
			}

			if(ServiceBinding.SOAP.equals(as.getServiceBinding())) {

				// Controlli di stato
				if(StatiAccordo.bozza.toString().equals(as.getStatoPackage()) == false){
	
					// Validazione necessaria sia ad uno stato operativo che finale
					if(utilizzoAzioniDiretteInAccordoAbilitato==false){
						if(as.sizePortTypeList()==0){
							erroreValidazione.addErroreValidazione("non sono definiti Servizi");
						}
						for(int j=0; j<as.sizePortTypeList(); j++){
							if(as.getPortType(j).sizeAzioneList()==0){
								erroreValidazione.addErroreValidazione("servizio["+as.getPortType(j).getNome()+"] non possiede azioni");
							}
						}
					}else{
						if(as.sizePortTypeList()==0 && as.sizeAzioneList()==0 && as.getUtilizzoSenzaAzione()==false){
							erroreValidazione.addErroreValidazione("non sono definite ne Azioni (utilizzoSenzaAzione=false) ne Servizi");
						}
						if(as.sizePortTypeList()!=0){
							for(int j=0; j<as.sizePortTypeList(); j++){
								if(as.getPortType(j).sizeAzioneList()==0){
									erroreValidazione.addErroreValidazione("servizio["+as.getPortType(j).getNome()+"] non possiede azioni");
								}
							}
						}
						if(as.sizePortTypeList()!=0){
							for(int j=0; j<as.sizePortTypeList(); j++){
								PortType pt = as.getPortType(j);
								for(int k=0;k<pt.sizeAzioneList();k++){
									Operation op = pt.getAzione(k);
									ProfiloCollaborazione profiloCollaborazioneOP = op.getProfiloCollaborazione();
									if(CostantiRegistroServizi.PROFILO_AZIONE_DEFAULT.equals(op.getProfAzione())){
										profiloCollaborazioneOP = pt.getProfiloCollaborazione();
										if(CostantiRegistroServizi.PROFILO_AZIONE_DEFAULT.equals(pt.getProfiloPT())){
											profiloCollaborazioneOP = as.getProfiloCollaborazione();
										}
									}
									if(CostantiRegistroServizi.ASINCRONO_ASIMMETRICO.equals(profiloCollaborazioneOP) ||
											CostantiRegistroServizi.ASINCRONO_SIMMETRICO.equals(profiloCollaborazioneOP)	){
										if(op.getCorrelata()==null){
											// Verifico che esista un altra azione correlata a questa.
											boolean trovataCorrelazione = false;
											for(int verificaPTIndex=0; verificaPTIndex<as.sizePortTypeList(); verificaPTIndex++){
												PortType ptVerifica = as.getPortType(verificaPTIndex);
												for(int verificaOPIndex=0; verificaOPIndex<ptVerifica.sizeAzioneList(); verificaOPIndex++){
													Operation opVerifica = ptVerifica.getAzione(verificaOPIndex);
													if(opVerifica.getCorrelata()!=null && opVerifica.getCorrelata().equals(op.getNome())){
														// potenziale correlazione, verifico servizio
														if(opVerifica.getCorrelataServizio()==null){
															if(CostantiRegistroServizi.ASINCRONO_ASIMMETRICO.equals(profiloCollaborazioneOP) && ptVerifica.getNome().equals(pt.getNome())){
																// la correlazione per l'asincrono asimmetrico puo' avvenire sullo stesso port type
																trovataCorrelazione = true;
																break;
															}
														}
														else if(opVerifica.getCorrelataServizio().equals(pt.getNome())){
															trovataCorrelazione = true;
															break;
														}
													}
												}
											}
											if(trovataCorrelazione==false){
												erroreValidazione.addErroreValidazione("L'azione ["+op.getNome()+"] del servizio["+as.getPortType(j).getNome()+"] non risulta correlata da altre azioni");
											}
										}
									}
								}
							}
						}
					}
	
					if(StatiAccordo.finale.toString().equals(as.getStatoPackage())){
	
						String wsdlConcettuale = (as.getByteWsdlConcettuale()!=null ? new String(as.getByteWsdlConcettuale()) : null);
						String wsdlLogicoErogatore = (as.getByteWsdlLogicoErogatore()!=null ? new String(as.getByteWsdlLogicoErogatore()) : null);
						wsdlConcettuale = wsdlConcettuale!=null && !"".equals(wsdlConcettuale.trim().replaceAll("\n", "")) ? wsdlConcettuale : null;
						wsdlLogicoErogatore = wsdlLogicoErogatore!=null && !"".equals(wsdlLogicoErogatore.trim().replaceAll("\n", "")) ? wsdlLogicoErogatore : null;
	
						if(	wsdlConcettuale == null){
							erroreValidazione.addErroreValidazione("interfaccia WSDL Concettuale non definita");
						}
						if(	wsdlLogicoErogatore == null){
							erroreValidazione.addErroreValidazione("interfaccia WSDL LogicoErogatore non definita");
						}
	
						if(as.getServizioComposto()!=null){
							if(as.getServizioComposto().getIdAccordoCooperazione()<=0){
								erroreValidazione.addErroreValidazione("accordo di cooperazione (id) non definito");
							}else{
								try{
									AccordoCooperazione ac = this.driver.getAccordoCooperazione(as.getServizioComposto().getIdAccordoCooperazione());
									if(StatiAccordo.finale.toString().equals(ac.getStatoPackage())==false){
										erroreValidazione.addErroreValidazione("accordo di cooperazione ["+this.driver.idAccordoCooperazioneFactory.getUriFromAccordo(ac)+"] in uno stato non finale ["+ac.getStatoPackage()+"]");
									}
								}catch(DriverRegistroServiziNotFound dNot){
									erroreValidazione.addErroreValidazione("accordo di cooperazione non definito");
								}
							}
	
							if(as.getServizioComposto().sizeSpecificaCoordinamentoList()<=0){
								erroreValidazione.addErroreValidazione("specifica di coordinamento non definita");
							}
	
	
							if(as.getServizioComposto().sizeServizioComponenteList()<=0){
								erroreValidazione.addErroreValidazione("servizi componenti non definiti");	
							}else{
								if(as.getServizioComposto().sizeServizioComponenteList()<2){
									erroreValidazione.addErroreValidazione("almeno 2 servizi componenti sono necessari per realizzare un servizio composto");	
								}else{
									for(int i=0; i<as.getServizioComposto().sizeServizioComponenteList(); i++){
										if(as.getServizioComposto().getServizioComponente(i).getIdServizioComponente()<=0){
											erroreValidazione.addErroreValidazione("servizio componente ["+i+"] (id) non definito");
										}else{
											try{
												AccordoServizioParteSpecifica sc = this.driver.getAccordoServizioParteSpecifica(as.getServizioComposto().getServizioComponente(i).getIdServizioComponente());
												if(StatiAccordo.finale.toString().equals(sc.getStatoPackage())==false){
													String uriServizioComponente = this.driver.idServizioFactory.getUriFromAccordo(sc);
													erroreValidazione.addErroreValidazione("servizio componente ["+uriServizioComponente+"] in uno stato non finale ["+sc.getStatoPackage()+"]");
												}
											}catch(DriverRegistroServiziNotFound dNot){
												erroreValidazione.addErroreValidazione("servizio componente ["+i+"] non definito");
											}
										}
									}
								}
							}
						}
	
					}
	
				}
			}
			else {
				
				// REST
				
				// Controlli di stato
				if(StatiAccordo.bozza.toString().equals(as.getStatoPackage()) == false){
					
					if(as.sizeResourceList()==0){
						erroreValidazione.addErroreValidazione("non sono definite alcune risorse");
					}
					
				}
				
				if(StatiAccordo.finale.toString().equals(as.getStatoPackage())){
					
					String wsdlConcettuale = (as.getByteWsdlConcettuale()!=null ? new String(as.getByteWsdlConcettuale()) : null);
					wsdlConcettuale = wsdlConcettuale!=null && !"".equals(wsdlConcettuale.trim().replaceAll("\n", "")) ? wsdlConcettuale : null;
					
					if(	wsdlConcettuale == null){
						erroreValidazione.addErroreValidazione("Specifica di interfaccia non definita");
					}
					
				}
				
			}

		}catch(Exception e){
			throw new ValidazioneStatoPackageException(e);
		}

		if(erroreValidazione.sizeErroriValidazione()>0){
			throw erroreValidazione;
		}
	}
	
	protected void controlloUnicitaImplementazioneAccordoPerSoggetto(String portType,
			IDSoggetto idSoggettoErogatore, long idSoggettoErogatoreLong, 
			IDAccordo idAccordoServizioParteComune, long idAccordoServizioParteComuneLong,
			IDServizio idAccordoServizioParteSpecifica, long idAccordoServizioParteSpecificaLong,
			boolean isUpdate,boolean isServizioCorrelato,
			boolean isAbilitatoControlloUnicitaImplementazioneAccordoPerSoggetto,
			boolean isAbilitatoControlloUnicitaImplementazionePortTypePerSoggetto) throws DriverRegistroServiziException{
		/*
		 * Controllo che non esistano 2 servizi con stesso soggetto erogatore e accordo di servizio 
		 * che siano entrambi correlati o non correlati. 
		 * Al massimo possono esistere 2 servizi di uno stesso accordo erogati da uno stesso soggetto, 
		 * purche' siano uno correlato e uno no. 
		 * Se tipoOp = change, devo fare attenzione a non escludere il servizio selezionato che sto
		 * cambiando 
		 */

		String tmpServCorr = CostantiRegistroServizi.DISABILITATO.toString();
		if(isServizioCorrelato){
			tmpServCorr = CostantiRegistroServizi.ABILITATO.toString();
		}
		String s = "servizio";
		if (isServizioCorrelato) {
			s = "servizio correlato";
		}

		// se il servizio non definisce port type effettuo controllo che non
		// esistano 2 servizi con stesso soggetto,
		// accordo e servizio correlato
		if (portType == null || "-".equals(portType)) {

			if(isAbilitatoControlloUnicitaImplementazioneAccordoPerSoggetto){

				// Controllo che non esistano 2 servizi con stesso soggetto,
				// accordo e servizio correlato
				// Se tipoOp = change, devo fare attenzione a non escludere il servizio selezionato

				long idAccordoServizioParteSpecificaAlreadyExists = 
						this.driver.getServizioWithSoggettoAccordoServCorr(idSoggettoErogatoreLong, idAccordoServizioParteComuneLong, 
								tmpServCorr);

				boolean addError = ((!isUpdate) && (idAccordoServizioParteSpecificaAlreadyExists > 0));

				boolean changeError = false;
				if (isUpdate && (idAccordoServizioParteSpecificaAlreadyExists > 0)) {
					changeError = (idAccordoServizioParteSpecificaLong != idAccordoServizioParteSpecificaAlreadyExists);
				}

				if (addError || changeError) {
					throw new DriverRegistroServiziException("Esiste gi&agrave; un " + s + " del Soggetto "+idSoggettoErogatore+
							" che implementa l'accordo selezionato ["+IDAccordoFactory.getInstance().getUriFromIDAccordo(idAccordoServizioParteComune)+"]");
				}

			}

		} else {

			if(isAbilitatoControlloUnicitaImplementazionePortTypePerSoggetto){

				// Controllo che non esistano 2 servizi con stesso soggetto,
				// accordo e servizio correlato e port-type

				long idAccordoServizioParteSpecificaAlreadyExists =  
						this.driver.getServizioWithSoggettoAccordoServCorrPt(idSoggettoErogatoreLong, idAccordoServizioParteComuneLong, 
								tmpServCorr, portType);

				boolean addError = ((!isUpdate) && (idAccordoServizioParteSpecificaAlreadyExists > 0));

				boolean changeError = false;
				if (isUpdate && (idAccordoServizioParteSpecificaAlreadyExists > 0)) {
					changeError = (idAccordoServizioParteSpecificaLong != idAccordoServizioParteSpecificaAlreadyExists);
				}

				if (addError || changeError) {
					throw new DriverRegistroServiziException("Esiste gi&agrave; un " + s + " del Soggetto "+idSoggettoErogatore+
							" che implementa il servizio "+portType+" dell'accordo selezionato ["+IDAccordoFactory.getInstance().getUriFromIDAccordo(idAccordoServizioParteComune)+"]");
				}

			}
		}
	}
}
