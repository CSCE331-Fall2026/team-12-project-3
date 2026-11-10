package com.csce331.authentication;

import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.Optional;

import com.csce331.common.DatabaseConnector;
import com.csce331.common.ExceptionLogger;
import com.csce331.models.UserRole;

public class AuthenticationManager extends DatabaseConnector {
    public AuthenticationManager() {
        super();
    }
    
    public Optional<UserRole> getUserRoleByEmail(String email) throws SQLException {
        try {
            Statement emailStatement = this.createStatement();
            String query = "SELECT role FROM users WHERE email = '" + email + "' LIMIT 1";
            ResultSet resultSet = emailStatement.executeQuery(query);
            if (resultSet.next()) {
                String roleString = resultSet.getString("role");
                return Optional.of(UserRole.fromString(roleString));
            } else {
                return Optional.empty();
            }
        } catch (SQLException e) {
            ExceptionLogger.logException(e);
            throw e;
        }
    }

    public Optional<UserRole> getUserRoleById(String googleId) throws SQLException {
        try {
            Statement idStatement = this.createStatement();
            String query = "SELECT role FROM users WHERE id = " + googleId + " LIMIT 1";
            ResultSet resultSet = idStatement.executeQuery(query);
            if (resultSet.next()) {
                String roleString = resultSet.getString("role");
                return Optional.of(UserRole.fromString(roleString));
            } else {
                return Optional.empty();
            }
        } catch (SQLException e) {
            ExceptionLogger.logException(e);
            throw e;
        }
    }

    public void createUser(String email, UserRole role, String googleId) throws SQLException {
        try {
            Statement createStatement = this.createStatement();
            String query = "INSERT INTO users (email, role, google_id) VALUES " +
                "('" + email + "', '" + role.toString() + "', '" + googleId + "')";
            createStatement.executeUpdate(query);
        } catch (SQLException e) {
            ExceptionLogger.logException(e);
            throw e;
        }
    }
}
