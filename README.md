# License Lib by WinniePatGG
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

LicenseClient.LicenseResult syncResult = LicenseClient.check(apiUrl, plugin, licenseKey, serverId);
```

### Usage (Async)

Here you can also work with result.isValid() and handle it from there
````java
import de.winniepat.licenselib.LicenseClient;

LicenseClient.checkAsync(apiUrl, plugin, licenseKey, serverId)
    .thenAccept(result -> {
        if (result.isValid()) {
            System.out.println("Async Valid: " + result);
        } else {
            System.out.println("Async Invalid: " + result);
        }
    });