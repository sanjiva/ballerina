package org.wso2.ballerinalang.util;

import org.ballerinalang.compiler.BLangCompilerException;
import org.wso2.ballerinalang.compiler.util.ProjectDirConstants;

import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Home repository util methods.
 */
public class HomeRepoUtils {
    public static final String USER_HOME = "user.home";

    /**
     * Create and get the home repository path.
     *
     * @return home repository path
     */
    public static Path createAndGetHomeReposPath() {
        Path homeRepoPath;
        String homeRepoDir = System.getenv(ProjectDirConstants.HOME_REPO_ENV_KEY);
        if (homeRepoDir == null || homeRepoDir.isEmpty()) {
            String userHomeDir = System.getProperty(USER_HOME);
            if (userHomeDir == null || userHomeDir.isEmpty()) {
                throw new BLangCompilerException("Error creating home repository: unable to get user home directory");
            }
            homeRepoPath = Paths.get(userHomeDir, ProjectDirConstants.HOME_REPO_DEFAULT_DIRNAME);
        } else {
            // User has specified the home repo path with env variable.
            homeRepoPath = Paths.get(homeRepoDir);
        }

        homeRepoPath = homeRepoPath.toAbsolutePath();
        if (Files.exists(homeRepoPath) && !Files.isDirectory(homeRepoPath, LinkOption.NOFOLLOW_LINKS)) {
            throw new BLangCompilerException("Home repository is not a directory: " + homeRepoPath.toString());
        }
        return homeRepoPath;
    }
}
