package fr.abes.convergence.kbartws.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.io.IOException;

@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
@Slf4j
public class ExceptionControllerHandler extends ResponseEntityExceptionHandler {
    private ResponseEntity<Object> buildResponseEntity(ApiReturnError apiReturnError) {
        return new ResponseEntity<>(apiReturnError, apiReturnError.getStatus());
    }


    /**
     * Erreur dans la validité des paramètres de la requête
     *
     * @param ex : l'exception catchée
     * @return l'objet du message d'erreur
     */
    @ExceptionHandler(IllegalArgumentException.class)
    protected ResponseEntity<Object> handleIllegalArgumentException(IllegalArgumentException ex) {
        String error = "Erreur dans les paramètres de la requête";
        log.debug(ex.getLocalizedMessage());
        return buildResponseEntity(new ApiReturnError(HttpStatus.BAD_REQUEST, error, ex));
    }

    /**
     * Vérifier les méthodes correspondent avec les URI dans le controller
     *
     * @param ex      : l'exception catchée
     * @param headers headers de la requête http
     * @param status  status de renvoie
     * @param request requête http
     * @return l'objet du message d'erreur
     */
    @Override
    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        String error = "Method is not supported for this request";
        log.error(ex.getLocalizedMessage());
        return buildResponseEntity(new ApiReturnError(HttpStatus.METHOD_NOT_ALLOWED, error, ex));
    }

    @ExceptionHandler(IOException.class)
    protected ResponseEntity<Object> handleIOException(IOException ex) {
        String error = "Erreur dans l'accès aux données";
        return buildResponseEntity(new ApiReturnError(HttpStatus.SERVICE_UNAVAILABLE, error, ex));
    }
}
