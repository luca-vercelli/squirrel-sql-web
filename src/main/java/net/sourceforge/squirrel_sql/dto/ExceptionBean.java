package net.sourceforge.squirrel_sql.dto;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response.Status;

/**
 * JSON bean representing exceptions
 * 
 * @see http://www.odata.org/documentation/odata-version-3-0/json-verbose-format/#representingerrorsinaresponse
 * 
 */
public class ExceptionBean {

	public static class OdataExceptionErrorBean {
		private String lang = "it";
		private String value;

		public OdataExceptionErrorBean() {
		}

		public OdataExceptionErrorBean(String lang, String value) {
			this.lang = lang;
			this.value = value;
		}

		public String getLang() {
			return lang;
		}

		public void setLang(String lang) {
			this.lang = lang;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}
	}

	private OdataExceptionErrorBean error = new OdataExceptionErrorBean();
	private String code;

	public ExceptionBean() {

	}

	public ExceptionBean(int status, String errorMsg) {
		this.code = Integer.toString(status);
		this.error.value = errorMsg;
	}

	public ExceptionBean(Status status) {
		this(status.getStatusCode(), status.getReasonPhrase());
	}

	public ExceptionBean(Status status, String errorMsg) {
		this(status.getStatusCode(), errorMsg);
	}

	public ExceptionBean(int status, String lang, String errorMsg) {
		this(status, errorMsg);
		this.error.lang = lang;
	}

	public ExceptionBean(Status status, String lang, String errorMsg) {
		this(status.getStatusCode(), errorMsg);
		this.error.lang = lang;
	}

	public ExceptionBean(WebApplicationException exc) {
		this(exc.getResponse().getStatus(), exc.getMessage());
	}

	public OdataExceptionErrorBean getError() {
		return error;
	}

	public void setError(OdataExceptionErrorBean error) {
		this.error = error;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
}
