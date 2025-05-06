
# Association in Object-Oriented Programming

Association is a fundamental relationship between objects in object-oriented programming. It represents how objects interact with each other, where each object maintains its own identity and lifecycle.

## Two Types of Association

The two main types of association in OOP are:

1.  **Aggregation** - A "has-a" relationship where:
    -   One object contains references to other objects
    -   The contained objects can exist independently of the container
    -   It's a "weak" relationship where objects have separate lifecycles
    -   Example: A university has departments, but departments could exist even if the university closes
2.  **Composition** - A "part-of" relationship where:
    -   One object not only contains other objects but is responsible for their lifecycle
    -   The contained objects cannot meaningfully exist without the container
    -   It's a "strong" relationship where the lifecycle of contained objects is tied to the container
    -   Example: A house has rooms, and the rooms cannot exist without the house
    
**SOLID principles**: Extensions help maintain:

-   Single Responsibility Principle: Keep core class focused while adding context-specific behavior via extensions
-   Open/Closed Principle: Extend functionality without modifying existing code
-   Less boilerplate compared to adapter patterns or inheritance approaches


# How to secure Android App?

## Data Storage & Encryption

-   Use Android Keystore for secure key management
-   Implement encryption for sensitive data (AES, RSA)
-   Avoid storing sensitive data in SharedPreferences unencrypted
-   Use encrypted databases like SQLCipher when necessary
-   Apply file-level encryption for local storage

## Network Security

-   Enforce HTTPS for all network communications
-   Implement certificate pinning to prevent MITM attacks
-   Use proper authentication mechanisms (OAuth 2.0, JWT)
-   Apply network security configuration to restrict cleartext traffic
-   Validate all server responses and implement proper error handling

## Code Security

-   Apply proper ProGuard/R8 rules for obfuscation
-   Implement root detection mechanisms
-   Use SafetyNet Attestation API to verify device integrity
-   Add tampering detection to identify modified apps
-   Remove debug logs and sensitive information from release builds

## Input Validation & Authorization

-   Validate all user input and sanitize data
-   Implement proper authorization checks for component access
-   Use parameterized queries to prevent SQL injection
-   Apply principle of least privilege for permissions
-   Use intent filters correctly to prevent unauthorized access

## Authentication

-   Implement biometric authentication when appropriate
-   Use secure authentication libraries
-   Apply proper session management techniques
-   Enforce strong password policies
-   Consider multi-factor authentication for sensitive operations

## Secure IPC & Component Access

-   Set proper export flags on components
-   Use explicit intents when possible
-   Implement signature-based permissions for custom IPC
-   Apply content provider permissions appropriately
-   Validate all incoming intents

## Third-Party Dependencies

-   Regularly update dependencies to address security vulnerabilities
-   Audit third-party libraries for security issues
-   Consider using dependency scanning tools
-   Verify the integrity of external SDKs