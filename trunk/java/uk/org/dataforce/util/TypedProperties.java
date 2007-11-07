/*
 * Copyright (c) 2006-2007 Shane Mc Cormack
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 * SVN: $Id$
 */
package uk.org.dataforce.util;

import java.util.Properties;

/**
 * Properties file that allows for getting/setting of typed properties
 */
public class TypedProperties extends Properties {
	/**
	 * Creates an empty property list with no default values.
	 */
	public TypedProperties() {
		super();
	}
	
	/**
	 * Creates an empty property list with the specified defaults.
	 *
	 * @param defaults The Defaults
	 */
	public TypedProperties(final Properties defaults) {
		super(defaults);
	}
	
	/**
	 * Check if a property exists
	 *
	 * @param key key for property
	 * @return True if the property exists, else false
	 */
	public static boolean hasProperty(final String key) {
		return (getProperty(key) == null);
	}
	
	/**
	 * Get a Byte property from the config
	 *
	 * @param key key for property
	 * @param fallback Value to return if key is not found
	 * @return the requested property, or the fallback value if not defined
	 */
	public static byte getByteProperty(final String key, final byte fallback) {
		return Byte.parseByte(getProperty(key, Byte.toString(fallback)));
	}
	
	/**
	 * Set a Byte property in the config
	 *
	 * @param key key for property
	 * @param value Value for property
	 */
	public static void setByteProperty(final String key, final byte value) {
		setProperty(key, Byte.toString(value));
	}
	
	/**
	 * Get a Short property from the config
	 *
	 * @param key key for property
	 * @param fallback Value to return if key is not found
	 * @return the requested property, or the fallback value if not defined
	 */
	public static short getShortProperty(final String key, final short fallback) {
		return Short.parseShort(getProperty(key, Short.toString(fallback)));
	}
	
	/**
	 * Set a Short property in the config
	 *
	 * @param key key for property
	 * @param value Value for property
	 */
	public static void setShortProperty(final String key, final short value) {
		setProperty(key, Short.toString(value));
	}
	
	/**
	 * Get an integer property from the config
	 *
	 * @param key key for property
	 * @param fallback Value to return if key is not found
	 * @return the requested property, or the fallback value if not defined
	 */
	public static int getIntProperty(final String key, final int fallback) {
		return Integer.parseInt(getProperty(key, Integer.toString(fallback)));
	}
	
	/**
	 * Set an integer property in the config
	 *
	 * @param key key for property
	 * @param value Value for property
	 */
	public static void setIntProperty(final String key, final int value) {
		setProperty(key, Integer.toString(value));
	}
	
	/**
	 * Get a Long property from the config
	 *
	 * @param key key for property
	 * @param fallback Value to return if key is not found
	 * @return the requested property, or the fallback value if not defined
	 */
	public static long getLongProperty(final String key, final long fallback) {
		return Long.parseLong(getProperty(key, Long.toString(fallback)));
	}
	
	/**
	 * Set a Long property in the config
	 *
	 * @param key key for property
	 * @param value Value for property
	 */
	public static void setLongProperty(final String key, final long value) {
		setProperty(key, Long.toString(value));
	}
	
	/**
	 * Get a float property from the config
	 *
	 * @param key key for property
	 * @param fallback Value to return if key is not found
	 * @return the requested property, or the fallback value if not defined
	 */
	public static float getFloatProperty(final String key, final float fallback) {
		return Float.parseFloat(getProperty(key, Float.toString(fallback)));
	}
	
	/**
	 * Set a float property in the config
	 *
	 * @param key key for property
	 * @param value Value for property
	 */
	public static void setFloatProperty(final String key, final float value) {
		setProperty(key, Float.toString(value));
	}
	
	/**
	 * Get a double property from the config
	 *
	 * @param key key for property
	 * @param fallback Value to return if key is not found
	 * @return the requested property, or the fallback value if not defined
	 */
	public static double getDoubleProperty(final String key, final double fallback) {
		return Double.parseDouble(getProperty(key, Double.toString(fallback)));
	}
	
	/**
	 * Set a double property in the config
	 *
	 * @param key key for property
	 * @param value Value for property
	 */
	public static void setDoubleProperty(final String key, final double value) {
		setProperty(key, Double.toString(value));
	}
	
	/**
	 * Get a boolean property from the config
	 *
	 * @param key key for property
	 * @param fallback Value to return if key is not found
 	 * @return the requested property, or the fallback value if not defined
	 */
	public static boolean getBoolProperty(final String key, final boolean fallback) {
		return Boolean.parseBoolean(getProperty(key, Boolean.toString(fallback)));
	}
	
	/**
	 * Set a Boolean property in the config
	 *
	 * @param key key for property
	 * @param value Value for property
	 */
	public static void setBoolProperty(final String key, final boolean value) {
		setProperty(key, Boolean.toString(value));
	}
	
	/**
	 * Get a Char property from the config
	 *
	 * @param key key for property
	 * @param fallback Value to return if key is not found
	 * @return the requested property, or the fallback value if not defined
	 */
	public static char getCharProperty(final String key, final char fallback) {
		return Character.parseChar(getProperty(key, Character.toString(fallback)));
	}
	
	/**
	 * Set a Char property in the config
	 *
	 * @param key key for property
	 * @param value Value for property
	 */
	public static void setCharProperty(final String key, final char value) {
		setProperty(key, Character.toString(value));
	}
}