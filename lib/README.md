# Custom Dependencies (`lib/`)

This project depends on several JARs that are **not available on Maven Central**.
They must be installed to your local Maven repository before building.

## Required JARs

| File Name (place in this folder)              | GroupId      | ArtifactId    | Version |
|-----------------------------------------------|--------------|---------------|---------|
| `com.custom--pickerfx--1.2.0.jar`             | com.custom   | pickerfx      | 1.2.0   |
| `com.custom--tree-showing--0.2.2.jar`         | com.custom   | tree-showing  | 0.2.2   |
| `com.custom--unitfx--1.0.10.jar`              | com.custom   | unitfx        | 1.0.10  |
| `com.custom--jmemorybuddy--0.5.1.jar`         | com.custom   | jmemorybuddy  | 0.5.1   |
| `com.custom--dpuareu--1.0.0.jar`              | com.custom   | dpuareu       | 1.0.0   |

## How to use

### Option A — Automatic (recommended)

1. Place the JAR files in this `lib/` folder using the naming convention above:
   `groupId--artifactId--version.jar`

2. Run the install script:

   **Windows (PowerShell):**
   ```powershell
   .\lib\install-libs.ps1
   ```

   The GitHub Actions CI pipeline also uses this convention automatically.

### Option B — Manual

Install each JAR individually:

```bash
mvn install:install-file -Dfile=lib/com.custom--pickerfx--1.2.0.jar -DgroupId=com.custom -DartifactId=pickerfx -Dversion=1.2.0 -Dpackaging=jar
mvn install:install-file -Dfile=lib/com.custom--tree-showing--0.2.2.jar -DgroupId=com.custom -DartifactId=tree-showing -Dversion=0.2.2 -Dpackaging=jar
mvn install:install-file -Dfile=lib/com.custom--unitfx--1.0.10.jar -DgroupId=com.custom -DartifactId=unitfx -Dversion=1.0.10 -Dpackaging=jar
mvn install:install-file -Dfile=lib/com.custom--jmemorybuddy--0.5.1.jar -DgroupId=com.custom -DartifactId=jmemorybuddy -Dversion=0.5.1 -Dpackaging=jar
mvn install:install-file -Dfile=lib/com.custom--dpuareu--1.0.0.jar -DgroupId=com.custom -DartifactId=dpuareu -Dversion=1.0.0 -Dpackaging=jar
```

## Note

These JARs are **not committed to the repository** due to licensing. Obtain them from the original sources or contact the project maintainer.
