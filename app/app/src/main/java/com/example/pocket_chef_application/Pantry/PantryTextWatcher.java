package com.example.pocket_chef_application.Pantry;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import java.util.Calendar;

public class PantryTextWatcher implements TextWatcher {

    EditText date;
    private String current = "";
    private String ddmmyyyy = "MMDDYYYY";
    private Calendar cal = Calendar.getInstance();

    public PantryTextWatcher(EditText date){
        this.date = date;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (!s.toString().equals(current)) {
            String clean = s.toString().replaceAll("[^\\d.]|\\.", "");
            String cleanC = current.replaceAll("[^\\d.]|\\.", "");

            int cl = clean.length();
            int sel = cl;
            for (int i = 2; i <= cl && i < 6; i += 2) {
                sel++;
            }
            //Fix for pressing delete next to a forward slash
            if (clean.equals(cleanC)) sel--;

            if (clean.length() < 8){
                clean = clean + ddmmyyyy.substring(clean.length());
            }
            else{
                int day  = Integer.parseInt(clean.substring(2,4));
                int mon  = Integer.parseInt(clean.substring(0,2));
                int year = Integer.parseInt(clean.substring(4,8));

                mon = mon < 1 ? 1 : Math.min(mon, 12);
                cal.set(Calendar.MONTH, mon-1);
                year = (year<1900)?1900: Math.min(year, 2100);
                cal.set(Calendar.YEAR, year);

                day = Math.min(day, cal.getActualMaximum(Calendar.DATE));
                clean = String.format("%02d%02d%02d", mon, day, year);
            }

            clean = String.format("%s/%s/%s", clean.substring(0, 2),
                    clean.substring(2, 4),
                    clean.substring(4, 8));

            sel = sel < 0 ? 0 : sel;
            current = clean;
            this.date.setText(current);
            this.date.setSelection(Math.min(sel, current.length()));
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
