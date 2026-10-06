package com.ecommerce.api.exception;

/** Se lanza si se pide más cantidad que el stock disponible. Se traduce a HTTP 400. */
public class StockInsuficienteException extends RuntimeException {

    public StockInsuficienteException(String producto, int disponible, int solicitado) {
        super("Stock insuficiente para '" + producto + "': disponible " + disponible
                + ", solicitado " + solicitado);
    }
}
