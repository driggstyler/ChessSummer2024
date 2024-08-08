package services;

import dataaccess.dao.AuthtokenDAO;
import dataaccess.dao.UserDAO;
import models.Authtoken;
import models.User;
import requests.RegisterRequest;
import results.RegisterResult;
import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;

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
    public RegisterResult Execute(RegisterRequest registerRequest){
        RegisterResult registerResult = new RegisterResult();
        try (Connection conn = DatabaseManager.getConnection()) {
            AuthtokenDAO authtokenDAO = new AuthtokenDAO(conn);
            UserDAO userDAO = new UserDAO(conn);
            if (registerRequest.getUsername() == null ||
                    registerRequest.getPassword() == null) {
                registerResult.setSuccess(false);
                registerResult.setMessage("Error: Missing information to register.");
                return registerResult;
            }
            if (userDAO.Find(registerRequest.getUsername()) != null) {
                registerResult.setSuccess(false);
                registerResult.setMessage("Error: Username already taken.");
                return registerResult;
            }
            String authtoken = UUID.randomUUID().toString();
            authtokenDAO.Insert(new Authtoken(authtoken, registerRequest.getUsername()));
            userDAO.Insert(new User(registerRequest.getUsername(), registerRequest.getPassword(), registerRequest.getEmail()));
            registerResult.setUsername(registerRequest.getUsername());
            registerResult.setAuthtoken(authtoken);
            registerResult.setSuccess(true);
            registerResult.setMessage("Registered successfully.");
        } catch (DataAccessException | SQLException e) {
            e.printStackTrace();
            registerResult.setSuccess(false);
            registerResult.setMessage("Error in registering.");
        }
        return registerResult;
    }
}
