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

import java.util.Date;

import com.kolich.common.entities.KolichCommonEntity;

public abstract class KolichViewSerializable {

	/**
	 * Returns the entity payload of this response.
	 * @throws Exception
	 */
	public abstract KolichCommonEntity getEntity() throws Exception;
	
	/**
	 * Should return the Date that can be included in an Expires
	 * HTTP response header.  Return null for no Expires response
	 * header to be added.
	 * @return default January 1, 1970, 00:00:00 GMT
	 */
	public Date getExpires() {
		return new Date(1L);
	}
	
	/**
	 * Returns true if this response cannot be cached.  This triggers 
	 * the view renderer to append an appropriate Cache-Control and
	 * Pragma header to the response so no intermediate proxies cache
	 * the response.
	 * @return default true
	 */
	public boolean disableCaching() {
		return true;
	}
	
}
