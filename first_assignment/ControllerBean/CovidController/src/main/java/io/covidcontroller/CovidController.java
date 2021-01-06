/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.covidcontroller;

import java.beans.*;

/**
 *
 * @author azzeg
 */
public class CovidController implements VetoableChangeListener{
    private int reducedCapacity=25;
    
    public CovidController() {}
    public CovidController(int reducedCapacity) {
        this.reducedCapacity=reducedCapacity;
    }
    

    @Override
    public void vetoableChange(PropertyChangeEvent evt) throws PropertyVetoException {
        if("numPassenger".equals(evt.getPropertyName()) && (int)evt.getNewValue() > reducedCapacity){
           throw new PropertyVetoException("Passengers number exceed the reduced capacity", evt); 
        }
    }
    
}
