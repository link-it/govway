package org.openspcoop2.web.monitor.core.dao;

import java.util.List;

public interface IPaginatedList<T,K> extends ICrudDAO<T, K>{
	public List<T> findAll(int start, int limit);
	public int totalCount();
}
