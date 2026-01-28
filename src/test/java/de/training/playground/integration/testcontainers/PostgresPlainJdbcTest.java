package de.training.playground.integration.testcontainers;

import io.qameta.allure.*;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * TestContainers mit Plain JDBC (ohne Spring).
 *
 * <p>Zeigt die Verwendung von TestContainers ohne Spring-Framework,
 * direkt mit JDBC-Verbindungen.
 *
 * <p>Demonstriert:
 * <ul>
 *   <li>{@code DriverManager.getConnection()} - manuelle Verbindung</li>
 *   <li>{@code Statement} und {@code PreparedStatement}</li>
 *   <li>DDL-Befehle (CREATE TABLE)</li>
 *   <li>DML-Befehle (INSERT, UPDATE, DELETE)</li>
 *   <li>Batch-Insert fuer Performance</li>
 *   <li>Transaktionen mit {@code commit()} und {@code rollback()}</li>
 * </ul>
 *
 * <p><b>Anwendungsfaelle:</b>
 * <ul>
 *   <li>Datenbank-Migrationen testen (Flyway, Liquibase)</li>
 *   <li>SQL-Skripte validieren</li>
 *   <li>Performance-Tests mit Batch-Operationen</li>
 *   <li>Tests ohne Spring-Overhead</li>
 * </ul>
 *
 * <p><b>Voraussetzung:</b> Docker muss laufen!
 *
 * @see java.sql.Connection
 * @see java.sql.PreparedStatement
 * @see PostgreSQLContainer
 */
@Epic("Integration Tests")
@Feature("testcontainers")
@Testcontainers
@DisplayName("TestContainers: Plain JDBC")
class PostgresPlainJdbcTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    private Connection connection;

    @BeforeEach
    void setUp() throws SQLException {
        connection = DriverManager.getConnection(
                postgres.getJdbcUrl(),
                postgres.getUsername(),
                postgres.getPassword()
        );

        // Tabelle erstellen
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS todo (
                    id SERIAL PRIMARY KEY,
                    title VARCHAR(255) NOT NULL,
                    description TEXT,
                    done BOOLEAN DEFAULT FALSE
                )
            """);
            stmt.execute("TRUNCATE TABLE todo");
        }
    }

    @AfterEach
    void tearDown() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    @Test
    @DisplayName("Verbindung zur Datenbank steht")
    void connectionWorks() throws SQLException {
        assertThat(connection.isValid(5)).isTrue();

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT version()")) {
            assertThat(rs.next()).isTrue();
            String version = rs.getString(1);
            assertThat(version).containsIgnoringCase("PostgreSQL");
        }
    }

    @Test
    @DisplayName("INSERT und SELECT funktionieren")
    void insertAndSelect() throws SQLException {
        // Insert
        try (PreparedStatement ps = connection.prepareStatement(
                "INSERT INTO todo (title, description) VALUES (?, ?)",
                Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, "Test Todo");
            ps.setString(2, "Beschreibung");
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                assertThat(keys.next()).isTrue();
                assertThat(keys.getLong(1)).isGreaterThan(0);
            }
        }

        // Select
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM todo WHERE title = 'Test Todo'")) {
            assertThat(rs.next()).isTrue();
            assertThat(rs.getString("title")).isEqualTo("Test Todo");
            assertThat(rs.getBoolean("done")).isFalse();
        }
    }

    @Test
    @DisplayName("Batch-Insert fuer viele Datensaetze")
    void batchInsert() throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement(
                "INSERT INTO todo (title) VALUES (?)")) {

            for (int i = 1; i <= 100; i++) {
                ps.setString(1, "Todo " + i);
                ps.addBatch();
            }
            int[] results = ps.executeBatch();

            assertThat(results).hasSize(100);
        }

        // Verifizieren
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM todo")) {
            rs.next();
            assertThat(rs.getInt(1)).isEqualTo(100);
        }
    }

    @Test
    @DisplayName("UPDATE und Transaktionen")
    void updateWithTransaction() throws SQLException {
        connection.setAutoCommit(false);

        try {
            // Insert
            try (PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO todo (title, done) VALUES (?, ?)")) {
                ps.setString(1, "Transaktion Test");
                ps.setBoolean(2, false);
                ps.executeUpdate();
            }

            // Update
            try (PreparedStatement ps = connection.prepareStatement(
                    "UPDATE todo SET done = true WHERE title = ?")) {
                ps.setString(1, "Transaktion Test");
                int updated = ps.executeUpdate();
                assertThat(updated).isEqualTo(1);
            }

            connection.commit();
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(true);
        }

        // Verifizieren
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(
                     "SELECT done FROM todo WHERE title = 'Transaktion Test'")) {
            assertThat(rs.next()).isTrue();
            assertThat(rs.getBoolean("done")).isTrue();
        }
    }

    @Test
    @DisplayName("DELETE mit Bedingung")
    void deleteWithCondition() throws SQLException {
        // Setup: Drei Todos einfuegen
        try (PreparedStatement ps = connection.prepareStatement(
                "INSERT INTO todo (title, done) VALUES (?, ?)")) {
            ps.setString(1, "Offen");
            ps.setBoolean(2, false);
            ps.executeUpdate();

            ps.setString(1, "Erledigt 1");
            ps.setBoolean(2, true);
            ps.executeUpdate();

            ps.setString(1, "Erledigt 2");
            ps.setBoolean(2, true);
            ps.executeUpdate();
        }

        // Alle erledigten loeschen
        try (Statement stmt = connection.createStatement()) {
            int deleted = stmt.executeUpdate("DELETE FROM todo WHERE done = true");
            assertThat(deleted).isEqualTo(2);
        }

        // Nur offene uebrig
        List<String> remaining = new ArrayList<>();
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT title FROM todo")) {
            while (rs.next()) {
                remaining.add(rs.getString("title"));
            }
        }
        assertThat(remaining).containsExactly("Offen");
    }
}
