package com.ualberta.cmput301w17t22.moodswing;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

/**
 * Main class for the Model portion of the MVC architecture.
 *
 * Handles backend information like the main participants information and saving/loading.
 *
 * Created by nyitrai on 2/26/2017.
 */

public class MoodSwing extends MSModel<MSView> {

    /**
     * This will be the list of MoodEvents that we are displaying to the user.
     * Either the mood history or the mood feed / mood map.
     * Will be filled with MoodEvents once the user chooses to view the history/feed/map.
     */
    private ArrayList<MoodEvent> moodList;

    /**
     * The main participant. The current user of the app from the Android device.
     */
    private Participant mainParticipant;

    /**
     * Adds the mainParticipant for the MoodSwing app to ElasticSearch. This is used when logging
     * in and the username entered is correct but not found.
     *
     * This will initialize the participant with the entered username, and post it to
     * ElasticSearch.
     */
    public void newMainParticipantByUsername(String username) {

        // Get ElasticSearchController.
        ElasticSearchController elasticSearchController =
                MoodSwingApplication.getElasticSearchController();

        // Create a new participant and add it to ElasticSearch.
        this.setMainParticipant(elasticSearchController.newParticipantByUsername(username));

        // Notify Views so they update their information.
        notifyViews();
    }

    /**
     * Loads a Participant by username from ElasticSearch into the mainParticipant.
     *
     * This will be used when first logging in as a participant.
     */
    public void loadMainParticipantByUsername(String username) {

        // Get ElasticSearchController.
        ElasticSearchController elasticSearchController =
                MoodSwingApplication.getElasticSearchController();

        // Get Participant from ElasticSearchController and load it into mainParticipant.
        this.setMainParticipant(elasticSearchController.getParticipantByUsername(username));

        // Then notify the Views that they need to update their information.
        notifyViews();
    }

    /**
     * Add a new mood event to the main participant's mood history.
     * @param emotionalState
     * @param trigger
     * @param socialSituation
     * @param photoLocation
     * @param iconLocation
     * @param location
     */
    public void addMoodEventToMainParticipant(EmotionalState emotionalState,
                             String trigger,
                             SocialSituation socialSituation,
                             String photoLocation,
                             String iconLocation,
                             LatLng location) {

        // Add the mood event to the main participant.
        getMainParticipant().addMoodEvent(
                emotionalState,
                trigger,
                socialSituation,
                photoLocation,
                iconLocation,
                location);

        // Post change to ElasticSearch.
        saveMainParticipant();

        // Notify all the views of the new information.
        notifyViews();
    }

    /**
     * Saves the main participant information (mood history, follower list, following list)
     * to ElasticSearch if online.
     *
     * This will be used when the main participant edits a mood event, creates a new mood event,
     * accepts/declines a follower request, sends out a new follow request, etc.
     */
    public void saveMainParticipant() {
        // Get ElasticSearchController.
        ElasticSearchController elasticSearchController =
                MoodSwingApplication.getElasticSearchController();

        // Update the main participant on ElasticSearch.
        elasticSearchController.updateParticipantByParticipant(mainParticipant);
    }

    /**
     * An alternative to saveOnline that is used if the device is not online. This will store
     * the same information onto a temporary file on the device.
     *
     * This will be used if offline when the main participant edits a mood event, creates
     * a new mood event, accepts/declines a follower request, sends out a new follow request, etc.
     */
    public void saveLocal() {

    }

    // --- START: Getters and Setters

    public ArrayList<MoodEvent> getMoodList() { return moodList; }

    public void setMoodList(ArrayList<MoodEvent> moodList) { this.moodList = moodList; }

    public Participant getMainParticipant() { return mainParticipant; }

    public void setMainParticipant(Participant mainParticipant) {
        this.mainParticipant = mainParticipant;
    }

    // --- END: Getters and Setters

    MoodSwing() { super(); }
}
