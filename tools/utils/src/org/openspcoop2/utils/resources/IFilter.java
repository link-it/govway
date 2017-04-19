package org.openspcoop2.utils.resources;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

public interface IFilter {

	public void init(Object ... params) throws IOException, ServletException;
	public void destroy();
	public void doInput(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException ;
	public void doOutput(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException ;
}
