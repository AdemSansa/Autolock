package tn.esprit.autoloc.web.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.HashMap;
import java.util.Map;

/**
 * Gestion centralisée des erreurs de tous les contrôleurs.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Étape 5.3 — DTO invalide (@Valid) : 400 avec, pour chaque champ invalide, le message de sa contrainte.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> erreurs = new HashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(fe -> erreurs.putIfAbsent(fe.getField(), fe.getDefaultMessage()));
        return ResponseEntity.badRequest().body(erreurs);
    }

    /**
     * Étape 5.3 — ressource absente : 404 avec le message de l'exception.
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<String> handleNotFound(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    /**
     * Règle métier violée (ex. immatriculation déjà utilisée) : 409.
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<String> handleBusiness(BusinessException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }

    /**
     * Paramètre métier invalide levé par un service : 400.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    /**
     * JSON mal formé ou valeur d'enum inconnue dans le corps (ex. "categorie": "AVION") : 400.
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<String> handleNotReadable(HttpMessageNotReadableException ex) {
        return ResponseEntity.badRequest().body("Corps de requête invalide (JSON mal formé ou valeur inconnue)");
    }

    /**
     * @PathVariable du mauvais type (ex. /recherche/categorie/AVION/... ou /tarif/abc/100) : 400.
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<String> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        return ResponseEntity.badRequest()
                .body("Valeur invalide pour le paramètre '" + ex.getName() + "' : " + ex.getValue());
    }
}
