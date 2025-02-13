package edu.ucsd.cse110.habitizer.lib.domain;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;

import java.util.Objects;

public class Task {

    private @NonNull String name;
    private @Nullable Integer id;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return isCheckedOff == task.isCheckedOff && Objects.equals(getName(), task.getName()) && Objects.equals(getId(), task.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getId(), isCheckedOff);
    }

    private boolean isCheckedOff;
    public Task(int id, String name) {
        this.id = id;
        this.name = name;
        this.isCheckedOff = false;
    }

    public @NonNull String getName(){
        return this.name;
    }

    public @Nullable Integer getId(){
        return this.id;
    }

    public boolean getIsCheckedOff(){
        return this.isCheckedOff;
    }

    public void checkOff(){
        this.isCheckedOff = !isCheckedOff;
    }

    public void rename(@NonNull String newName) {
        this.name = newName;
    }
}
