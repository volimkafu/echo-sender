package com.exo.engine.config;

public final class EmailConfig {

	private String homePage = "EkoBuzz Echo (http://www.ekobuzz.com)";
	private String abuseUrl = "http://www.ekobuzz.com/abuse?u=";
	private String unsubscribeUrl = "http://echo.ekobuzz.com:8081/echo/Remove";

	private String smtpHost = "mail.eko01.net";
	private Integer smtpPort = new Integer(2525);
	private String smtpUser = "ekostaging@eko01.net";
	private String smtpPwd = "ekostaging";

	public String getHomePage() {
		return homePage;
	}

	public String getXHomePage() {
		return new StringBuilder("X-").append(homePage).toString();
	}

	public void setHomePage(String homePage) {
		this.homePage = homePage;
	}

	public String getAbuseUrl() {
		return abuseUrl;
	}

	public void setAbuseUrl(String abuseUrl) {
		this.abuseUrl = abuseUrl;
	}

	public String getHttpUrlUnSubscribe() {
		return unsubscribeUrl;
	}

	public String getSmtpHost() {
		return smtpHost;
	}

	public void setSmtpHost(String smtpHost) {
		this.smtpHost = smtpHost;
	}

	public Integer getSmtpPort() {
		return smtpPort;
	}

	public void setSmtpPort(Integer smtpPort) {
		this.smtpPort = smtpPort;
	}

	public String getSmtpUser() {
		return smtpUser;
	}

	public void setSmtpUser(String smtpUser) {
		this.smtpUser = smtpUser;
	}

	public String getSmtpPwd() {
		return smtpPwd;
	}

	public void setSmtpPwd(String smtpPwd) {
		this.smtpPwd = smtpPwd;
	}

}
