/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0 which accompanies this
 * distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import uk.co.nickthecoder.itchy.Behaviour;

/**
 * Annotate Behaviour's sub-class's attributes with '@Property(label="whatever")', to make that
 * field available within the scene editor.
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = { ElementType.FIELD, ElementType.METHOD })
public @interface Property {

    String label();

    String[] aliases() default {};

    boolean recurse() default false;

    boolean allowNull() default false;

    boolean alpha() default true; // Only used for RGBA properties

    Class<?> baseClass() default Behaviour.class; // Only used for ClassName properties
}
