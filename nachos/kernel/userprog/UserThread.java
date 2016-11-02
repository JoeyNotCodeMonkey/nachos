// UserThread.java
//	A UserThread is a NachosThread extended with the capability of
//	executing user code.
//
// Copyright (c) 1992-1993 The Regents of the University of California.
// Copyright (c) 1998 Rice University.
// Copyright (c) 2003 State University of New York at Stony Brook.
// All rights reserved.  See the COPYRIGHT file for copyright notice and
// limitation of liability and disclaimer of warranty provisions.

package nachos.kernel.userprog;

import nachos.machine.MIPS;
import nachos.machine.NachosThread;
import nachos.kernel.devices.ConsoleDriver;
import nachos.machine.CPU;
import nachos.kernel.devices.ConsoleManager;

/**
 * A UserThread is a NachosThread extended with the capability of
 * executing user code.  It is kept separate from AddrSpace to provide
 * for the possibility of having multiple UserThreads running in a
 * single AddrSpace.
 * 
 * @author Thomas Anderson (UC Berkeley), original C++ version
 * @author Peter Druschel (Rice University), Java translation
 * @author Eugene W. Stark (Stony Brook University)
 */
public class UserThread extends NachosThread {

    /** The context in which this thread will execute. */
    public final AddrSpace space;
    
    private int quantum = 1000;
    

    private ConsoleDriver consoleDriver;
    
    private int burstLen;
    
    private int remaingTime;
    
    private int waitingTime;
    
    private double responseRatio;

    public int getWaitingTime() {
        return waitingTime;
    }

    public void setWaitingTime(int waitingTime) {
        this.waitingTime = waitingTime;
    }

    public int getBurstLen() {
        return burstLen;
    }

    public void setBurstLen(int burstLen) {
        this.burstLen = burstLen;
        setRemaingTime(burstLen);
        SPN.getInstance().update();
    }

    private boolean isTopLevel;

    // A thread running a user program actually has *two* sets of 
    // CPU registers -- one for its state while executing user code,
    // and one for its state while executing kernel code.
    // The kernel registers are managed by the super class.
    // The user registers are managed here.


    /** User-level CPU register state. */
    private int userRegisters[] = new int[MIPS.NumTotalRegs];

    /**
     * Initialize a new user thread.
     *
     * @param name  An arbitrary name, useful for debugging.
     * @param runObj Execution of the thread will begin with the run()
     * method of this object.
     * @param addrSpace  The context to be installed when this thread
     * is executing in user mode.
     */
    public UserThread(String name, Runnable runObj, AddrSpace addrSpace) {
	super(name, runObj);
	space = addrSpace;
	burstLen = 0;
	
	if(!(runObj instanceof Task)&&!(runObj instanceof ForkTask)) {
	    consoleDriver = ConsoleManager.getInstance().getConsole();
	    isTopLevel = true;
	} else if(runObj instanceof Task){
	    isTopLevel = false;
	    Task t = (Task) runObj;
	    UserThread parentThread = t.getParentThread();
	    consoleDriver = parentThread.getConsoleDriver();
	}else if (runObj instanceof ForkTask){
	    isTopLevel = false;
	    ForkTask t = (ForkTask) runObj;
	    UserThread parentThread = t.getParentThread();
	    consoleDriver = parentThread.getConsoleDriver();
	    
	}
    }
    
    public int getQuantum() {
        return quantum;
    }


    public void setQuantum(int quantum) {
        this.quantum = quantum;
    }

    public boolean isTopLevel() {
        return isTopLevel;
    }

    public ConsoleDriver getConsoleDriver() {
        return consoleDriver;
    }

    public void setConsoleDriver(ConsoleDriver consoleDriver) {
        this.consoleDriver = consoleDriver;
    }



    /**
     * Save the CPU state of a user program on a context switch.
     */
    @Override
    public void saveState() {
	// Save state associated with the address space.
	space.saveState();  

	// Save user-level CPU registers.
	for (int i = 0; i < MIPS.NumTotalRegs; i++)
	    userRegisters[i] = CPU.readRegister(i);

	// Save kernel-level CPU state.
	super.saveState();
    }

    /**
     * Restore the CPU state of a user program on a context switch.
     */
    @Override
    public void restoreState() {
	// Restore the kernel-level CPU state.
	super.restoreState();

	// Restore the user-level CPU registers.
	for (int i = 0; i < MIPS.NumTotalRegs; i++)
	    CPU.writeRegister(i, userRegisters[i]);

	// Restore state associated with the address space.
	space.restoreState();
    }

    public int getRemaingTime() {
	return remaingTime;
    }

    public void setRemaingTime(int remaingTime) {
	this.remaingTime = remaingTime;
    }

    public double getResponseRatio() {
	return responseRatio;
    }

    public void setResponseRatio(double responseRatio) {
	this.responseRatio = responseRatio;
    }
}
