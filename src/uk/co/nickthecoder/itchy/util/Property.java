package uk.co.nickthecoder.itchy.util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Annotate Behaviour's sub-class's attributes with '@Property(label="whatever")',
 * to make that field available within the scene editor.
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Property {

    String label();

    boolean allowNull() default false;    

    boolean alpha() default true; // Only used for RGBA properties
}
