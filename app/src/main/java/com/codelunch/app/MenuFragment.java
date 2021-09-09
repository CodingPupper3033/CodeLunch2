package com.codelunch.app;

import android.os.Bundle;
import android.text.format.DateFormat;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.codelunch.app.api.NutrisliceMultipleRequester;
import com.codelunch.app.api.NutrisliceMultipleRequesterListener;
import com.codelunch.app.api.NutrisliceMultipleRequesterMaker;
import com.codelunch.app.api.NutrisliceRequestErrorData;
import com.codelunch.app.notification.NutrisliceDataConverter;
import com.codelunch.app.settings.storage.PastRequestStorage;

import org.json.JSONArray;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class MenuFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_menu, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Reload
        reload();
    }

    public void reload() {
        // Turn on progress spinner
        getView().findViewById(R.id.menu_fragment_progressBar).setVisibility(View.VISIBLE);

        NutrisliceMultipleRequester multipleRequester = NutrisliceMultipleRequesterMaker.make(getContext());

        multipleRequester.getFoodNow(new NutrisliceMultipleRequesterListener() {
            @Override
            public void onReceive(JSONArray successData, ArrayList<NutrisliceRequestErrorData> errors) {
                // Set the text
                setText();
            }
        });
    }

    public void setText() {
        StringBuffer text = new StringBuffer();

        text.append(getResources().getString(R.string.time_updated) + ": ");

        SimpleDateFormat format;
        if (DateFormat.is24HourFormat(getContext())) {
            format = new SimpleDateFormat("EEE, MMM dd @ HH:mm"); // 24hr
        } else {
            format = new SimpleDateFormat("EEE, MMM dd @ hh:mm aa"); // 12hr
        }


        text.append(format.format(PastRequestStorage.getTime(getContext()).getTime()) + '\n'); // Time

        text.append(NutrisliceDataConverter.requestDataToNotifString(getContext(), PastRequestStorage.getData(getContext())));

        TextView textView = getView().findViewById(R.id.menu_fragment_text);
        textView.setText(text);
        textView.setMovementMethod(new ScrollingMovementMethod());

        // Turn off progress spinner
        getView().findViewById(R.id.menu_fragment_progressBar).setVisibility(View.INVISIBLE);
    }
}