package com.kolich.spring.beans;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.springframework.beans.factory.InitializingBean;

import com.kolich.spring.exceptions.KolichSpringException;

public abstract class KolichWebAppProperties implements InitializingBean {
		
	/**
	 * An internal list of properties that are set once on Spring startup
	 * then never set again (or, at least, we never let the caller set
	 * these properties later).
	 */
	private final Map<String, Object> properties_ =
		Collections.synchronizedMap(new HashMap<String, Object>());
	
	/**
	 * Keeps track of our properties map and if it has already been
	 * set by Spring -- our properties map can only be set once.
	 */
	private boolean alreadySet_ = false;
	
	@Override
	public abstract void afterPropertiesSet() throws Exception;
		
	/**
	 * Fetch the value (the Object) corresponding to the property with the
	 * given name.  If no property with the given name exits, this method
	 * returns null.  The caller should know what type of Object is returned
	 * (what is it an instance of) then cast it later when the value is
	 * actually needed for something useful.
	 * @param name the name of the property to fetch.  These
	 * properties are set in beans.xml inside of our Spring
	 * configuration XML.
	 * @return
	 */
	public Object getProperty(final String name) {
		return properties_.get(name);
	}
		
	/**
	 * Called by Spring when this bean is first instantiated.  If you
	 * attempt to call this method again later, you'll get a nice
	 * {@link TwitterFeedException} complaining about your folly.
	 * How's that sound?
	 * @param properties
	 */
	public void setProperties(Map<String, Object> properties) {
		if(alreadySet_) {
			// Can't set the properties map once Spring has already
			// set it on web-app startup.
			throw new KolichSpringException("Global web-app properties map " +
				"has already been set, but you're trying to set it again.");
		} else {
			// Insert our webapp properties into the map statically.
			final Iterator<String> it = properties.keySet().iterator();
			while(it.hasNext()) {
				final String name = it.next();
				properties_.put(name, properties.get(name));
			}
			// Done, to prevent these from being set later.
			alreadySet_ = true;
		}
	}
	
	/**
	 * Returns an unmodifiable view of the current properties map.
	 * @return
	 */
	public Map<String, Object> getProperties() {
		return Collections.unmodifiableMap(properties_);
	}
	
}
