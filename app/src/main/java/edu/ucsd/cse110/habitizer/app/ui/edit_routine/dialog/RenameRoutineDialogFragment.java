package edu.ucsd.cse110.habitizer.app.ui.edit_routine.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import edu.ucsd.cse110.habitizer.app.MainViewModel;
import edu.ucsd.cse110.habitizer.app.databinding.FragmentDialogRenameRoutineBinding;
import edu.ucsd.cse110.habitizer.lib.domain.Routine;

public class RenameRoutineDialogFragment extends DialogFragment {

    private FragmentDialogRenameRoutineBinding view;
    private MainViewModel activityModel;
    private RenameRoutineDialogFragment.OnRoutineRenameListener listener;
    private Routine routine;

    public RenameRoutineDialogFragment(Routine routine)
    {
        this.routine = routine;
    }

    public interface OnRoutineRenameListener {
        void onRoutineRename(String newName);
    }

    public static RenameRoutineDialogFragment newInstance(Routine routine)
    {
        var fragment = new RenameRoutineDialogFragment(routine);
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public void setRenameRoutineListener(RenameRoutineDialogFragment.OnRoutineRenameListener listener) {
        this.listener = listener;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        var modelOwner = requireActivity();
        var modelFactory = ViewModelProvider.Factory.from(MainViewModel.initializer);
        var modelProvider = new ViewModelProvider(modelOwner, modelFactory);
        this.activityModel = modelProvider.get(MainViewModel.class);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        this.view = FragmentDialogRenameRoutineBinding.inflate(getLayoutInflater());

        return new AlertDialog.Builder(getActivity())
                .setTitle("Rename Routine")
                .setMessage("Please provide the new name of this routine.")
                .setView(view.getRoot())
                .setPositiveButton("Rename", this::onPositiveButtonClick)
                .setNegativeButton("Cancel", this::onNegativeButtonClick)
                .create();
    }

    private void onPositiveButtonClick(DialogInterface dialog, int which) {
        var newName = view.renameRoutineText.getText().toString();
        routine.rename(newName);
        if (listener != null) {
            listener.onRoutineRename(newName);
        }
        dialog.dismiss();
    }

    private void onNegativeButtonClick(DialogInterface dialog, int which)
    {
        dialog.cancel();
    }
}
