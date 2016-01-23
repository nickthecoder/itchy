package uk.co.nickthecoder.itchy;

import uk.co.nickthecoder.itchy.property.PropertySubject;

public interface NamedSubject<S extends PropertySubject<S>> extends Named, PropertySubject<S>
{

}
