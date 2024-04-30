package org.babinkuk.diff;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation should be used with {@link Diffable}
 * indicates the field to which this annotation applies 
 * <p/>
 * this annotation cn take as a parameter the user-defines application-specific data type
 * of th field, used by <code>DiffGenerator.difference</code> to determine which DataResolver to use
 * <p/>
 * This annotaton has no meaning if used in a class not annotated with Diffable
 * 
 * @author BabinKuk
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface DiffField {
	String id() default "";
	String type() default "";
}
