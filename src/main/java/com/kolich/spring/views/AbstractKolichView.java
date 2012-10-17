/**
 * Copyright (c) 2012 Mark S. Kolich
 * http://mark.koli.ch
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

package com.kolich.spring.views;

import static org.apache.http.HttpHeaders.CACHE_CONTROL;
import static org.apache.http.HttpHeaders.EXPIRES;
import static org.apache.http.HttpHeaders.PRAGMA;

import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.web.servlet.view.AbstractView;

public abstract class AbstractKolichView extends AbstractView {
	
	private static final Logger logger__ = 
		Logger.getLogger(AbstractKolichView.class);
	
	public static final String VIEW_PAYLOAD = "payload";
			
	private static final String PRAGMA_NO_CACHE = "no-cache";
	private static final String CACHE_CONTROL_NO_CACHE =
		"no-cache, no-store, max-age=0";
					
	public AbstractKolichView(String contentType) {
		setContentType(contentType);
	}
	
	@Override
    public void prepareResponse(final HttpServletRequest request,
    	final HttpServletResponse response) {
		// All of the other default stuff the abstract view
		// handles for the caller.
    	response.setContentType(getContentType());
    }

	@Override
	public void renderMergedOutputModel(final Map<String, Object> model,
		final HttpServletRequest request,
		final HttpServletResponse response)
		throws Exception {
		try {
			final KolichViewSerializable payload =
				(KolichViewSerializable)model.get(VIEW_PAYLOAD);
			// If the payload has an Expires date attached to it, then
			// we should honor that by adding the Expires: header to the
			// response.  Controllers may set this so that upstream caches
			// do not attempt to cache the resource.
			final Date expires;
			if((expires = payload.getExpires()) != null) {
				response.addDateHeader(EXPIRES, expires.getTime());
			}
			// If we are asked to disable caching of this response,
			// then we need to append the correct Cache-Control and Pragma
			// headers here.
			if(payload.disableCaching()) {
				response.addHeader(PRAGMA, PRAGMA_NO_CACHE);
	    		response.addHeader(CACHE_CONTROL, CACHE_CONTROL_NO_CACHE);
			}
			// Call the custom prepare response after anything else has
	    	// been set so that the view can override these if necessary.
			myPrepareResponse(payload, request, response);
			myRenderMergedOutputModel(payload, request, response);
		} catch(Exception e) {
			logger__.error(e);
			throw e;
		}
	}
	
	public void myPrepareResponse(final KolichViewSerializable payload,
		final HttpServletRequest request, final HttpServletResponse response) {
		// Optional override.
	}
	
	public void myRenderMergedOutputModel(final KolichViewSerializable payload,
		final HttpServletRequest request, final HttpServletResponse response)
		throws Exception {
		// Optional override.
	}
	
		
}
