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

package com.kolich.spring.controllers;

import static com.kolich.spring.views.AbstractKolichView.VIEW_PAYLOAD;
import static org.springframework.http.MediaType.APPLICATION_JSON;

import org.slf4j.Logger;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.ModelAndView;

import com.kolich.common.entities.KolichCommonEntity;
import com.kolich.spring.exceptions.KolichSpringException;
import com.kolich.spring.views.KolichViewSerializable;

public abstract class KolichControllerClosure<T> {
			
	protected final String comment_;
	protected final Logger logger_;
	
	public KolichControllerClosure(final String comment, final Logger logger) {
		comment_ = comment;
		logger_ = logger;
	}
	
	public abstract T doit() throws Exception;
	
	/**
	 * Default execution handler with very minimal exception handling.
	 * Extending classes should ideally override this method and call
	 * super.execute() as needed from their own execute() method.
	 */
	public T execute() {
		try {
			return doit();
		} catch (IllegalArgumentException e) {
			logger_.debug(comment_, e);
			throw e;
		} catch (Exception e) {
			logger_.debug(comment_, e);
			throw new KolichSpringException(e);
		}
	}
	
	public static final ModelAndView getModelAndView(final String viewName) {
		return new ModelAndView(viewName);
	}
	
	public static final ModelAndView getModelAndView(final String viewName,
		final KolichViewSerializable serializable) {
		return getModelAndView(viewName).addObject(VIEW_PAYLOAD, serializable);
	}
	
	public static final ModelAndView getModelAndView(final String viewName,
		final KolichCommonEntity entity) {
		return getModelAndView(viewName, new KolichViewSerializable() {
			@Override
			public KolichCommonEntity getEntity() {
				return entity;
			}
		});
	}
	
	public static final ResponseEntity<byte[]> getResponseEntity(
		final byte[] body, final HttpHeaders headers, final HttpStatus status) {
		return new ResponseEntity<byte[]>(body, headers, status);
	}
	
	public static final ResponseEntity<byte[]> getResponseEntity(
		final KolichCommonEntity entity, final HttpHeaders headers,
		final HttpStatus status) {
		return getResponseEntity(entity.getBytes(), headers, status);
	}
	
	public static final ResponseEntity<byte[]> getResponseEntity(
		final KolichCommonEntity entity, final HttpHeaders headers,
		final MediaType contentType, final HttpStatus status) {
		final HttpHeaders merged = new HttpHeaders();
		if(headers != null) {
			merged.putAll(headers);
		}
		merged.setContentType(contentType);
		return getResponseEntity(entity, merged, status);
	}
	
	public static final ResponseEntity<byte[]> getResponseEntity(
		final KolichCommonEntity entity, final MediaType contentType,
		final HttpStatus status) {
		return getResponseEntity(entity, null, contentType, status);
	}
	
	public static final ResponseEntity<byte[]> getJsonResponseEntity(
		final KolichCommonEntity entity, final HttpHeaders headers,
		final HttpStatus status) {
		return getResponseEntity(entity, headers, APPLICATION_JSON, status);
	}
	
	public static final ResponseEntity<byte[]> getJsonResponseEntity(
		final KolichCommonEntity entity, final HttpStatus status) {
		return getJsonResponseEntity(entity, null, status);
	}
	
	public static final ResponseEntity<byte[]> getEmptyResponseEntity(
		final HttpStatus status) {
		// Setting the body and header map to null is equivalent to telling
		// Spring "no body, and no additional headers -- just send a status
		// code"
		return new ResponseEntity<byte[]>(null, null, status);
	}
	
}
