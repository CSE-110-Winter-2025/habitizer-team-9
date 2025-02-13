package edu.ucsd.cse110.habitizer.app.ui.edit_routine.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import edu.ucsd.cse110.habitizer.app.databinding.FragmentDialogRenameTaskBinding;

public class RenameTaskDialogFragment extends DialogFragment {

    private FragmentDialogRenameTaskBinding view;

    public RenameTaskDialogFragment() {
        // Required empty public constructor
    }

    public static RenameTaskDialogFragment newInstance() {
        var fragment = new RenameTaskDialogFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        this.view = FragmentDialogRenameTaskBinding.inflate(getLayoutInflater());

        return new AlertDialog.Builder(getActivity())
                .setTitle("Rename Task")
                .setMessage("Please provide the new name of this task.")
                .setView(view.getRoot())
                .setPositiveButton("Rename", this::onPositiveButtonClick)
                .setNegativeButton("Cancel", this::onNegativeButtonClick)
                .create();
    }

    private void onPositiveButtonClick(DialogInterface dialog, int which) {
        dialog.dismiss();
    }

    private void onNegativeButtonClick(DialogInterface dialog, int which) {
        dialog.cancel();
    }
}
