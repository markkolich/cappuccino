package org.kolich.cappuccino.http;

import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;

public class CappuccinoDefaultHttpClient {
	
	/**
	 * The various static Http schemes we support.
	 */
	private static final String HTTP = "http";
	private static final String HTTPS = "https";
	
	/**
	 * A default set of basic Http parameters.
	 */
	private static final BasicHttpParams defaultParams__;
	
	/**
	 * A connection manager of some sort, in our case, a
	 * ThreadSafeClientConnManager.
	 */
	private static final ClientConnectionManager manager__;
	
	/**
	 * A SchemeRegistry tells the HttpClient we must follow
	 * certain protocols on specific ports.  For example,
	 * Port 80 is always HTTP but Port 443 is always HTTPS.
	 */
	private static final SchemeRegistry scheme__;
		
	/**
	 * "THE" HttpClient of them all; the Big Kahuna;
	 * the head honcho; he's a singleton BTW
	 */
	private static final HttpClient client__;
		
	static {
		
		// The max number of connections per host is equal to the max
		// number of total connections.  This is our default.
		// We have the ability to configure the default maximum number
		// of connections allowed per host, and the default maximum
		// number of connections allowed overall.  In this case, we
		// make these equal to each other.
		defaultParams__ = new BasicHttpParams();
		ConnManagerParams.setMaxTotalConnections(defaultParams__,
				ConnManagerParams.getMaxTotalConnections( defaultParams__ ));
		HttpProtocolParams.setVersion(defaultParams__, HttpVersion.HTTP_1_1);
		
		// We supposedely have to tell the client to do
		// HTTP on some ports, and HTTPS on others.  It dosen't
		// figure that out on it's own anymore.  So the
		// scheme registry is how we tell it to do what, and
		// where.
		scheme__ = new SchemeRegistry();
		scheme__.register(
                new Scheme(HTTP, PlainSocketFactory.getSocketFactory(), 80));
		scheme__.register(
                new Scheme(HTTPS, SSLSocketFactory.getSocketFactory(), 443));
		
		manager__ = new ThreadSafeClientConnManager(defaultParams__, scheme__);
		
		// Get a new client instance with a default set of parameters we
		// setup here in this static context.
		client__ = getInstance(defaultParams__, manager__);
		
	}
	
	/**
	 * Shouldn't call this. We made the constructor private so
	 * the consumers of this class don't try to instantiate a new
	 * CappuccinoDefaultHttpClient for any reason.
	 */
	private CappuccinoDefaultHttpClient () {
		throw new Error("Not supposed to call this!");
	}
	
	/**
	 * Does the real work to instantiate a new HttpClient
	 * instance using the set HttpParams and requested
	 * ClientConnectionManager.  You can call this method
	 * to instantiate a new HttpClient with your own
	 * HttpClient parameters or connection manager.
	 * @return
	 */
	public static final HttpClient getInstance(
		HttpParams params, ClientConnectionManager manager ){
		return new DefaultHttpClient(manager, params);
	}
		
	/**
	 * Get a new thread safe HttpClient using the
	 * Cappuccino HttpClient defaults. This should be
	 * called when you just want to get the Cappuccino default
	 * HttpClient singleton. If you need your own special
	 * thread safe HttpClient with slightly tweaked connection
	 * parameters, then use the getInstance(HttpConnectionManagerParams)
	 * method instead.  Note that the HttpClient returned from
	 * this method has the DEFAULT Jre ProxySelector set which
	 * obeys web-proxy configurations.
	 * @return
	 */
	public static final HttpClient getDefaultInstance(){
		return client__;
	}
			
	/**
	 * Returns a clone of the default Cappuccino HttpClient
	 * parameters. If for any reason you need to get the
	 * default set of connection manager parameters, you can
	 * do so using this method.  This lets you read the
	 * defaults, tweak the HttpConnectionManagerParams with
	 * your own tunables and then get an HttpClient using
	 * your newly tweaked connection tunables.
	 * @return
	 */
	public static final BasicHttpParams getDefaultParameters(){		
		try {
			return (BasicHttpParams)defaultParams__.clone();
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}
	}

}
