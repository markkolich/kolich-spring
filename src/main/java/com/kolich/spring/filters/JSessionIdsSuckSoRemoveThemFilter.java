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

package com.kolich.spring.filters;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

/**
 * Removes the annoying <pre>;jsessionid=63657FD23453465</pre> from URL's
 * that happen to make their way into the app.
 * 
 * @author Mark Kolich
 *
 */
public final class JSessionIdsSuckSoRemoveThemFilter implements Filter {

	@Override
	public void doFilter(final ServletRequest req,
		final ServletResponse res, final FilterChain chain)
		throws IOException, ServletException {
		final HttpServletRequest request = (HttpServletRequest) req;
        final HttpServletResponse response = (HttpServletResponse) res;
        if(request.isRequestedSessionIdFromURL()) {
        	final String url = request.getRequestURL().append(
        		(request.getQueryString() != null) ?
        			"?" + request.getQueryString() : "").toString();
        	response.sendRedirect(url);
        }
		final HttpServletResponseWrapper wrappedResponse =
	    	new HttpServletResponseWrapper(response) {
	        @Override
	        public String encodeRedirectURL(final String url) {
	            return url;
	        }
	        @Override
	        public String encodeURL(final String url) {
	        	return url;
	        }
		};
		chain.doFilter(req, wrappedResponse);
	}
	
	@Override
	public void destroy() {
		// Nothing.
	}

	@Override
	public void init(final FilterConfig config) throws ServletException {
		// Nothing
	}
	
}
