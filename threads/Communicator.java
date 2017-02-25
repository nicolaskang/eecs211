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
	private SynchList Speaker; 
	private SynchList Listener; 
	private Lock lock;
	/**
	 * Allocate a new communicator.
	 */
	public Communicator() {
		Speaker = new SynchList(); 
		Listener = new SynchList(); 
		lock = new Lock();
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
		lock.acquire();
		Speaker.add(word);
		Listener.removeFirst(); 
		lock.release();
	}

	/**
	 * Wait for a thread to speak through this communicator, and then return the
	 * <i>word</i> that thread passed to <tt>speak()</tt>.
	 * 
	 * @return the integer transferred.
	 */
	public int listen() {
		int word =(Integer)Speaker.removeFirst(); 
		Listener.add(word);
		return word; 
	}
	
	private static class Speaker implements Runnable{
		private Communicator c;
		
		Speaker(Communicator c){
			this.c = c;
		}
		
		public void run(){
			for(int i = 0; i < 10; i++){
				System.out.println("speaker"+i+ "speaking " + i);
				c.speak(i);
				System.out.println("speaker spoken");
			}
		}
	}
	
	public static void selfTest(){
		Communicator c = new Communicator();
		new KThread(new Speaker(c)).setName("Speaker").fork();
		
		for(int i = 0; i < 10; i++){
			//System.out.println("listener listening " + i);
			int x = c.listen();
			System.out.println("listener"+i+" listened, word = " + x);
		}
		
	}
}
