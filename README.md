# LicenseLib by WinniePatGG
## This library is built for the [license system by Pietriss](https://github.com/Pietriss/pietlizenz)

### Installation
1. Add the repository to your project:
```gradle
repositories {
    maven { url = "https://maven.winniepat.de/repository/maven-public/" }
}
```
2. Add the dependency:
```gradle
dependencies {
    implementation 'de.winniepat:license-lib:+' (or specify a version instead of +)
}
```

### Usage (Sync)

You can work with syncResult.isValid() to check if the license is valid and handle it from there
```java
import de.winniepat.licenselib.LicenseClient;
import de.winniepat.licenselib.LicenseSuccess;

public class main {
    public static void main(String[] args) {
        LicenseClient.LicenseResult result = LicenseClient.check(
                apiUrl,
                plugin,
                licenseKey,
                serverId //can be null
        );
        if (result instanceof LicenceSuccess success) {
            if(success.isValid()) {
                System.out.println("Valid: " + success);
            } else {
                System.out.println("Invalid: " + success);
            }
        }
    }
}
```

### Usage (Async)

Here you can also work with result.isValid() and handle it from there
```java
import de.winniepat.licenselib.LicenseClient;
import de.winniepat.licenselib.LicenseSuccess;

import java.util.concurrent.CompletableFuture;

public class main {
    public static void main(String[] args) {
        CompletableFuture<LicenseClient.LicenseResult> resultAsync = LicenseClient.checkAsync(
                apiUrl,
                plugin,
                licenseKey,
                serverId
        );
        if (result instanceof LicenseSuccess success) {
            if (success.valid()) {
                System.out.println("Valid: " + success);
            } else {
                System.out.println("Invalid " + success);
            }
        }
    }
}
```