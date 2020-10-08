package org.openspcoop2.web.ctrlstat.servlet.utils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.servlet.ConsoleHelper;
import org.openspcoop2.web.lib.mvc.PageData;

public class UtilsHelper extends ConsoleHelper{

	public UtilsHelper(HttpServletRequest request, PageData pd, HttpSession session) throws Exception {
		super(request, pd,  session);
	}
	public UtilsHelper(ControlStationCore core, HttpServletRequest request, PageData pd, HttpSession session) throws Exception {
		super(core, request, pd,  session);
	}

}
