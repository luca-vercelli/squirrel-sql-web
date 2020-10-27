package net.sourceforge.squirrel_sql.jaxrs;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sourceforge.squirrel_sql.dto.ExceptionBean;

@Provider
public class DefaultExceptionHandler implements ExceptionMapper<Exception> {

	static final Logger logger = LoggerFactory.getLogger(DefaultExceptionHandler.class);

	@Override
	public Response toResponse(Exception e) {

		e = extractParentException(e);
		ExceptionContent excContent = extractMessage(e);

		if (excContent.shouldBeLogged()) {
			logger.error("Internal server error:", e);
		}

		ExceptionBean excBean = new ExceptionBean(excContent.getHttpCode(), excContent.getMessage());
		return Response.status(excContent.getHttpCode()) //
				.entity(excBean)//
				.type(MediaType.APPLICATION_JSON)//
				.build();
	}

	/**
	 * Skip usually unuseful exceptions
	 * 
	 * @param e
	 * @return
	 */
	public Exception extractParentException(Exception e) {

		while ((e instanceof javax.ejb.EJBException || e instanceof javax.persistence.PersistenceException
				|| e instanceof javax.transaction.RollbackException
				|| e instanceof org.hibernate.exception.GenericJDBCException) && e.getCause() != null
				&& e.getCause() instanceof Exception) {
			e = (Exception) e.getCause();
		}

		return e;
	}

	/**
	 * Extract human-readable data from exception
	 * 
	 * @param e
	 * @return
	 */
	public ExceptionContent extractMessage(Exception e) {

		ExceptionContent ret = new ExceptionContent();

		if (e instanceof WebApplicationException) {
			// don't print stack trace, if client issue
			// Unluckily, some library prints it anyway.
			ret.httpCode = ((WebApplicationException) e).getResponse().getStatus();

			if (ret.httpCode == Status.INTERNAL_SERVER_ERROR.getStatusCode()) {
				ret.shouldBeLogged = true;
			}
			ret.message = Status.fromStatusCode(ret.httpCode).getReasonPhrase();
			if (e.getMessage() != null)
				ret.message += " - " + e.getMessage();

		} else if (e instanceof IllegalArgumentException) {
			ret.httpCode = Status.BAD_REQUEST.getStatusCode();
			ret.message = e.getMessage();

		} else {
			ret.shouldBeLogged = true;
			ret.httpCode = Status.INTERNAL_SERVER_ERROR.getStatusCode();
			ret.message = e.getMessage() != null ? e.getMessage() : Status.INTERNAL_SERVER_ERROR.getReasonPhrase();
		}

		return ret;
	}

	public static class ExceptionContent {
		private int httpCode;
		private String message;
		private boolean shouldBeLogged = false;

		public int getHttpCode() {
			return httpCode;
		}

		public void setHttpCode(int httpCode) {
			this.httpCode = httpCode;
		}

		public String getMessage() {
			return message;
		}

		public void setMessage(String message) {
			this.message = message;
		}

		public boolean shouldBeLogged() {
			return shouldBeLogged;
		}

		public void setShouldBeLogged(boolean shouldBeLogged) {
			this.shouldBeLogged = shouldBeLogged;
		}
	}
}