package util;

import java.nio.file.Path;
import java.nio.file.Paths;

public class FixPath {

	public static Path getAbsolutePath(Path path) {
		// User executed form commands/ directory, but working directory is ../ from there.
		// If specified a relative directory, need to base it correctly from user perspective.
		if(!path.isAbsolute()) {
			path = Paths.get("commands/").toAbsolutePath().resolve(path);
		}
		return path;
	}
	
}
