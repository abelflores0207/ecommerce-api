package com.ecommerce.api.exception;

/** Se lanza si ya existe un usuario con ese email. Se traduce a HTTP 409. */
public class EmailDuplicadoException extends RuntimeException {

    public EmailDuplicadoException(String email) {
        super("Ya existe un usuario registrado con el email " + email);
    }
}
