package org.babinkuk.exception;

/**
 * main custom service exception
 * 
 * @author BabinKuk
 *
 */
public class ApplicationServiceException extends RuntimeException {

    private static final long serialVersionUID = 1L;

	public ApplicationServiceException(String message) {
        super(message);
    }

    public ApplicationServiceException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public ApplicationServiceException(Throwable cause) {
        super(cause);
    }
}    