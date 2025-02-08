package edu.ucsd.cse110.habitizer.app;

import android.util.Log;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.viewmodel.ViewModelInitializer;

import java.util.List;

import edu.ucsd.cse110.habitizer.lib.domain.Routine;
import edu.ucsd.cse110.habitizer.lib.domain.RoutineRepository;
import edu.ucsd.cse110.observables.PlainMutableSubject;
import edu.ucsd.cse110.observables.Subject;

import static androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY;

public class MainViewModel extends ViewModel {
    private final RoutineRepository routineRepository;

    private final PlainMutableSubject<List<Routine>> routines;

    public static final ViewModelInitializer<MainViewModel> initializer = new ViewModelInitializer<>(
            MainViewModel.class,
            creationExtras -> {
                var app = (HabitizerApplication) creationExtras.get(APPLICATION_KEY);
                assert app != null;
                return new MainViewModel(app.getRoutineRepository());
            }
    );

    public MainViewModel(RoutineRepository routineRepository) {
        this.routineRepository = routineRepository;
        this.routines = new PlainMutableSubject<>();

        routineRepository.findAll().observe(routines -> {
           if(routines == null) return;
           this.routines.setValue(routines);
        });
    }

    public Subject<List<Routine>> getRoutines() {return routines;}

    public List<Routine> getListRoutines() {return routines.getValue();}
}
