package org.openspcoop2.web.monitor.core.datamodel;

import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.generic_project.expression.SortOrder;

import it.link.pdd.core.utenti.Utente;
import org.openspcoop2.web.monitor.core.datamodel.BaseDataModelWithSearchForm;
import org.openspcoop2.web.monitor.core.bean.UtentiSearchForm;
import org.openspcoop2.web.monitor.core.dao.IUserService;
import org.openspcoop2.web.monitor.core.mbean.UtentiBean;

public class UtentiDM extends BaseDataModelWithSearchForm<Long, UtentiBean, IUserService, UtentiSearchForm>{

	private static final long serialVersionUID = 4494962270743603849L;
	
	@Override
	protected List<UtentiBean> findObjects(int start, int limit, String sortField, SortOrder sortOrder) {
		List<Utente> list =  this.getDataProvider().findAll(start, limit);
		
		List<UtentiBean> toRet = new ArrayList<UtentiBean>();
		
		if (list != null && list.size() > 0) {
			for (Utente r : list) {
				UtentiBean u = new UtentiBean();
				u.setUser(r);
				toRet.add(u);
			}
		}
		
		return toRet;
	}

	@Override
	public Long getId(UtentiBean object) {
		if(object != null && object.getUser() != null)
			return object.getUser().getId();

		return null;
	}
}
