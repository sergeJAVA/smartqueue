package com.smartqueue.queue.handler;

import com.smartqueue.queue.exception.QueueEntryNotFoundException;
import com.smartqueue.queue.exception.QueueEventNotFoundException;
import com.smartqueue.queue.exception.QueueNotFoundException;
import com.smartqueue.queue.exception.UserAlreadyInQueueException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserAlreadyInQueueException.class)
    public ResponseEntity<String> userAlreadyInQueue(UserAlreadyInQueueException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(QueueNotFoundException.class)
    public ResponseEntity<String> queueNotFound(QueueNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(QueueEventNotFoundException.class)
    public ResponseEntity<String> queueEventNotFound(QueueEventNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(QueueEntryNotFoundException.class)
    public ResponseEntity<String> queueEntryNotFound(QueueEntryNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

}
