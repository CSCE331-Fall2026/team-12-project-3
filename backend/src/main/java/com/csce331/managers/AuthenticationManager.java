package com.csce331.managers;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.Optional;

import com.csce331.common.DatabaseConnector;
import com.csce331.common.ExceptionLogger;
import com.csce331.models.UserRole;

/**
 * Manages user accounts: looking up a user's role and registering new accounts.
 *
 * <p>Each method reads from or writes to the database and returns the result.
 * A failed query is logged and rethrown.
 */
public class AuthenticationManager extends DatabaseConnector {
    /**
     * Creates a manager connected to the user database.
     */
    public AuthenticationManager() {
        super();
    }
    
    /**
     * Returns the role assigned to the user with the given email address.
     *
     * @param email the email address to look up
     * @return the user's role, or an empty optional if no user has that email
     * @throws SQLException if the database query fails
     */
    public Optional<UserRole> getUserRoleByEmail(String email) throws SQLException {
        String query = "SELECT role FROM accounts WHERE email = ? LIMIT 1";

        try (PreparedStatement emailStatement =
                this.getConnection().prepareStatement(query)) {
            emailStatement.setString(1, email);

            try (ResultSet resultSet = emailStatement.executeQuery()) {
                if (resultSet.next()) {
                    String roleString = resultSet.getString("role");
                    return Optional.of(UserRole.fromString(roleString));
                } else {
                    return Optional.empty();
                }
            }
        } catch (SQLException e) {
            ExceptionLogger.logException(e);
            throw e;
        }
    }

    /**
     * Returns the role assigned to the user with the given Google ID.
     *
     * @param googleId the Google ID to look up
     * @return the user's role, or an empty optional if no user has that ID
     * @throws SQLException if the database query fails
     */
    public Optional<UserRole> getUserRoleById(String googleId) throws SQLException {
        String query = "SELECT role FROM accounts WHERE id = ?::integer LIMIT 1";

        try (PreparedStatement idStatement =
                this.getConnection().prepareStatement(query)) {
            idStatement.setString(1, googleId);

            try (ResultSet resultSet = idStatement.executeQuery()) {
                if (resultSet.next()) {
                    String roleString = resultSet.getString("role");
                    return Optional.of(UserRole.fromString(roleString));
                } else {
                    return Optional.empty();
                }
            }
        } catch (SQLException e) {
            ExceptionLogger.logException(e);
            throw e;
        }
    }

    /**
     * Registers a new user with the given email address, role, and Google ID.
     * The user is added to the database.
     *
     * @param email the user's email address
     * @param role the role to assign the user
     * @param googleId the user's Google ID
     * @throws SQLException if the database update fails
     */
    public void createUser(String email, UserRole role, String googleId) throws SQLException {
        String query = "INSERT INTO accounts (email, role, google_id) VALUES " +
            "(?, ?::user_role, ?)";

        try (PreparedStatement createUserStatement =
                this.getConnection().prepareStatement(query)) {
            createUserStatement.setString(1, email);
            createUserStatement.setString(2, role.toString());
            createUserStatement.setString(3, googleId);
            createUserStatement.executeUpdate();
        } catch (SQLException e) {
            ExceptionLogger.logException(e);
            throw e;
        }
    }
}
