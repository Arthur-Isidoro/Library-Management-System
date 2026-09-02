package com.seuprojeto.library.exception;

public class LivroIndisponivelException extends RuntimeException {

    public LivroIndisponivelException(String message) {
        super(message);
    }
}