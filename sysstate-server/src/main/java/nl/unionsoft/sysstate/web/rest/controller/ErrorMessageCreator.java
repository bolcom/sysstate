package nl.unionsoft.sysstate.web.rest.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import nl.unionsoft.sysstate.sysstate_1_0.ErrorMessage;

public class ErrorMessageCreator {

    private static final Logger log = LoggerFactory.getLogger(ErrorMessageCreator.class);
    
    public static ResponseEntity<ErrorMessage> createMessageFromException(Exception ex) {
        log.error("Handling Exception", ex);
        ErrorMessage errorMessage = new ErrorMessage();
        errorMessage.setMessage(ex.getMessage());
        return new ResponseEntity<ErrorMessage>(errorMessage, HttpStatus.BAD_REQUEST);
    }
}
