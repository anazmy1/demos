package com.rms.rest.exception;

public class NonUniqueException extends RuntimeException {

    public NonUniqueException( String resourceName, String value){
        super( generateMessage(resourceName, value) );
    }
    private static String generateMessage(String resourceName, String value ) {
        StringBuilder messageBuilder = new StringBuilder(resourceName).append(" ").
                 append(value).append(" is already used" );
        return messageBuilder.toString();
    }
}
