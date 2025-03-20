package de.tobi1craft.rapidtrack.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class AssetsHelper {

    private static final String ASSETS_FILE_PATH = "assets.txt";
    private static List<String> assets;

    /**
     * Reads the assets.txt file and returns a list of asset entries.
     *
     * @return A list of asset file paths as read from the file.
     */
    public static List<String> readAssetsFile() {
        if(assets != null && !assets.isEmpty()) return assets;
        FileHandle file = Gdx.files.internal(ASSETS_FILE_PATH);
        String content = file.readString();
        String[] lines = content.split("\\r?\\n");
        List<String> assets = new ArrayList<>();
        for (String line : lines) {
            if (!line.trim().isEmpty()) {
                assets.add(line.trim());
            }
        }
        AssetsHelper.assets = assets;
        return assets;
    }

    /**
     * Returns a list of asset entries that belong to the specified directory.
     * If the directory is an empty string or null, all asset entries are returned.
     * Paths are normalized by converting backslashes to forward slashes.
     *
     * @param directory The directory to filter by (e.g., "music/main").
     *                  Pass an empty string ("") or null to return all files.
     * @return A list of asset file paths within the specified directory.
     */
    public static List<String> getFilesInDirectory(String directory) {
        List<String> assets = readAssetsFile();
        if (directory == null || directory.isEmpty()) {
            return assets;
        }

        List<String> filteredFiles = new ArrayList<>();
        // Normalize the directory path to use forward slashes and ensure it starts without ends with a slash.
        String normalizedDir = directory.replace("\\", "/");
        if (normalizedDir.startsWith("/")) {
            normalizedDir = normalizedDir.substring(1);
        }
        if (!normalizedDir.endsWith("/")) {
            normalizedDir += "/";
        }

        for (String asset : assets) {
            String normalizedAsset = asset.replace("\\", "/");
            if (normalizedAsset.startsWith(normalizedDir)) {
                filteredFiles.add(normalizedAsset);
            }
        }
        return filteredFiles;
    }

    /**
     * Returns a list of asset entries that belong to the specified directory and pass the provided regex filters.
     *
     * @param directory     The directory to filter by (e.g., "music/main"). Use an empty string ("") to return all files.
     * @param includeFilter A regex pattern to include files (pass an empty string or null to include all files).
     * @param excludeFilter A regex pattern to exclude files (pass an empty string or null to exclude nothing).
     * @return A list of asset file paths within the specified directory that satisfy the filters.
     */
    public static List<String> getFilesInDirectory(String directory, String includeFilter, String excludeFilter) {
        List<String> files = getFilesInDirectory(directory);

        if (includeFilter != null && !includeFilter.isEmpty()) {
            Pattern includePattern = Pattern.compile(includeFilter);
            files.removeIf(file -> !includePattern.matcher(file).find());
        }

        if (excludeFilter != null && !excludeFilter.isEmpty()) {
            Pattern excludePattern = Pattern.compile(excludeFilter);
            files.removeIf(file -> excludePattern.matcher(file).find());
        }

        return files;
    }
}
