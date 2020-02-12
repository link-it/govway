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
import org.openspcoop2.web.monitor.transazioni.bean.TransazioneApplicativoServerBean;
import org.openspcoop2.web.monitor.transazioni.dao.ITransazioniApplicativoServerService;
import org.openspcoop2.web.monitor.transazioni.mbean.DiagnosticiBean;
import org.slf4j.Logger;

public class TransazioniApplicativoServerDM extends BaseDataModel<Long, TransazioneApplicativoServerBean, ITransazioniApplicativoServerService> {

	private static final long serialVersionUID = 8645779291901248165L;
	private static Logger log =  LoggerManager.getPddMonitorCoreLogger();
	
	private DiagnosticiBean diagnosticiBean;

	@Override
	public void walk(FacesContext context, DataVisitor visitor, Range range, Object argument) throws IOException {
		try {

			if (this.detached) {
				for (Long key : this.wrappedKeys) {
					setRowKey(key);
					visitor.process(context, key, argument);
				}
			} else {
				int start = ((SequenceRange)range).getFirstRow();
				int limit = ((SequenceRange)range).getRows();

				this.wrappedKeys = new ArrayList<Long>();
				this.getDataProvider().setProtocollo(this.diagnosticiBean.getProtocollo());
				this.getDataProvider().setIdTransazione(this.diagnosticiBean.getIdTransazione());
				
				List<TransazioneApplicativoServerBean> list = this.getDataProvider().findAll(start, limit);
				
				for (TransazioneApplicativoServerBean r : list) {
					this.wrappedData.put(r.getId(), r);
					this.wrappedKeys.add(r.getId());
					visitor.process(context, r.getId(), argument);
				}
			}
		} catch (Exception e) {
			TransazioniApplicativoServerDM.log.error(e.getMessage(), e);
		}

	}
	
	@Override
	public int getRowCount() {
		try {
			if (this.rowCount == null) {
				
				this.getDataProvider().setProtocollo(this.diagnosticiBean.getProtocollo());
				this.getDataProvider().setIdTransazione(this.diagnosticiBean.getIdTransazione());
				this.rowCount = this.getDataProvider().totalCount();
			}
		} catch (Exception e) {
			TransazioniApplicativoServerDM.log.error(e.getMessage(), e);
			this.rowCount = 0;
		}

		return this.rowCount;

	}
	
	public DiagnosticiBean getDiagnosticiBean() {
		return this.diagnosticiBean;
	}

	public void setDiagnosticiBean(DiagnosticiBean diagnosticiBean) {
		this.diagnosticiBean = diagnosticiBean;
	}
}
