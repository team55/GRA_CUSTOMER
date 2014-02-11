package ru.team55.gra.api.Adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import ru.team55.gra.api.pocoForms.pocoFormFieldValues;

import java.util.List;

public class FormFieldArrayAdapter extends ArrayAdapter {

    private Context context;
    private int textViewResourceId;
    private List<pocoFormFieldValues> objects;

    //public static boolean flag = false;
    public boolean flag = false;

    public FormFieldArrayAdapter(Context context, int textViewResourceId, List<pocoFormFieldValues> objects) {
        super(context, textViewResourceId, objects);
        this.context = context;
        this.textViewResourceId = textViewResourceId;
        this.objects = objects;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null)
            convertView = View.inflate(context, textViewResourceId, null);

        if (flag != false) {
            TextView tv = (TextView) convertView;
            tv.setText(objects.get(position).description);
        }

        return convertView;
    }
}
