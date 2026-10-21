package regression;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.json.simple.JSONArray;
import org.json.simple.JSONValue;

/** Read-only deployment audit for maps, skills and feature item data. */
public final class DatabaseFeatureAudit {
    private static int checks;

    public static void main(String[] args) throws Exception {
        Map<String, String> config = readConfig(Path.of("hso.conf"));
        String host = config.getOrDefault("mysql-host", "127.0.0.1");
        String database = config.getOrDefault("mysql-database", "hso2");
        String user = config.getOrDefault("mysql-user", "root");
        String password = config.getOrDefault("mysql-password", "");
        auditSkillPackets();
        Class.forName("com.mysql.cj.jdbc.Driver");
        try (Connection connection = DriverManager.getConnection(
                "jdbc:mysql://" + host + ":3306/" + database
                        + "?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Bangkok",
                user, password)) {
            auditDatabase(connection);
        }
        System.out.println("PASS - " + checks + " deployment audit checks");
    }

    private static void auditSkillPackets() throws Exception {
        String[] classes = {"chienbinh", "satthu", "phapsu", "xathu"};
        for (String clazz : classes) {
            Path packet = Path.of("data", "msg", "msg_29_" + clazz);
            check(Files.isRegularFile(packet), "missing skill packet " + clazz);
            try (DataInputStream input = new DataInputStream(new FileInputStream(packet.toFile()))) {
                check(input.readUnsignedByte() == 21, clazz + " must expose 21 skills");
            }
        }
    }

    private static void auditDatabase(Connection connection) throws Exception {
        Set<Integer> mapIds = new HashSet<>();
        Map<Integer, String> vgos = new HashMap<>();
        try (Statement statement = connection.createStatement();
             ResultSet result = statement.executeQuery("SELECT id, vgos, maxzone FROM maps")) {
            while (result.next()) {
                int id = result.getInt("id");
                mapIds.add(id);
                vgos.put(id, result.getString("vgos"));
                check(result.getInt("maxzone") > 0, "map " + id + " has no zones");
                check(Files.isRegularFile(Path.of("data", "mapnew", Integer.toString(id))),
                        "missing map resource " + id);
            }
        }
        System.out.println("INFO - playable maps in database: " + mapIds.size());
        check(mapIds.size() >= 90, "expected at least 90 playable maps");
        for (Map.Entry<Integer, String> entry : vgos.entrySet()) {
            Object parsed = JSONValue.parse(entry.getValue());
            check(parsed instanceof JSONArray, "invalid vgo JSON in map " + entry.getKey());
            if (!(parsed instanceof JSONArray)) continue;
            for (Object value : (JSONArray) parsed) {
                if (!(value instanceof JSONArray) || ((JSONArray) value).isEmpty()) continue;
                int destination = ((Number) ((JSONArray) value).get(0)).intValue();
                check(mapIds.contains(destination), "map " + entry.getKey() + " links to missing map " + destination);
            }
        }
        for (int id = 53; id <= 61; id++) check(mapIds.contains(id), "missing battleground map " + id);
        check(count(connection, "SELECT COUNT(*) FROM level") >= 200, "level table incomplete");
        check(count(connection, "SELECT COUNT(*) FROM item3 WHERE type IN (14,15)") > 0,
                "missing pet/mount equipment");
        check(count(connection, "SELECT COUNT(*) FROM item3 WHERE type=16") >= 4,
                "missing medal equipment");
        check(count(connection, "SELECT COUNT(*) FROM item3 WHERE type BETWEEN 21 AND 27") > 0,
                "missing fashion equipment");
        check(count(connection, "SELECT COUNT(*) FROM item7 WHERE id BETWEEN 46 AND 145") >= 80,
                "medal material set incomplete");
    }

    private static long count(Connection connection, String query) throws Exception {
        try (Statement statement = connection.createStatement(); ResultSet result = statement.executeQuery(query)) {
            result.next();
            return result.getLong(1);
        }
    }

    private static Map<String, String> readConfig(Path path) throws Exception {
        Map<String, String> result = new HashMap<>();
        for (String line : Files.readAllLines(path)) {
            int separator = line.indexOf(':');
            if (separator > 0) result.put(line.substring(0, separator).trim(), line.substring(separator + 1).trim());
        }
        return result;
    }

    private static void check(boolean condition, String message) {
        checks++;
        if (!condition) throw new AssertionError(message);
    }
}
