package com.example.client_simple;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

import com.example.server.Server;
import com.example.server.models.Course;
import com.example.server.models.RegistrationForm;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Client {
    // attributes
    private Socket clientSocket;
    private ObjectOutputStream objectOutputStream;

    private ObjectInputStream objectInputStream;
    private static ArrayList<Course> courses = null;
    private String cmd;
    private String args;



    public Client(int port) throws IOException {
        clientSocket = new Socket("127.0.0.1", port);
    }

    public void run() {

        try {
            objectOutputStream = new ObjectOutputStream(clientSocket.getOutputStream());
            objectInputStream = new ObjectInputStream(clientSocket.getInputStream());

        } catch (IOException e) {
            e.printStackTrace();
        }

        menu();
    }

    public void menu() {
        // First, asks to choose a semester to display courses. Will only display the following message if it is the
        // first request to the server
        if (ClientLauncher.firstRequest) {
            System.out.println("*** Bienvenue au portail d'inscription de cours de l'UDEM");
            displayCoursesMenu();
        }


        else {

            boolean validChoice;  // will be set to false if choice is invalid

            do {
                validChoice = true;

                // 2nd, asks user to choose between displaying the courses of another semester or register for a course
                System.out.println("> Choix:");
                System.out.println("1. Consulter les cours offerts pour une autre session");
                System.out.println("2. Inscription à un cours");

                Scanner scanner = new Scanner(System.in);
                String userInput = scanner.nextLine();
                System.out.println("> Choix: " + userInput);

                switch (userInput) {
                    case "1": {
                        displayCoursesMenu();
                        break;
                    }
                    case "2": {
                        registerForCourses();
                        break;
                    }
                    default: {
                        System.out.println("Choix invalide. Veuillez choisir parmi les choix disponibles");
                        validChoice = false;
                    }
                }

            } while (!validChoice);

        }

    }

    // menu that displays the courses
    public void displayCoursesMenu() {

        boolean invalidChoice;

        do {
            invalidChoice = false;
            
            Scanner scanner = new Scanner(System.in);

            System.out.println("Veuillez choisir la session pour laquelle vous voulez consulter la liste des cours");
            System.out.println("1. Automne \n2. Hiver \n3. Ete");

            String userInput = scanner.nextLine();
            cmd = Server.LOAD_COMMAND;

            // the user can enter either enter the semesters name or the corresponding number (1, 2 or 3)
            if (userInput.equalsIgnoreCase("Automne")) {
                userInput = "1";
            }   else if (userInput.equalsIgnoreCase("Hiver")) {
                userInput = "2";
            }   else if (userInput.equalsIgnoreCase("Ete")) {
                userInput = "3";
            }   else if (userInput.equalsIgnoreCase("Été")) {
                userInput = "3";
            }

            System.out.println("> Choix: " + userInput);


            switch (userInput) {
                case "1":
                    args = "Automne";
                    break;
                case "2":
                    args = "Hiver";
                    break;
                case "3":
                    args = "Ete";
                    break;
                default:
                    System.out.println("Choix invalide de semestre. Choisissez parmi les 3 choix disponibles.");
                    invalidChoice = true;
            }
        } while (invalidChoice);
        

        if (args != null) {
            try {
                objectOutputStream.writeObject(cmd + " " + args);
                objectOutputStream.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        getAvailableCourses();
        displayCourses();
    }

    public void getAvailableCourses() {

        try {
            this.courses = (ArrayList<Course>) objectInputStream.readObject();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void displayCourses() {
        // display courses
        System.out.println("Les cours offerts pendant la session d'" + args + " sont:");

        for (Course course: this.courses) {
            System.out.println(course.getCode() + "\t" +
                    course.getName() + "\t" +
                    course.getSession() + "\t");
        }
    }

    public void registerForCourses() {

        Scanner scanner = new Scanner(System.in);

        System.out.println("Veuillez saisir votre prénom:");
        String prenom = scanner.nextLine();

        System.out.println("Veuillez saisir votre nom:");
        String nom = scanner.nextLine();


        // email needs to be validated
        boolean emailIsValid;
        String email;
        do {
            System.out.println("Veuillez saisir votre email:");
            email = scanner.nextLine();
            emailIsValid = validateEmail(email);

            if (!emailIsValid) { // invalid email entered
                System.out.println("Veuillez entrer un email valide");
            }
        } while (!emailIsValid);


        // ID needs to be validated
        boolean IDIsValid;
        String matricule;
        String ID;
        do {
            System.out.println("Veuillez saisir votre matricule");
            matricule = scanner.nextLine();
            IDIsValid = validateID(matricule);

            if (!IDIsValid) {
                System.out.println("Veuillez entrer un matricule étudiant valide (exactement 8 chiffres");
            }
        } while (!IDIsValid);


        // ensures that the entered course is offered for the specified semester
        boolean validChoice;
        Course course;
        do {
            validChoice = true;
            System.out.println("Veuillez saisir le code du cours");
            String courseCodeEntered = scanner.nextLine();
            course = validateCourse(courseCodeEntered);

            if (course == null) {   // invalid code for the semester
                System.out.println("Ce cours n'est pas disponible pour ce trimestre");
                System.out.println("Échec de l'inscription");
                validChoice = false;
            }
        } while (!validChoice);

            // creates a registration form when all inputs are valid
            RegistrationForm registrationForm = new RegistrationForm(prenom, nom, email, matricule, course);

            try {
                cmd = Server.REGISTER_COMMAND;
                objectOutputStream.writeObject(cmd + " " + args);
                objectOutputStream.flush();
                objectOutputStream.writeObject(registrationForm);
                objectOutputStream.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }

            // receive confirmation message from client
            try {
                String confirmationMessage = (String) objectInputStream.readObject();
                System.out.println(confirmationMessage);
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }

    // checks if the course code is in the list of available courses for the semester
    // if found, returns the corresponding Course object
    public Course validateCourse(String courseCodeEntered) {

        for (Course course: this.courses) {
            if (course.getCode().equals(courseCodeEntered)) {
                return course;    // valid code for the semester
            }
        }

        return null;    // invalid course for the semester
    }

    public static boolean validateEmail(String input) {
        String emailRegex = "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$";
        Pattern emailPattern = Pattern.compile(emailRegex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = emailPattern.matcher(input);
        return matcher.find();
    }

    public static boolean validateID(String input) {
        String IDRegex = "^[0-9]{8}$";
        Pattern IDPattern = Pattern.compile(IDRegex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = IDPattern.matcher(input);
        return matcher.find();
    }

}
