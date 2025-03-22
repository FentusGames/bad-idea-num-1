package core.assets;

import java.nio.file.Path;
import java.util.List;

public interface AssetProcessor<T> {
	boolean isSupportedFile(Path path);

	void processFiles(List<Path> files);
}
