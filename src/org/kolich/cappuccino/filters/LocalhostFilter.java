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

package org.kolich.cappuccino.filters;

import static org.kolich.cappuccino.constants.Constants.LOCALHOST;
import static org.kolich.cappuccino.constants.Constants.LOCALHOST_127;
import static org.kolich.cappuccino.resources.ExitResource.EXIT;

import java.util.concurrent.ConcurrentLinkedQueue;

import org.kolich.cappuccino.logging.CappuccinoLogger;
import org.restlet.Filter;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;

/**
 * Protects critical resources so that only localhost (127.0.0.1)
 * can issues requests to them.
 * 
 * @author Mark Kolich
 *
 */
public class LocalhostFilter extends Filter {
	
	private static final ConcurrentLinkedQueue<String> protectedResources_ =
								new ConcurrentLinkedQueue<String>();
	static {
		// All of the resources that should be restricted only to
		// localhost should be placed in this list.  This queue
		// is completely thread-safe; hence why we're using it.
		protectedResources_.add(EXIT);
	}
	
	@Override
	protected int beforeHandle(Request request, Response response) {
		int status = CONTINUE;
		final String path = request.getResourceRef().getPath();
		if(isProtectedResource(path)){
			final String ip = request.getClientInfo().getAddress();
			if(!isLocalhost(ip)){
				response.setStatus(Status.CLIENT_ERROR_FORBIDDEN);
				status = STOP;
				CappuccinoLogger.logger.warn("Denying access to " + path +
							" from IP address: " + ip);
			}
		}
		return status;
	}

	private boolean isProtectedResource(String path) {
		return protectedResources_.equals(path);
	}
	
	private boolean isLocalhost(String ip){
		return (ip.contains(LOCALHOST) || ip.contains(LOCALHOST_127));
	}
	
}
