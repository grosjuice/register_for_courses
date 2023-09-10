package com.example.client_fx;

import com.example.server.models.Course;
import com.example.server.models.RegistrationForm;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.scene.control.*;

/**
 * controller in the MVC pattern
 */
public class GUIController implements Initializable {

    // attributes
    @FXML
    private TextField textFieldFirstName;
    @FXML
    private TextField textFieldLastName;
    @FXML
    private TextField textFieldEmail;
    @FXML
    private TextField textFieldMatricule;
    @FXML
    private ChoiceBox<String> choiceBox;
    @FXML ListView<String> listView;
    private String[] semesters = {"Automne", "Hiver", "Ete"};
    private Model model;
    private RegistrationForm registrationForm;


    /**
     *  constructor of the GUIController class
     */
    public GUIController() {
        try {
            this.model = new Model();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * initializes the elements of the choice box
     *
     * @param url passes the location of the FXML file that contains the controller
     * @param resourceBundle used to pass a bundle of resources that can be used by the controller
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle){
        choiceBox.getItems().addAll(semesters);
    }

    /**
     * loads available courses for the semester
     *
     * @param e action event triggered when the user clicks on the "charger" button
     */
    public void buttonChargerClicked(ActionEvent e) {

        // clears elements in the listView to make sure that only courses of the selected semester are displayed
        listView.getItems().clear();

        // get available courses for the selected semester
        model.loadCourses(this.choiceBox.getValue());
        model.getAvailableCourses();

        // add course info to list view
        for (Course course: Model.courses) {
            listView.getItems().add(course.getCode() + "\t\t\t" + course.getName());
        }

        // reconnects to server after being disconnected
        model.connect();

    }

    /**
     * registers the user for the selected course
     *
     * @param e event triggered when user clicks on the "envoyer" button
     */
    public void buttonInscriptionClicked(ActionEvent e) {

        // get info entered by user
        String firstName = this.textFieldFirstName.getText();
        String lastName = this.textFieldLastName.getText();
        String email = this.textFieldEmail.getText();
        String matricule = this.textFieldMatricule.getText();

        // exception thrown if user doesn't select a course and clicks on "envoyer"
        String[] courseCodeAndName = null;
        try {

            MultipleSelectionModel<String> selectionModel = listView.getSelectionModel();
            courseCodeAndName = selectionModel.getSelectedItem().split("\t");
            String courseCode = courseCodeAndName[0];

            // finds the course selected in the list view in the arrayList of courses available for the semester
            // will throw null pointer exception if no course is selected by user
            Course courseFound = null;
            for (Course course: Model.courses) {
                if (course.getCode().equals(courseCode)) {
                    courseFound = course;
                    break;
                }
            }

            // if email entered is invalid
            if (!model.validateEmail(email)) {
                alertEmailErrorMessage();
                throw new IllegalArgumentException();
            }

            // if ID entered is invalid
            if (!model.validateID(matricule)) {
                alertIDErrorMessage();
                throw new IllegalArgumentException();
            }

            // create and send registration form to model if and only if no error thrown
            registrationForm = new RegistrationForm(firstName, lastName, email, matricule, courseFound); //
            model.registerForCourses(registrationForm);

            // will show confirmation message if and only if there were no errors thrown
            printConfirmationMessage();

            // reconnects to server after being disconnected
            model.connect();

        } catch (IllegalArgumentException exInvalidEmail) {
            System.out.println("Invalid email or ID");
        } catch (NullPointerException exNoSelection) {
            System.out.println("No course selected");
            alertNoSelectedCourseErrorMessage();
        }
    }

    /**
     * shows an alert error window whenever the user enters an invalid email
     */
    public void alertEmailErrorMessage() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur email");
        alert.setHeaderText("Il semble avoir une erreur dans votre email");
        alert.setContentText("SVP veuillez entrer un email valide!");
        alert.showAndWait();
    }

    /**
     * shows an alert error window whenever the user enters an invalid ID
     */
    public void alertIDErrorMessage() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur matricule");
        alert.setHeaderText("Il semble avoir une erreur dans votre matricule");
        alert.setContentText("SVP veuillez entrer un matricule valide! (8 chiffres)");
        alert.showAndWait();
    }

    /**
     * shows an alert error window whenever the user doesn't select a course to register before clicking on "envoyer"
     */
    public void alertNoSelectedCourseErrorMessage() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur sélection du cours");
        alert.setHeaderText("Erreur dans la sélection du cours");
        alert.setContentText("Vous devez sélectionner un cours avant de cliquer sur envoyer!");
        alert.showAndWait();
    }


    /**
     * shows a confirmation message to user when the registration was succesful
     */
    public void printConfirmationMessage() {
        model.setConfirmationMessage();
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Confirmation d'inscription");
        alert.setHeaderText("Information sur votre confirmation d'inscription");
        alert.setContentText(model.getConfirmationMessage());
        alert.showAndWait();
    }




}
