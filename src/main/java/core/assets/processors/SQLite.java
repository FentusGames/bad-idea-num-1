package core.assets.processors;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SQLite {
	private static final Logger logger = LoggerFactory.getLogger(SQLite.class);

	private final Map<String, DSLContext> databases = new HashMap<>();

	public void loadFrom(Path folderPath) {
		if (!Files.exists(folderPath)) {
			logger.warn("SQLite folder does not exist: {}", folderPath.toAbsolutePath());
			return;
		}

		try (Stream<Path> stream = Files.walk(folderPath)) {
			var dbFiles = stream.filter(Files::isRegularFile).filter(path -> path.toString().toLowerCase().endsWith(".db")).collect(Collectors.toList());

			for (Path file : dbFiles) {
				try {
					String filename = file.getFileName().toString();
					String name = filename.substring(0, filename.lastIndexOf('.')).toLowerCase();

					Connection connection = DriverManager.getConnection("jdbc:sqlite:" + file.toAbsolutePath());
					DSLContext context = DSL.using(connection, SQLDialect.SQLITE);
					databases.put(name, context);

					logger.info("Loaded SQLite DB: {} Key: {}", file.getFileName(), name);
				} catch (Exception e) {
					logger.error("Failed to load SQLite DB: {}", file, e);
				}
			}
		} catch (Exception e) {
			logger.error("Failed to walk SQLite folder: {}", folderPath, e);
		}
	}

	public DSLContext getDB(String name) {
		return databases.get(name);
	}
}
