package core.assets.processors;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import core.assets.AssetProcessor;

public class SQLiteProcessor implements AssetProcessor<Map<String, DSLContext>> {
	private static final Logger logger = LoggerFactory.getLogger(SQLiteProcessor.class);

	private final Map<String, DSLContext> databases = new HashMap<>();
	private final List<Path> collectedFiles = new ArrayList<>();

	@Override
	public boolean isSupportedFile(Path path) {
		return path.toString().toLowerCase().endsWith(".db");
	}

	@Override
	public void processFiles(List<Path> files) {
		collectedFiles.clear();
		collectedFiles.addAll(files);
		logger.info("Collected {} SQLite database file(s).", collectedFiles.size());

		process();
	}

	public void process() {
		for (Path file : collectedFiles) {
			try {
				String filename = file.getFileName().toString();
				String name = filename.substring(0, filename.lastIndexOf('.')).toLowerCase();
				String key = name;

				Connection connection = DriverManager.getConnection("jdbc:sqlite:" + file.toAbsolutePath());
				DSLContext context = DSL.using(connection, SQLDialect.SQLITE);
				databases.put(key, context);

				logger.info("Loaded SQLite DB: {} Key: {}", file.getFileName(), key);
			} catch (Exception e) {
				logger.error("Failed to load SQLite DB: {}", file, e);
			}
		}

		collectedFiles.clear();
	}

	public DSLContext getDB(String name) {
		return databases.get(name);
	}
}
