package uk.co.nickthecoder.itchy.gui;

public class TextBox extends EntryBox<TextBox>
{
    public TextBox( String text )
    {
        super(text);
        this.addStyle("textBox");
    }

    public void setText( String text )
    {
        this.setEntryText(text);
    }
}
