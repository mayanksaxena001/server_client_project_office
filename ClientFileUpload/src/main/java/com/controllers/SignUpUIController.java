package com.controllers;

import java.net.URL;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.service.LoginService;
import com.util.Gender;
import com.util.User;
import com.util.UserDetail;
import com.util.Utility;

@Component
@Qualifier("signUpUIController")
public class SignUpUIController implements Initializable {

    private static final String DATE_PATTERN = "yyyy-MM-dd";
    private static final String PASSWORD_PATTERN = "((?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%]).{6,20})";
    private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
	    + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    @FXML
    TextField firstNameTextField;

    @FXML
    TextField lastNameTextField;

    @FXML
    TextField emailTextField;

    @FXML
    TextField userNameTextField;

    @FXML
    TextField passwordTextField;

    @FXML
    PasswordField passwordField;

    @FXML
    RadioButton maleRadioButton;

    @FXML
    Button okButton;

    @FXML
    DatePicker dobPicker;

    @Autowired
    LoginService loginService;

    @Autowired
    BaseUIController baseUIController;

    @Autowired
    private FileUploadClientUIController fileUploadClientUIController;;

    public void initialize(URL arg0, ResourceBundle arg1) {
	reset();
	dobPicker.setShowWeekNumbers(false);
	dobPicker.setPromptText(DATE_PATTERN.toLowerCase());
	dobPicker.setConverter(new StringConverter<LocalDate>() {
	    DateTimeFormatter dateFormatter = DateTimeFormatter
		    .ofPattern(DATE_PATTERN);

	    @Override
	    public String toString(LocalDate date) {
		if (date != null) {
		    return dateFormatter.format(date);
		} else {
		    return "";
		}
	    }

	    @Override
	    public LocalDate fromString(String string) {
		if (string != null && !string.isEmpty()) {
		    return LocalDate.parse(string, dateFormatter);
		} else {
		    return null;
		}
	    }
	});
	okButton.disableProperty().bind(
		userNameTextField
			.textProperty()
			.isEmpty()
			.or(firstNameTextField
				.textProperty()
				.isEmpty()
				.or(lastNameTextField
					.textProperty()
					.isEmpty()
					.or(passwordField
						.textProperty()
						.isEmpty()
						.or(passwordTextField
							.textProperty()
							.isEmpty())
						.or(emailTextField
							.textProperty()
							.isEmpty())))));
	temporaryFields();
    }

    public void reset() {
	firstNameTextField.clear();
	lastNameTextField.clear();
	emailTextField.clear();
	passwordField.clear();
	passwordTextField.clear();
	userNameTextField.clear();
	maleRadioButton.setSelected(true);
	dobPicker.setValue(LocalDate.now());
    }

    @FXML
    public void handleOkButtonAction(Event event) {
	try {
	    UserDetail userDetail = validate();
	    // TO-DO save user
	    loginService.storeUserDetailToServer(userDetail);
	    baseUIController.loadDashBoardScreen();
	    fileUploadClientUIController.setCurrentUserLabel(userDetail
		    .getUser());
	    event.consume();
	} catch (Exception e) {
	    Utility.reportError("Errors\n" + e.getMessage());
	}
    }

    private UserDetail validate() throws Exception {
	UserDetail userDetail = new UserDetail();
	StringBuilder errors = new StringBuilder();

	Date date = Date.from(dobPicker.getValue()
		.atStartOfDay(ZoneId.systemDefault()).toInstant());
	if (firstNameTextField.getText().isEmpty()
		|| firstNameTextField.getText() == null) {
	    errors.append("First Name TextField is invalid\n");
	    firstNameTextField.clear();
	}
	if (lastNameTextField.getText().isEmpty()
		|| lastNameTextField.getText() == null) {
	    errors.append("Last Name TextField is invalid\n");
	    lastNameTextField.clear();
	}
	if (!validateEmail(emailTextField.getText())) {
	    errors.append("E Mail TextField is invalid\n");
	    emailTextField.clear();
	}

	if (!validatePassword(passwordField.getText(),
		passwordTextField.getText())) {
	    errors.append("Password is not as per criteria\n");
	    passwordField.clear();
	    passwordTextField.clear();
	}else if (!passwordField.getText().equals(passwordTextField.getText())) {
	    errors.append("Password does not match\n");
	    passwordField.clear();
	    passwordTextField.clear();
	}
	if (!validateDateOfBirth(date)) {
	    errors.append("Date of birth is invalid\n");
	}

	if (!errors.toString().isEmpty()) {
	    throw new RuntimeException(errors.toString());

	}
	if (validateUserName(userNameTextField.getText())) {
	    errors.append("User Name is already present\n");
	    throw new RuntimeException(errors.toString());
	}

	userDetail.setDateOfBirth(date);
	userDetail.setFirstName(firstNameTextField.getText().trim());
	userDetail.setLastName(lastNameTextField.getText().trim());
	userDetail.seteMailId(emailTextField.getText().trim());
	userDetail.setUser(new User(userNameTextField.getText().trim(),
		passwordField.getText()));
	userDetail.setGender(maleRadioButton.isSelected() ? Gender.MALE
		: Gender.FEMALE);
	return userDetail;
    }

    private boolean validateDateOfBirth(Date date) {
	// TO-DO check for 18 yrs age
	return true;
    }

    private boolean validatePassword(String text, String text2) {
	return Pattern.compile(PASSWORD_PATTERN).matcher(text).matches();
    }

    private boolean validateUserName(String text) {
	return loginService.getUserNameList().contains(text);
    }

    private boolean validateEmail(String text) {
	return Pattern.compile(EMAIL_PATTERN).matcher(text).matches();
    }

    private void temporaryFields() {
	firstNameTextField.setText("Mayank");
	lastNameTextField.setText("Saxena");
	emailTextField.setText("MayankSaxena001@gmail.com");
	passwordField.setText("#Mayank1#");
	passwordTextField.setText("#Mayank1#");
	userNameTextField.setText("Mayank001");
	maleRadioButton.setSelected(true);

    }
}
