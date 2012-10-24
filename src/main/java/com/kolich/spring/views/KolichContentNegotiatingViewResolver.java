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

import java.util.List;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.view.ContentNegotiatingViewResolver;

/**
 * We use our own view resolver because Spring's default
 * {@link ContentNegotiatingViewResolver} does not quite do what we want. In
 * the case of a {@link ContentNegotiatingViewResolver}, it attempts to resolve
 * a view based on a number of things including a dot-path extension and an
 * Accept Http header.  So, first, ask the app default
 * {@link ContentNegotiatingViewResolver} if he can find an appropriate view
 * for the request.  If not, that's fine, we will then look through a list of
 * custom view resolvers based only on view name to see if we can find the
 * right one.  This second check essentially elminates the need to look at
 * the accept header, etc.
 * 
 * @author Mark Kolich
 *
 */
public final class KolichContentNegotiatingViewResolver
	extends ContentNegotiatingViewResolver {

	private static final Logger logger__ = 
		LoggerFactory.getLogger(KolichContentNegotiatingViewResolver.class);
	
	/**
	 * All error views, like 404 Not Found, etc. have a view name
	 * that will start with "errors".  For example "errors/404-not-found".
	 */
	private static final String ERROR_VIEWS = "errors";

	private List<ViewResolver> viewResolvers_;	
	private String globalDefaultView_ = null;
	
	@Override
	public View resolveViewName(String viewName, Locale locale)
		throws Exception {
		// If the view name does not start with "errors", then
		// we can ask the ContentNegotiatingViewResolver what it
		// thinks the request should resolve to.  Otherwise, if it's a request
		// for an error view, we immeaditely ask the other view resolvers
		// to resolve the view based ONLY on the name.  This is because
		// when resolving a request for an error view, we don't care about
		// the requesting content type or accept header.  We always return
		// text/html for an error view (404 Not Found page, etc.) even if
		// the request was say for a .json (application/json) resource.
		View view = null;
		if(!viewName.startsWith(ERROR_VIEWS)) {
			// First, ask the ContentNegotiatingViewResolver if he can find an
			// appropriate view for us.  If not, that's fine, we will then look
			// through a list of view resolvers based only on view name to see
			// if we can find the right one.
			if((view = super.resolveViewName(viewName, locale)) != null) {
				return view;
			}
		}
		// If the ContentNegotiatingViewResolver produced nothing, see if
		// our standard set of view resolvers can find a good view without
		// caring about the media-type.  Note that we only get here if the
		// ContentNegotiatingViewResolver was really unable to find a view
		// suitable for the request based on the Accept and Content-Type
		// headers.
		if((view = resolveLocalViewName(viewName, locale)) != null) {
			return view;
		}
		// Ok, wow, the ContentNegotiatingViewResolver and our own set of view
		// resolvers found no good view.  That must mean we have to use our
		// global default view, if set.
		if((view = resolveLocalViewName(globalDefaultView_, locale)) != null) {
			return view;
		}
		// Should really never get here in a production environment. If we get
		// here that means that we didn't set our globalDefaultView so there's
		// no default view to render if nothing else matched.
		logger__.error("Utterly failed to resolve view with name: " + viewName);
		return null;
	}
	
	private View resolveLocalViewName(String viewName, Locale locale)
		throws Exception {
		if(viewName == null) {
			logger__.debug("Was asked to resolve a null viewName.");
			return null;
		}
		View view = null;
		for(final ViewResolver viewResolver : viewResolvers_) {
			view = viewResolver.resolveViewName(viewName, locale);
			if(view != null) {
				break;
			}
		}
		return view;
	}
		
	/**
	 * Sets the view resolvers to be wrapped by this view resolver.
	 */
	@Override
	public void setViewResolvers(List<ViewResolver> viewResolvers) {
		viewResolvers_ = viewResolvers;
		super.setViewResolvers(viewResolvers_);
	}
	
	/**
	 * If no other view can be resolved, we always use this view by default.
	 */
	public void setGlobalDefaultView(String globalDefaultView) {
		globalDefaultView_ = globalDefaultView;
	}
	
}
