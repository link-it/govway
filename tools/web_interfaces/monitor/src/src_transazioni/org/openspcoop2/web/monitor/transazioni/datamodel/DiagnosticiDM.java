/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 * 
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2019 Link.it srl (http://link.it).
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
package org.openspcoop2.web.monitor.transazioni.datamodel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.faces.context.FacesContext;

import org.ajax4jsf.model.DataVisitor;
import org.ajax4jsf.model.Range;
import org.ajax4jsf.model.SequenceRange;
import org.openspcoop2.protocol.sdk.diagnostica.DriverMsgDiagnosticiNotFoundException;
import org.openspcoop2.protocol.sdk.diagnostica.FiltroRicercaDiagnostici;
import org.openspcoop2.protocol.sdk.diagnostica.FiltroRicercaDiagnosticiConPaginazione;
import org.openspcoop2.protocol.sdk.diagnostica.IDiagnosticDriver;
import org.openspcoop2.protocol.sdk.diagnostica.MsgDiagnostico;
import org.openspcoop2.utils.beans.BlackListElement;
import org.openspcoop2.web.monitor.core.core.PddMonitorProperties;
import org.openspcoop2.web.monitor.core.datamodel.BaseDataModel;
import org.openspcoop2.web.monitor.core.logger.LoggerManager;
import org.openspcoop2.web.monitor.core.utils.BeanUtils;
import org.openspcoop2.web.monitor.transazioni.mbean.DiagnosticiBean;
import org.openspcoop2.web.monitor.transazioni.mbean.MsgDiagnosticoBean;
import org.slf4j.Logger;

/**
 * DiagnosticiDM
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class DiagnosticiDM extends
		BaseDataModel<Integer, MsgDiagnostico, IDiagnosticDriver> {

	private static final long serialVersionUID = 6120236437017709423L;
	private static Logger log = LoggerManager.getPddMonitorCoreLogger();

	private DiagnosticiBean diagnosticiBean;

	public DiagnosticiDM(){
		try {

			PddMonitorProperties govwayMonitorProperties = PddMonitorProperties.getInstance(DiagnosticiDM.log);

			this.setDataProvider(govwayMonitorProperties.getDriverMsgDiagnostici());


		} catch (Exception e) {
			DiagnosticiDM.log
			.warn("Inizializzazione driverDiagnostici fallita.....",
					e);
		}
	}
	
	
	public void setDiagnosticiBean(DiagnosticiBean diagnosticiBean) {
		this.diagnosticiBean = diagnosticiBean;
	}


	
	@Override
	public int getRowCount() {
		try {
			if (this.rowCount == null) {
				FiltroRicercaDiagnostici filter = new FiltroRicercaDiagnostici();

				// devo solo settare l'idtransazione
				// filter.setIdEgov(this.diagnosticiBean.getIdEgov());
				Hashtable<String, String> properties = new Hashtable<String, String>();
				properties.put("id_transazione",
						this.diagnosticiBean.getIdTransazione());
				filter.setProperties(properties);
				
				filter.setCheckApplicativoIsNull(this.diagnosticiBean.getForceNomeServizioApplicativoNull());
				filter.setApplicativo(this.diagnosticiBean.getNomeServizioApplicativo());

				// NON CI VUOLE: altrimenti non vengono visualizzati i primi diagnostici che possiedono un identificativo porta differente
//				IDSoggetto dominio = new IDSoggetto();
//				dominio.setCodicePorta(this.diagnosticiBean
//						.getIdentificativoPorta());
//				filter.setDominio(dominio);

				this.rowCount = this.getDataProvider()
						.countMessaggiDiagnostici(filter);
			}
		} catch (Exception e) {
			DiagnosticiDM.log.error(e.getMessage(), e);
			this.rowCount = 0;
		}

		return this.rowCount;

	}

	@Override
	public Object getRowData() {
		try {
			if (this.currentPk == null)
				return null;

			return this.wrappedData.get(this.currentPk);
		} catch (Exception e) {
			DiagnosticiDM.log.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	public void walk(FacesContext context, DataVisitor visitor, Range range,
			Object argument) throws IOException {
		try {

			if (this.detached) {
				for (Integer key : this.wrappedKeys) {
					setRowKey(key);
					visitor.process(context, key, argument);
				}
			} else {
				int start = ((SequenceRange) range).getFirstRow();
				int limit = ((SequenceRange) range).getRows();

				this.wrappedKeys = new ArrayList<Integer>();
				FiltroRicercaDiagnosticiConPaginazione filter = new FiltroRicercaDiagnosticiConPaginazione();
				filter.setOffset(start);
				filter.setLimit(limit);

				// devo impostare solo l'idtransazione
				// filter.setIdEgov(this.diagnosticiBean.getIdEgov());
				Hashtable<String, String> properties = new Hashtable<String, String>();
				properties.put("id_transazione",
						this.diagnosticiBean.getIdTransazione());
				filter.setProperties(properties);
				
				filter.setCheckApplicativoIsNull(this.diagnosticiBean.getForceNomeServizioApplicativoNull());
				filter.setApplicativo(this.diagnosticiBean.getNomeServizioApplicativo());
				
				// NON CI VUOLE: altrimenti non vengono visualizzati i primi diagnostici che possiedono un identificativo porta differente
//				IDSoggetto dominio = new IDSoggetto();
//				dominio.setCodicePorta(this.diagnosticiBean
//						.getIdentificativoPorta());
//				filter.setDominio(dominio);

				List<MsgDiagnostico> msgs = null;
				try{
					msgs = this.getDataProvider()
							.getMessaggiDiagnostici(filter);
				}catch(DriverMsgDiagnosticiNotFoundException notFound){
					msgs = new ArrayList<MsgDiagnostico>();
					DiagnosticiDM.log.debug(notFound.getMessage(), notFound);
				}

				// salvo tot count
				//this.rowCount = (int) msgs.size();

				List<BlackListElement> metodiEsclusi = new ArrayList<BlackListElement>(0);
				
				for (int i = 0; i < msgs.size(); i++) {
					MsgDiagnostico msgDiagnostico = msgs.get(i);
					MsgDiagnosticoBean msgDiagnosticoBean = new MsgDiagnosticoBean();
									
					BeanUtils.copy(msgDiagnosticoBean, msgDiagnostico, metodiEsclusi);
					
					Integer k = new Long(msgDiagnostico.getId()).intValue();
					this.wrappedData.put(k, msgDiagnosticoBean);
					this.wrappedKeys.add(k);
					visitor.process(context, k, argument);
				}
			}
		} catch (Exception e) {
			DiagnosticiDM.log.error(e.getMessage(), e);
		}

	}

}
