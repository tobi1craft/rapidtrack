# RapidTrack

A simple racing game implemented in java using libgdx.

## Platforms

- `core`: Main module with the application logic shared by all platforms.
- `lwjgl3`: Primary desktop platform using LWJGL3; was called 'desktop' in older docs.
- `teavm`: Experimental web platform using TeaVM and WebGL. **(Currently broken)**
- `server`: A separate application without access to the `core` module. **(Not yet implemented)**
- `shared`: A common module shared by `core` and `server` platforms. **(Not yet implemented)**

## Gradle

This project uses [Gradle](https://gradle.org/) to manage dependencies.
Useful Gradle tasks and flags:

- `lwjgl3:jar`: builds application's runnable jar, which can be found at `lwjgl3/build/libs`.
- `lwjgl3:run`: starts the application.
- `lwjgl3:packageLinuxX64`: packages the application into a native Linux executable.
- `lwjgl3:packageWindowsX64`: packages the application into a native Windows executable.
- `server:run`: runs the server application.
- `teavm:build`: builds the JavaScript application into the build/dist/webapp folder.
- `teavm:run`: serves the JavaScript application at http://localhost:8080 via a local Jetty server.

### Notices

A [libGDX](https://libgdx.com/) project generated with [gdx-liftoff](https://github.com/libgdx/gdx-liftoff).
