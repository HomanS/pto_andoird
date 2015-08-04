package nu.pto.androidapp.base.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import nu.pto.androidapp.base.db.model.Client;
import nu.pto.androidapp.base.db.model.ClientPackage;
import nu.pto.androidapp.base.db.model.Diet;
import nu.pto.androidapp.base.db.model.DietCategory;
import nu.pto.androidapp.base.db.model.Exercise;
import nu.pto.androidapp.base.db.model.Message;
import nu.pto.androidapp.base.db.model.Package;
import nu.pto.androidapp.base.db.model.PackageCategory;
import nu.pto.androidapp.base.db.model.Set;
import nu.pto.androidapp.base.db.model.SetSession;
import nu.pto.androidapp.base.db.model.Trainer;
import nu.pto.androidapp.base.db.model.Workout;
import nu.pto.androidapp.base.db.model.WorkoutCategory;
import nu.pto.androidapp.base.db.model.WorkoutSession;


public class RequestGson {

    @Expose(serialize = true, deserialize = false)
    public int logout = 0;

    @Expose(serialize = true, deserialize = false)
    private List<Trainer> trainers = new ArrayList<Trainer>();

    @Expose(serialize = true, deserialize = false)
    private List<Client> clients = new ArrayList<Client>();

    @Expose(serialize = false, deserialize = false)
    @SerializedName(value = "package_categories")
    private List<PackageCategory> packageCategories = new ArrayList<PackageCategory>();

    @Expose(serialize = false, deserialize = false)
    private List<Package> packages = new ArrayList<Package>();

    @Expose(serialize = false, deserialize = false)
    @SerializedName(value = "client_packages")
    private List<ClientPackage> clientPackages = new ArrayList<ClientPackage>();

    @Expose(serialize = false, deserialize = false)
    @SerializedName(value = "diet_categories")
    private List<DietCategory> dietCategories = new ArrayList<DietCategory>();

    @Expose(serialize = false, deserialize = false)
    private List<Diet> diets = new ArrayList<Diet>();

    @Expose(serialize = false, deserialize = false)
    @SerializedName(value = "workout_categories")
    private List<WorkoutCategory> workoutCategories = new ArrayList<WorkoutCategory>();

    @Expose(serialize = false, deserialize = false)
    private List<Workout> workouts = new ArrayList<Workout>();

    @Expose(serialize = false, deserialize = false)
    private List<Exercise> exercises = new ArrayList<Exercise>();

    @Expose(serialize = false, deserialize = false)
    private List<Set> sets = new ArrayList<Set>();

    @Expose(serialize = true, deserialize = false)
    @SerializedName(value = "workout_sessions")
    private List<WorkoutSession> workoutSessions = new ArrayList<WorkoutSession>();

    @Expose(serialize = true, deserialize = false)
    @SerializedName(value = "set_sessions")
    private List<SetSession> setSessions = new ArrayList<SetSession>();

    @Expose(serialize = true, deserialize = false)
    private List<Message> messages = new ArrayList<Message>();

    public void setLogout(int logout) {
        this.logout = logout;
    }

    public void setTrainers(List<Trainer> trainers) {
        this.trainers = trainers;
    }

    public void setClients(List<Client> clients) {
        this.clients = clients;
    }

    public void setPackageCategories(List<PackageCategory> packageCategories) {
        this.packageCategories = packageCategories;
    }

    public void setPackages(List<Package> packages) {
        this.packages = packages;
    }

    public void setClientPackages(List<ClientPackage> clientPackages) {
        this.clientPackages = clientPackages;
    }

    public void setDietCategories(List<DietCategory> dietCategories) {
        this.dietCategories = dietCategories;
    }

    public void setDiets(List<Diet> diets) {
        this.diets = diets;
    }

    public void setWorkoutCategories(List<WorkoutCategory> workoutCategories) {
        this.workoutCategories = workoutCategories;
    }

    public void setWorkouts(List<Workout> workouts) {
        this.workouts = workouts;
    }

    public void setExercises(List<Exercise> exercises) {
        this.exercises = exercises;
    }

    public void setSets(List<Set> sets) {
        this.sets = sets;
    }

    public void setWorkoutSessions(List<WorkoutSession> workoutSessions) {
        this.workoutSessions = workoutSessions;
    }

    public void setSetSessions(List<SetSession> setSessions) {
        this.setSessions = setSessions;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }
}
