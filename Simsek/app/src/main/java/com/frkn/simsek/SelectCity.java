package com.frkn.simsek;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by frkn on 08.01.2017.
 */

public class SelectCity extends DialogFragment {

    ArrayAdapter<String> arrayAdapter;
    ListView list;
    List<String> myList = new ArrayList<String>();
    JSONObject data;
    Boolean flag = false;
    int pos1, pos2;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Functions.city_flag = false;
        Functions.country_flag = false;
        readData();
        loadList(1, 0);
    }

    private void readData() {
        InputStream is = getResources().openRawResource(R.raw.info);
        Writer writer = new StringWriter();
        char[] buffer = new char[1024];
        try {
            Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            int n;
            while ((n = reader.read(buffer)) != -1) {
                writer.write(buffer, 0, n);
            }
            data = new JSONObject(writer.toString());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e1) {
            e1.printStackTrace();
        }
    }

    private void loadList(int type, int pos) {
        if (type == 1) {
            try {
                myList.clear();
                JSONArray countries = data.getJSONArray("Countries");
                for (int i = 0; i < countries.length(); i++) {
                    JSONObject jObject = countries.getJSONObject(i);
                    myList.add(jObject.getString("Name"));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                JSONArray cities = data.getJSONArray("Countries").getJSONObject(pos).getJSONArray("Cities");
                List<String> temp = new ArrayList<String>();
                for (int i = 0; i < cities.length(); i++)
                    temp.add(cities.getJSONObject(i).getString("name"));
                myList.clear();
                myList.addAll(new ArrayList<String>(temp));
                arrayAdapter.notifyDataSetChanged();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void fillListView() {
        arrayAdapter = new ArrayAdapter(this.getContext(), android.R.layout.simple_list_item_1, myList) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView text1 = (TextView) view.findViewById(android.R.id.text1);
                text1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
                return view;
            }
        };

        list.setAdapter(arrayAdapter);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.select_city, container,
                false);

        list = (ListView) rootView.findViewById(R.id.listView);
        fillListView();

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selected = (String) list.getItemAtPosition(position);
                System.out.println(selected);
                if (!flag) {
                    flag = true;
                    pos1 = position;
                    Functions.country_flag = true;
                    loadList(2, position);
                } else {
                    pos2 = position;
                    try {
                        Functions.city_flag = true;
                        JSONObject country = data.getJSONArray("Countries").getJSONObject(pos1);
                        Functions.countryname = country.getString("Name");
                        JSONObject city = country.getJSONArray("Cities").getJSONObject(pos2);
                        Functions.cityname = city.getString("name");
                        Functions.cityid = city.getString("id");
                        Functions.url = city.getString("link");
                        getDialog().dismiss();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        getDialog().setTitle("DialogFragment Tutorial");

        AutoCompleteTextView autoCompleteTextView = (AutoCompleteTextView) rootView.findViewById(R.id.auto_complete_textview);

        ArrayAdapter adapter = new ArrayAdapter(this.getContext(), android.R.layout.select_dialog_item, myList);
        autoCompleteTextView.setAdapter(adapter);
        autoCompleteTextView.setThreshold(1);

        return rootView;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        final Activity activity = getActivity();
        if (activity instanceof DialogInterface.OnDismissListener) {
            ((DialogInterface.OnDismissListener) activity).onDismiss(dialog);
        }
    }
}
