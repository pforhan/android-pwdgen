# Design Recommendations

This document tracks specific, actionable design recommendations to take the password generator from "functional" to "polished."

## Active Recommendations

### 1. Typography
* The font is monospace, which is good, but it looks a bit thin. Use a robust, easier-to-read font like **JetBrains Mono**, **Fira Code**, or **Roboto Mono**.

### 2. Controls (Slider & Settings)
The current slider and text dump are disconnecting the user from the result.
* **The Slider:**
  * Move the specific length number (currently "Total: 16") so it sits **directly above or beside** the slider.
  * Example label: **"Length: 16"**.
  * Remove the dotted line track style; use a solid, continuous bar for a cleaner look.
* **The "Stats" Text Block:**
  * The breakdown (Lower case: 5, Upper case: 1, etc.) is "technical clutter."
  * **Recommendation:** Replace this text block with toggle chips or checkboxes for complexity options (e.g., [x] Uppercase, [x] Numbers, [x] Symbols).
  * If you must keep the counts, make them small, horizontal pills below the password, rather than a vertical list.

### 3. Color & Polish
* **Palette:**
  * Try a deep indigo or violet for a "security" vibe, or a bright teal/mint for a "fresh" vibe.
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

---

## Won't Do / Obsolete

- [ ] **Hero - Large Font:** "let's hold off on this for now, as the text is already large and I'd need to measure multiple devices" (User preference).
- [ ] **Action Buttons - Big "GENERATE NEW" Button:** Replaced with integrated icon buttons in the hero card.
- [ ] **Action Buttons - "Outlined" style Copy button:** Integrated as icon button instead.
- [ ] **Controls - "Avoid Ambiguous" Switch:** User maintained the checkbox and updated instructions for the "+" icon instead. (Marked as obsolete for now based on recent layout simplification).
