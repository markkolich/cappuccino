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

package org.kolich.cappuccino.logging;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.slf4j.bridge.SLF4JBridgeHandler;

public class CappuccinoLogger {
			
	// The default log level is INFO.
	private static final Level DEFAULT_LOGGER_LEVEL = Level.DEBUG;
	
	// STDOUT
	private static final String SYSTEM_OUT = "System.out";
	
	// This is the pattern that Log4j will use when logging:
	// %d{ISO8601} %5p %c{1}:%L - %m%n
	private static final String LOG_PATTERN = "%d{ISO8601} %5p [%l] - %m%n";
	
	// We use the root logger for everything so we can capture all of the output
	// from shared libraries that use Log4j too
	public static final Logger logger = Logger.getRootLogger();
	static {
		try {
						
			// Create a new pattern layout with our requested log pattern.
			final PatternLayout pl = new PatternLayout( LOG_PATTERN );
						
			// Set the default level of this logger.
			logger.setLevel( DEFAULT_LOGGER_LEVEL );
						
			// Also log to the console so we can see what's going on.
			final ConsoleAppender cp = new ConsoleAppender( pl, SYSTEM_OUT );
			cp.setImmediateFlush( true );
            logger.addAppender( cp );
            
			// Install the SLF4JBridgeHandler so that java.util.logging
			// output from Restlet is redirected to Log4j
			SLF4JBridgeHandler.install();
            
		}
		catch ( Exception e ) {
			// If something bad happened, log the exception to System.err
			e.printStackTrace( System.err );
		}
	}

}
