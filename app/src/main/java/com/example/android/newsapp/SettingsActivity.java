package com.example.android.newsapp;

import android.annotation.TargetApi;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.sip.SipSession;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.DatePicker;
import android.widget.Toast;

import java.net.InterfaceAddress;
import java.util.Calendar;

public class SettingsActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
    }

    public static class NewsPreferenceFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener{
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings_main);

            Preference orderBy = findPreference(getString(R.string.settings_order_by_key));
            bindPreferenceSummaryToValue(orderBy);

            Preference minDate = findPreference(getString(R.string.settings_min_date_key));
            minDate.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                private DatePickerDialog.OnDateSetListener dateSetListener =
                        new DatePickerDialog.OnDateSetListener() {
                            public void onDateSet(DatePicker view, int year, int month, int day) {
                                Toast.makeText(getActivity(), "selected date is " + view.getYear() +
                                        " / " + (view.getMonth()+1) +
                                        " / " + view.getDayOfMonth(), Toast.LENGTH_SHORT).show();
                            }
                        };

                @TargetApi(Build.VERSION_CODES.HONEYCOMB)
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    showDateDialog(getContext(), dateSetListener);
                    return false;
                }
            });

           /* Preference maxDate = findPreference(getString(R.string.settings_max_date_key));
            maxDate.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @TargetApi(Build.VERSION_CODES.HONEYCOMB)
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    showDateDialog();
                    return false;
                }
            });*/

        }
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            // The code in this method takes care of updating the displayed preference summary after it has been changed
            String stringValue = value.toString();
            preference.setSummary(stringValue);
            if (preference instanceof ListPreference) {
                ListPreference listPreference = (ListPreference) preference;
                int prefIndex = listPreference.findIndexOfValue(stringValue);
                if (prefIndex >= 0) {
                    CharSequence[] labels = listPreference.getEntries();
                    preference.setSummary(labels[prefIndex]);
                }
            } else {
                preference.setSummary(stringValue);
            }
            return true;
        }
        private void bindPreferenceSummaryToValue(Preference preference) {
            preference.setOnPreferenceChangeListener(this);
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(preference.getContext());
            String preferenceString = preferences.getString(preference.getKey(), "");
            onPreferenceChange(preference, preferenceString);
        }

    }

    @Override
    public void onDateSet(DatePicker datePicker, int i, int i2, int i3) {
        Log.i("dasd","year "+i+" month "+i2+" day "+i3);
    }

    private static void showDateDialog(Context context, DatePickerDialog.OnDateSetListener listener ){
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        new DatePickerDialog(context,listener, year, month, day).show();
    }

}

