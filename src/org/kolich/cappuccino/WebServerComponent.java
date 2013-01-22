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
import java.io.IOException;

import org.kolich.cappuccino.logging.CappuccinoLogger;
import org.restlet.Component;
import org.restlet.data.Protocol;

/**
 * The actual component that starts and manages the
 * internal Restlet HTTP server.
 * 
 * @author Mark Kolich
 *
 */
public class WebServerComponent {
	
	/**
	 * The name of the directory that the web-server will
	 * serve files out of.
	 */
	private static final String WWW_ROOT_DIR = "www";
	
	/**
	 * The component; the grand-daddy of them all.
	 */
	private Component component_;

	private File serverRoot_;
	private String listenAddress_;
	private int listenPort_;
	
	public WebServerComponent ( File root, String listenAddress,
			int listenPort ) {
		
		this.listenAddress_ = listenAddress;
		this.listenPort_ = listenPort;
		
		this.component_ = new Component();		
		this.component_.getServers().add(Protocol.HTTP,
							this.listenAddress_, this.listenPort_);
		this.component_.getClients().add(Protocol.FILE);
		
		// The server root is the root given by the user plus
		// the default www/ root directory.
		this.serverRoot_ = new File(root, WWW_ROOT_DIR);
		if(!this.serverRoot_.exists()){
			if(!this.serverRoot_.mkdir()){
				String message = "Sorry, I was unable to create the " +
							"web-server root directory";
				try {
					throw new Error(String.format("%s at:\n%s",
							message, this.serverRoot_.getCanonicalPath()));
				}
				catch(IOException e) {
					throw new Error(String.format("%s!",message));
				}
			}
		}
		
		this.component_.getDefaultHost().attachDefault(
							new CappuccinoApplication(
									this.serverRoot_));
		
		CappuccinoLogger.logger.info("Restlet component " + toString() +
				" setup successfully!");
		
	}
	
	public void start() throws Exception {
		this.component_.start();
	}
	
	public void stop() throws Exception {
		this.component_.stop();
	}
	
	public String getListenAddress() {
		return this.listenAddress_;
	}
	
	public int getListenPort() {
		return this.listenPort_;
	}
	
	@Override
	public String toString(){
		return String.format("%s:%s", this.listenAddress_, this.listenPort_);
	}
	
}
