package com.codelunch.app.api.finder;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.codelunch.app.R;
import com.codelunch.app.searchHandlers.SearchSchoolHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class NutrisliceFinderAdapter extends RecyclerView.Adapter<NutrisliceFinderAdapter.ViewHolder>{
    private final boolean announcement;
    private final JSONArray textArray;
    private final JSONArray dataArray;
    private final Activity activity;
    private RecyclerView reloadRecyclerView;
    private final NutrisliceFinder finder;
    private final MenuItem search;
    private final RecyclerView recyclerView;

    public NutrisliceFinderAdapter(Activity activity, MenuItem search, RecyclerView recyclerView, RecyclerView reloadRecyclerView, JSONArray data, NutrisliceFinder finder) {
        this.activity = activity;
        this.search = search;
        this.recyclerView = recyclerView;
        this.reloadRecyclerView = reloadRecyclerView;
        this.finder = finder;
        this.announcement = false;
        this.textArray = convertDataToTextList(data);
        this.dataArray = data;
    }

    public NutrisliceFinderAdapter(String string) {
        this.activity = null;
        this.announcement = true;
        this.dataArray = null;
        this.finder = null;
        this.recyclerView = null;
        this.search = null;
        this.reloadRecyclerView = null;
        textArray = new JSONArray();

        JSONObject announceObject = new JSONObject();
        try {
            announceObject.put("text",string);
            textArray.put(announceObject);
        } catch (JSONException e) {
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.search_result_view, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        try {
            JSONObject currentTextObject = textArray.getJSONObject(position);

            holder.setAllText(currentTextObject);

            if (!announcement) {
                JSONObject currentDataObject = dataArray.getJSONObject(position);
                holder.view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            SearchSchoolHandler searchSchoolHandler = new SearchSchoolHandler(activity, reloadRecyclerView, finder, currentDataObject);

                            // Close Dialogue
                            search.collapseActionView();
                            recyclerView.setVisibility(View.INVISIBLE);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        } catch (JSONException e) {
        }
    }

    @Override
    public int getItemCount() {
        return textArray.length();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final View view;
        private final TextView textView;
        private final TextView textViewSub;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.view = itemView;
            this.textView = itemView.findViewById(R.id.search_result);
            this.textViewSub = itemView.findViewById(R.id.search_result_sub);
        }

        public void setText(String text) {
            textView.setText(text);
        }

        public void setSubText(String text) {
            textViewSub.setText(text);
            textViewSub.setVisibility(View.VISIBLE);
        }

        public void setAllText(JSONObject data) {
            String text = "";
            String subtext = null;
            try {
                text = data.getString("text");
                subtext = data.getString("subtext");
            } catch (JSONException e) {
            }

            setText(text);

            if (subtext == null) {
                textViewSub.setVisibility(View.GONE);
            } else {
                setSubText(subtext);
            }
        }
    }

    private JSONArray convertDataToTextList(JSONArray data) {

        JSONArray convertedArray = new JSONArray();

        for (int i = 0; i < data.length(); i++) { // Add all names to the array
            try {
                JSONObject currentResult = data.getJSONObject(i);
                JSONObject textOutput = new JSONObject();

                textOutput.put("text", currentResult.getString("name")); // Set text as name

                boolean hasOrgName = currentResult.has("org_name") && !currentResult.isNull("org_name");
                boolean hasOrgState = currentResult.has("org_state") && !currentResult.isNull("org_state");

                if (hasOrgName || hasOrgState) {
                    StringBuffer subText = new StringBuffer(activity.getResources().getString(R.string.organization) + ':');
                    if (hasOrgName) subText.append(activity.getResources().getString(R.string.name) + " - " + currentResult.getString("org_name") + " ");
                    if (hasOrgState) subText.append(activity.getResources().getString(R.string.state) + " - " + currentResult.getString("org_state"));
                    textOutput.put("subtext", subText.toString());
                }

                convertedArray.put(textOutput);
            } catch (JSONException e) {
            }
        }



        return convertedArray;
    }
}
