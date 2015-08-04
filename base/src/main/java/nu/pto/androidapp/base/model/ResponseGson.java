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
import nu.pto.androidapp.base.db.model.ExerciseType;
import nu.pto.androidapp.base.db.model.Image;
import nu.pto.androidapp.base.db.model.Message;
import nu.pto.androidapp.base.db.model.MuscleGroup;
import nu.pto.androidapp.base.db.model.Package;
import nu.pto.androidapp.base.db.model.PackageCategory;
import nu.pto.androidapp.base.db.model.PackageLevel;
import nu.pto.androidapp.base.db.model.PackagePrice;
import nu.pto.androidapp.base.db.model.PackageWeek;
import nu.pto.androidapp.base.db.model.Set;
import nu.pto.androidapp.base.db.model.SetSession;
import nu.pto.androidapp.base.db.model.Trainer;
import nu.pto.androidapp.base.db.model.Workout;
import nu.pto.androidapp.base.db.model.WorkoutCategory;
import nu.pto.androidapp.base.db.model.WorkoutSession;


public class ResponseGson {

    @Expose
    @SerializedName(value = "expired_user")
    private int expiredUser;

    @Expose
    @SerializedName(value = "package_prices")
    private List<PackagePrice> packagePrices = new ArrayList<PackagePrice>();

    @Expose
    @SerializedName(value = "package_levels")
    private List<PackageLevel> packageLevels = new ArrayList<PackageLevel>();

    @Expose
    @SerializedName(value = "package_weeks")
    private List<PackageWeek> packageWeeks = new ArrayList<PackageWeek>();

    @Expose
    @SerializedName(value = "muscle_groups")
    private List<MuscleGroup> muscleGroups = new ArrayList<MuscleGroup>();


    @Expose
    private List<Image> images = new ArrayList<Image>();


    @Expose
    private List<Trainer> trainers = new ArrayList<Trainer>();

    @Expose
    private List<Client> clients = new ArrayList<Client>();

    @Expose
    @SerializedName(value = "package_categories")
    private List<PackageCategory> packageCategories = new ArrayList<PackageCategory>();

    @Expose
    private List<Package> packages = new ArrayList<Package>();


    @Expose
    @SerializedName(value = "client_packages")
    private List<ClientPackage> clientPackages = new ArrayList<ClientPackage>();


    @Expose
    @SerializedName(value = "diet_categories")
    private List<DietCategory> dietCategories = new ArrayList<DietCategory>();

    @Expose
    private List<Diet> diets = new ArrayList<Diet>();


    @Expose
    @SerializedName(value = "workout_categories")
    private List<WorkoutCategory> workoutCategories = new ArrayList<WorkoutCategory>();

    @Expose
    private List<Workout> workouts = new ArrayList<Workout>();


    @Expose
    @SerializedName(value = "exercise_types")
    private List<ExerciseType> exerciseTypes = new ArrayList<ExerciseType>();

    @Expose
    private List<Exercise> exercises = new ArrayList<Exercise>();


    @Expose
    private List<Set> sets = new ArrayList<Set>();

    @Expose
    @SerializedName(value = "workout_sessions")
    private List<WorkoutSession> workoutSessions = new ArrayList<WorkoutSession>();

    @Expose
    @SerializedName(value = "set_sessions")
    private List<SetSession> setSessions = new ArrayList<SetSession>();

    @Expose
    private List<Message> messages = new ArrayList<Message>();


    public ResponseGson() {
    }


    public int getExpiredUser() {
        return expiredUser;
    }

    public List<Image> getImages() {
        return images;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public List<Client> getClients() {
        return clients;
    }

    public List<Trainer> getTrainers() {
        return trainers;
    }


    public List<DietCategory> getDietCategories() {
        return dietCategories;
    }

    public List<Package> getPackages() {
        return packages;
    }

    public List<PackageCategory> getPackageCategories() {
        return packageCategories;
    }

    public List<Diet> getDiets() {
        return diets;
    }

    public List<PackageLevel> getPackageLevels() {
        return packageLevels;
    }

    public List<PackagePrice> getPackagePrices() {
        return packagePrices;
    }

    public List<PackageWeek> getPackageWeeks() {
        return packageWeeks;
    }

    public List<MuscleGroup> getMuscleGroups() {
        return muscleGroups;
    }

    public List<ClientPackage> getClientPackages() {
        return clientPackages;
    }

    public List<WorkoutCategory> getWorkoutCategories() {
        return workoutCategories;
    }

    public List<Workout> getWorkouts() {
        return workouts;
    }

    public List<ExerciseType> getExerciseTypes() {
        return exerciseTypes;
    }

    public List<Exercise> getExercises() {
        return exercises;
    }

    public List<Set> getSets() {
        return sets;
    }

    public List<SetSession> getSetSessions() {
        return setSessions;
    }

    public List<WorkoutSession> getWorkoutSessions() {
        return workoutSessions;
    }
}
