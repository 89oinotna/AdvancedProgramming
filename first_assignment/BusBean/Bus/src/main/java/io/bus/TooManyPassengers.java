/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.bus;

/**
 *
 * @author azzeg
 */
public class TooManyPassengers extends RuntimeException{
    
    public TooManyPassengers(){
        super();
    }
    
    public TooManyPassengers(String msg){
        super(msg);
    }
    
}
