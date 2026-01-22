# Design Recommendations

This document tracks specific, actionable design recommendations to take the password generator from "functional" to "polished."

## Active Recommendations

### 1. Typography
* The font is monospace, which is good, but it looks a bit thin. Use a robust, easier-to-read font like **JetBrains Mono**, **Fira Code**, or **Roboto Mono**.

### 2. Color & Polish
* **Background:** instead of pure white, try a very light off-white (#F5F5F7) for the background, and keep the content areas pure white.

---

## Completed

- [x] **Hero - Card Container:** Converted password display into an `ElevatedCard`.
- [x] **Hero - Vertical Alignment:** Centered password text vertically within its container.
- [x] **Hero - Integrated Actions:** Moved "Copy", "New" (Generate), and "Reset" (Clear) into the password card as icon buttons.
- [x] **Recent List - Cards:** Wrapped each history item in its own card.
- [x] **Recent List - Typography:** Used monospace font for history entries for scanability.
- [x] **Recent List - Spacing:** Added internal padding to history cards.
- [x] **Tooltips:** Added long-press help text to all action buttons in cards.
- [x] **Feedback:** Ensure there is a "Toast" notification when copying to clipboard.
- [x] **Indicators - Character Types:** Replaced legacy stats block with horizontal indicator pills ("a A 2 @") inside the password card.
- [x] **Indicators - Interaction:** Added long-press tooltips to indicator pills to show precise character counts.
- [x] **Color - Fresh Palette:** Introduced "MintHighlight" teal/mint color for active indicator states.
- [x] **Controls - Slider Label:** Moved length number to a "Length: X" label directly above the slider.
- [x] **Controls - Slider Style:** Removed dotted/tick marks for a solid, continuous track.
- [x] **Controls - Toggle Switch:** Replaced the "Avoid Ambiguous" checkbox with a mint-colored Switch.
- [x] **Controls - Touch Targets:** Made the "Avoid Ambiguous" text label a touch target for toggling the setting.

---

## Won't Do / Obsolete

- [ ] **Hero - Large Font:** "let's hold off on this for now, as the text is already large and I'd need to measure multiple devices" (User preference).
- [ ] **Action Buttons - Big "GENERATE NEW" Button:** Replaced with integrated icon buttons in the hero card.
- [ ] **Action Buttons - "Outlined" style Copy button:** Integrated as icon button instead.
- [ ] **Indicator - Statistics Block:** Replaced with condensed indicator pills inside the hero card.
