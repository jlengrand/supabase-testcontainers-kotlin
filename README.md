# Supabase Test Container for Kotlin

This repo demonstrates the use of [Test Containers](https://testcontainers.com/) against a full local [Supabase](https://supabase.com/) instance

Supabase allows for self-hosting (see [here](https://supabase.com/docs/guides/self-hosting/docker)). We use that to run a local instance of Supabase for testing.


## Manual steps (for now) - do it once

* You need to have the Supabase repo available locally to play around
  
```bash 
$ git clone --depth 1 https://github.com/supabase/supabase src/test/resources/supabase
$ cp src/test/resources/supabase/docker/.env.example src/test/resources/supabase/docker/.env
```

_Note : I've added `src/test/resources/supabase` to the `.gitignore` file, so that it does not get pushed to the repo._ 

### Container names fun stuff

Interestingly, Test Containers don't support the `container_name` property in the `docker-compose.yml` file, [and don't seem to intend to either](https://github.com/testcontainers/testcontainers-java/pull/2741).
But it also doesn't get ignored meaning that if you run the tests now, you'll get the following error: 

```
java.lang.IllegalStateException: Compose file supabase-testcontainers-kotlin/src/test/resources/supabase/docker/docker-compose.yml has 'container_name' property set for service 'studio' but this property is not supported by Testcontainers, consider removing it
	at org.testcontainers.containers.ParsedDockerComposeFile.validateNoContainerNameSpecified(ParsedDockerComposeFile.java:113)
```

To avoid that, we'll remove the `container_name` properties from the `docker-compose.yml` file. Do note that it might have an impact if you want to run the containers manually though.

```bash
$ grep -v "container_name" docker/docker-compose.yml > tmpfile && mv tmpfile docker/docker-compose.yml
```

## Updating the Supabase repo to the latest version

Cloning the repo is great, but we also need to stay in think with sync with the latest version of Supabase. 
Once in a while, you'll need to update the repo.

```bash
$ cd src/test/resources/supabase
$ git pull
```

_Note : Remember, we modified `docker-compose.yml` to remove the `container_name` property. This might lead to conflict at times if the file gets modified_

## Running the tests

For now, we'll avoid the creation of database and more, and simply run against existing tables of the public database and see what's up.

Run the test in `MainTest.kt`.
Note that the rests are quite long to run (obviously, I'd say), and that the Supabase compose file is setup so that volumes are saved in between runs.

```
$ ./gradlew test                                                                                                                                                                ✔ ╱ 17:38:27  

Deprecated Gradle features were used in this build, making it incompatible with Gradle 9.0.

You can use '--warning-mode all' to show the individual deprecation warnings and determine if they come from your own scripts or plugins.

For more on this, please refer to https://docs.gradle.org/8.2/userguide/command_line_interface.html#sec:command_line_warnings in the Gradle documentation.

BUILD SUCCESSFUL in 53s
4 actionable tasks: 3 executed, 1 up-to-date

```

## Author

* Julien Lengrand-Lambert (@jlengrand)