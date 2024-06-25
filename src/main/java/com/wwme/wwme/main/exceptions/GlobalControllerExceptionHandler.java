package com.wwme.wwme.main.exceptions;


import com.wwme.wwme.group.DTO.ErrorWrapDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class GlobalControllerExceptionHandler {
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ResponseEntity<?> handleGlobalControllerException(Exception e) {
        return new ResponseEntity<>(new ErrorWrapDTO(e.toString()), HttpStatus.BAD_REQUEST);
    }
}
