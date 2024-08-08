package services;

import dataaccess.dao.AuthtokenDAO;
import dataaccess.dao.UserDAO;
import models.Authtoken;
import models.User;
import requests.RegisterRequest;
import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import results.LoginResult;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.UUID;

/**
 * A service to handle the logic for the register operation.
 */
public class RegisterService {
    /**
     * Register a new user into the database.
     * @param registerRequest Contains the information needed to register the new user.
     * @return A RegisterResult object containing the results of the register operation.
     */
    public LoginResult execute(RegisterRequest registerRequest){
        LoginResult loginResult = new LoginResult();
        try (Connection conn = DatabaseManager.getConnection()) {
            AuthtokenDAO authtokenDAO = new AuthtokenDAO(conn);
            UserDAO userDAO = new UserDAO(conn);
            if (registerRequest.getUsername() == null ||
                    registerRequest.getPassword() == null) {
                loginResult.setSuccess(false);
                loginResult.setMessage("Error: Missing information to register.");
                return loginResult;
            }
            if (userDAO.find(registerRequest.getUsername()) != null) {
                loginResult.setSuccess(false);
                loginResult.setMessage("Error: Username already taken.");
                return loginResult;
            }
            String authtoken = UUID.randomUUID().toString();
            authtokenDAO.insert(new Authtoken(authtoken, registerRequest.getUsername()));
            userDAO.insert(new User(registerRequest.getUsername(), registerRequest.getPassword(), registerRequest.getEmail()));
            loginResult.setUsername(registerRequest.getUsername());
            loginResult.setAuthtoken(authtoken);
            loginResult.setSuccess(true);
            loginResult.setMessage("Registered successfully.");
        } catch (DataAccessException | SQLException e) {
            e.printStackTrace();
            loginResult.setSuccess(false);
            loginResult.setMessage("Error in registering.");
        }
        return loginResult;
    }
}
