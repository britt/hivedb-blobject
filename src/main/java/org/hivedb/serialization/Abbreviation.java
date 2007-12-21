package org.hivedb.serialization;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/***
 *  Abbreviates a class or property name for efficient serialization.
 * @author alikuski@cafepress.com
 *
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Abbreviation {
	String value();
}
