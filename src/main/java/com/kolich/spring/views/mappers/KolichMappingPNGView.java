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

package com.kolich.spring.views.mappers;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;

import com.kolich.spring.views.AbstractKolichView;
import com.kolich.spring.views.KolichViewSerializable;

public final class KolichMappingPNGView extends AbstractKolichView {
			
	/**
     * Default content type. Overridable as bean property.
     */
	private static final String DEFAULT_CONTENT_TYPE = "image/png";
    
    public KolichMappingPNGView() {
    	super(DEFAULT_CONTENT_TYPE);
    }
    
	@Override
	public void myRenderMergedOutputModel(final KolichViewSerializable payload,
		final HttpServletRequest request, final HttpServletResponse response)
		throws Exception {
		final ServletOutputStream os = response.getOutputStream();
		// Be sure to set the Content-Length header on a MP3 audio file
		// response to the client (some MP3 players don't like chunked
		// transfer encoding so this fixes that).
		final byte[] bytes = payload.getEntity().getBytes();
		response.setContentLength(bytes.length);
		os.write(bytes);
		// Quietly close the output stream.
		IOUtils.closeQuietly(os);
	}
	
}
