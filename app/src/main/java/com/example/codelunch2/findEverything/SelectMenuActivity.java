package com.example.codelunch2.findEverything;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.codelunch2.R;
import com.example.codelunch2.api.NutrisliceRequester;
import com.example.codelunch2.settings.NutrisliceStorage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

public class SelectMenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_menu);

        try {
            JSONObject schoolObjectFromAPI = new JSONObject(getIntent().getStringExtra("object"));
            String schoolName = schoolObjectFromAPI.getString("name");
            String slug = schoolObjectFromAPI.getString("slug");
            String url = getIntent().getStringExtra("menus_domain");

            // Add school to storage
            NutrisliceStorage.addSchool(getApplicationContext(), schoolName, url, slug); // TODO Add days enabled

            // Set title of menu
            TextView textViewSchoolName = findViewById(R.id.selectMenuSchoolName);
            textViewSchoolName.setText(schoolName);

            // List View
            ListView listView = findViewById(R.id.selectSchoolListView);

            // Menus data
            JSONArray menusArray = schoolObjectFromAPI.getJSONArray("active_menu_types");

            // Loop Through menus
            String[] menuNames = new String[menusArray.length()];
            for (int i = 0; i < menusArray.length(); i++) {
                JSONObject menuObject = menusArray.getJSONObject(i);
                String menuName = menuObject.getString("name");
                JSONObject urls = menuObject.getJSONObject("urls");

                // Add to Nutrislice storage
                NutrisliceStorage.addMenu(getApplicationContext(), schoolName, menuName, urls);

                // Add Name to Menu names
                menuNames[i] = menuName;
            }

            // Add Menu Names to list
            ArrayAdapter adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.text_view_search_result, menuNames);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    try {
                        JSONObject selectedItem = menusArray.getJSONObject(position);
                        String menuName = selectedItem.getString("name");

                        Log.d("TAG", "onItemClick: ");

                        NutrisliceRequester categoryGetter = new NutrisliceRequester(getApplicationContext(), schoolName, menuName);
                        Calendar calendar = Calendar.getInstance(); // TODO Remove
                        calendar.set(Calendar.MONTH, 9);
                        calendar.set(Calendar.DAY_OF_MONTH, 13);
                        categoryGetter.getCategoryList(calendar, new Response.Listener() { // TODO remove
                        //categoryGetter.getCategoryList(Calendar.getInstance(), new Response.Listener() {
                            @Override
                            public void onResponse(Object response) {
                                // Add categories it can find right now to storage
                                ArrayList<String> categories = (ArrayList<String>)response;
                                for (int i = 0; i < categories.size(); i++) {
                                    NutrisliceStorage.addCategory(getApplicationContext(), schoolName, menuName, categories.get(i));
                                }

                                // Tell user it added this
                                Toast toast = new Toast(getApplicationContext());
                                toast.setDuration(Toast.LENGTH_LONG);
                                toast.setText(R.string.added_to_storage);
                                toast.show();

                                // Leave!
                                finish();
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                // Tell user it failed
                                Toast toast = new Toast(getApplicationContext());
                                toast.setDuration(Toast.LENGTH_LONG);
                                toast.setText(R.string.unable_to_connect_to_nutrislice);
                                toast.show();

                                // Leave!
                                finish();
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}