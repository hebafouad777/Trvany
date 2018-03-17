package com.mediaoasis.trvany.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.mediaoasis.trvany.R;

import java.util.List;

/**
 * Created by Nasr on 1/12/2016.
 */
public class SpinnerAdapter extends ArrayAdapter<String> {
    private Context context;
    private List<String> data;
    public Resources res;
    LayoutInflater inflater;

    /*************
     * CustomAdapter Constructor
     *****************/
    public SpinnerAdapter(Activity activity, int textViewResourceId, List objects) {
        super(activity, textViewResourceId, objects);

        /********** Take passed values **********/
        context = activity;

//        if (type == 1)
//            data = new ArrayList<Speciality>();
//        else if (type == 2)
//            data = new ArrayList<PriceRange>();
//        else if (type == 3)
//            data = new ArrayList<City>();

        data = objects;

        /***********  Layout inflator to call external xml layout () **********************/
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    // This funtion called for each row ( Called data.size() times )
    public View getCustomView(int position, View convertView, ViewGroup parent) {

        /********** Inflate spinner_rows.xml file for each row ( Defined below ) ************/
        View row = inflater.inflate(R.layout.item_simple_spinner, parent, false);

        /***** Get each Model object from Arraylist ********/
//        tempValues = null;

        TextView label = (TextView) row.findViewById(R.id.textViewSpinnerListItem);
//        TextView sub          = (TextView)row.findViewById(R.id.sub);
//        ImageView companyLogo = (ImageView)row.findViewById(R.id.image);
       label.setText(data.get(position));

        return row;
    }
}