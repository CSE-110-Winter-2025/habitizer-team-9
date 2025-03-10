package edu.ucsd.cse110.habitizer.app.ui.routines.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import edu.ucsd.cse110.habitizer.app.MainViewModel;

public class DeleteRoutineDialogFragment extends DialogFragment {
    private int routineId;
    private MainViewModel activityModel;

    public DeleteRoutineDialogFragment() {
    }

    public static DeleteRoutineDialogFragment newInstance(int routineId) {
        var fragment = new DeleteRoutineDialogFragment();
        var args = new Bundle();
        args.putInt("routineId", routineId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        var modelOwner = requireActivity();
        var modelFactory = ViewModelProvider.Factory.from(MainViewModel.initializer);
        var modelProvider = new ViewModelProvider(modelOwner, modelFactory);
        this.activityModel = modelProvider.get(MainViewModel.class);

        if (getArguments() != null) {
            this.routineId = getArguments().getInt("routineId");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return new AlertDialog.Builder(getActivity())
                .setTitle("Delete Routine")
                .setMessage("Are you sure you want to delete this routine?")
                .setPositiveButton("Delete", this::onPositiveButtonClick)
                .setNegativeButton("Cancel", this::onNegativeButtonClick)
                .create();
    }

    private void onPositiveButtonClick(DialogInterface dialog, int which) {
        activityModel.deleteRoutine(routineId);
        dialog.dismiss();
        navigateBack();
    }

    private void navigateBack() {
        FragmentActivity activity = requireActivity();
        activity.getSupportFragmentManager().popBackStack();
    }

    private void onNegativeButtonClick(DialogInterface dialog, int which) {
        dialog.cancel();
    }
}

