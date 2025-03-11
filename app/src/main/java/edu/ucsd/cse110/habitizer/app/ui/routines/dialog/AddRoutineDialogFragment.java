package edu.ucsd.cse110.habitizer.app.ui.routines.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import edu.ucsd.cse110.habitizer.app.MainViewModel;
import edu.ucsd.cse110.habitizer.app.R;
import edu.ucsd.cse110.habitizer.app.databinding.FragmentDialogAddRoutineBinding;
import edu.ucsd.cse110.habitizer.app.ui.edit_routine.EditRoutineFragment;
import edu.ucsd.cse110.habitizer.lib.domain.Routine;

public class AddRoutineDialogFragment extends DialogFragment {

    private FragmentDialogAddRoutineBinding view;
    private MainViewModel activityModel;

    private Routine routine;

    public AddRoutineDialogFragment() {
    }

    public static AddRoutineDialogFragment newInstance() {
        var fragment = new AddRoutineDialogFragment();
        Bundle args = new Bundle();
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
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        this.view = FragmentDialogAddRoutineBinding.inflate(getLayoutInflater());

        return new AlertDialog.Builder(getActivity())
                .setTitle("Add Routine")
                .setMessage("Please provide the name of this new routine.")
                .setView(view.getRoot())
                .setPositiveButton("Add", this::onPositiveButtonClick)
                .setNegativeButton("Cancel", this::onNegativeButtonClick)
                .create();
    }

    private void onPositiveButtonClick(DialogInterface dialog, int which) {
        var routineName = view.routineNameText.getText().toString();
        if (routineName.equals(""))
        {
            routineName = "New Routine";
        }
        var routineId = activityModel.getRoutines().getValue().size();
        routine = new Routine(routineId, routineName);
        activityModel.addRoutine(routine);


        dialog.dismiss();
        FragmentActivity activity = (FragmentActivity) getContext();
        FragmentTransaction transaction = activity.getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, EditRoutineFragment.newInstance(routine));
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void onNegativeButtonClick(DialogInterface dialog, int which)
    {
        dialog.cancel();
    }
}
