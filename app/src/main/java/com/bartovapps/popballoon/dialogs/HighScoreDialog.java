package com.bartovapps.popballoon.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Created by motibartov on 11/04/2017.
 */

public class HighScoreDialog extends DialogFragment {

    public static final String TITLE_KEY = "title_key";
    public static final String MESSAGE_KEY = "message_key";

    public HighScoreDialog() {
    }

    public static HighScoreDialog newInstance(String title, String message){

        Bundle b = new Bundle();
        b.putString(TITLE_KEY, title);
        b.putString(MESSAGE_KEY, message);
        HighScoreDialog highScoreDialog = new HighScoreDialog();
        highScoreDialog.setArguments(b);
        return  highScoreDialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String title = getArguments().getString(TITLE_KEY);
        String message = getArguments().getString(MESSAGE_KEY);
        AlertDialog.Builder builder =  new AlertDialog.Builder(getActivity());
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setNeutralButton(android.R.string.ok, null);
        return builder.create();
    }
}
