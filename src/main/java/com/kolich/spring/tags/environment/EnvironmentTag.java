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

package com.kolich.spring.tags.environment;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import com.kolich.spring.exceptions.tags.EnvironmentVariableTagException;
import com.kolich.spring.exceptions.tags.TagSetupException;
import com.kolich.spring.tags.ContextAwareTag;

public abstract class EnvironmentTag extends ContextAwareTag {

	private static final long serialVersionUID = 8156385022283270968L;
	
	private static final String CTX_COMP_ENV = "java:comp/env";
	
	private static final Context environment__;
	static {
		try {
			environment__ = (Context)new InitialContext().lookup(CTX_COMP_ENV);
		} catch (NamingException e) {
			throw new TagSetupException("Failed to setup initial " +
				"context.", e);
		}
	}
		
	/**
	 * Returns a String containing the value of the named context-wide
	 * environment variable, or null if the parameter does not exist.
	 * @param name
	 * @return
	 */
	protected final String getEnvironmentVariable(final String name) {
		// Threads that need to access a single InitialContext instance
		// concurrently should synchronize amongst themselves and provide
		// the necessary locking.
		synchronized(environment__) {
			try {
				return (String)environment__.lookup(name);
			} catch (NamingException e) {
				throw new EnvironmentVariableTagException("Could not " +
					"lookup Environment variable ('" + name + "')", e);
			}
		}
	}
	
}