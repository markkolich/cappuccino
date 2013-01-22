package org.kolich.cappuccino.handlers;

import org.kolich.cappuccino.logging.CappuccinoLogger;
import org.restlet.Context;
import org.restlet.Directory;
import org.restlet.data.Request;
import org.restlet.data.Response;

public class NodeHandler extends Directory {

	public NodeHandler(Context context, String rootUri){
		super(context, rootUri);
	}
	
	@Override
	public void handle(Request request, Response response){
				
		final String path = request.getResourceRef().getPath();
		CappuccinoLogger.logger.debug("Received incoming request " +
				"for path " + path );
		
		super.handle(request, response);
		
	}
	
}
