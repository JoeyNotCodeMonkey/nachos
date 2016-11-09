// ProgTest.java
//	Test class for demonstrating that Nachos can load
//	a user program and execute it.  
//
// Copyright (c) 1992-1993 The Regents of the University of California.
// Copyright (c) 1998 Rice University.
// Copyright (c) 2003 State University of New York at Stony Brook.
// All rights reserved.  See the COPYRIGHT file for copyright notice and
// limitation of liability and disclaimer of warranty provisions.

package nachos.kernel.userprog.test;

import nachos.Debug;
import nachos.Options;
import nachos.machine.CPU;
import nachos.machine.NachosThread;
import nachos.kernel.Nachos;
import nachos.kernel.userprog.AddrSpace;
import nachos.kernel.userprog.UserThread;
import nachos.kernel.filesys.OpenFile;

/**
 * This is a test class for demonstrating that Nachos can load a user
 * program and execute it.
 * 
 * @author Thomas Anderson (UC Berkeley), original C++ version
 * @author Peter Druschel (Rice University), Java translation
 * @author Eugene W. Stark (Stony Brook University)
 */
public class testDriver implements Runnable {

    /** The name of the program to execute. */
    private String execName;

    /**
     * Start the test by creating a new address space and user thread,
     * then arranging for the new thread to begin executing the run() method
     * of this class.
     *
     * @param filename The name of the program to execute.
     */
    public testDriver(String filename, int num) {
	int count = 1;
	
	int temp = 0;
	while(count <= 200){
	    
	    
	String name = "testDriver"+ count + "(" + filename + ")";
	
	Debug.println('+', "creating testDriver: " + count);

	execName = filename;
	AddrSpace space = new AddrSpace();
	UserThread t = new UserThread(name, this, space);	
	
	Nachos.scheduler.readyToRun(t);	
	
	count++;
	
	//sleep
	
	int start = 1;
	double randNumber = Math.random();
	int ticks = 0;
	
	while(true) {
	    if(randNumber < 0.1) {
		ticks = start * 500;
		temp++;
		break;
	    }
	    
	    start += 2;
	    randNumber = Math.random();
	}
	
//	if(temp>5){
	Debug.println('+', "Will sleep for :" + ticks);
	Nachos.scheduler.sleepThread(ticks);
	//}
	
	}
	
	
	
    }

    /**
     * Entry point for the thread created to run the user program.
     * The specified executable file is used to initialize the address
     * space for the current thread.  Once this has been done,
     * CPU.run() is called to transfer control to user mode.
     */
    public void run() {
	OpenFile executable;

	if((executable = Nachos.fileSystem.open(execName)) == null) {
	    Debug.println('+', "Unable to open executable file: " + execName);
	    Nachos.scheduler.finishThread();
	    return;
	}

	AddrSpace space = ((UserThread)NachosThread.currentThread()).space;
	
	if(space.exec(executable) == -1) {
	    Debug.println('+', "Unable to read executable file: " + execName);
	    Nachos.scheduler.finishThread();
	    return;
	}

	space.initRegisters();		// set the initial register values
	space.restoreState();		// load page table register

	double randNumber = Math.random();
	double d  = randNumber *100;
	int randomInt = (int)d+1; //1-100
	
	int count = 0;
	while(true) {
	    if(randNumber < 0.1) {
		randomInt = randomInt  + 100 * count;
		break;
	    }
	    //Debug.println('+', "Random num is: " + randNumber);
	    count++;
	    randNumber = Math.random();
	}
	
	Debug.println('+', "random for this thread is : " + randomInt);
	CPU.writeRegister(4, randomInt);
	
	CPU.runUserCode();			// jump to the user progam
	Debug.ASSERT(false);		// machine->Run never returns;
	// the address space exits
	// by doing the syscall "exit"
    }

    /**
     * Entry point for the test.  Command line arguments are checked for
     * the name of the program to execute, then the test is started by
     * creating a new ProgTest object.
     */
    public static void start() {
	Debug.ASSERT(Nachos.options.FILESYS_REAL || Nachos.options.FILESYS_STUB,
			"A filesystem is required to execute user programs");
	final int[] count = new int[1];
	Nachos.options.processOptions
		(new Options.Spec[] {
			new Options.Spec
				("-v",
				 new Class[] {String.class},
				 "Usage: -v <executable file>",
				 new Options.Action() {
				    public void processOption(String flag, Object[] params) {
					new testDriver((String)params[0], count[0]++);
				    }
				 })
		 });
    }
}
