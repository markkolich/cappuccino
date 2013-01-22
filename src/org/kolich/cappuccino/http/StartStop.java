package org.kolich.cappuccino.http;

import static org.kolich.cappuccino.constants.Constants.HTTP;
import static org.kolich.cappuccino.constants.Constants.LOCALHOST_127;
import static org.kolich.cappuccino.resources.ExitResource.EXIT;

import java.io.IOException;
import java.net.SocketException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.kolich.cappuccino.logging.CappuccinoLogger;

public class StartStop {
	
	/**
	 * The multi-thread safe HttpClient that does the work behind
	 * the scenes for the StartStop class.
	 */
	private static final HttpClient client_;
	static {
		client_ = CappuccinoDefaultHttpClient.getDefaultInstance();
	}
	
	/**
	 * Attempts to call the /exit resource on localhost
	 * to see if we can stop the other Cappuccino instance
	 * that's running.
	 */
	public static final void stopOtherInstance(final int port) {
		
		final String url = String.format("%s%s:%s%s", HTTP,
									LOCALHOST_127, port, EXIT);
		
		CappuccinoLogger.logger.info("Attempting to stop other server " +
				"instance via: " + url);
		
		final HttpGet get = new HttpGet(url);
		HttpEntity entity = null;
		try {
			final HttpResponse response = client_.execute(get);
			entity = response.getEntity();
			if(entity==null){
				throw new Exception("Unable to load response entity!");
			}
			final int status = response.getStatusLine().getStatusCode();
			if(status != HttpStatus.SC_OK){
				throw new Exception("Failed to stop other instance " +
						"(status = " + status + ")");
			}
		} catch (SocketException se) {
			CappuccinoLogger.logger.trace("Semi-anticipated socket " +
					"exception.", se);
		} catch (Exception e) {
			CappuccinoLogger.logger.warn(e);
		}
		finally {
			if(entity != null){
				try {
					entity.consumeContent();
				} catch (IOException e) {
					CappuccinoLogger.logger.warn(e);
				}
			}
			client_.getConnectionManager().closeExpiredConnections();
		}
		
	}
	
}
