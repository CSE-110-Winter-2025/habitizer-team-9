package edu.ucsd.cse110.habitizer.app.ui.edit_routine.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import edu.ucsd.cse110.habitizer.app.MainViewModel;
import edu.ucsd.cse110.habitizer.app.databinding.FragmentDialogGoalTimeBinding;
import edu.ucsd.cse110.habitizer.lib.domain.Routine;

public class ChangeGoalTimeDialogFragment extends DialogFragment {

    private @NonNull FragmentDialogGoalTimeBinding view;
    private MainViewModel activityModel;
    private Routine routine;
    private OnGoalTimeUpdatedListener listener;
    ChangeGoalTimeDialogFragment(Routine routine) {
        this.routine = routine;
    }

    public interface OnGoalTimeUpdatedListener {
        void onGoalTimeUpdated(long newGoalTime);
    }

    public static ChangeGoalTimeDialogFragment newInstance(Routine routine) {
        var fragment = new ChangeGoalTimeDialogFragment(routine);
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public void setOnGoalTimeUpdatedListener(OnGoalTimeUpdatedListener listener) {
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
        this.view = FragmentDialogGoalTimeBinding.inflate(getLayoutInflater());

        return new AlertDialog.Builder(getActivity())
                .setTitle("Set Goal Time")
                .setMessage("Please provide the goal time for this routine.")
                .setView(view.getRoot())
                .setPositiveButton("Set", this::onPositiveButtonClick)
                .setNegativeButton("Cancel", this::onNegativeButtonClick)
                .create();
    }

    private void onPositiveButtonClick(DialogInterface dialog, int which) {
        String newGoalTime = view.goalTimeText.getText().toString();
        try {
            routine.setGoalTime(Long.parseLong(newGoalTime));
            long parsedGoalTime = Long.parseLong(newGoalTime);
            activityModel.updateGoalTime(routine.id(), parsedGoalTime);
        } catch (Exception e) {
            Log.d("Not a valid time", newGoalTime);
        }

        if (listener != null) {
            listener.onGoalTimeUpdated(Long.parseLong(newGoalTime));
        }

        dialog.dismiss();
    }

    private void onNegativeButtonClick(DialogInterface dialog, int which) {
        dialog.cancel();
    }
}
