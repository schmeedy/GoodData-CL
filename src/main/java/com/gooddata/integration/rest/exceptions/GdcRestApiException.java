/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.gooddata.integration.rest.exceptions;

/**
 * @author jiri.zaloudek
 */
public class GdcRestApiException extends Exception {


    /**
     * Creates a new instance of <code>GdcRestApiExecption</code> without detail message.
     */
    public GdcRestApiException() {
    }


    /**
     * Constructs an instance of <code>GdcRestApiExecption</code> with the specified detail message.
     *
     * @param msg the detail message.
     */
    public GdcRestApiException(String msg) {
        super(msg);
    }


    @Override
    public String getLocalizedMessage() {
        // TODO Auto-generated method stub
        return super.getLocalizedMessage();
    }


    @Override
    public String getMessage() {
        // TODO Auto-generated method stub
        return super.getMessage();
    }


    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return super.toString();
    }


}
