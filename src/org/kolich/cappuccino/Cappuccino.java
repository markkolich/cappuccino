/**
 * Copyright (c) 2009 Mark S. Kolich
 * http://mark.kolich.com
 *
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
*/

package org.kolich.cappuccino;

import java.io.File;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.kolich.cappuccino.http.StartStop;
import org.kolich.cappuccino.logging.CappuccinoLogger;

/**
 * A really simple web-server powered by the Restlet
 * internal HTTP server.
 * 
 * @author Mark Kolich
 *
 */
public class Cappuccino {
	
	/**
	 * If there's another Cappuccino running, STOP_OTHER_SERVER_TRIES is the 
	 * number of times to try stopping it gracefully before we give up.
	 */
	private static final int STOP_OTHER_SERVER_TRIES = 5;
	
	/**
	 * If there's another Cappuccino running, STOP_OTHER_SERVER_WAIT is
	 * the amount of time to wait between attempts to stop the other server,
	 * in milliseconds.
	 */ 
	private static final long STOP_OTHER_SERVER_WAIT = 4000L; /* 4 seconds */
	
	/**
	 * The default server root is the current directory from where
	 * the app is started from.
	 */
	private static final String DEFAULT_SERVER_ROOT = ".";
	
	/**
	 * Binds to 0.0.0.0 by default so this web-server is accessible
	 * to everyone.
	 */
	private static final String DEFAULT_LISTEN_ADDRESS = "0.0.0.0";
	
	/**
	 * By default, this server listens for requests on port 8080.
	 */
	private static final int DEFAULT_LISTEN_PORT = 8080;	
	
	public static void main ( String [] args ) {
		
		File root;
		String listenAddress = null;
		int port = -1;
				
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch(Exception e){
			CappuccinoLogger.logger.warn( "Unable to set native system look " +
											"and feel. Continuing ...", e );
		}
		
		try {
			
			try {
				port = Integer.parseInt(args[0]);				
			}
			catch ( Exception e ) {
				CappuccinoLogger.logger.trace(e);
				port = DEFAULT_LISTEN_PORT;
			}
			
			try {
				listenAddress = args[1];
			}
			catch ( Exception e ) {
				CappuccinoLogger.logger.trace(e);
				listenAddress = DEFAULT_LISTEN_ADDRESS;
			}
			
			try {
				root = new File(args[2]);
			}
			catch ( Exception e ) {
				CappuccinoLogger.logger.trace(e);
				root = new File(DEFAULT_SERVER_ROOT);
			}
			
			// Go do some real work.
			mainBody(root, listenAddress, port);
			
		}
		catch ( Throwable t ) {
			CappuccinoLogger.logger.fatal("Could not start web-server!", t);
			showError(t);
		}
		
	}
	
	private static final void mainBody(final File root,
		final String listen, final int port) {
		
		final WebServerComponent wsc =
					new WebServerComponent(root, listen, port);

		// Start the server, and try to stop any other servers running
		// on the port we need.
		boolean started = false;
		for (int i = 0; i < STOP_OTHER_SERVER_TRIES && !started; i++) {
			try {
				CappuccinoLogger.logger.debug("Attempting to start server " +
						"on port " + port);
				wsc.start();
				started = true;
			}
			catch (Exception e) {
				StartStop.stopOtherInstance(port);
				try {
					wsc.stop();
				} catch (Exception ce) {
					CappuccinoLogger.logger.warn("Failed to stop service " +
							"running on Port " + port, ce);
				}
				try {
					Thread.sleep(STOP_OTHER_SERVER_WAIT);
				} catch (InterruptedException f) {
					CappuccinoLogger.logger.warn(f);
				}
				started = false;
			}
		}
		
		if(!started){
			throw new Error("Unable to start Cappuccino! Is port " +
					port + " already in use?");
		}
		
		CappuccinoLogger.logger.info("Cappuccino started successfully!");
		
	}
	
	private static final void showError(final String message){		
		final Runnable worker = new Runnable() {
	        @Override
			public void run() {
		        JOptionPane.showMessageDialog(null, message,
		        		"Error", JOptionPane.ERROR_MESSAGE);
		        System.exit(-1);
	        }
	    };
	    SwingUtilities.invokeLater(worker);		
	}
	
	private static final void showError(final Throwable t){
		showError(t.getMessage());
	}

}
