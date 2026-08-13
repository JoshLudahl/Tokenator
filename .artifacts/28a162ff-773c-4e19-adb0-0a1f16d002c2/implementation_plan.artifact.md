# Replace Copy and Delete buttons with a "More" menu

This plan replaces the individual "Copy" and "Delete" buttons in the password list items with a single "More" (three-dot) button. Tapping this button will open a dropdown menu containing "Copy" and "Delete" options.

## Proposed Changes

### [app]

#### [MODIFY] [MainScreen.kt](file:///Users/joshy/Development/Android/Tokenator/app/src/main/java/com/token/tokenator/ui/main/MainScreen.kt)
- Update `VaultTokenItem` to use a `DropdownMenu`.
- Replace the two `IconButton`s for Copy and Delete with a single `IconButton` showing the `MoreVert` icon.
- Implement the `DropdownMenu` with "Copy" and "Delete" items.

#### [MODIFY] [SavedTokenScreen.kt](file:///Users/joshy/Development/Android/Tokenator/app/src/main/java/com/token/tokenator/ui/savedpassword/SavedTokenScreen.kt)
- Update `FinanceTokenItem` to use a `DropdownMenu`.
- Replace the "Copy" `IconButton` with a "More" menu.
- Include both "Copy" and "Delete" options (utilizing the existing `onDelete` parameter).

## Verification Plan

### Automated Tests
- I'll verify the build after changes.

### Manual Verification
- The user should verify the UI changes:
    1. Navigate to the main screen.
    2. Observe the "More" button (three dots) in each password item.
    3. Click the "More" button and verify that "Copy" and "Delete" options appear.
    4. Verify that "Copy" copies the password to the clipboard.
    5. Verify that "Delete" opens the confirmation dialog.
    6. Repeat for the "My Vault" screen (if applicable).
