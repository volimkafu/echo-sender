package com.exo.ui;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class LauncherServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		
		String result = formatSuccess("SUCCESS");
		
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		out.println(result);
	}

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String result = formatSuccess("SUCCESS");
		
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		out.println(result);
	}
	
	protected String formatSuccess(String str) {
		return "<b><font color='#00AA00'>" + str + "</font>";
	}

	protected String formatError(String err) {
		return "<font color='#FF0000'>" + err + "</font>";
	}
}
