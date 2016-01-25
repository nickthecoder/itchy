package uk.co.nickthecoder.itchy.editor;

import uk.co.nickthecoder.itchy.Costume;
import uk.co.nickthecoder.itchy.Costume.Event;
import uk.co.nickthecoder.itchy.Resources;

public class EditEvent extends EditSubject<Costume.Event>
{

    public EditEvent(Resources resources, ListSubjects<Event> listSubjects, Event subject, boolean isNew)
    {
        super(resources, listSubjects, subject, isNew);
    }

    @Override
    protected String getSubjectName()
    {
        return "Event";
    }

    @Override
    protected void onOk()
    {
        super.onOk();
        subject.update();
    }
    
    @Override
    protected void add()
    {
        // Do nothing
    }

}
