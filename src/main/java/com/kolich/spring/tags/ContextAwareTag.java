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

package com.kolich.spring.tags;

import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.TagSupport;

public abstract class ContextAwareTag extends TagSupport {

	private static final long serialVersionUID = -3054927094964814346L;
	
	private PageContext pageContext_;
	private ServletContext servletContext_;
	private JspWriter out_;
	
	private String bindVar_;
	
	@Override
	public final int doStartTag() throws JspException {
		try {
			pageContext_ = pageContext;
			servletContext_ = pageContext_.getServletContext();
			out_ = pageContext_.getOut();
			return myDoStartTag();
		} catch (JspException e) {
			throw e;
		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
			throw new JspTagException(e);
		}
	}
	
	/**
	 * Called once all required context aware tag properties are set.
	 * @return
	 * @throws Exception
	 */
	protected abstract int myDoStartTag() throws Exception;
	
	/**
	 * Either writes the value to the page context's {@link JspWriter}
	 * (essentially writing it directly into the JSP) or will bind the
	 * value to a page context variable if the variable to bind to as set via
	 * {@link EnvironmentTag#setBindToVar(String)} is non-null.
	 * If bound to a page context variable, the value can be accessed later
	 * using the standard <code>${variable}</code> syntax.
	 * @param value
	 * @throws IOException
	 */
	protected final void writeOrBind(final String value) throws IOException {
		if(bindVar_ != null) {
			pageContext.setAttribute(bindVar_, value);
		} else {
			getOut().print(value);
		}
	}
	
	protected final PageContext getPageContext() {
		return pageContext;
	}
	
	protected final ServletContext getServletContext() {
		return servletContext_;
	}
	
	protected final JspWriter getOut() {
		return out_;
	}
	
	public void setBindToVar(String bindVar) {
		bindVar_ = bindVar;
	}

}
