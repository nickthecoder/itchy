package uk.co.nickthecoder.itchy.gui;

import java.util.HashMap;

import uk.co.nickthecoder.jame.event.MouseButtonEvent;

public class PickerButton<T> extends Button
{
    private final HashMap<String, T> hashMap;

    private final Label label;

    private T value;

    private final String title;

    public PickerButton( String title, T current, HashMap<String, T> hashMap )
    {
        this.title = title;

        this.hashMap = hashMap;
        this.type = "pickerButton";
        this.value = current;
        this.focusable = true;

        String labelString = "<select>";
        if (current != null) {
            for (String key : hashMap.keySet()) {
                if (current.equals(hashMap.get(key))) {
                    labelString = key;
                    break;
                }
            }
        }

        this.label = new Label(labelString);
        this.addChild(this.label);
    }

    @Override
    public void onClick( final MouseButtonEvent e )
    {
        Picker<T> picker = new Picker<T>(this.title, this.hashMap, this.getValue()) {
            @Override
            public void pick( String label, T object )
            {
                PickerButton.this.label.setText(label);
                PickerButton.this.value = object;
                PickerButton.super.onClick(e);
            }
        };
        picker.show();
    }

    public T getValue()
    {
        return this.value;
    }
}
