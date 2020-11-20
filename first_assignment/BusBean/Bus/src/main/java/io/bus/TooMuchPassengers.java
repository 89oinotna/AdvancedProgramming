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
public class TooMuchPassengers extends RuntimeException{
    
    public TooMuchPassengers(){
        super();
    }
    
    public TooMuchPassengers(String msg){
        super(msg);
    }
    
}
