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
		
		logCaller();
		if (! isCallerAuthorised(request))
			throw new IOException("The caller is UNAUTHORIZED... ");
		
		
		String result = formatSuccess("SUCCESS");
		
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		out.println(result);
	}

	protected void logCaller() {
		// TODO::
		// log caller's key, uname, and hash
		
	}

	protected boolean isCallerAuthorised(HttpServletRequest request) {
		//TODO::
		//1. get key, compare with the one in hand
		//2. get caller's uname/pwd-hash, compare from the list of authorized callers in the store
		
		return false;
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
