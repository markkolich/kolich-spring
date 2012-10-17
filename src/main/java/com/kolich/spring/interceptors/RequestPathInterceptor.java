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

package com.kolich.spring.interceptors;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static java.util.regex.Pattern.CASE_INSENSITIVE;
import static java.util.regex.Pattern.compile;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.kolich.spring.exceptions.InvalidResourceException;

public final class RequestPathInterceptor extends HandlerInterceptorAdapter
	implements InitializingBean {

	private static final Logger logger__ = 
		Logger.getLogger(RequestPathInterceptor.class);
	
	private List<String> requestMappings_ = new ArrayList<String>();	
	private List<PathMapping> mappings_ = new ArrayList<PathMapping>();
		
	@Override
	public void afterPropertiesSet() throws Exception {
		// Convert each of the {METHOD}:{PATH} mappings into a proper
		// list of PathMapping's for processing.
		for(final String mapping : requestMappings_) {
			try {
				final PathMapping pm = new PathMapping(mapping);
				mappings_.add(pm);
				logger__.debug("Compiled path mapping: " + pm);
			} catch (Exception e) {
				logger__.warn("Failed to add request path mapping: " +
					mapping, e);
			}
		}
		// Be sure that we have at least one mapping to compare against;
		// if not, then this bean is useless so might as well go down
		// in flames.
		checkState(!mappings_.isEmpty(), "Request path mappings cannot " +
			"be empty!");
	}
	
	@Override
	public boolean preHandle(final HttpServletRequest request,
		final HttpServletResponse response, final Object handler)
		throws Exception {
		final String url = request.getRequestURL().toString();
		boolean found = false;
		for(final PathMapping pm : mappings_) {
			// Does the incoming request match the request of this mapping?
			if(pm.isWildcardMethod() || pm.getMethod().equalsIgnoreCase(request.getMethod())) {
				if(pm.getMatcher(url).find()) {
					found = true;
					break;
				}
			}
		}
		// If we got here, and found is false, that means that the incoming
		// request path does not match any of our expected mappings.
		if(!found) {
			logger__.debug("No path mapping was found that matched " +
				"to URL: " + url);
			throw new InvalidResourceException("No path mapping was found " +
				"that matched to URL: " + url);
		}
		// Always return true.
		return true;
	}
	
	public void setRequestMappings(List<String> requestMappings) {
		requestMappings_ = requestMappings;
	}
	
	private static final class PathMapping {
		
		private static final String MAPPING_COLON = ":";
		private static final String MAPPING_WILDCARD_METHOD = "*";
		
		private final String method_;
		private final Pattern regex_;
		
		private PathMapping(final String mapping) {
			checkNotNull(mapping, "Mapping cannot be null");
			final String[] tokens = mapping.split(MAPPING_COLON, 2);
			checkState(tokens.length >= 2, "Could not split mapping into " +
				"the right number of '" + MAPPING_COLON + "' separated " +
					"tokens.");
			method_ = tokens[0];
			// Compile the regex.
			regex_ = compile(tokens[1], CASE_INSENSITIVE);
		}
		
		public String getMethod() {
			return method_;
		}
		
		public boolean isWildcardMethod() {
			return method_.equals(MAPPING_WILDCARD_METHOD);
		}
		
		public Matcher getMatcher(final String input) {
			return regex_.matcher(input);
		}
		
		@Override
		public String toString() {
			return method_ + MAPPING_COLON + regex_;
		}
		
	}
	
}
