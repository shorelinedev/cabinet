package com.afollestad.cabinet.ui;

import android.os.Build;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.widget.ListView;

import com.afollestad.cabinet.R;
import com.afollestad.cabinet.fragments.AboutDialog;
import com.afollestad.cabinet.ui.base.ThemableActivity;
import com.afollestad.cabinet.utils.ThemeUtils;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * @author Aidan Follestad (afollestad)
 */
public class SettingsActivity extends ThemableActivity implements AboutDialog.DismissListener {

    private static boolean aboutDialogShown;

    public static class SettingsFragment extends PreferenceFragment {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings);

            final CheckBoxPreference translucentStatusbar = (CheckBoxPreference) findPreference("translucent_statusbar");
            final CheckBoxPreference translucentNavbar = (CheckBoxPreference) findPreference("translucent_navbar");

            translucentStatusbar.setChecked(ThemeUtils.isTranslucentStatusbar(getActivity()));
            translucentStatusbar.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object o) {
                    PreferenceManager.getDefaultSharedPreferences(getActivity()).edit()
                            .putBoolean("translucent_navbar", false).commit();
                    getActivity().recreate();
                    return true;
                }
            });

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                translucentStatusbar.setEnabled(false);
                translucentStatusbar.setSummary(R.string.translucentstatusbar_disabled);
            }

            translucentNavbar.setChecked(ThemeUtils.isTranslucentNavbar(getActivity()));
            translucentNavbar.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object o) {
                    getActivity().recreate();
                    return true;
                }
            });

            findPreference("dark_mode").setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object o) {
                    ImageLoader.getInstance().clearMemoryCache();
                    getActivity().recreate();
                    return true;
                }
            });
            findPreference("true_black").setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object o) {
                    getActivity().recreate();
                    return true;
                }
            });

            findPreference("about").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    if (aboutDialogShown) return false;
                    aboutDialogShown = true; // double clicking without this causes the dialog to be shown twice
                    new AboutDialog().show(getFragmentManager(), "ABOUT");
                    return true;
                }
            });
        }
    }

    @Override
    public void onDismiss() {
        aboutDialogShown = false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        DrawerActivity.setupTransparentTints(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.preference_activity_custom);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ListView list = (ListView) findViewById(android.R.id.list);
        DrawerActivity.setupTranslucentTopPadding(this, list);
        DrawerActivity.setupTranslucentBottomPadding(this, list);

        getFragmentManager().beginTransaction().replace(android.R.id.content, new SettingsFragment()).commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}