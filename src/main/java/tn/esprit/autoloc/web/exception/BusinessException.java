package tn.esprit.autoloc.web.exception;

/**
 * Règle métier non respectée (ex. immatriculation déjà utilisée) : renvoyée en 409 Conflict.
 */
public class BusinessException extends RuntimeException {
    public BusinessException(String message) {
        super(message);
    }
}
