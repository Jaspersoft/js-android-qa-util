/*
 * Copyright � 2015 TIBCO Software, Inc. All rights reserved.
 * http://community.jaspersoft.com/project/jaspermobile-android
 *
 * Unless you have purchased a commercial license agreement from Jaspersoft,
 * the following license terms apply:
 *
 * This program is part of Jaspersoft Mobile for Android.
 *
 * Jaspersoft Mobile is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Jaspersoft Mobile is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Jaspersoft Mobile for Android. If not, see
 * <http://www.gnu.org/licenses/lgpl>.
 */

package com.jaspersoft.android.jaspermobile.qa.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.NumberPicker;

import com.jaspersoft.android.jaspermobile.qa.R;

/**
 * @author Tom Koptel
 * @since 2.0
 */
public class NumberDialogFragment extends BaseDialogFragment implements DialogInterface.OnShowListener, DialogInterface.OnClickListener {

    private final static String MIN_VALUE_ARG = "min_value";
    private final static String MAX_VALUE_ARG = "max_value";
    private final static String CURRENT_VALUE_ARG = "mSelectedValue";

    private int mMinValue;
    private int mMaxValue;
    private int mSelectedValue;

    private EditText etNumber;
    private NumberPicker numberPicker;

    protected InputMethodManager inputMethodManager;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        ViewGroup customView = (ViewGroup) LayoutInflater.from(getActivity())
                .inflate(R.layout.number_dialog_layout, (ViewGroup) getView(), false);

        numberPicker = (NumberPicker) customView.findViewById(R.id.numberPicker);
        numberPicker.setMinValue(mMinValue);
        numberPicker.setMaxValue(mMaxValue);

        int inputId = getActivity().getResources().getIdentifier("numberpicker_input", "id", "android");
        etNumber = (EditText) numberPicker.findViewById(inputId);
        etNumber.addTextChangedListener(new PickerTextWatcher());

        builder.setTitle(R.string.dialog_current_page);
        builder.setView(customView);
        builder.setCancelable(true);
        builder.setPositiveButton(android.R.string.ok, this);
        builder.setNegativeButton(android.R.string.cancel, this);

        AlertDialog dialog = builder.create();
        if (savedInstanceState == null) {
            dialog.setOnShowListener(this);
        }

        return dialog;
    }

    @Override
    public void onShow(DialogInterface dialog) {
        numberPicker.setValue(mSelectedValue);
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if (which == DialogInterface.BUTTON_POSITIVE && mDialogListener != null) {
            ((NumberDialogClickListener) mDialogListener).onPageSelected(numberPicker.getValue(), requestCode);
        }
        inputMethodManager.hideSoftInputFromWindow(etNumber.getWindowToken(), 0);
    }

    @Override
    protected Class<NumberDialogClickListener> getDialogCallbackClass() {
        return NumberDialogClickListener.class;
    }

    protected void initDialogParams() {
        super.initDialogParams();

        Bundle args = getArguments();
        if (args != null) {
            if (args.containsKey(MIN_VALUE_ARG)) {
                mMinValue = args.getInt(MIN_VALUE_ARG);
            }
            if (args.containsKey(MAX_VALUE_ARG)) {
                mMaxValue = args.getInt(MAX_VALUE_ARG);
            }
            if (args.containsKey(CURRENT_VALUE_ARG)) {
                mSelectedValue = args.getInt(CURRENT_VALUE_ARG);
            }
        }
    }

    public static NumberDialogFragmentBuilder createBuilder(FragmentManager fragmentManager) {
        return new NumberDialogFragmentBuilder(fragmentManager);
    }

    //---------------------------------------------------------------------
    // Dialog Builder
    //---------------------------------------------------------------------

    public static class NumberDialogFragmentBuilder extends BaseDialogFragmentBuilder<NumberDialogFragment> {

        public NumberDialogFragmentBuilder(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        public NumberDialogFragmentBuilder setMinValue(int minValue) {
            args.putInt(MIN_VALUE_ARG, minValue);
            return this;
        }

        public NumberDialogFragmentBuilder setMaxValue(int maxValue) {
            args.putInt(MAX_VALUE_ARG, maxValue);
            return this;
        }

        public NumberDialogFragmentBuilder setCurrentValue(int value) {
            args.putInt(CURRENT_VALUE_ARG, value);
            return this;
        }

        @Override
        protected NumberDialogFragment build() {
            return new NumberDialogFragment();
        }
    }

    //---------------------------------------------------------------------
    // Dialog Callback
    //---------------------------------------------------------------------

    public interface NumberDialogClickListener extends DialogClickListener {
        public void onPageSelected(int page, int requestCode);
    }

    //---------------------------------------------------------------------
    // Nested classes
    //---------------------------------------------------------------------

    private class PickerTextWatcher implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence sequence, int start, int before, int count) {
            try {
                int value = Integer.valueOf(String.valueOf(sequence));
                if (value >= numberPicker.getMinValue() && value <= numberPicker.getMaxValue()) {
                    numberPicker.setValue(value);
                }

            } catch (NumberFormatException ex) {
                // swallow error
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }
}