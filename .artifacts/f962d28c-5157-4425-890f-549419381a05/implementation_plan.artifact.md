# App UI Refresh - Modern Finance Inspiration

Apply the design language from the provided inspiration image (personal finance app) to Tokenator. This includes a clean light theme, card-based layout, bold typography, and a specific color palette (dark grays, soft blues, and accent yellow).

## Proposed Changes

### [Theme & Styling]

#### [MODIFY] [Color.kt](file:///Users/joshy/Development/Android/Tokenator/app/src/main/java/com/token/tokenator/ui/theme/Color.kt)
- Update the palette to match the inspiration image:
    - Background: `#F5F7FA`
    - Dark Surface: `#2D343C` (for premium cards)
    - Primary Blue: `#96C1D8`
    - Accent Yellow: `#FFC107`
    - Soft Success: `#D8E9E4`
    - Soft Error: `#EBE4EB`
- Define full light and dark schemes.

#### [MODIFY] [TokenatorTheme.kt](file:///Users/joshy/Development/Android/Tokenator/app/src/main/java/com/token/tokenator/ui/theme/TokenatorTheme.kt)
- Wire up the new color scheme.
- Ensure `TokenatorShapes` uses generous corner radii (24dp - 32dp) as seen in the cards.

### [UI Screens]

#### [MODIFY] [MainScreen.kt](file:///Users/joshy/Development/Android/Tokenator/app/src/main/java/com/token/tokenator/ui/main/MainScreen.kt)
- **Top Bar**: Update to a cleaner header style with a grid icon (Dashboard-like).
- **Generated Token Card**: Use the dark `ColorSurfaceDark` with white text to make it look like the "Available balance" card.
- **Section Headers**: Use the bold, gray styling from the image.
- **Settings & Controls**: Wrap switches and sliders in white cards with subtle shadows/elevation.
- **Generate Button**: Style it as a prominent secondary action, possibly using the yellow accent.

#### [MODIFY] [SavedTokenScreen.kt](file:///Users/joshy/Development/Android/Tokenator/app/src/main/java/com/token/tokenator/ui/savedpassword/SavedTokenScreen.kt)
- **Token Items**: Refactor `TokenItem` to look like the transaction list in the inspiration image (rounded cards, soft-colored icons).
- **Header**: Use the "My Spending" header style.

## Verification Plan

### Automated Tests
- Run existing tests to ensure no regressions in functionality (token generation, saving).
- `gradlew test`
- `gradlew connectedCheck`

### Manual Verification
- Deploy to device/emulator.
- Verify light and dark mode consistency.
- Check accessibility (contrast for the new colors).
- Verify the "premium" card look for the generated token.
