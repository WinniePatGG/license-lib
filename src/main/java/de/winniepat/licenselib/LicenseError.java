package de.winniepat.licenselib;

/**
 * Represents an error that occurred during the license check process, such as network issues or invalid responses from the server.
 * @param message A descriptive error message providing details about the failure.
 */
public record LicenseError(
        String message
) implements LicenseClient.LicenseResult {
}