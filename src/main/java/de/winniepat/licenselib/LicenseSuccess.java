package de.winniepat.licenselib;

/**
 * Represents a successful license check result, containing all relevant information about the license status, plugin, customer, and expiration details.
 * @param valid Indicates whether the license is valid or not.
 * @param status A string representing the status of the license, such as "valid", "invalid", "expired", etc.
 * @param message A message providing additional information about the license status, which can be used for logging or debugging purposes.
 * @param plugin The name of the plugin associated with the license, which can be used to identify which plugin the license check was performed for.
 * @param customer The name of the customer or organization that owns the license, which can be useful for tracking and support purposes.
 * @param expiresAt The expiration date of the license in ISO 8601 format (e.g., "2024-12-31T23:59:59Z"), which indicates when the license will no longer be valid.
 * @param checkedAt The date and time when the license check was performed, also in ISO 8601 format, which can be used to determine how recent the license information is.
 */
public record LicenseSuccess(
        boolean valid,
        String status,
        String message,
        String plugin,
        String customer,
        String expiresAt,
        String checkedAt
) implements LicenseClient.LicenseResult {
}