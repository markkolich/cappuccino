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

import static org.kolich.cappuccino.constants.Constants.FILE_AND_SLASHES;
import static org.kolich.cappuccino.resources.ExitResource.EXIT;

import java.io.File;

import org.kolich.cappuccino.filters.LocalhostFilter;
import org.kolich.cappuccino.handlers.NodeHandler;
import org.kolich.cappuccino.logging.CappuccinoLogger;
import org.kolich.cappuccino.resources.ExitResource;
import org.restlet.Application;
import org.restlet.Restlet;
import org.restlet.Router;

/**
 * Acts as the default Restlet application that will
 * attach to the router for the web-server.
 * 
 * @author Mark Kolich
 *
 */
public class CappuccinoApplication
	extends Application {
	
	/**
	 * The web-server root on the file system.  Will usually
	 * be the current directory relative to where the app is
	 * started.
	 */
	private File root_;
	
	/**
	 * The canonical resolved, Restlet web-server root on
	 * the file system.
	 */
	private String resolvedRoot_;
	
	/**
	 * Create a new ReallySimpleWebServerApplication using
	 * the root as the web-server root on the file system.
	 * 
	 * @param root
	 */
	public CappuccinoApplication(File root) {
		
		super();		
		this.root_ = root;
		
		try {		
			final String resolving = this.root_.getCanonicalPath();
			this.resolvedRoot_ = String.format("%s%s",
										FILE_AND_SLASHES, resolving);
			CappuccinoLogger.logger.info("Resolved web-server document root: " +
											this.resolvedRoot_);	
		}
		catch(Exception e) {
			CappuccinoLogger.logger.error(e);
			throw new Error(e);
		}
	
	}
	
	/**
	 * Create a new Restlet application root.
	 */
	@Override
	public Restlet createRoot() {
		
		final Router router = new Router(getContext());
		
		// For graceful exiting of the server; to gracefully stop it.
		router.attach(EXIT, ExitResource.class);
		
		// We use our own custom directory listing resource that
		// extends Directory to intercept and handle the pretty
		// printing of directories accordingly.
		final NodeHandler nh = new NodeHandler(
				getContext(), this.resolvedRoot_);
		nh.setListingAllowed(true);
		nh.setModifiable(false);
		
		router.attachDefault(nh);
		
		// Attach the filters in order, especially the
		// the localhost filter that protects certain resources.
		LocalhostFilter localhostFilter = new LocalhostFilter();
		localhostFilter.setNext(router);
		
		return localhostFilter;
		
	}
	
}
