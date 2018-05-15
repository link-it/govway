package org.openspcoop2.web.monitor.transazioni.datamodel;

import org.openspcoop2.core.transazioni.Transazione;
import org.openspcoop2.web.monitor.core.datamodel.BaseDataModel;
import org.openspcoop2.web.monitor.core.logger.LoggerManager;
import org.openspcoop2.web.monitor.transazioni.bean.TransazioneBean;
import org.openspcoop2.web.monitor.transazioni.dao.ITransazioniService;
import org.openspcoop2.web.monitor.transazioni.mbean.DettagliBean;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.faces.context.FacesContext;

import org.ajax4jsf.model.DataVisitor;
import org.ajax4jsf.model.Range;
import org.ajax4jsf.model.SequenceRange;
import org.slf4j.Logger;

public class TransazioniDuplicatiDM extends
		BaseDataModel<Long, Transazione, ITransazioniService> {

	private static final long serialVersionUID = 8645779291901248165L;
	private static Logger log =  LoggerManager.getPddMonitorCoreLogger();

	private DettagliBean dettagliBean;
	private Boolean isRichiesta = false;
	
	public void setRichiesta(Boolean isRichiesta) {
		this.isRichiesta = isRichiesta;
	}

	public void setDettagliBean(DettagliBean dettagliBean) {
		this.dettagliBean = dettagliBean;
	}

	@Override
	public int getRowCount() {
		try {
			TransazioneBean dettaglio = this.dettagliBean.getDettaglio();
			String idtransazione = dettaglio.getIdTransazione();
			String idEgov = !this.isRichiesta ? dettaglio
					.getIdMessaggioRisposta() : dettaglio
					.getIdMessaggioRichiesta();
			int duplicati = !this.isRichiesta ? dettaglio
					.getDuplicatiRisposta() : dettaglio.getDuplicatiRichiesta();

			if (duplicati > 0) {
				// questa (dettaglio) e' la busta originale devo trovare i
				// duplicati
				return this.getDataProvider().countAllDuplicati(idtransazione,
						idEgov, !this.isRichiesta);
			}
			if (duplicati == -1) {
				// questa (dettaglio) e' la busta duplicata c'e' solo 1
				// originale
				return 1;
			}

		} catch (Exception e) {
			TransazioniDuplicatiDM.log.error(e.getMessage(), e);
		}

		return 0;
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
				List<TransazioneBean> list = null;
				// cerco duplicate o originali...
				TransazioneBean dettaglio = this.dettagliBean.getDettaglio();
				int duplicati = !this.isRichiesta ? dettaglio
						.getDuplicatiRisposta() : dettaglio
						.getDuplicatiRichiesta();

				String idtransazione = dettaglio.getIdTransazione();
				String idEgov = !this.isRichiesta ? dettaglio
						.getIdMessaggioRisposta() : dettaglio
						.getIdMessaggioRichiesta();
				if (duplicati > 0) {
					// questa (dettaglio) e' la busta originale devo trovare i
					// duplicati
					list = this.getDataProvider().findAllDuplicati(
							idtransazione, idEgov,
							!this.isRichiesta, start, limit);
				}
				if (duplicati == -1) {
					// questa (dettaglio) e' la busta duplicata voglio
					// l'originale
					TransazioneBean t = this.getDataProvider()
							.findTransazioneOriginale(idtransazione, idEgov,
									!this.isRichiesta);
					list = new ArrayList<TransazioneBean>();
					list.add(t);
				}

				for (TransazioneBean r : list) {
					this.wrappedData.put(r.getId(), r);
					this.wrappedKeys.add(r.getId());
					visitor.process(context, r.getId(), argument);
				}
			}
		} catch (Exception e) {
			TransazioniDuplicatiDM.log.error(e.getMessage(), e);
		}

	}

}
