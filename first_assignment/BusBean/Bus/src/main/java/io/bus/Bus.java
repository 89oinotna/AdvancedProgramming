/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.bus;

import java.beans.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * @author azzeg
 */
public class Bus {
    private int capacity=50;
    private boolean doorOpen=false;
    private AtomicInteger numPassenger=new AtomicInteger(20);
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final PropertyChangeSupport mPcs =
        new PropertyChangeSupport(this);
    private VetoableChangeSupport mVcs =
        new VetoableChangeSupport(this);
    
    private ScheduledFuture<?> mSf;
    //todo atomic counter for clising queue
    
    public Bus(){}
    
    public Bus(int capacity, int numPassenger){
        this.capacity=capacity;
        this.numPassenger.set(numPassenger);
    }
    
   
    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public boolean isDoorOpen() {
        return doorOpen;
    }

    public void setDoorOpen(boolean doorOpen) {
        boolean oldDoorOpen = this.doorOpen;
        this.doorOpen = doorOpen;
        mPcs.firePropertyChange("doorOpen",
                                   oldDoorOpen, doorOpen);
    }
    
    public int getNumPassenger() {
        return numPassenger.get();
    }

    public synchronized void increasePassengers(int increase){
        synchronized(numPassenger){
            System.out.println("Passengers entering: "+increase); 
            setNumPassenger(numPassenger.get()+increase);
        }
    
    }
    
    public synchronized void setNumPassenger(int numPassenger) {
        int oldNumPassenger = this.numPassenger.get();
        try{
            mVcs.fireVetoableChange("numPassenger",
                                    oldNumPassenger, numPassenger);
        }catch(PropertyVetoException e){
            System.out.println(e.getMessage());
            throw new TooManyPassengers(e.getMessage());
        }
        if(numPassenger>capacity){
            throw new TooManyPassengers("Passengers number exceed the full capacity");
        }
        //people can leave or enter the bus so i need that the ports are open
        //i need to cancel previous task es leaving if now people are also entering
        //otherwise ports will be closed while entering
        ScheduledFuture<?> sf=scheduler.schedule(() -> {
                synchronized(mSf){
                    setDoorOpen(false);
                }
            }, 3, TimeUnit.SECONDS);
        try{
            synchronized(mSf){   
                if(mSf!=null) mSf.cancel(true);
            }
        }catch(NullPointerException ignore){
        }
        finally{
                mSf=sf;
        }
        setDoorOpen(true);
        
        
        this.numPassenger.set(numPassenger);
        mPcs.firePropertyChange("numPassenger",
                                 oldNumPassenger, numPassenger);
    }
    
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        mPcs.addPropertyChangeListener(listener);
    }
    
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        mPcs.removePropertyChangeListener(listener);
    }
    
    public void addVetoableChangeListener(VetoableChangeListener listener) {
        mVcs.addVetoableChangeListener(listener);
    }
    
    public void removeVetoableChangeListener(VetoableChangeListener listener) {
        mVcs.removeVetoableChangeListener(listener);
    }

    
    public void activate(){
        scheduler.scheduleWithFixedDelay(
            () -> {
                synchronized(numPassenger){
                    int leaving=(int)(Math.random()*numPassenger.get());
                    System.out.println("Passengers leaving: " + leaving);
                    setNumPassenger(numPassenger.get()-leaving);
                }
            },
            10, 10, TimeUnit.SECONDS);
    }
    
}
