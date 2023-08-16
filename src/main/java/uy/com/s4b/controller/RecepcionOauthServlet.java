package uy.com.s4b.controller;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

@WebServlet(asyncSupported = false, loadOnStartup = 0, urlPatterns = { "/recepcionCode", "/recepcionTocken" })

public class RecepcionOauthServlet extends HttpServlet {

	private static final Logger log = Logger.getLogger(RecepcionOauthServlet.class);

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/***
	 * 
	 */
	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		log.info("<----- Recepcion del code/tocken ------>  ");

		try {
			Map params = request.getParameterMap();
			Iterator i = params.keySet().iterator();

			while (i.hasNext()) {
				String key = (String) i.next();
				String value = ((String[]) params.get(key))[0];
				log.info("Nombre parametro: " + key + " Valor parametro: " + value + "\n");
			}
			
			if (request.getParameter("code") != null) {
				String code = request.getParameter("code");
				log.info("Code recuperado: " + code);
				String appID = request.getSession().getAttribute("appID").toString();
				String appSecret = request.getSession().getAttribute("appSecret").toString();
				String redirect_uri = request.getSession().getAttribute("redirect_uri").toString();
				String urlAccessToken = request.getSession().getAttribute("urlAccessToken").toString();
				String urlProfile = request.getSession().getAttribute("urlProfile").toString();

				UtilOAuth oauth = new UtilOAuth(appID, appSecret, urlAccessToken, urlProfile, redirect_uri);

				// con el code voy a recuperar el accesstoken.
				String tocken = oauth.getAccessToken(code);

				// con el token voy a recuperar info del usuario.
				String infoUser = oauth.getUserProfile(tocken);

				log.info("------------------");
				log.info(infoUser);
				log.info("------------------");

				
				Writer out = new OutputStreamWriter(response.getOutputStream(), "UTF-8");

				out.write("<html>");
				out.write("<head/>");
				out.write("<body>");
				out.write(infoUser);
				out.write("</body>");
				out.write("</html>");
			 	out.flush(); 
			 	
			 	
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}

	}

}
