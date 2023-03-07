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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.core.commons.DBUtils;
import org.openspcoop2.core.commons.ISearch;
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.constants.ProprietariProtocolProperty;
import org.openspcoop2.core.id.IDPortType;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.Azione;
import org.openspcoop2.core.registry.Message;
import org.openspcoop2.core.registry.MessagePart;
import org.openspcoop2.core.registry.Operation;
import org.openspcoop2.core.registry.PortType;
import org.openspcoop2.core.registry.ProtocolProperty;
import org.openspcoop2.core.registry.constants.CostantiRegistroServizi;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziException;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziNotFound;
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
public class DriverRegistroServiziDB_accordiSoapDriver {

	private DriverRegistroServiziDB driver = null;
	
	protected DriverRegistroServiziDB_accordiSoapDriver(DriverRegistroServiziDB driver) {
		this.driver = driver;
	}
	
	protected void readPortTypes(AccordoServizioParteComune as,Connection conParam) throws DriverRegistroServiziException,DriverRegistroServiziNotFound{
		// Aggiungo port type

		Connection con = null;
		PreparedStatement stm = null;
		ResultSet rs = null;
		String sqlQuery = null;

		try {
			this.driver.log.debug("operazione atomica = " + this.driver.atomica);
			// prendo la connessione dal pool
			if(conParam!=null)
				con = conParam;
			else if (this.driver.atomica)
				con = this.driver.getConnectionFromDatasource("readPortTypes");
			else
				con = this.driver.globalConnection;

			if(as.getId()==null || as.getId()<=0)
				throw new Exception("Accordo id non definito");

			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.PORT_TYPE);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition("id_accordo = ?");
			sqlQueryObject.setANDLogicOperator(true);
			sqlQueryObject.addOrderBy("nome");
			sqlQueryObject.setSortType(true);
			sqlQuery = sqlQueryObject.createSQLQuery();
			stm = con.prepareStatement(sqlQuery);
			stm.setLong(1, as.getId());

			this.driver.log.debug("eseguo query : " + DriverRegistroServiziDB_LIB.formatSQLString(sqlQuery, as.getId()));
			rs = stm.executeQuery();

			while (rs.next()) {

				PortType pt = new PortType();

				String tmp = rs.getString("nome");
				pt.setNome(((tmp == null || tmp.equals("")) ? null : tmp));

				tmp = rs.getString("descrizione");
				pt.setDescrizione(((tmp == null || tmp.equals("")) ? null : tmp));

				tmp = rs.getString("profilo_collaborazione");
				pt.setProfiloCollaborazione(DriverRegistroServiziDB_LIB.getEnumProfiloCollaborazione(((tmp == null || tmp.equals("")) ? null : tmp)));

				tmp = rs.getString("filtro_duplicati");
				pt.setFiltroDuplicati(DriverRegistroServiziDB_LIB.getEnumStatoFunzionalita(((tmp == null || tmp.equals("")) ? null : tmp)));

				tmp = rs.getString("conferma_ricezione");
				pt.setConfermaRicezione(DriverRegistroServiziDB_LIB.getEnumStatoFunzionalita(((tmp == null || tmp.equals("")) ? null : tmp)));

				tmp = rs.getString("identificativo_collaborazione");
				pt.setIdCollaborazione(DriverRegistroServiziDB_LIB.getEnumStatoFunzionalita(((tmp == null || tmp.equals("")) ? null : tmp)));

				tmp = rs.getString("id_riferimento_richiesta");
				pt.setIdRiferimentoRichiesta(DriverRegistroServiziDB_LIB.getEnumStatoFunzionalita(((tmp == null || tmp.equals("")) ? null : tmp)));
				
				tmp = rs.getString("consegna_in_ordine");
				pt.setConsegnaInOrdine(DriverRegistroServiziDB_LIB.getEnumStatoFunzionalita(((tmp == null || tmp.equals("")) ? null : tmp)));

				tmp = rs.getString("scadenza");
				pt.setScadenza(((tmp == null || tmp.equals("")) ? null : tmp));

				tmp = rs.getString("profilo_pt");
				if (tmp == null || tmp.equals(""))
					pt.setProfiloPT(CostantiRegistroServizi.PROFILO_AZIONE_DEFAULT);
				else
					pt.setProfiloPT(tmp);

				tmp = rs.getString("soap_style");
				pt.setStyle(DriverRegistroServiziDB_LIB.getEnumBindingStyle(((tmp == null || tmp.equals("")) ? null : tmp)));

				tmp = rs.getString("message_type");
				pt.setMessageType(DriverRegistroServiziDB_LIB.getEnumMessageType((tmp == null || tmp.equals("")) ? null : tmp));
				
				pt.setIdAccordo(as.getId());

				long idPortType = rs.getLong("id");
				pt.setId(idPortType);

				// Aggiungo azioni a port type
				this.readAzioniPortTypes(pt,con);

				// Protocol Properties
				try{
					List<ProtocolProperty> listPP = DriverRegistroServiziDB_LIB.getListaProtocolProperty(idPortType, ProprietariProtocolProperty.PORT_TYPE, con, this.driver.tipoDB);
					if(listPP!=null && listPP.size()>0){
						for (ProtocolProperty protocolProperty : listPP) {
							pt.addProtocolProperty(protocolProperty);
						}
					}
				}catch(DriverRegistroServiziNotFound dNotFound){}
				
				as.addPortType(pt);

			}
			rs.close();
			stm.close();

		}catch (DriverRegistroServiziNotFound e) {
			throw e;
		}catch (Exception se) {
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::readPortTypes] Exception :" + se.getMessage(),se);
		} finally {

			JDBCUtilities.closeResources(rs, stm);

			this.driver.closeConnection(conParam, con);

		}
	}

	private void readAzioniPortTypes(PortType pt,Connection conParam) throws DriverRegistroServiziException,DriverRegistroServiziNotFound{
		// Aggiungo port type

		Connection con = null;
		PreparedStatement stm = null;
		ResultSet rs = null;
		String sqlQuery = null;

		try {
			this.driver.log.debug("operazione atomica = " + this.driver.atomica);
			// prendo la connessione dal pool
			if(conParam!=null)
				con = conParam;
			else if (this.driver.atomica)
				con = this.driver.getConnectionFromDatasource("readAzioniPortTypes");
			else
				con = this.driver.globalConnection;

			if(pt.getId()==null || pt.getId()<=0)
				throw new Exception("Port Type id non definito");

			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.PORT_TYPE_AZIONI);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition("id_port_type = ?");
			sqlQueryObject.setANDLogicOperator(true);
			sqlQueryObject.addOrderBy("nome");
			sqlQueryObject.setSortType(true);
			sqlQuery = sqlQueryObject.createSQLQuery();
			stm = con.prepareStatement(sqlQuery);
			stm.setLong(1, pt.getId());

			this.driver.log.debug("eseguo query : " + DriverRegistroServiziDB_LIB.formatSQLString(sqlQuery, pt.getId()));

			rs = stm.executeQuery();

			org.openspcoop2.core.registry.Operation azionePT = null;
			while (rs.next()) {

				azionePT = new org.openspcoop2.core.registry.Operation();

				String tmp = rs.getString("conferma_ricezione");
				azionePT.setConfermaRicezione(DriverRegistroServiziDB_LIB.getEnumStatoFunzionalita(((tmp == null || tmp.equals("")) ? null : tmp)));

				tmp = rs.getString("consegna_in_ordine");
				azionePT.setConsegnaInOrdine(DriverRegistroServiziDB_LIB.getEnumStatoFunzionalita(((tmp == null || tmp.equals("")) ? null : tmp)));

				tmp = rs.getString("filtro_duplicati");
				azionePT.setFiltroDuplicati(DriverRegistroServiziDB_LIB.getEnumStatoFunzionalita(((tmp == null || tmp.equals("")) ? null : tmp)));

				tmp = rs.getString("identificativo_collaborazione");
				azionePT.setIdCollaborazione(DriverRegistroServiziDB_LIB.getEnumStatoFunzionalita(((tmp == null || tmp.equals("")) ? null : tmp)));

				tmp = rs.getString("id_riferimento_richiesta");
				azionePT.setIdRiferimentoRichiesta(DriverRegistroServiziDB_LIB.getEnumStatoFunzionalita(((tmp == null || tmp.equals("")) ? null : tmp)));
				
				tmp = rs.getString("nome");
				azionePT.setNome(((tmp == null || tmp.equals("")) ? null : tmp));

				tmp = rs.getString("scadenza");
				azionePT.setScadenza(((tmp == null || tmp.equals("")) ? null : tmp));

				tmp = rs.getString("profilo_collaborazione");
				azionePT.setProfiloCollaborazione(DriverRegistroServiziDB_LIB.getEnumProfiloCollaborazione(((tmp == null || tmp.equals("")) ? null : tmp)));

				tmp = rs.getString("correlata_servizio");
				azionePT.setCorrelataServizio(((tmp == null || tmp.equals("")) ? null : tmp));

				tmp = rs.getString("correlata");
				azionePT.setCorrelata(((tmp == null || tmp.equals("")) ? null : tmp));

				tmp = rs.getString("profilo_pt_azione");
				if (tmp == null || tmp.equals(""))
					azionePT.setProfAzione(CostantiRegistroServizi.PROFILO_AZIONE_DEFAULT);
				else
					azionePT.setProfAzione(tmp);

				tmp = rs.getString("soap_style");
				azionePT.setStyle(DriverRegistroServiziDB_LIB.getEnumBindingStyle(((tmp == null || tmp.equals("")) ? null : tmp)));

				tmp = rs.getString("soap_action");
				azionePT.setSoapAction(((tmp == null || tmp.equals("")) ? null : tmp));

				azionePT.setIdPortType(pt.getId());

				long idAzionePortType = rs.getLong("id");
				azionePT.setId(idAzionePortType);

				// Aggiungo messages nell'azione del port type
				this.readMessagesAzioniPortTypes(azionePT,con);

				String msgInput = rs.getString("soap_use_msg_input");
				if(azionePT.getMessageInput()!=null || msgInput!=null){
					if(azionePT.getMessageInput()==null){
						azionePT.setMessageInput(new Message());
					}
					azionePT.getMessageInput().setUse(DriverRegistroServiziDB_LIB.getEnumBindingUse(((msgInput == null || msgInput.equals("")) ? null : msgInput)));
					tmp = rs.getString("soap_namespace_msg_input");
					azionePT.getMessageInput().setSoapNamespace(((tmp == null || tmp.equals("")) ? null : tmp));
				}

				String msgOutput = rs.getString("soap_use_msg_output");
				if(azionePT.getMessageOutput()!=null || msgOutput!=null){
					if(azionePT.getMessageOutput()==null){
						azionePT.setMessageOutput(new Message());
					}
					 
					azionePT.getMessageOutput().setUse(DriverRegistroServiziDB_LIB.getEnumBindingUse(((msgOutput == null || msgOutput.equals("")) ? null : msgOutput)));
					tmp = rs.getString("soap_namespace_msg_output");
					azionePT.getMessageOutput().setSoapNamespace(((tmp == null || tmp.equals("")) ? null : tmp));
				}

				// Protocol Properties
				try{
					List<ProtocolProperty> listPP = DriverRegistroServiziDB_LIB.getListaProtocolProperty(idAzionePortType, ProprietariProtocolProperty.OPERATION, con, this.driver.tipoDB);
					if(listPP!=null && listPP.size()>0){
						for (ProtocolProperty protocolProperty : listPP) {
							azionePT.addProtocolProperty(protocolProperty);
						}
					}
				}catch(DriverRegistroServiziNotFound dNotFound){}
				
				pt.addAzione(azionePT);

			}
			rs.close();
			stm.close();

		}catch (DriverRegistroServiziNotFound e) {
			throw e;
		}catch (Exception se) {
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::readAzioniPortTypes] Exception :" + se.getMessage(),se);
		} finally {

			JDBCUtilities.closeResources(rs, stm);

			this.driver.closeConnection(conParam, con);
		}
	}

	private void readMessagesAzioniPortTypes(Operation azionePT,Connection conParam) throws DriverRegistroServiziException,DriverRegistroServiziNotFound{
		// Aggiungo port type

		Connection con = null;
		PreparedStatement stm = null;
		ResultSet rs = null;
		String sqlQuery = null;

		try {
			this.driver.log.debug("operazione atomica = " + this.driver.atomica);
			// prendo la connessione dal pool
			if(conParam!=null)
				con = conParam;
			else if (this.driver.atomica)
				con = this.driver.getConnectionFromDatasource("readMessagesAzioniPortTypes");
			else
				con = this.driver.globalConnection;

			if(azionePT.getId()==null || azionePT.getId()<=0)
				throw new Exception("Port Type azione id non definito");

			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.PORT_TYPE_AZIONI_OPERATION_MESSAGES);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition("id_port_type_azione = ?");
			sqlQueryObject.setANDLogicOperator(true);
			sqlQuery = sqlQueryObject.createSQLQuery();
			stm = con.prepareStatement(sqlQuery);
			stm.setLong(1, azionePT.getId());

			this.driver.log.debug("eseguo query : " + DriverRegistroServiziDB_LIB.formatSQLString(sqlQuery, azionePT.getId()));

			rs = stm.executeQuery();

			org.openspcoop2.core.registry.Message messageInputPart = null;
			org.openspcoop2.core.registry.Message messageOutputPart = null;
			while (rs.next()) {

				boolean isInputMessage = false;
				if(rs.getInt("input_message")==1)
					isInputMessage = true;

				MessagePart part = new MessagePart();

				String name = rs.getString("name");
				name = ((name == null || name.equals("")) ? null : name);
				part.setName(name);

				String elementName = rs.getString("element_name");
				elementName = ((elementName == null || elementName.equals("")) ? null : elementName);
				part.setElementName(elementName);

				String elementNamespace = rs.getString("element_namespace");
				elementNamespace = ((elementNamespace == null || elementNamespace.equals("")) ? null : elementNamespace);
				part.setElementNamespace(elementNamespace);

				String typeName = rs.getString("type_name");
				typeName = ((typeName == null || typeName.equals("")) ? null : typeName);
				part.setTypeName(typeName);

				String typeNamespace = rs.getString("type_namespace");
				typeNamespace = ((typeNamespace == null || typeNamespace.equals("")) ? null : typeNamespace);
				part.setTypeNamespace(typeNamespace);

				long idMessage = rs.getLong("id");
				part.setId(idMessage);

				if(isInputMessage){

					if(messageInputPart==null)
						messageInputPart = new org.openspcoop2.core.registry.Message();
					messageInputPart.addPart(part);

				}else{

					if(messageOutputPart==null)
						messageOutputPart = new org.openspcoop2.core.registry.Message();
					messageOutputPart.addPart(part);

				}

			}

			if(messageInputPart!=null){
				azionePT.setMessageInput(messageInputPart);
			}
			if(messageOutputPart!=null){
				azionePT.setMessageOutput(messageOutputPart);
			}

			rs.close();
			stm.close();

		}catch (DriverRegistroServiziNotFound e) {
			throw e;
		}catch (Exception se) {
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::readMessagesAzioniPortTypes] Exception :" + se.getMessage(),se);
		} finally {

			JDBCUtilities.closeResources(rs, stm);

			this.driver.closeConnection(conParam, con);

		}
	}
	
	
	
	protected PortType getPortType(long id) throws DriverRegistroServiziException {
		String nomeMetodo = "getPortType(long)";

		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String queryString;

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("getPortType");

			} catch (Exception e) {
				throw new DriverRegistroServiziException("[DriverRegistroServiziDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(), e);

			}

		} else {
			con = this.driver.globalConnection;
		}

		this.driver.log.debug("operazione this.driver.atomica = " + this.driver.atomica);

		try {

			// Controllo che il pdd non sia in uso
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.PORT_TYPE);
			sqlQueryObject.setANDLogicOperator(true);
			sqlQueryObject.addWhereCondition(CostantiDB.PORT_TYPE+".id=?");
			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, id);

			rs = stmt.executeQuery();
			PortType pt = null;
			if (rs.next()) {
				pt = new PortType();
				String tmp = rs.getString("nome");
				pt.setNome(((tmp == null || tmp.equals("")) ? null : tmp));

				tmp = rs.getString("descrizione");
				pt.setDescrizione(((tmp == null || tmp.equals("")) ? null : tmp));

				tmp = rs.getString("profilo_collaborazione");
				pt.setProfiloCollaborazione(DriverRegistroServiziDB_LIB.getEnumProfiloCollaborazione(((tmp == null || tmp.equals("")) ? null : tmp)));

				tmp = rs.getString("filtro_duplicati");
				pt.setFiltroDuplicati(DriverRegistroServiziDB_LIB.getEnumStatoFunzionalita(((tmp == null || tmp.equals("")) ? null : tmp)));

				tmp = rs.getString("conferma_ricezione");
				pt.setConfermaRicezione(DriverRegistroServiziDB_LIB.getEnumStatoFunzionalita(((tmp == null || tmp.equals("")) ? null : tmp)));

				tmp = rs.getString("identificativo_collaborazione");
				pt.setIdCollaborazione(DriverRegistroServiziDB_LIB.getEnumStatoFunzionalita(((tmp == null || tmp.equals("")) ? null : tmp)));
				
				tmp = rs.getString("id_riferimento_richiesta");
				pt.setIdRiferimentoRichiesta(DriverRegistroServiziDB_LIB.getEnumStatoFunzionalita(((tmp == null || tmp.equals("")) ? null : tmp)));

				tmp = rs.getString("consegna_in_ordine");
				pt.setConsegnaInOrdine(DriverRegistroServiziDB_LIB.getEnumStatoFunzionalita(((tmp == null || tmp.equals("")) ? null : tmp)));

				tmp = rs.getString("scadenza");
				pt.setScadenza(((tmp == null || tmp.equals("")) ? null : tmp));

				tmp = rs.getString("profilo_pt");
				if (tmp == null || tmp.equals(""))
					pt.setProfiloPT(CostantiRegistroServizi.PROFILO_AZIONE_DEFAULT);
				else
					pt.setProfiloPT(tmp);

				tmp = rs.getString("soap_style");
				pt.setStyle(DriverRegistroServiziDB_LIB.getEnumBindingStyle(((tmp == null || tmp.equals("")) ? null : tmp)));

				pt.setIdAccordo(rs.getLong("id_accordo"));

				long idPortType = rs.getLong("id");
				pt.setId(idPortType);

				// Aggiungo azioni a port type
				this.readAzioniPortTypes(pt,con);
			}

			return pt;

		} catch (Exception se) {
			throw new DriverRegistroServiziException("[DriverRegistroServiziException::" + nomeMetodo + "] Exception: " + se.getMessage(),se);
		} finally {
			// Chiudo statement and resultset
			JDBCUtilities.closeResources(rs, stmt);
			
			this.driver.closeConnection(con);
		}
	}
	
	
	protected List<PortType> accordiPorttypeCompatibiliList(long idAccordo,boolean isErogazione, ISearch ricerca) throws DriverRegistroServiziException {
		String nomeMetodo = "accordiPorttypeCompatibiliList";
		int idLista = Liste.ACCORDI_PORTTYPE;
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

		ArrayList<PortType> lista = new ArrayList<PortType>();

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("accordiPorttypeCompatibiliList");

			} catch (Exception e) {
				throw new DriverRegistroServiziException("[DriverRegistroServiziDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.log.debug("operazione this.driver.atomica = " + this.driver.atomica);

		try {

			ISQLQueryObject sqlquery = DriverRegistroServiziDB_accordiLIB.getSQLRicercaServiziValidiByIdAccordo(isErogazione);
			sqlquery.setANDLogicOperator(true);
			if (!search.equals("")) {
				//query con search
				sqlquery.addSelectCountField("*", "cont");
				sqlquery.addWhereLikeCondition("nome", search, true, true);
				sqlquery.setANDLogicOperator(true);
				queryString = sqlquery.createSQLQuery();
			} else {
				sqlquery.addSelectCountField("*", "cont");
				queryString = sqlquery.createSQLQuery();
			}
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1,idAccordo);
			risultato = stmt.executeQuery();
			if (risultato.next())
				ricerca.setNumEntries(idLista,risultato.getInt("cont"));
			risultato.close();
			stmt.close();


			//resetto query
			sqlquery = DriverRegistroServiziDB_accordiLIB.getSQLRicercaServiziValidiByIdAccordo(isErogazione);
			sqlquery.setANDLogicOperator(true);
			// ricavo le entries
			if (limit == 0) // con limit
				limit = ISQLQueryObject.LIMIT_DEFAULT_VALUE;
			if (!search.equals("")) { // con search

				sqlquery.addSelectField("id_accordo");
				sqlquery.addSelectField("nome");
				sqlquery.addSelectField("descrizione");
				sqlquery.addSelectField("profilo_collaborazione");
				sqlquery.addSelectField("filtro_duplicati");
				sqlquery.addSelectField("conferma_ricezione");
				sqlquery.addSelectField("identificativo_collaborazione");
				sqlquery.addSelectField("id_riferimento_richiesta");
				sqlquery.addSelectField("consegna_in_ordine");
				sqlquery.addSelectField("scadenza");
				sqlquery.addSelectField("profilo_pt");
				sqlquery.addSelectField("soap_style");
				sqlquery.addSelectField("id");

				sqlquery.addWhereLikeCondition("nome", search, true, true);
				sqlquery.setANDLogicOperator(true);
				sqlquery.addOrderBy("nome");
				sqlquery.setSortType(true);
				sqlquery.setLimit(limit);
				sqlquery.setOffset(offset);
				queryString = sqlquery.createSQLQuery();
			} else {
				// senza search
				sqlquery.addSelectField("id_accordo");
				sqlquery.addSelectField("nome");
				sqlquery.addSelectField("descrizione");
				sqlquery.addSelectField("profilo_collaborazione");
				sqlquery.addSelectField("filtro_duplicati");
				sqlquery.addSelectField("conferma_ricezione");
				sqlquery.addSelectField("identificativo_collaborazione");
				sqlquery.addSelectField("id_riferimento_richiesta");
				sqlquery.addSelectField("consegna_in_ordine");
				sqlquery.addSelectField("scadenza");
				sqlquery.addSelectField("profilo_pt");
				sqlquery.addSelectField("soap_style");
				sqlquery.addSelectField("id");

				sqlquery.addOrderBy("nome");
				sqlquery.setSortType(true);
				sqlquery.setLimit(limit);
				sqlquery.setOffset(offset);
				queryString = sqlquery.createSQLQuery();
			}
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idAccordo);
			risultato = stmt.executeQuery();

			PortType pt;
			while (risultato.next()) {
				pt = new PortType();

				String tmp = risultato.getString("nome");
				pt.setNome(((tmp == null || tmp.equals("")) ? null : tmp));
				tmp = risultato.getString("descrizione");
				pt.setDescrizione(((tmp == null || tmp.equals("")) ? null : tmp));
				tmp = risultato.getString("profilo_collaborazione");
				pt.setProfiloCollaborazione(DriverRegistroServiziDB_LIB.getEnumProfiloCollaborazione(((tmp == null || tmp.equals("")) ? null : tmp)));
				tmp = risultato.getString("filtro_duplicati");
				pt.setFiltroDuplicati(DriverRegistroServiziDB_LIB.getEnumStatoFunzionalita(((tmp == null || tmp.equals("")) ? null : tmp)));
				tmp = risultato.getString("conferma_ricezione");
				pt.setConfermaRicezione(DriverRegistroServiziDB_LIB.getEnumStatoFunzionalita(((tmp == null || tmp.equals("")) ? null : tmp)));
				tmp = risultato.getString("identificativo_collaborazione");
				pt.setIdCollaborazione(DriverRegistroServiziDB_LIB.getEnumStatoFunzionalita(((tmp == null || tmp.equals("")) ? null : tmp)));
				tmp = risultato.getString("id_riferimento_richiesta");
				pt.setIdRiferimentoRichiesta(DriverRegistroServiziDB_LIB.getEnumStatoFunzionalita(((tmp == null || tmp.equals("")) ? null : tmp)));
				tmp = risultato.getString("consegna_in_ordine");
				pt.setConsegnaInOrdine(DriverRegistroServiziDB_LIB.getEnumStatoFunzionalita(((tmp == null || tmp.equals("")) ? null : tmp)));
				tmp = risultato.getString("scadenza");
				pt.setScadenza(((tmp == null || tmp.equals("")) ? null : tmp));
				tmp = risultato.getString("profilo_pt");
				if (tmp == null || tmp.equals(""))
					pt.setProfiloPT(CostantiRegistroServizi.PROFILO_AZIONE_DEFAULT);
				else
					pt.setProfiloPT(tmp);
				tmp = risultato.getString("soap_style");
				pt.setStyle(DriverRegistroServiziDB_LIB.getEnumBindingStyle(((tmp == null || tmp.equals("")) ? null : tmp)));
				pt.setIdAccordo(risultato.getLong("id_accordo"));
				long idPortType = risultato.getLong("id");
				pt.setId(idPortType);

				this.readAzioniPortTypes(pt, con);

				lista.add(pt);				
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
	
	
	protected void updatePortType(org.openspcoop2.core.registry.PortType portType) throws DriverRegistroServiziException {

		if (portType == null)
			throw new DriverRegistroServiziException("Il port-type non puo' essere null.");

		String nome = portType.getNome();
		if (nome == null || nome.equals(""))
			throw new DriverRegistroServiziException("Il nome del port-type non e' valido.");

		Connection connection = null;
		PreparedStatement stm = null, updateStmt = null;
		ResultSet rs =null;
		//String sqlQuery = ""; // updateQuery = "";

		boolean error = false;

		if (this.driver.atomica) {
			try {
				connection = this.driver.getConnectionFromDatasource("updatePortType");
				connection.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverRegistroServiziException("DriverRegistroServiziDB::updatePortType] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			connection = this.driver.globalConnection;

		this.driver.log.debug("operazione atomica = " + this.driver.atomica);

		try {

			// NOTA: questo metodo viene chiamato solo quando si vogliono aggiornare le azioni

			//Prendo l'accordo per gestire il caso di "usa profilo accordo"
			AccordoServizioParteComune as = this.driver.getAccordoServizioParteComune(portType.getIdAccordo(), connection);

			if(portType.getId()==null || portType.getId()<=0){
				for (PortType ptCheck : as.getPortTypeList()) {
					if(ptCheck.getNome().equals(portType.getNome())){
						portType.setId(ptCheck.getId());
					}
				}
			}
			if(portType.getId()==null || portType.getId()<=0){
				throw new Exception("Id PortType undefined");
			}
			
			//TODO possibile ottimizzazione
			//la lista contiene tutte e sole le azioni necessarie
			//prima cancello le azioni e poi reinserisco quelle nuove
//			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
//			sqlQueryObject.addDeleteTable(CostantiDB.PORT_TYPE_AZIONI);
//			sqlQueryObject.addWhereCondition("id_port_type=?");
//			sqlQuery = sqlQueryObject.createSQLDelete();
//			stm=connection.prepareStatement(sqlQuery);
//			stm.setLong(1, portType.getId());
//			int n=stm.executeUpdate();
//			stm.close();
			
			List<Long> idsOperation = new ArrayList<Long>();
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.PORT_TYPE_AZIONI);
			sqlQueryObject.addWhereCondition("id_port_type=?");
			String queryString = sqlQueryObject.createSQLQuery();
			stm = connection.prepareStatement(queryString);
			stm.setLong(1, portType.getId());
			rs=stm.executeQuery();
			while(rs.next()){
				idsOperation.add(rs.getLong("id"));
			}
			rs.close();
			stm.close();
			
			if(idsOperation!=null && idsOperation.size()>0){
				for (Long id : idsOperation) {
					ISQLQueryObject sqlQueryObjectMessages = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
					sqlQueryObjectMessages.addDeleteTable(CostantiDB.PORT_TYPE_AZIONI_OPERATION_MESSAGES);
					sqlQueryObjectMessages.addWhereCondition("id_port_type_azione=?");
					String updateString = sqlQueryObjectMessages.createSQLDelete();
					stm = connection.prepareStatement(updateString);
					stm.setLong(1, id);
					int n=stm.executeUpdate();
					stm.close();
					this.driver.log.debug("Cancellate "+n+" operation messages associate all'azione con id "+id+" del port type "+portType.getNome()+ " dell'accordo: "+as.getNome());
				}
			}
			
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addDeleteTable(CostantiDB.PORT_TYPE_AZIONI);
			sqlQueryObject.addWhereCondition("id_port_type=?");
			String updateString = sqlQueryObject.createSQLDelete();
			stm = connection.prepareStatement(updateString);
			stm.setLong(1, portType.getId());
			int n=stm.executeUpdate();
			stm.close();
			this.driver.log.debug("Cancellate "+n+" azioni associate al portType "+portType.getNome()+ " dell'accordo: "+as.getNome());
			
//			Operation azione = null;
			for (int i = 0; i < portType.sizeAzioneList(); i++) {
				Operation azione = portType.getAzione(i);			
				DriverRegistroServiziDB_accordiSoapLIB.CRUDAzionePortType(CostantiDB.CREATE,as,portType,azione, connection, portType.getId());
			}
			this.driver.log.debug("inserite " + portType.sizeAzioneList() + " azioni relative al port type["+portType.getNome()+"] id-porttype["+portType.getId()+"]");
		} 
//		catch (SQLException se) {
//			this.driver.log.error(se);
//			error = true;
//			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::updatePortType] SQLException [" + se.getMessage() + "].",se);
//		} 
		catch (Exception se) {
			this.driver.log.error(se.getMessage(),se);
			error = true;
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::updatePortType] Exception [" + se.getMessage() + "].",se);
		}finally {
			JDBCUtilities.closeResources(rs, stm);
			JDBCUtilities.closeResources(updateStmt);
			this.driver.closeConnection(error,connection);
		}

	}
	
	protected List<Azione> accordiAzioniList(long idAccordo, ISearch ricerca) throws DriverRegistroServiziException{
		String nomeMetodo = "accordiAzioniList";
		int idLista = Liste.ACCORDI_AZIONI;
		int offset;
		int limit;
		String search;
		String queryString;

		limit = ricerca.getPageSize(idLista);
		offset = ricerca.getIndexIniziale(idLista);
		search = (org.openspcoop2.core.constants.Costanti.SESSION_ATTRIBUTE_VALUE_RICERCA_UNDEFINED.equals(ricerca.getSearchString(idLista)) ? "" : ricerca.getSearchString(idLista));
		ricerca.getSearchString(idLista);

		Connection con = null;
		boolean error = false;
		PreparedStatement stmt = null;
		ResultSet risultato = null;
		ArrayList<Azione> lista = new ArrayList<Azione>();

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("accordiAzioniList");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverRegistroServiziException("[DriverRegistroServiziDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.log.debug("operazione this.driver.atomica = " + this.driver.atomica);

		try {

			// ricavo il numero di entries totale
			if (!search.equals("")) {
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.ACCORDI_AZIONI);
				sqlQueryObject.addSelectCountField("*", "cont");
				sqlQueryObject.addWhereCondition("id_accordo = ?");
				sqlQueryObject.addWhereLikeCondition("nome", search, true, true);
				sqlQueryObject.setANDLogicOperator(true);
				queryString = sqlQueryObject.createSQLQuery();
			} else {
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.ACCORDI_AZIONI);
				sqlQueryObject.addSelectCountField("*", "cont");
				sqlQueryObject.addWhereCondition("id_accordo = ?");
				queryString = sqlQueryObject.createSQLQuery();
			}
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idAccordo);
			risultato = stmt.executeQuery();
			if (risultato.next())
				ricerca.setNumEntries(idLista,risultato.getInt(1));
			risultato.close();
			stmt.close();

			// ricavo le entries
			if (limit == 0) // con limit
				limit = ISQLQueryObject.LIMIT_DEFAULT_VALUE;
			if (!search.equals("")) { // con search
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.ACCORDI_AZIONI);
				sqlQueryObject.addSelectAliasField("id","idAzione");
				sqlQueryObject.addSelectField("nome");
				sqlQueryObject.addSelectField("conferma_ricezione");
				sqlQueryObject.addSelectField("consegna_in_ordine");
				sqlQueryObject.addSelectField("filtro_duplicati");
				sqlQueryObject.addSelectField("identificativo_collaborazione");
				sqlQueryObject.addSelectField("id_riferimento_richiesta");
				sqlQueryObject.addSelectField("scadenza");
				sqlQueryObject.addSelectField("profilo_collaborazione");
				sqlQueryObject.addSelectField("profilo_azione");
				sqlQueryObject.addSelectField("id_accordo");
				sqlQueryObject.addWhereCondition("id_accordo = ?");
				sqlQueryObject.addWhereLikeCondition("nome", search, true, true);
				sqlQueryObject.setANDLogicOperator(true);
				sqlQueryObject.addOrderBy("nome");
				sqlQueryObject.setSortType(true);
				sqlQueryObject.setLimit(limit);
				sqlQueryObject.setOffset(offset);
				queryString = sqlQueryObject.createSQLQuery();
			} else {
				// senza search
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.ACCORDI_AZIONI);
				sqlQueryObject.addSelectAliasField("id","idAzione");
				sqlQueryObject.addSelectField("nome");
				sqlQueryObject.addSelectField("conferma_ricezione");
				sqlQueryObject.addSelectField("consegna_in_ordine");
				sqlQueryObject.addSelectField("filtro_duplicati");
				sqlQueryObject.addSelectField("identificativo_collaborazione");
				sqlQueryObject.addSelectField("id_riferimento_richiesta");
				sqlQueryObject.addSelectField("scadenza");
				sqlQueryObject.addSelectField("profilo_collaborazione");
				sqlQueryObject.addSelectField("profilo_azione");
				sqlQueryObject.addSelectField("id_accordo");
				sqlQueryObject.addWhereCondition("id_accordo = ?");
				sqlQueryObject.addOrderBy("nome");
				sqlQueryObject.setSortType(true);
				sqlQueryObject.setLimit(limit);
				sqlQueryObject.setOffset(offset);
				queryString = sqlQueryObject.createSQLQuery();
			}

			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idAccordo);
			risultato = stmt.executeQuery();

			Azione az;
			while (risultato.next()) {

				az = new Azione();

				//az.setId(risultato.getLong("id_accordo"));  nn c'e' un id per questo oggetto

				//fix by stefano: Aggiunto campo idAzione alla query in modo da leggere l'id dell'oggetto azione
				az.setId(risultato.getLong("idAzione"));
				az.setNome(risultato.getString("nome"));
				az.setConfermaRicezione(DriverRegistroServiziDB_LIB.getEnumStatoFunzionalita(risultato.getString("conferma_ricezione")));
				az.setConsegnaInOrdine(DriverRegistroServiziDB_LIB.getEnumStatoFunzionalita(risultato.getString("consegna_in_ordine")));
				az.setFiltroDuplicati(DriverRegistroServiziDB_LIB.getEnumStatoFunzionalita(risultato.getString("filtro_duplicati")));
				az.setIdCollaborazione(DriverRegistroServiziDB_LIB.getEnumStatoFunzionalita(risultato.getString("identificativo_collaborazione")));
				az.setIdRiferimentoRichiesta(DriverRegistroServiziDB_LIB.getEnumStatoFunzionalita(risultato.getString("id_riferimento_richiesta")));
				az.setScadenza(risultato.getString("scadenza"));
				az.setProfiloCollaborazione(DriverRegistroServiziDB_LIB.getEnumProfiloCollaborazione(risultato.getString("profilo_collaborazione")));
				az.setProfAzione(risultato.getString("profilo_azione"));
				lista.add(az);
			}

			return lista;

		} catch (Exception qe) {
			error = true;
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::" + nomeMetodo + "] Errore : " + qe.getMessage(),qe);
		} finally {

			//Chiudo statement and resultset
			JDBCUtilities.closeResources(risultato, stmt);

			this.driver.closeConnection(error,con);
		}
	}

	/**
	 * Recupera la lista di azioni dell'Accordo con id idAccordo, con il profilo di collaborazione specificato
	 * Il profilo di collaborazione viene ricercato nell'accordo di servizio se l'azione ha profilo di defaul
	 * altrimenti viene controllato il profilo di collaborazione dell'azione 
	 * @param idAccordo
	 * @param profiloCollaborazione Opzionale
	 * @param ricerca
	 * @return Lista di {@link Azione} dell'accordo idAccordo con profilo di collaborazione 'profiloCollaborazione' se specificato altrimenti tutte.
	 * @throws DriverRegistroServiziException
	 */
	protected List<Azione> accordiAzioniList(long idAccordo,String profiloCollaborazione, ISearch ricerca) throws DriverRegistroServiziException {
		String nomeMetodo = "accordiAzioniList";
		int idLista = Liste.ACCORDI_AZIONI;
		int offset;
		int limit;
		String search;
		String queryString;

		limit = ricerca.getPageSize(idLista);
		offset = ricerca.getIndexIniziale(idLista);
		search = (org.openspcoop2.core.constants.Costanti.SESSION_ATTRIBUTE_VALUE_RICERCA_UNDEFINED.equals(ricerca.getSearchString(idLista)) ? "" : ricerca.getSearchString(idLista));
		ricerca.getSearchString(idLista);

		Connection con = null;
		boolean error = false;
		PreparedStatement stmt = null;
		ResultSet risultato = null;
		ArrayList<Azione> lista = new ArrayList<Azione>();

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("accordiAzioniList");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverRegistroServiziException("[DriverRegistroServiziDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.log.debug("operazione this.driver.atomica = " + this.driver.atomica);

		try {

			/**
			 * SELECT a.id as idAccordo,aa.id,aa.nome 
			 * from accordi as a, accordi_azioni as aa 
			 * where a.id=aa.id_accordo 
			 * and( (aa.profilo_azione='ridefinito' and aa.profilo_collaborazione='asincronoAsimmetrico') or a.profilo_collaborazione='asincronoAsimmetrico') 
			 * and aa.id_accordo=12;
			 */
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.ACCORDI);
			sqlQueryObject.addFromTable(CostantiDB.ACCORDI_AZIONI);
			sqlQueryObject.addSelectCountField(CostantiDB.ACCORDI_AZIONI+".id", "cont");
			sqlQueryObject.addWhereCondition(CostantiDB.ACCORDI_AZIONI+".id_accordo = "+CostantiDB.ACCORDI+".id");
			sqlQueryObject.addWhereCondition(CostantiDB.ACCORDI_AZIONI+".id_accordo = ?");
			//(aa.profilo_azione='ridefinito' and aa.profilo_collaborazione='asincronoAsimmetrico') or a.profilo_collaborazione='asincronoAsimmetrico')
			sqlQueryObject.addWhereCondition(false, 
					CostantiDB.ACCORDI_AZIONI+".profilo_collaborazione = ? AND "+CostantiDB.ACCORDI_AZIONI+".profilo_azione= ?",
					CostantiDB.ACCORDI+".profilo_collaborazione=?");
			sqlQueryObject.setANDLogicOperator(true);

			if (!search.equals("")) {
				sqlQueryObject.addWhereLikeCondition("nome", search, true, true);
			}

			queryString = sqlQueryObject.createSQLQuery();

			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idAccordo);
			stmt.setString(2, profiloCollaborazione);
			stmt.setString(3, CostantiRegistroServizi.PROFILO_AZIONE_RIDEFINITO);
			stmt.setString(4, profiloCollaborazione);

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
			sqlQueryObject.addFromTable(CostantiDB.ACCORDI_AZIONI);

			sqlQueryObject.addSelectField(CostantiDB.ACCORDI_AZIONI,"nome");
			sqlQueryObject.addSelectField(CostantiDB.ACCORDI_AZIONI,"conferma_ricezione");
			sqlQueryObject.addSelectField(CostantiDB.ACCORDI_AZIONI,"consegna_in_ordine");
			sqlQueryObject.addSelectField(CostantiDB.ACCORDI_AZIONI,"filtro_duplicati");
			sqlQueryObject.addSelectField(CostantiDB.ACCORDI_AZIONI,"identificativo_collaborazione");
			sqlQueryObject.addSelectField(CostantiDB.ACCORDI_AZIONI,"id_riferimento_richiesta");
			sqlQueryObject.addSelectField(CostantiDB.ACCORDI_AZIONI,"scadenza");
			sqlQueryObject.addSelectField(CostantiDB.ACCORDI_AZIONI,"profilo_collaborazione");
			sqlQueryObject.addSelectField(CostantiDB.ACCORDI_AZIONI,"profilo_azione");
			sqlQueryObject.addSelectField(CostantiDB.ACCORDI_AZIONI,"id_accordo");
			sqlQueryObject.addSelectField(CostantiDB.ACCORDI_AZIONI,"correlata");
			//where
			sqlQueryObject.addWhereCondition(CostantiDB.ACCORDI_AZIONI+".id_accordo = "+CostantiDB.ACCORDI+".id");
			sqlQueryObject.addWhereCondition(CostantiDB.ACCORDI_AZIONI+".id_accordo = ?");
			//(aa.profilo_azione='ridefinito' and aa.profilo_collaborazione='asincronoAsimmetrico') or a.profilo_collaborazione='asincronoAsimmetrico')
			sqlQueryObject.addWhereCondition(false, 
					CostantiDB.ACCORDI_AZIONI+".profilo_collaborazione = ? AND "+CostantiDB.ACCORDI_AZIONI+".profilo_azione= ?",
					CostantiDB.ACCORDI+".profilo_collaborazione=?");

			sqlQueryObject.setANDLogicOperator(true);
			sqlQueryObject.setLimit(limit);
			sqlQueryObject.setOffset(offset);

			sqlQueryObject.addOrderBy(CostantiDB.ACCORDI_AZIONI+".nome");

			if (!search.equals("")) {
				sqlQueryObject.addWhereLikeCondition("nome", search, true, true);
			}

			queryString = sqlQueryObject.createSQLQuery();


			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idAccordo);
			stmt.setString(2, profiloCollaborazione);
			stmt.setString(3, CostantiRegistroServizi.PROFILO_AZIONE_RIDEFINITO);
			stmt.setString(4, profiloCollaborazione);

			risultato = stmt.executeQuery();

			Azione az;
			while (risultato.next()) {

				az = new Azione();

				//az.setId(risultato.getLong("id_accordo"));  nn c'e' un id per questo oggetto

				az.setNome(risultato.getString("nome"));
				az.setConfermaRicezione(DriverRegistroServiziDB_LIB.getEnumStatoFunzionalita(risultato.getString("conferma_ricezione")));
				az.setConsegnaInOrdine(DriverRegistroServiziDB_LIB.getEnumStatoFunzionalita(risultato.getString("consegna_in_ordine")));
				az.setFiltroDuplicati(DriverRegistroServiziDB_LIB.getEnumStatoFunzionalita(risultato.getString("filtro_duplicati")));
				az.setIdCollaborazione(DriverRegistroServiziDB_LIB.getEnumStatoFunzionalita(risultato.getString("identificativo_collaborazione")));
				az.setIdRiferimentoRichiesta(DriverRegistroServiziDB_LIB.getEnumStatoFunzionalita(risultato.getString("id_riferimento_richiesta")));
				az.setScadenza(risultato.getString("scadenza"));
				az.setProfiloCollaborazione(DriverRegistroServiziDB_LIB.getEnumProfiloCollaborazione(risultato.getString("profilo_collaborazione")));
				az.setProfAzione(risultato.getString("profilo_azione"));
				az.setCorrelata(risultato.getString("correlata"));
				lista.add(az);
			}

			return lista;

		} catch (Exception qe) {
			error = true;
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::" + nomeMetodo + "] Errore : " + qe.getMessage(),qe);
		} finally {

			//Chiudo statement and resultset
			JDBCUtilities.closeResources(risultato, stmt);

			this.driver.closeConnection(error,con);
		}
	}
	
	protected void deleteAzione(long idAccordo, String nomeAzione) throws DriverRegistroServiziException {
		String nomeMetodo = "deleteAzione";
		Connection con = null;
		PreparedStatement stmt = null;

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("deleteAzione");

			} catch (Exception e) {
				throw new DriverRegistroServiziException("[DriverRegistroServiziDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.log.debug("operazione this.driver.atomica = " + this.driver.atomica);

		try {
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addDeleteTable(CostantiDB.ACCORDI_AZIONI);
			sqlQueryObject.addWhereCondition("id_accordo=?");
			sqlQueryObject.addWhereCondition("nome=?");
			sqlQueryObject.setANDLogicOperator(true);
			String updateString = sqlQueryObject.createSQLDelete();
			stmt = con.prepareStatement(updateString);
			stmt.setLong(1, idAccordo);
			stmt.setString(2, nomeAzione);
			stmt.executeUpdate();
			stmt.close();

		} catch (Exception se) {
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::" + nomeMetodo + "] Exception: " + se.getMessage(),se);
		} finally {
			//Chiudo statement and resultset
			JDBCUtilities.closeResources(stmt);
			
			this.driver.closeConnection(con);
		}

	}
	
	protected List<PortType> accordiPorttypeList(long idAccordo, ISearch ricerca) throws DriverRegistroServiziException {
		String nomeMetodo = "accordiPorttypeList";
		int idLista = Liste.ACCORDI_PORTTYPE;
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

		ArrayList<PortType> lista = new ArrayList<PortType>();

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("accordiPorttypeList");

			} catch (Exception e) {
				throw new DriverRegistroServiziException("[DriverRegistroServiziDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.log.debug("operazione this.driver.atomica = " + this.driver.atomica);

		try {

			if (!search.equals("")) {
				//query con search
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORT_TYPE);
				sqlQueryObject.addSelectCountField("*", "cont");
				sqlQueryObject.addWhereCondition("id_accordo = ?");
				sqlQueryObject.addWhereLikeCondition("nome", search, true, true);
				sqlQueryObject.setANDLogicOperator(true);
				queryString = sqlQueryObject.createSQLQuery();
			} else {
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORT_TYPE);
				sqlQueryObject.addSelectCountField("*", "cont");
				sqlQueryObject.addWhereCondition("id_accordo = ?");
				queryString = sqlQueryObject.createSQLQuery();
			}
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1,idAccordo);
			risultato = stmt.executeQuery();
			if (risultato.next())
				ricerca.setNumEntries(idLista,risultato.getInt("cont"));
			risultato.close();
			stmt.close();

			// ricavo le entries
			if (limit == 0) // con limit
				limit = ISQLQueryObject.LIMIT_DEFAULT_VALUE;
			if (!search.equals("")) { // con search
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORT_TYPE);
				sqlQueryObject.addSelectField("id");
				sqlQueryObject.addSelectField("nome");
				sqlQueryObject.addWhereCondition("id_accordo = ?");
				sqlQueryObject.addWhereLikeCondition("nome", search, true, true);
				sqlQueryObject.setANDLogicOperator(true);
				sqlQueryObject.addOrderBy("nome");
				sqlQueryObject.setSortType(true);
				sqlQueryObject.setLimit(limit);
				sqlQueryObject.setOffset(offset);
				queryString = sqlQueryObject.createSQLQuery();
			} else {
				// senza search
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORT_TYPE);
				sqlQueryObject.addSelectField("id");
				sqlQueryObject.addSelectField("nome");
				sqlQueryObject.addWhereCondition("id_accordo = ?");
				sqlQueryObject.addOrderBy("nome");
				sqlQueryObject.setSortType(true);
				sqlQueryObject.setLimit(limit);
				sqlQueryObject.setOffset(offset);
				queryString = sqlQueryObject.createSQLQuery();
			}
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idAccordo);
			risultato = stmt.executeQuery();

			List<Long> listID = new ArrayList<Long>();
			while (risultato.next()) {
				
				long idPortType = risultato.getLong("id");
				listID.add(idPortType);
			}

			if(listID.size()>0) {
				for (Long idPT : listID) {
					lista.add(this.getPortType(idPT));
				}
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

	protected List<PortType> accordiPorttypeList(long idAccordo, String profiloCollaborazione, ISearch ricerca) throws DriverRegistroServiziException {
		String nomeMetodo = "accordiPorttypeList";
		int idLista = Liste.ACCORDI_PORTTYPE;
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

		ArrayList<PortType> lista = new ArrayList<PortType>();

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("accordiPorttypeList");

			} catch (Exception e) {
				throw new DriverRegistroServiziException("[DriverRegistroServiziDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.log.debug("operazione this.driver.atomica = " + this.driver.atomica);

		try {

			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.ACCORDI);
			sqlQueryObject.addFromTable(CostantiDB.PORT_TYPE);
			sqlQueryObject.addSelectCountField("*", "cont");
			sqlQueryObject.setANDLogicOperator(true);
			sqlQueryObject.addWhereCondition(CostantiDB.PORT_TYPE+".id_accordo = "+CostantiDB.ACCORDI+".id");
			sqlQueryObject.addWhereCondition(CostantiDB.PORT_TYPE+".id_accordo = ?");
			if(profiloCollaborazione!=null){
				sqlQueryObject.addWhereCondition(false,
						CostantiDB.ACCORDI+".profilo_collaborazione=?"
						,CostantiDB.PORT_TYPE+".profilo_collaborazione = ? AND "+CostantiDB.PORT_TYPE+".profilo_pt= ?");
			}
			if (!search.equals("")) {
				//query con search
				sqlQueryObject.addWhereLikeCondition("nome", search, true, true);
			}
			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1,idAccordo);
			if(profiloCollaborazione!=null){
				stmt.setString(2,profiloCollaborazione);
				stmt.setString(3,profiloCollaborazione);
				stmt.setString(4,CostantiRegistroServizi.PROFILO_AZIONE_RIDEFINITO);
			}
			risultato = stmt.executeQuery();
			if (risultato.next())
				ricerca.setNumEntries(idLista,risultato.getInt("cont"));
			risultato.close();
			stmt.close();

			// ricavo le entries
			if (limit == 0) // con limit
				limit = ISQLQueryObject.LIMIT_DEFAULT_VALUE;

			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.ACCORDI);
			sqlQueryObject.addFromTable(CostantiDB.PORT_TYPE);
			sqlQueryObject.addSelectField(CostantiDB.PORT_TYPE, "id_accordo");
			sqlQueryObject.addSelectField(CostantiDB.PORT_TYPE, "nome");
			sqlQueryObject.addSelectField(CostantiDB.PORT_TYPE, "descrizione");
			sqlQueryObject.addSelectField(CostantiDB.PORT_TYPE, "profilo_collaborazione");
			sqlQueryObject.addSelectField(CostantiDB.PORT_TYPE, "filtro_duplicati");
			sqlQueryObject.addSelectField(CostantiDB.PORT_TYPE, "conferma_ricezione");
			sqlQueryObject.addSelectField(CostantiDB.PORT_TYPE, "identificativo_collaborazione");
			sqlQueryObject.addSelectField(CostantiDB.PORT_TYPE, "id_riferimento_richiesta");
			sqlQueryObject.addSelectField(CostantiDB.PORT_TYPE, "consegna_in_ordine");
			sqlQueryObject.addSelectField(CostantiDB.PORT_TYPE, "scadenza");
			sqlQueryObject.addSelectField(CostantiDB.PORT_TYPE, "profilo_pt");
			sqlQueryObject.addSelectField(CostantiDB.PORT_TYPE, "soap_style");
			sqlQueryObject.addSelectField(CostantiDB.PORT_TYPE, "id");
			sqlQueryObject.setANDLogicOperator(true);
			sqlQueryObject.addWhereCondition(CostantiDB.PORT_TYPE+".id_accordo = "+CostantiDB.ACCORDI+".id");
			sqlQueryObject.addWhereCondition(CostantiDB.PORT_TYPE+".id_accordo = ?");
			if(profiloCollaborazione!=null){
				sqlQueryObject.addWhereCondition(false,
						CostantiDB.ACCORDI+".profilo_collaborazione=?"
						,CostantiDB.PORT_TYPE+".profilo_collaborazione = ? AND "+CostantiDB.PORT_TYPE+".profilo_pt= ?");
			}
			if (!search.equals("")) { // con search
				sqlQueryObject.addWhereLikeCondition("nome", search, true, true);
			}
			sqlQueryObject.addOrderBy(CostantiDB.PORT_TYPE+".nome");
			sqlQueryObject.setSortType(true);
			sqlQueryObject.setLimit(limit);
			sqlQueryObject.setOffset(offset);
			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idAccordo);
			if(profiloCollaborazione!=null){
				stmt.setString(2,profiloCollaborazione);
				stmt.setString(3,profiloCollaborazione);
				stmt.setString(4,CostantiRegistroServizi.PROFILO_AZIONE_RIDEFINITO);
			}
			risultato = stmt.executeQuery();

			PortType pt;
			while (risultato.next()) {
				pt = new PortType();

				String tmp = risultato.getString("nome");
				pt.setNome(((tmp == null || tmp.equals("")) ? null : tmp));
				tmp = risultato.getString("descrizione");
				pt.setDescrizione(((tmp == null || tmp.equals("")) ? null : tmp));
				tmp = risultato.getString("profilo_collaborazione");
				pt.setProfiloCollaborazione(DriverRegistroServiziDB_LIB.getEnumProfiloCollaborazione(((tmp == null || tmp.equals("")) ? null : tmp)));
				tmp = risultato.getString("filtro_duplicati");
				pt.setFiltroDuplicati(DriverRegistroServiziDB_LIB.getEnumStatoFunzionalita(((tmp == null || tmp.equals("")) ? null : tmp)));
				tmp = risultato.getString("conferma_ricezione");
				pt.setConfermaRicezione(DriverRegistroServiziDB_LIB.getEnumStatoFunzionalita(((tmp == null || tmp.equals("")) ? null : tmp)));
				tmp = risultato.getString("identificativo_collaborazione");
				pt.setIdCollaborazione(DriverRegistroServiziDB_LIB.getEnumStatoFunzionalita(((tmp == null || tmp.equals("")) ? null : tmp)));
				tmp = risultato.getString("id_riferimento_richiesta");
				pt.setIdRiferimentoRichiesta(DriverRegistroServiziDB_LIB.getEnumStatoFunzionalita(((tmp == null || tmp.equals("")) ? null : tmp)));
				tmp = risultato.getString("consegna_in_ordine");
				pt.setConsegnaInOrdine(DriverRegistroServiziDB_LIB.getEnumStatoFunzionalita(((tmp == null || tmp.equals("")) ? null : tmp)));
				tmp = risultato.getString("scadenza");
				pt.setScadenza(((tmp == null || tmp.equals("")) ? null : tmp));
				tmp = risultato.getString("profilo_pt");
				if (tmp == null || tmp.equals(""))
					pt.setProfiloPT(CostantiRegistroServizi.PROFILO_AZIONE_DEFAULT);
				else
					pt.setProfiloPT(tmp);
				tmp = risultato.getString("soap_style");
				pt.setStyle(DriverRegistroServiziDB_LIB.getEnumBindingStyle(((tmp == null || tmp.equals("")) ? null : tmp)));
				pt.setIdAccordo(risultato.getLong("id_accordo"));
				long idPortType = risultato.getLong("id");
				pt.setId(idPortType);
				lista.add(pt);
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
	
	protected List<Operation> accordiPorttypeOperationList(long idPortType,String profiloCollaborazione, ISearch ricerca) throws DriverRegistroServiziException {
		String nomeMetodo = "accordiPorttypeOperationsList";
		int idLista = Liste.ACCORDI_PORTTYPE_AZIONI;
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

		ArrayList<Operation> lista = new ArrayList<Operation>();

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("accordiPorttypeOperationList");

			} catch (Exception e) {
				throw new DriverRegistroServiziException("[DriverRegistroServiziDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.log.debug("operazione this.driver.atomica = " + this.driver.atomica);

		try {

			/*SELECT a.id,pt.nome,pta.nome 
			 * from accordi a,port_type pt,port_type_azioni pta
				where a.id=pt.id_accordo and pt.id=pta.id_port_type
				and 
				(
					a.profilo_collaborazione='asincronoAsimmetrico'
					OR (pt.profilo_pt='ridefinito' AND pt.profilo_collaborazione='asincronoAsimmetrico')
					OR (pta.profilo_pt_azione='ridefinito' AND pta.profilo_collaborazione='asincronoAsimmetrico')
				));
			 */	
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.ACCORDI);
			sqlQueryObject.addFromTable(CostantiDB.PORT_TYPE);
			sqlQueryObject.addFromTable(CostantiDB.PORT_TYPE_AZIONI);
			//count
			sqlQueryObject.addSelectCountField(CostantiDB.PORT_TYPE_AZIONI+".id", "cont");
			//condizioni di join
			sqlQueryObject.setANDLogicOperator(true);

			sqlQueryObject.addWhereCondition(CostantiDB.PORT_TYPE+".id_accordo = "+CostantiDB.ACCORDI+".id");
			sqlQueryObject.addWhereCondition(CostantiDB.PORT_TYPE_AZIONI+".id_port_type = "+CostantiDB.PORT_TYPE+".id");
			sqlQueryObject.addWhereCondition(CostantiDB.PORT_TYPE+".id = ?");

			sqlQueryObject.addWhereCondition(false,
					CostantiDB.ACCORDI+".profilo_collaborazione=?"
					,CostantiDB.PORT_TYPE+".profilo_collaborazione = ? AND "+CostantiDB.PORT_TYPE+".profilo_pt= ?"
					,CostantiDB.PORT_TYPE_AZIONI+".profilo_collaborazione = ? AND "+CostantiDB.PORT_TYPE_AZIONI+".profilo_pt_azione= ?");

			if (!search.equals("")) {
				sqlQueryObject.addWhereLikeCondition("nome", search, true, true);
			}

			queryString = sqlQueryObject.createSQLQuery();

			stmt = con.prepareStatement(queryString);
			stmt.setLong(1,idPortType);
			stmt.setString(2,profiloCollaborazione);
			stmt.setString(3,profiloCollaborazione);
			stmt.setString(4,CostantiRegistroServizi.PROFILO_AZIONE_RIDEFINITO);
			stmt.setString(5,profiloCollaborazione);
			stmt.setString(6,CostantiRegistroServizi.PROFILO_AZIONE_RIDEFINITO);

			risultato = stmt.executeQuery();
			if (risultato.next())
				ricerca.setNumEntries(idLista,risultato.getInt("cont"));
			risultato.close();
			stmt.close();

			// ricavo le entries
			if (limit == 0) // con limit
				limit = ISQLQueryObject.LIMIT_DEFAULT_VALUE;


			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.ACCORDI);
			sqlQueryObject.addFromTable(CostantiDB.PORT_TYPE);
			sqlQueryObject.addFromTable(CostantiDB.PORT_TYPE_AZIONI);
			//select
			sqlQueryObject.addSelectField(CostantiDB.PORT_TYPE_AZIONI,"id_port_type");
			sqlQueryObject.addSelectAliasField(CostantiDB.PORT_TYPE_AZIONI,"nome","nomePTAz");
			sqlQueryObject.addSelectAliasField(CostantiDB.PORT_TYPE_AZIONI,"profilo_collaborazione","profCollPTAz");
			sqlQueryObject.addSelectAliasField(CostantiDB.PORT_TYPE_AZIONI,"filtro_duplicati","filtro_duplicatiPTAz");
			sqlQueryObject.addSelectAliasField(CostantiDB.PORT_TYPE_AZIONI,"conferma_ricezione","conferma_ricezionePTAz");
			sqlQueryObject.addSelectAliasField(CostantiDB.PORT_TYPE_AZIONI,"identificativo_collaborazione","idCollPTAz");
			sqlQueryObject.addSelectAliasField(CostantiDB.PORT_TYPE_AZIONI,"id_riferimento_richiesta","idRifRichiestaPTAz");
			sqlQueryObject.addSelectAliasField(CostantiDB.PORT_TYPE_AZIONI,"consegna_in_ordine","consegna_in_ordinePTAz");
			sqlQueryObject.addSelectAliasField(CostantiDB.PORT_TYPE_AZIONI,"scadenza","scadenzaPTAz");
			sqlQueryObject.addSelectAliasField(CostantiDB.PORT_TYPE_AZIONI,"correlata","correlataPTAz");
			sqlQueryObject.addSelectAliasField(CostantiDB.PORT_TYPE_AZIONI,"correlata_servizio","correlataServizioPTAz");
			sqlQueryObject.addSelectField(CostantiDB.PORT_TYPE_AZIONI,"profilo_pt_azione");
			sqlQueryObject.addSelectAliasField(CostantiDB.PORT_TYPE_AZIONI,"id","idPTAz");
			//condizioni di join
			sqlQueryObject.setANDLogicOperator(true);

			sqlQueryObject.addWhereCondition(CostantiDB.PORT_TYPE+".id_accordo = "+CostantiDB.ACCORDI+".id");
			sqlQueryObject.addWhereCondition(CostantiDB.PORT_TYPE_AZIONI+".id_port_type = "+CostantiDB.PORT_TYPE+".id");
			sqlQueryObject.addWhereCondition(CostantiDB.PORT_TYPE+".id = ?");

			sqlQueryObject.addWhereCondition(false,
					CostantiDB.ACCORDI+".profilo_collaborazione=?"
					,CostantiDB.PORT_TYPE+".profilo_collaborazione = ? AND "+CostantiDB.PORT_TYPE+".profilo_pt= ?"
					,CostantiDB.PORT_TYPE_AZIONI+".profilo_collaborazione = ? AND "+CostantiDB.PORT_TYPE_AZIONI+".profilo_pt_azione= ?");

			if (!search.equals("")) {
				sqlQueryObject.addWhereLikeCondition("nome", search, true, true);
			}

			sqlQueryObject.addOrderBy(CostantiDB.PORT_TYPE_AZIONI+".nome");
			sqlQueryObject.setSortType(true);
			sqlQueryObject.setLimit(limit);
			sqlQueryObject.setOffset(offset);

			queryString = sqlQueryObject.createSQLQuery();

			this.driver.log.debug("Query: "+queryString);

			stmt = con.prepareStatement(queryString);
			stmt.setLong(1,idPortType);
			stmt.setString(2,profiloCollaborazione);
			stmt.setString(3,profiloCollaborazione);
			stmt.setString(4,CostantiRegistroServizi.PROFILO_AZIONE_RIDEFINITO);
			stmt.setString(5,profiloCollaborazione);
			stmt.setString(6,CostantiRegistroServizi.PROFILO_AZIONE_RIDEFINITO);


			risultato = stmt.executeQuery();
			Operation op;
			while (risultato.next()) {
				op = new Operation();

				String tmp = risultato.getString("nomePTAz");
				op.setNome(((tmp == null || tmp.equals("")) ? null : tmp));
				tmp = risultato.getString("profCollPTAz");
				op.setProfiloCollaborazione(DriverRegistroServiziDB_LIB.getEnumProfiloCollaborazione(((tmp == null || tmp.equals("")) ? null : tmp)));
				tmp = risultato.getString("filtro_duplicatiPTAz");
				op.setFiltroDuplicati(DriverRegistroServiziDB_LIB.getEnumStatoFunzionalita(((tmp == null || tmp.equals("")) ? null : tmp)));
				tmp = risultato.getString("conferma_ricezionePTAz");
				op.setConfermaRicezione(DriverRegistroServiziDB_LIB.getEnumStatoFunzionalita(((tmp == null || tmp.equals("")) ? null : tmp)));
				tmp = risultato.getString("idCollPTAz");
				op.setIdCollaborazione(DriverRegistroServiziDB_LIB.getEnumStatoFunzionalita(((tmp == null || tmp.equals("")) ? null : tmp)));
				tmp = risultato.getString("idRifRichiestaPTAz");
				op.setIdRiferimentoRichiesta(DriverRegistroServiziDB_LIB.getEnumStatoFunzionalita(((tmp == null || tmp.equals("")) ? null : tmp)));
				tmp = risultato.getString("consegna_in_ordinePTAz");
				op.setConsegnaInOrdine(DriverRegistroServiziDB_LIB.getEnumStatoFunzionalita(((tmp == null || tmp.equals("")) ? null : tmp)));
				tmp = risultato.getString("scadenzaPTAz");
				op.setScadenza(((tmp == null || tmp.equals("")) ? null : tmp));
				tmp = risultato.getString("correlataPTAz");
				op.setCorrelata(((tmp == null || tmp.equals("")) ? null : tmp));
				tmp = risultato.getString("correlataServizioPTAz");
				op.setCorrelataServizio(((tmp == null || tmp.equals("")) ? null : tmp));
				tmp = risultato.getString("profilo_pt_azione");
				if (tmp == null || tmp.equals(""))
					op.setProfAzione(CostantiRegistroServizi.PROFILO_AZIONE_DEFAULT);
				else
					op.setProfAzione(tmp);
				op.setIdPortType(risultato.getLong("id_port_type"));
				long idOperation = risultato.getLong("idPTAz");
				op.setId(idOperation);
				lista.add(op);
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


	protected List<Operation> accordiPorttypeOperationList(long idPortType, ISearch ricerca) throws DriverRegistroServiziException {
		String nomeMetodo = "accordiPorttypeOperationsList";
		int idLista = Liste.ACCORDI_PORTTYPE_AZIONI;
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

		ArrayList<Operation> lista = new ArrayList<Operation>();

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("accordiPorttypeOperationList");

			} catch (Exception e) {
				throw new DriverRegistroServiziException("[DriverRegistroServiziDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.log.debug("operazione this.driver.atomica = " + this.driver.atomica);

		try {

			if (!search.equals("")) {
				//query con search
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORT_TYPE_AZIONI);
				sqlQueryObject.addSelectCountField("*", "cont");
				sqlQueryObject.addWhereCondition("id_port_type = ?");
				sqlQueryObject.addWhereLikeCondition("nome", search, true, true);
				sqlQueryObject.setANDLogicOperator(true);
				queryString = sqlQueryObject.createSQLQuery();
			} else {
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORT_TYPE_AZIONI);
				sqlQueryObject.addSelectCountField("*", "cont");
				sqlQueryObject.addWhereCondition("id_port_type = ?");
				queryString = sqlQueryObject.createSQLQuery();
			}
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1,idPortType);
			risultato = stmt.executeQuery();
			if (risultato.next())
				ricerca.setNumEntries(idLista,risultato.getInt("cont"));
			risultato.close();
			stmt.close();

			// ricavo le entries
			if (limit == 0) // con limit
				limit = ISQLQueryObject.LIMIT_DEFAULT_VALUE;
			if (!search.equals("")) { // con search
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORT_TYPE_AZIONI);
				sqlQueryObject.addSelectField("id_port_type");
				sqlQueryObject.addSelectField("nome");
				sqlQueryObject.addSelectField("profilo_collaborazione");
				sqlQueryObject.addSelectField("filtro_duplicati");
				sqlQueryObject.addSelectField("conferma_ricezione");
				sqlQueryObject.addSelectField("identificativo_collaborazione");
				sqlQueryObject.addSelectField("id_riferimento_richiesta");
				sqlQueryObject.addSelectField("consegna_in_ordine");
				sqlQueryObject.addSelectField("scadenza");
				sqlQueryObject.addSelectField("profilo_pt_azione");
				sqlQueryObject.addSelectField("id");
				sqlQueryObject.addWhereCondition("id_port_type = ?");
				sqlQueryObject.addWhereLikeCondition("nome", search, true, true);
				sqlQueryObject.setANDLogicOperator(true);
				sqlQueryObject.addOrderBy("nome");
				sqlQueryObject.setSortType(true);
				sqlQueryObject.setLimit(limit);
				sqlQueryObject.setOffset(offset);
				queryString = sqlQueryObject.createSQLQuery();
			} else {
				// senza search
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORT_TYPE_AZIONI);
				sqlQueryObject.addSelectField("id_port_type");
				sqlQueryObject.addSelectField("nome");
				sqlQueryObject.addSelectField("profilo_collaborazione");
				sqlQueryObject.addSelectField("filtro_duplicati");
				sqlQueryObject.addSelectField("conferma_ricezione");
				sqlQueryObject.addSelectField("identificativo_collaborazione");
				sqlQueryObject.addSelectField("id_riferimento_richiesta");
				sqlQueryObject.addSelectField("consegna_in_ordine");
				sqlQueryObject.addSelectField("scadenza");
				sqlQueryObject.addSelectField("profilo_pt_azione");
				sqlQueryObject.addSelectField("id");
				sqlQueryObject.addWhereCondition("id_port_type = ?");
				sqlQueryObject.addOrderBy("nome");
				sqlQueryObject.setSortType(true);
				sqlQueryObject.setLimit(limit);
				sqlQueryObject.setOffset(offset);
				queryString = sqlQueryObject.createSQLQuery();
			}
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idPortType);
			risultato = stmt.executeQuery();

			Operation op;
			while (risultato.next()) {
				op = new Operation();

				String tmp = risultato.getString("nome");
				op.setNome(((tmp == null || tmp.equals("")) ? null : tmp));
				tmp = risultato.getString("profilo_collaborazione");
				op.setProfiloCollaborazione(DriverRegistroServiziDB_LIB.getEnumProfiloCollaborazione(((tmp == null || tmp.equals("")) ? null : tmp)));
				tmp = risultato.getString("filtro_duplicati");
				op.setFiltroDuplicati(DriverRegistroServiziDB_LIB.getEnumStatoFunzionalita(((tmp == null || tmp.equals("")) ? null : tmp)));
				tmp = risultato.getString("conferma_ricezione");
				op.setConfermaRicezione(DriverRegistroServiziDB_LIB.getEnumStatoFunzionalita(((tmp == null || tmp.equals("")) ? null : tmp)));
				tmp = risultato.getString("identificativo_collaborazione");
				op.setIdCollaborazione(DriverRegistroServiziDB_LIB.getEnumStatoFunzionalita(((tmp == null || tmp.equals("")) ? null : tmp)));
				tmp = risultato.getString("id_riferimento_richiesta");
				op.setIdRiferimentoRichiesta(DriverRegistroServiziDB_LIB.getEnumStatoFunzionalita(((tmp == null || tmp.equals("")) ? null : tmp)));
				tmp = risultato.getString("consegna_in_ordine");
				op.setConsegnaInOrdine(DriverRegistroServiziDB_LIB.getEnumStatoFunzionalita(((tmp == null || tmp.equals("")) ? null : tmp)));
				tmp = risultato.getString("scadenza");
				op.setScadenza(((tmp == null || tmp.equals("")) ? null : tmp));
				tmp = risultato.getString("profilo_pt_azione");
				if (tmp == null || tmp.equals(""))
					op.setProfAzione(CostantiRegistroServizi.PROFILO_AZIONE_DEFAULT);
				else
					op.setProfAzione(tmp);
				op.setIdPortType(risultato.getLong("id_port_type"));
				long idOperation = risultato.getLong("id");
				op.setId(idOperation);
				lista.add(op);
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
	
	protected List<MessagePart> accordiPorttypeOperationMessagePartList(long idOperation, boolean isInput, ISearch ricerca) throws DriverRegistroServiziException {
		String nomeMetodo = "accordiPorttypeOperationMessagePartList";
		int idLista = Liste.ACCORDI_PORTTYPE_AZIONI_MESSAGE_INPUT;
		if(!isInput)
			idLista = Liste.ACCORDI_PORTTYPE_AZIONI_MESSAGE_OUTPUT;
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

		ArrayList<MessagePart> lista = new ArrayList<MessagePart>();

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("accordiPorttypeOperationMessagePartList");

			} catch (Exception e) {
				throw new DriverRegistroServiziException("[DriverRegistroServiziDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.log.debug("operazione this.driver.atomica = " + this.driver.atomica);

		try {

			if (!search.equals("")) {
				//query con search
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORT_TYPE_AZIONI_OPERATION_MESSAGES);
				sqlQueryObject.addSelectCountField("*", "cont");
				sqlQueryObject.addWhereCondition("id_port_type_azione = ?");
				if(isInput)
					sqlQueryObject.addWhereCondition("input_message = 1");
				else
					sqlQueryObject.addWhereCondition("input_message != 1");
				sqlQueryObject.addWhereLikeCondition("name", search, true, true);
				sqlQueryObject.setANDLogicOperator(true);
				queryString = sqlQueryObject.createSQLQuery();
			} else {
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORT_TYPE_AZIONI_OPERATION_MESSAGES);
				sqlQueryObject.addSelectCountField("*", "cont");
				sqlQueryObject.addWhereCondition("id_port_type_azione = ?");
				
				if(isInput)
					sqlQueryObject.addWhereCondition("input_message = 1");
				else
					sqlQueryObject.addWhereCondition("input_message != 1");
				sqlQueryObject.setANDLogicOperator(true);
				queryString = sqlQueryObject.createSQLQuery();
			}
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1,idOperation);
			risultato = stmt.executeQuery();
			if (risultato.next())
				ricerca.setNumEntries(idLista,risultato.getInt("cont"));
			risultato.close();
			stmt.close();

			// ricavo le entries
			if (limit == 0) // con limit
				limit = ISQLQueryObject.LIMIT_DEFAULT_VALUE;
			if (!search.equals("")) { // con search
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORT_TYPE_AZIONI_OPERATION_MESSAGES);
				sqlQueryObject.addSelectField("name");
				sqlQueryObject.addSelectField("element_name");
				sqlQueryObject.addSelectField("element_namespace");
				sqlQueryObject.addSelectField("type_name");
				sqlQueryObject.addSelectField("type_namespace");
				sqlQueryObject.addSelectField("id");
				sqlQueryObject.addWhereCondition("id_port_type_azione = ?");
				if(isInput)
					sqlQueryObject.addWhereCondition("input_message = 1");
				else
					sqlQueryObject.addWhereCondition("input_message != 1");
				sqlQueryObject.addWhereLikeCondition("nome", search, true, true);
				sqlQueryObject.setANDLogicOperator(true);
				sqlQueryObject.addOrderBy("name");
				sqlQueryObject.setSortType(true);
				sqlQueryObject.setLimit(limit);
				sqlQueryObject.setOffset(offset);
				queryString = sqlQueryObject.createSQLQuery();
			} else {
				// senza search
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORT_TYPE_AZIONI_OPERATION_MESSAGES);
				sqlQueryObject.addSelectField("name");
				sqlQueryObject.addSelectField("element_name");
				sqlQueryObject.addSelectField("element_namespace");
				sqlQueryObject.addSelectField("type_name");
				sqlQueryObject.addSelectField("type_namespace");
				sqlQueryObject.addSelectField("id");
				sqlQueryObject.addWhereCondition("id_port_type_azione = ?");
				if(isInput)
					sqlQueryObject.addWhereCondition("input_message = 1");
				else
					sqlQueryObject.addWhereCondition("input_message != 1");
				sqlQueryObject.setANDLogicOperator(true);
				sqlQueryObject.addOrderBy("name");
				sqlQueryObject.setSortType(true);
				sqlQueryObject.setLimit(limit);
				sqlQueryObject.setOffset(offset);
				queryString = sqlQueryObject.createSQLQuery();
			}
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idOperation);
			risultato = stmt.executeQuery();

			
			MessagePart mp;
			
			while (risultato.next()) {
				mp = new MessagePart();

				String tmp = risultato.getString("name");
				mp.setName(((tmp == null || tmp.equals("")) ? null : tmp));
				tmp = risultato.getString("element_name");
				mp.setElementName(((tmp == null || tmp.equals("")) ? null : tmp));
				tmp = risultato.getString("element_namespace");
				mp.setElementNamespace(((tmp == null || tmp.equals("")) ? null : tmp));
				tmp = risultato.getString("type_name");
				mp.setTypeName(((tmp == null || tmp.equals("")) ? null : tmp));
				tmp = risultato.getString("type_namespace");
				mp.setTypeNamespace(((tmp == null || tmp.equals("")) ? null : tmp));
				long idMessage = risultato.getLong("id");
				mp.setId(idMessage);
				lista.add(mp);
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
	
	/**
	 * Controlla se l'azione e' usata come Azione correlata in qualche azione dell'accordo con id idAccordo 
	 * @param idAccordo
	 * @param nomeAzione
	 * @return true se se l'azione e' usata come Azione correlata in qualche azione dell'accordo con id idAccordo 
	 * @throws DriverRegistroServiziException
	 */
	protected boolean isCorrelata(long idAccordo,String nomeAzione,String owner) throws DriverRegistroServiziException {
		String nomeMetodo = "isCorrelata";

		String queryString;

		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet risultato = null;

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("isCorrelata");

			} catch (Exception e) {
				throw new DriverRegistroServiziException("[DriverRegistroServiziDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.log.debug("operazione this.driver.atomica = " + this.driver.atomica);

		try {			

			// ricavo le entries

			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.ACCORDI_AZIONI);
			sqlQueryObject.addSelectCountField("id", "tot");

			sqlQueryObject.addWhereCondition("id_accordo = ?");
			sqlQueryObject.addWhereCondition("correlata = ?");
			sqlQueryObject.addWhereCondition("nome <> ?");

			sqlQueryObject.setANDLogicOperator(true);

			queryString = sqlQueryObject.createSQLQuery();

			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idAccordo);
			stmt.setString(2, nomeAzione);
			stmt.setString(3, owner);

			risultato = stmt.executeQuery();

			int tot=0;
			if (risultato.next()) {
				tot=risultato.getInt("tot");
			}
			risultato.close();

			return tot>0;


		} catch (Exception se) {

			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::" + nomeMetodo + "] Exception: " + se.getMessage(),se);
		} finally {
			//Chiudo statement and resultset
			JDBCUtilities.closeResources(risultato, stmt);
			
			this.driver.closeConnection(con);
		}
	}

	/**
	 * Controlla se l'azione e' usata come Azione correlata in qualche azione dell'accordo con id idAccordo 
	 * @param idPortType
	 * @param nomeCorrelata
	 * @return true se l'azione e' usata come Azione correlata in qualche azione dell'accordo con id idAccordo 
	 * @throws DriverRegistroServiziException
	 */
	protected boolean isOperationCorrelata(long idPortType,String nomeCorrelata,String owner) throws DriverRegistroServiziException {
		String nomeMetodo = "isOperationCorrelata";

		String queryString;

		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet risultato = null;

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("isOperationCorrelata");

			} catch (Exception e) {
				throw new DriverRegistroServiziException("[DriverRegistroServiziDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.log.debug("operazione this.driver.atomica = " + this.driver.atomica);

		try {			

			// ricavo le entries

			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.PORT_TYPE_AZIONI);
			sqlQueryObject.addSelectCountField("id", "tot");

			sqlQueryObject.addWhereCondition("id_port_type = ?");
			sqlQueryObject.addWhereCondition("correlata = ?");
			sqlQueryObject.addWhereCondition("nome <> ?");

			sqlQueryObject.setANDLogicOperator(true);

			queryString = sqlQueryObject.createSQLQuery();

			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idPortType);
			stmt.setString(2, nomeCorrelata);
			stmt.setString(3, owner);

			risultato = stmt.executeQuery();

			int tot=0;
			if (risultato.next()) {
				tot=risultato.getInt("tot");
			}
			risultato.close();

			return tot>0;


		} catch (Exception se) {

			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::" + nomeMetodo + "] Exception: " + se.getMessage(),se);
		} finally {
			//Chiudo statement and resultset
			JDBCUtilities.closeResources(risultato, stmt);
			
			this.driver.closeConnection(con);
		}
	}

	protected boolean isOperationCorrelataDaAltraAzione(String nomePortType,long idPortType,String azioneDaVerificare,long idAzioneDaVerificare) throws DriverRegistroServiziException {
		return this.isOperationCorrelata(nomePortType, idPortType, azioneDaVerificare, idAzioneDaVerificare, false, true);
	}
	protected boolean isOperationCorrelataRichiesta(String nomePortType,long idPortType,String azioneDaVerificare,long idAzioneDaVerificare) throws DriverRegistroServiziException {
		return this.isOperationCorrelata(nomePortType, idPortType, azioneDaVerificare, idAzioneDaVerificare, true, false);
	}
	protected boolean isOperationCorrelata(String nomePortType,long idPortType,String azioneDaVerificare,long idAzioneDaVerificare) throws DriverRegistroServiziException {
		return this.isOperationCorrelata(nomePortType, idPortType, azioneDaVerificare, idAzioneDaVerificare, true, true);
	}
	private boolean isOperationCorrelata(String nomePortType,long idPortType,String azioneDaVerificare,long idAzioneDaVerificare,
			boolean checkCorrelazioneARichiesta,boolean checkCorrelazioneDaAltraAzione) throws DriverRegistroServiziException {
		String nomeMetodo = "isOperationCorrelata";

		String queryString;

		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet risultato = null;

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("isOperationCorrelata(nomePortType,id,azione,idAzione,check)");

			} catch (Exception e) {
				throw new DriverRegistroServiziException("[DriverRegistroServiziDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.log.debug("operazione this.driver.atomica = " + this.driver.atomica);

		try {			

			// VERIFICO CORRELAZIONE AD UNA RICHIESTA
			boolean correlataAdUnaRichiesta = false;
			if(checkCorrelazioneARichiesta){
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORT_TYPE_AZIONI);
				sqlQueryObject.addSelectCountField("id", "tot");
				// seleziono il port type
				sqlQueryObject.addWhereCondition(CostantiDB.PORT_TYPE_AZIONI+".id_port_type = ?");
				// seleziono l'azione
				sqlQueryObject.addWhereCondition("nome = ?");
				// verifico che l'azione non sia correlata ad una richiesta
				sqlQueryObject.addWhereCondition(false,CostantiDB.PORT_TYPE_AZIONI+".correlata is not null",CostantiDB.PORT_TYPE_AZIONI+".correlata_servizio is not null");
				sqlQueryObject.setANDLogicOperator(true);
				queryString = sqlQueryObject.createSQLQuery();
				this.driver.log.debug("VERIFICO CORRELAZIONE AD UNA RICHIESTA ["+idPortType+"] ["+azioneDaVerificare+"]: "+queryString);
				stmt = con.prepareStatement(queryString);
				stmt.setLong(1, idPortType);
				stmt.setString(2, azioneDaVerificare);
				risultato = stmt.executeQuery();
				int tot=0;
				if (risultato.next()) {
					tot=risultato.getInt("tot");
				}
				risultato.close();
				stmt.close();
				correlataAdUnaRichiesta = tot>0;
				this.driver.log.debug("VERIFICO CORRELAZIONE AD UNA RICHIESTA, risultato ["+tot+"]: "+correlataAdUnaRichiesta);
			}

			// VERIFICO CHE NON SIA CORRELATA DA UN'ALTRA AZIONE
			boolean correlataDaUnAltraAzione = false;
			if(checkCorrelazioneDaAltraAzione){

				// Correlazione precisa: per asincrono simmetrico e asincrono asimmetrico con port type diversi
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORT_TYPE_AZIONI);
				sqlQueryObject.addSelectCountField("id", "tot");
				sqlQueryObject.addWhereCondition(CostantiDB.PORT_TYPE_AZIONI+".id<>?");
				sqlQueryObject.addWhereCondition(true,CostantiDB.PORT_TYPE_AZIONI+".correlata =?",CostantiDB.PORT_TYPE_AZIONI+".correlata_servizio =?");
				sqlQueryObject.setANDLogicOperator(true);
				queryString = sqlQueryObject.createSQLQuery();
				stmt = con.prepareStatement(queryString);
				this.driver.log.debug("VERIFICO CHE NON SIA CORRELATA DA UN'ALTRA AZIONE (AS e AA con PT diverso) ["+idAzioneDaVerificare+"] ["+azioneDaVerificare+"] ["+nomePortType+"]: "+queryString);
				stmt.setLong(1, idAzioneDaVerificare);
				stmt.setString(2, azioneDaVerificare);
				stmt.setString(3, nomePortType);
				risultato = stmt.executeQuery();
				int tot=0;
				if (risultato.next()) {
					tot=risultato.getInt("tot");
				}
				risultato.close();
				stmt.close();
				correlataDaUnAltraAzione = tot>0;
				this.driver.log.debug("VERIFICO CHE NON SIA CORRELATA DA UN'ALTRA AZIONE (AS e AA con PT diverso), risultato ["+tot+"]: "+correlataDaUnAltraAzione);

				if(correlataDaUnAltraAzione==false){
					// Correlazione sullo stesso port type
					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
					sqlQueryObject.addFromTable(CostantiDB.PORT_TYPE_AZIONI);
					sqlQueryObject.addSelectCountField("id", "tot");
					sqlQueryObject.addWhereCondition(CostantiDB.PORT_TYPE_AZIONI+".id<>?");
					sqlQueryObject.addWhereCondition(true,CostantiDB.PORT_TYPE_AZIONI+".correlata =?",CostantiDB.PORT_TYPE_AZIONI+".correlata_servizio is null",CostantiDB.PORT_TYPE_AZIONI+".id_port_type = ?");
					sqlQueryObject.setANDLogicOperator(true);
					queryString = sqlQueryObject.createSQLQuery();
					stmt = con.prepareStatement(queryString);
					this.driver.log.debug("VERIFICO CHE NON SIA CORRELATA DA UN'ALTRA AZIONE (AA con stesso port type) ["+idAzioneDaVerificare+"] ["+azioneDaVerificare+"] ["+nomePortType+"] ["+azioneDaVerificare+"] ["+idPortType+"]: "+queryString);
					stmt.setLong(1, idAzioneDaVerificare);
					stmt.setString(2, azioneDaVerificare);
					stmt.setLong(3, idPortType);
					risultato = stmt.executeQuery();
					tot=0;
					if (risultato.next()) {
						tot=risultato.getInt("tot");
					}
					risultato.close();
					stmt.close();
					correlataDaUnAltraAzione = tot>0;
					this.driver.log.debug("VERIFICO CHE NON SIA CORRELATA DA UN'ALTRA AZIONE (AA con stesso port type), risultato ["+tot+"]: "+correlataDaUnAltraAzione);
				}
			}

			if(correlataAdUnaRichiesta){
				return true;
			}
			else if(correlataDaUnAltraAzione){
				return true;
			}
			else{
				return false;
			}

		} catch (Exception se) {

			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::" + nomeMetodo + "] Exception: " + se.getMessage(),se);
		} finally {
			//Chiudo statement and resultset
			JDBCUtilities.closeResources(risultato, stmt);
			
			this.driver.closeConnection(con);
		}
	}
	
	protected PortType getPortType(IDPortType idPT) throws DriverRegistroServiziException {
		String nomeMetodo = "getPortType";

		Connection con = null;

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("getPortType");

			} catch (Exception e) {
				throw new DriverRegistroServiziException("[DriverRegistroServiziDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(), e);

			}

		} else {
			con = this.driver.globalConnection;
		}

		this.driver.log.debug("operazione this.driver.atomica = " + this.driver.atomica);

		try {
		
			return this.getPortType(DBUtils.getIdPortType(DBUtils.getIdAccordoServizioParteComune(idPT.getIdAccordo(), con, this.driver.tipoDB), idPT.getNome(), con));
			
		} catch (Exception se) {
			throw new DriverRegistroServiziException("[DriverRegistroServiziException::" + nomeMetodo + "] Exception: " + se.getMessage(),se);
		} finally {
			// Chiudo statement and resultset
			
			this.driver.closeConnection(con);
		}
		
	}


	protected List<IDServizio> getIdServiziWithPortType(IDPortType idPT) throws DriverRegistroServiziException,DriverRegistroServiziNotFound {
		String nomeMetodo = "getIdServiziWithPortType";

		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String queryString;

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("getIdServiziWithPortType");

			} catch (Exception e) {
				throw new DriverRegistroServiziException("[DriverRegistroServiziDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(), e);

			}

		} else {
			con = this.driver.globalConnection;
		}

		this.driver.log.debug("operazione this.driver.atomica = " + this.driver.atomica);

		List<IDServizio> idServizi = new ArrayList<IDServizio>(); 
		try {

			//recupero idAccordo
			long idAccordoLong = DBUtils.getIdAccordoServizioParteComune(idPT.getIdAccordo(), con, this.driver.tipoDB);
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
			sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI+".port_type=?");
			sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI+".id_soggetto="+CostantiDB.SOGGETTI+".id");
			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idAccordoLong);
			stmt.setString(2, idPT.getNome());

			rs = stmt.executeQuery();
			while (rs.next()) {
				IDSoggetto soggettoErogatore = new IDSoggetto(rs.getString("tipo_soggetto"), rs.getString("nome_soggetto"));
				IDServizio idServizio = 
						this.driver.idServizioFactory.getIDServizioFromValues(rs.getString("tipo_servizio"), 
								rs.getString("nome_servizio"), soggettoErogatore, rs.getInt("versione_servizio"));
				idServizi.add(idServizio);
			}

			if(idServizi.size()<=0){
				throw new DriverRegistroServiziNotFound("Servizi non trovato che implementano il servizio "+idPT.getNome()+" dell'accordo di servizio "+idPT.getIdAccordo().toString());
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
	
	protected boolean isUnicaAzioneInAccordi(String azione) throws DriverRegistroServiziException {
		String nomeMetodo = "isUnicaAzioneInAccordi";

		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String queryString;

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("isUnicaAzioneInAccordi");

			} catch (Exception e) {
				throw new DriverRegistroServiziException("[DriverRegistroServiziDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(), e);

			}

		} else {
			con = this.driver.globalConnection;
		}

		this.driver.log.debug("operazione this.atomica = " + this.driver.atomica);

		try {

			// AZIONI direttamente negli accordi
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.ACCORDI_AZIONI);
			sqlQueryObject.addSelectCountField("nome", "countAzioni");
			sqlQueryObject.addWhereCondition(CostantiDB.ACCORDI_AZIONI+".nome=?");
			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			stmt.setString(1, azione);
			rs = stmt.executeQuery();
			int count = 0;
			if(rs.next()){
				count = rs.getInt("countAzioni");
			}
			else{
				throw new Exception("Azione ["+azione+"] non trovata (rs.next fallita)");
			}
			rs.close();
			stmt.close();

			// AZIONI dei port types
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.PORT_TYPE_AZIONI);
			sqlQueryObject.addSelectCountField("nome", "countAzioni");
			sqlQueryObject.addWhereCondition(CostantiDB.PORT_TYPE_AZIONI+".nome=?");
			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			stmt.setString(1, azione);
			rs = stmt.executeQuery();
			if(rs.next()){
				count = count + rs.getInt("countAzioni");
			}
			else{
				throw new Exception("Azione ["+azione+"] non trovata (rs.next fallita pt)");
			}

			if(count<=0){
				throw new Exception("Azione ["+azione+"] non trovata");
			}else{
				return count==1;
			}

		}catch (Exception se) {
			throw new DriverRegistroServiziException("[DriverRegistroServiziException::" + nomeMetodo + "] Exception: " + se.getMessage(),se);
		} finally {
			// Chiudo statement and resultset
			JDBCUtilities.closeResources(rs, stmt);
			
			this.driver.closeConnection(con);
		}
	}
}
