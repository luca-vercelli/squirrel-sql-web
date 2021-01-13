package net.sourceforge.squirrel_sql.ws.exceptions;

/**
 * Error during token extraction.
 * This is not a RuntimeException, so that EJB transactions so not rollback.
 * @author luca
 *
 */
public class AuthorizationException extends Exception {

    private static final long serialVersionUID = 4781701507549786977L;

    public AuthorizationException() {
        super();
    }

    public AuthorizationException(String arg0, Throwable arg1, boolean arg2, boolean arg3) {
        super(arg0, arg1, arg2, arg3);
    }

    public AuthorizationException(String message, Throwable cause) {
        super(message, cause);
    }

    public AuthorizationException(String message) {
        super(message);
    }

    public AuthorizationException(Throwable cause) {
        super(cause);
    }

}
