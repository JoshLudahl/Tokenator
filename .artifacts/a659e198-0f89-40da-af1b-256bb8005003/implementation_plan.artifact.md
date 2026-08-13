# Mask Password when Copying from Main Screen

Ensure that when a password is copied from the menu option on the main screen, it is masked in the clipboard overlay, consistent with other parts of the app.

## Proposed Changes

### UI Components

#### [MODIFY] [MainScreen.kt](file:///Users/joshy/Development/Android/Tokenator/app/src/main/java/com/token/tokenator/ui/main/MainScreen.kt)

Update the `onCopy` lambda passed to `VaultTokenItem` in both the search results and the main list to use `isSensitive = true` when calling `Clipuous.copyToClipboard`.

## Verification Plan

### Manual Verification
1. Run the app on a device running Android 13 or higher.
2. Go to the main screen.
3. Find a saved password item and open its menu.
4. Tap "Copy Password".
5. Verify that the clipboard overlay shows the password as masked (dots) rather than plain text.
6. Verify that "Copy Username" still shows the username as plain text (as it is not sensitive).
7. Repeat the same for the search results.
