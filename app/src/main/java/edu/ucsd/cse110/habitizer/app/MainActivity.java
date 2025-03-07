package edu.ucsd.cse110.habitizer.app;

import android.os.Bundle;
import android.view.Menu;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import edu.ucsd.cse110.habitizer.app.databinding.ActivityMainBinding;
import edu.ucsd.cse110.habitizer.app.ui.routines.RoutinesFragment;
import edu.ucsd.cse110.habitizer.app.ui.tasks.TaskListFragment;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding view;
    private boolean isShowingRoutines = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.view = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(view.getRoot());
    }
}