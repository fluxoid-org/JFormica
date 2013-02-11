/*
*    Copyright (c) 2013, Will Szumski
*    Copyright (c) 2013, Doug Szumski
*
*    This file is part of Cyclismo.
*
*    Cyclismo is free software: you can redistribute it and/or modify
*    it under the terms of the GNU General Public License as published by
*    the Free Software Foundation, either version 3 of the License, or
*    (at your option) any later version.
*
*    Cyclismo is distributed in the hope that it will be useful,
*    but WITHOUT ANY WARRANTY; without even the implied warranty of
*    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
*    GNU General Public License for more details.
*
*    You should have received a copy of the GNU General Public License
*    along with Cyclismo.  If not, see <http://www.gnu.org/licenses/>.
*/
package org.cowboycoders.cyclisimo;

import org.cowboycoders.cyclisimo.R;

import android.content.Context;
import android.preference.EditTextPreference;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import org.cowboycoders.cyclisimo.util.PreferencesUtils;

/**
 * The {@link AutoCompleteTextPreference} class is a preference that allows for
 * string input using auto complete . It is a subclass of
 * {@link EditTextPreference} and shows the {@link AutoCompleteTextView} in a
 * dialog.
 * <p>
 * This preference will store a string into the SharedPreferences.
 * 
 * @author Rimas Trumpa (with Matt Levan)
 */
public class AutoCompleteTextPreference extends EditTextPreference {

  private AutoCompleteTextView mEditText = null;

  public AutoCompleteTextPreference(Context context, AttributeSet attrs) {
    super(context, attrs);
    mEditText = new AutoCompleteTextView(context, attrs);
    mEditText.setThreshold(0);

    // Gets autocomplete values for 'Default Activity' preference
    if (PreferencesUtils.getKey(context, R.string.default_activity_key).equals(getKey())) {
      ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
          context, R.array.activity_types, android.R.layout.simple_dropdown_item_1line);
      mEditText.setAdapter(adapter);
    }
  }

  @Override
  protected void onBindDialogView(View view) {
    AutoCompleteTextView editText = mEditText;
    editText.setText(getText());

    ViewParent oldParent = editText.getParent();
    if (oldParent != view) {
      if (oldParent != null) {
        ((ViewGroup) oldParent).removeView(editText);
      }
      onAddEditTextToDialogView(view, editText);
    }
  }

  @Override
  protected void onDialogClosed(boolean positiveResult) {
    if (positiveResult) {
      String value = mEditText.getText().toString();
      if (callChangeListener(value)) {
        setText(value);
      }
    }
  }
}
