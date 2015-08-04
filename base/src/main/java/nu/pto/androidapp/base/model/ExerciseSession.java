package nu.pto.androidapp.base.model;

import java.util.ArrayList;

import nu.pto.androidapp.base.db.model.Exercise;
import nu.pto.androidapp.base.db.model.SetSession;

public class ExerciseSession {
    private Exercise exercise;
    private ArrayList<SetSession> setSessions;
    private String imageId;

    public ExerciseSession() {
        setSessions = new ArrayList<SetSession>();
    }

    public Exercise getExercise() {
        return exercise;
    }

    public void setExercise(Exercise exercise) {
        this.exercise = exercise;
        String[] decodedImages = exercise.imagesIds.split("#\\|#");
        if (decodedImages.length > 0) {
            imageId = decodedImages[0];
        }
    }

    public ArrayList<SetSession> getSetSessions() {
        return setSessions;
    }

    public void setSetSessions(ArrayList<SetSession> setSessions) {
        this.setSessions = setSessions;
    }

    public void addSetSession(SetSession setSession) {
        this.setSessions.add(setSession);
    }

    public String getImageId() {
        return imageId;
    }

    @Override
    public String toString() {
        return "ExerciseId : " + exercise.serverExerciseId + " | " + setSessions.size() + " | ";
    }
}
