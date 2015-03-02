package org.openspcoop2.pdd.services.skeleton;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import org.openspcoop2.pdd.services.connector.ConnectorUtils;
import org.openspcoop2.protocol.engine.constants.IDService;

public class IntegrationManagerEngineFilter implements Filter {

	@Override
	public void destroy() {

	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {

		// L'invocazione dell'I.M. deve avvenire via OpenSPCoopServlet dispatcher
		HttpServletResponse httpRes = (HttpServletResponse) response;
		httpRes.sendError(404, ConnectorUtils.generateError404Message(ConnectorUtils.getFullCodeEngineFilter(IDService.INTEGRATION_MANAGER_SOAP)));

	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {

	}

}
