package launchers;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import org.jooq.codegen.GenerationTool;
import org.jooq.meta.jaxb.Configuration;

public class JooqGenTool {
	public static void main(String[] args) {
		try {
			// Load the XML configuration file
			File configFile = new File("src/main/resources/jooq-config.xml");

			if (!configFile.exists()) {
				throw new RuntimeException("Configuration file NOT FOUND at: " + configFile.getAbsolutePath());
			}

			// Print XML content to debug potential encoding issues
			String xmlContent = Files.readString(configFile.toPath(), StandardCharsets.UTF_8);
			System.out.println("jooq-config.xml FOUND. Content:\n" + xmlContent);

			System.out.println("Starting JOOQ Code Generation...");

			JAXBContext jaxbContext = JAXBContext.newInstance(Configuration.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			Configuration configuration = (Configuration) jaxbUnmarshaller.unmarshal(configFile);

			// Print the parsed Configuration object (debugging)
			System.out.println("Parsed Configuration Object: " + configuration);

			// Execute JOOQ Code Generation
			GenerationTool.generate(configuration);

			System.out.println("JOOQ Code Generation Completed Successfully!");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
