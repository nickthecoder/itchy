package uk.co.nickthecoder.itchy.editor;

import uk.co.nickthecoder.itchy.Input;
import uk.co.nickthecoder.itchy.Resources;

public class EditInput extends EditSubject<Input>
{

    public EditInput(Resources resources, ListInputs listInputs, Input subject, boolean adding)
    {
        super(resources, listInputs, subject, adding);
    }

    @Override
    protected String getSubjectName()
    {
        return "Input";
    }

    @Override
    protected Input getSubjectByName(String name)
    {
        return resources.getInput(name);
    }

    @Override
    protected void add()
    {
        resources.addInput(subject);
    }

}
