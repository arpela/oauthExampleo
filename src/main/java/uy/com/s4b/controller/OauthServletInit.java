package uy.com.s4b.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

@WebServlet(asyncSupported = false, loadOnStartup = 0, urlPatterns = { "/init"})

public class OauthServletInit extends HttpServlet {

	
	private static final Logger log = Logger.getLogger(OauthServletInit.class);
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/***
	 * 
	 */
	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		log.info(" ======== inicializando contexto ejecucion ======== ");
		
		try {
			String appID = request.getParameter("appID");
			
			String urlRedirect = request.getParameter("urlAutorizacion").trim().
					concat("?scope=personal_info openid email document&response_type=code&client_id="+ 
					appID + "&redirect_uri=" + request.getParameter("redirect_uri").trim());
			
			request.getSession().setAttribute("appID", appID);
			request.getSession().setAttribute("appSecret", request.getParameter("appSecret"));
			request.getSession().setAttribute("redirect_uri", request.getParameter("redirect_uri"));
			request.getSession().setAttribute("urlAccessToken", request.getParameter("urlAccessToken"));
			request.getSession().setAttribute("urlProfile", request.getParameter("urlProfile"));
			response.sendRedirect(urlRedirect);	
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}
	
	
	
}
