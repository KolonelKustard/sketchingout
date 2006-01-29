package com.totalchange.sketchingout;

import java.util.ArrayList;

/***
 * <p>This class represents a thread factory for processing completed drawings.</p>
 * 
 * @author RalphJones
 */
public class CompletionFactory {

	/***
	 * <p>This Thread means drawings are processed as complete out-of-process of
	 * the web server.  It means users don't have a stupid long wait when they
	 * complete a drawing as the drawing gets processed into emails and into the
	 * gallery.</p>
	 * 
	 * @author RalphJones
	 */
	private class CompletionThread extends Thread {
		private CompletionFactory parent;
		private boolean stillRunning = true;
		
		public CompletionThread(CompletionFactory parent) {
			this.parent = parent;
		}

		public void run() {
			while (stillRunning) {
				// Ask the parent for the next drawing
				String drawingID = parent.getNextDrawingToProcess();
				
				// If got a drawing, process it.  Otherwise tell thread to wait.
				if (drawingID != null) {
					CompleteDrawingProcessor.processDrawing(drawingID);
				}
				else {
					try {
						this.wait();
					}
					catch (InterruptedException ie) {
						// If gets interrupted, just stop
						stillRunning = false;
					}
				}
			}
		}
	}
	
	/***
	 * Stores a list of drawings that are pending being processed as complete
	 */
	private ArrayList pendingDrawings = new ArrayList();
	
	/***
	 * Represents the single thread that processes completed drawings
	 */
	private CompletionThread thread = new CompletionThread(this);
	
	/***
	 * Returns the next drawing to process from the list
	 * 
	 * @return Next drawing to process
	 */
	private synchronized String getNextDrawingToProcess() {
		// If no drawings return null
		if (pendingDrawings.isEmpty()) return null;
		
		// Otherwise get first drawing still waiting then get rid of it from the
		// list.
		String next = (String) pendingDrawings.get(0);
		pendingDrawings.remove(0);
		
		return next;
	}
	
	public CompletionFactory() {
		// Just start the thread going - albeit with nothing to process yet
		thread.start();
	}
	
	public void processDrawing(String drawingID) {
		// Add to the list to process
		pendingDrawings.add(drawingID);
		
		// Notify the thread that it should start doing something (if it's not already)
		thread.notify();
	}
}
