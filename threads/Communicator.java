package nachos.threads;

import nachos.machine.*;

/**
 * A <i>communicator</i> allows threads to synchronously exchange 32-bit
 * messages. Multiple threads can be waiting to <i>speak</i>, and multiple
 * threads can be waiting to <i>listen</i>. But there should never be a time
 * when both a speaker and a listener are waiting, because the two threads can
 * be paired off at this point.
 */
public class Communicator {
	/**
	 * Allocate a new communicator.
	 */
	public Communicator() {
		communicatorLock = new Lock();
		conditionVar = new Condition(communicatorLock);
	}

	/**
	 * Wait for a thread to listen through this communicator, and then transfer
	 * <i>word</i> to the listener.
	 * 
	 * <p>
	 * Does not return until this thread is paired up with a listening thread.
	 * Exactly one listener should receive <i>word</i>.
	 * 
	 * @param word the integer to transfer.
	 */
	public void speak(int word) {
		communicatorLock.acquire();
		wordForTransfer=-1;
		listenThread = KThread.currentThread();
		while(speakThread == null){
			conditionVar.sleep();
		}
		conditionVar.wake();
		
		while(wordForTransfer==-1){
			conditionVar.sleep();
		}
		conditionVar.wake();
		
		word = wordForTransfer;
		listenThread = null;
		while(speakThread != null){
			conditionVar.sleep();
		}
		conditionVar.wake();
		
		communicatorLock.release();
		return;
	}

	/**
	 * Wait for a thread to speak through this communicator, and then return the
	 * <i>word</i> that thread passed to <tt>speak()</tt>.
	 * 
	 * @return the integer transferred.
	 */
	public int listen() {
		//Lib.assertTrue();
		communicatorLock.acquire();
		wordForTransfer=-1;
		speakThread = KThread.currentThread();
		while(listenThread == null){
			conditionVar.sleep();
		}
		conditionVar.wake();
		
		wordForTransfer = speakThread.hashCode();
		while(listenThread != null){
			conditionVar.sleep();
		}
		conditionVar.wake();
		
		speakThread = null;
		communicatorLock.release();
		return wordForTransfer;
	}
	
	private int wordForTransfer;
	private Lock communicatorLock;
	private Condition conditionVar;
	private static KThread speakThread = null;
	private static KThread listenThread = null;
}
