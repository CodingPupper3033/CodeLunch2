package com.codelunch.app.findEverything;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.codelunch.app.R;
import com.codelunch.app.api.NutrisliceFinder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SelectSchoolActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_school);

        try {
            JSONObject schoolObjectFromAPI = new JSONObject(getIntent().getStringExtra("object"));
            String name = schoolObjectFromAPI.getString("name");
            String url = schoolObjectFromAPI.getString("menus_domain");

            // Set title of menu
            TextView textViewSchoolName = findViewById(R.id.selectSchoolOrganizationName);
            textViewSchoolName.setText(name);

            // List View
            ListView listView = findViewById(R.id.selectSchoolListView);

            // Finder
            NutrisliceFinder finder = new NutrisliceFinder(this);

            // Add Schools
            finder.makeSchoolRequest(url, new Response.Listener() {
                @Override
                public void onResponse(Object response) {
                    try {
                        JSONArray schoolsArray = (JSONArray) response;
                        String[] resultsNames = new String[schoolsArray.length()]; // output
                        for (int i = 0; i < schoolsArray.length(); i++) {
                            JSONObject currentSchool = schoolsArray.getJSONObject(i);
                            resultsNames[i] = currentSchool.getString("name");
                            ArrayAdapter adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.text_view_search_result, resultsNames);
                            listView.setAdapter(adapter);
                            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    try {
                                        JSONObject selectedItem = schoolsArray.getJSONObject(position);

                                        // Start activity to select School
                                        Intent intent = new Intent(getApplicationContext(), SelectMenuActivity.class);
                                        intent.putExtra("object", selectedItem.toString());
                                        intent.putExtra("menus_domain", url);
                                        startActivity(intent);
                                        finish();

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    String[] noConnectionStringArray = {getResources().getString(R.string.unable_to_connect_to_nutrislice_retry)};
                    ArrayAdapter adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.text_view_search_result, noConnectionStringArray);
                    listView.setAdapter(adapter);
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            // Retry
                            Intent intent = new Intent(getApplicationContext(), SelectSchoolActivity.class);
                            intent.putExtra("object", schoolObjectFromAPI.toString());
                            startActivity(intent);
                            finish();
                        }
                    });
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}