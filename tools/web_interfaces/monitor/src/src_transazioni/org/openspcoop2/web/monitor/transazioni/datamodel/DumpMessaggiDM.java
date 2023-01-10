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
package org.openspcoop2.web.monitor.transazioni.datamodel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.faces.context.FacesContext;

import org.ajax4jsf.model.DataVisitor;
import org.ajax4jsf.model.Range;
import org.ajax4jsf.model.SequenceRange;
import org.openspcoop2.web.monitor.core.datamodel.BaseDataModel;
import org.openspcoop2.web.monitor.core.logger.LoggerManager;
import org.openspcoop2.web.monitor.transazioni.bean.DumpMessaggioBean;
import org.openspcoop2.web.monitor.transazioni.dao.ITransazioniService;
import org.openspcoop2.web.monitor.transazioni.mbean.DiagnosticiBean;
import org.slf4j.Logger;

/**
 * DiagnosticiDM
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class DumpMessaggiDM extends
		BaseDataModel<Long, DumpMessaggioBean, ITransazioniService> {

	private static final long serialVersionUID = 6120236437017709423L;
	private static Logger log = LoggerManager.getPddMonitorCoreLogger();

	private DiagnosticiBean diagnosticiBean;

	public DumpMessaggiDM(){ }
	
	
	public void setDiagnosticiBean(DiagnosticiBean diagnosticiBean) {
		this.diagnosticiBean = diagnosticiBean;
	}


	
	@Override
	public int getRowCount() {
		try {
			if (this.rowCount == null) {
				this.rowCount = this.getDataProvider().countDumpMessaggiGByDataConsegnaErogatore(this.diagnosticiBean.getIdTransazione(),this.diagnosticiBean.getNomeServizioApplicativo());
			}
		} catch (Exception e) {
			DumpMessaggiDM.log.error(e.getMessage(), e);
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
			DumpMessaggiDM.log.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	public void walk(FacesContext context, DataVisitor visitor, Range range,
			Object argument) throws IOException {
		try {

			if (this.detached) {
				for (Long key : this.wrappedKeys) {
					setRowKey(key);
					visitor.process(context, key, argument);
				}
			} else {
				int start = ((SequenceRange) range).getFirstRow();
				int limit = ((SequenceRange) range).getRows();

				this.wrappedKeys = new ArrayList<Long>();

				List<DumpMessaggioBean> lista = this.getDataProvider().listDumpMessaggiGByDataConsegnaErogatore(this.diagnosticiBean.getIdTransazione(), 
						this.diagnosticiBean.getNomeServizioApplicativo(),start,limit);
				
				for (int i = 0; i < lista.size(); i++) {
					DumpMessaggioBean dump = lista.get(i);
					
					dump.setProtocollo(this.diagnosticiBean.getProtocollo());
					
					this.wrappedData.put(dump.getId(), dump);
					this.wrappedKeys.add(dump.getId());
					visitor.process(context, dump.getId(), argument);
				}
			}
		} catch (Exception e) {
			DumpMessaggiDM.log.error(e.getMessage(), e);
		}

	}

}
