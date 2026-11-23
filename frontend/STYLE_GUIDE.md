# Panda Express Frontend Style Guide

## 1. Design principles

Build a warm, clear, and efficient experience for two audiences: customers placing an order and managers checking stock. Make the next action easy to find. Keep food and order details inviting, and keep operational information compact and easy to scan.

Every screen should follow these principles:

- **Clear hierarchy:** one primary page heading, grouped content, and an obvious main action.
- **Warm restraint:** use the brand colors as accents against cream and white surfaces.
- **Fast scanning:** align prices, quantities, and statuses; keep supporting copy concise.
- **Consistent interaction:** controls with the same purpose should look and behave the same across screens.
- **Accessible by default:** preserve keyboard access, readable contrast, and text labels for meaningful states.

## 2. Brand colors

Use these color tokens throughout the interface. Do not add a new accent color for an individual screen without extending this system deliberately.

| Token | Hex | Application |
|---|---|---|
| `--brand-red` | `#C8102E` | Primary actions, active navigation, key brand accents |
| `--brand-orange` | `#F36C21` | Secondary highlights, progress, warm emphasis |
| `--page` | `#FFFAF2` | Main page background |
| `--surface` | `#FFFFFF` | Cards, panels, header, menus |
| `--ink` | `#2A2420` | Main text and headings |
| `--text-muted` | `#786E65` | Supporting text and secondary labels |
| `--text-subtle` | `#94897E` | Footnotes and low-priority metadata |
| `--border` | `#EEE4D8` | Card borders, dividers, table rules |
| `--focus` | `#F3A340` | Keyboard focus indicator |
| `--success` | `#34724B` | Successful/completed and in-stock states |
| `--success-bg` | `#EAF4EC` | Success badge background |
| `--danger` | `#A51D23` | Validation and error text |
| `--danger-bg` | `#FDE9E6` | Error and low-stock backgrounds |
| `--neutral-bg` | `#F1EEE9` | Unknown or neutral status background |

Use red for the primary action and active state. Use orange sparingly to support the brand. Use green, red, and neutral gray for status only, and always pair status color with a text label or icon.

## 3. Typography

Use the following font stack: `Inter, ui-sans-serif, system-ui, -apple-system, BlinkMacSystemFont, "Segoe UI", sans-serif`.

| Style | Desktop size | Weight | Use |
|---|---:|---:|---|
| Page title | 37–54 px | 700–800 | One per screen; adapt to the title length |
| Section title | 18–28 px | 650–750 | Major content groups |
| Card/item title | 14–23 px | 600–700 | Menu items, panels, role choices |
| Body | 13–15 px | 400 | Main descriptions and instructions |
| Control label | 11–13 px | 600–700 | Buttons, tabs, fields, compact metadata |
| Eyebrow | 10–11 px | 700–800 | Short uppercase context label |
| Footnote | 9–11 px | 400–500 | Secondary help and supporting metadata |

Use tight line height and slightly negative letter spacing for large titles. Body text should use comfortable line spacing. Eyebrows use uppercase letters and expanded tracking. Use tabular numerals for prices, totals, quantities, and other values that users compare.

## 4. Spacing, shape, and elevation

Use a 4 px spacing scale: `4, 8, 12, 16, 20, 24, 32, 40, 48` px. Prefer these values for padding, gaps, and margins.

- Use 16–24 px inside standard cards and panels.
- Use 8–12 px corner radii for cards and selection tiles; use 6–8 px for controls.
- Use thin warm borders to separate surfaces. Reserve shadows for elements that need clear elevation, such as a success card or floating mobile order bar.
- Keep primary buttons at least 43 px high. Make quantity controls and other frequent touch actions at least 32 px high and wide.
- Use dividers to separate related rows; avoid boxing every piece of information.

## 5. Application shell

### Header

Use a white header with the wordmark on the left and account actions on the right. Anchor it with a 3 px brand-red bottom rule. The desktop header is 76 px tall; compact it to 58 px on narrow screens. Hide optional contextual text before compressing the wordmark or account action.

### Page canvas

Use the cream page background as the default. Center wide content and cap customer ordering content at approximately 1240 px. Provide generous desktop page margins and reduce them on mobile. Use white surfaces to group related information and controls.

## 6. Components

### Primary button

Use a full brand-red fill, white bold text, and a 6–8 px radius. Center its label and optional icon. Use a subtle red-tinted shadow only when it helps distinguish the main action. Show disabled state with reduced opacity and no shadow. Keep the action label specific, such as “Continue to checkout” or “Place order · $12.50.”

### Secondary button and text action

Use a white surface with a red outline for secondary actions in prominent locations. Use red text actions for compact row-level actions. Use muted text for back and cancel actions. Provide a clear hover and keyboard-focus state.

### Category tabs

Lay tabs in a horizontal row with a warm divider underneath. Show the selected tab with brand-red text and a 3 px underline. Keep tabs on one line; allow horizontal scrolling on narrow viewports. Expose selection semantically and retain a visible focus style.

### Cards and selection tiles

Use white surfaces, a thin warm border, and consistent corner radii. On hover, selected, or active state, shift the border toward brand orange or red and apply a very light warm tint. Keep text alignment and internal spacing consistent across a card group.

### Form fields

Use a visible label, a white background, a 1 px warm border, and a 6 px radius. Keep field text at least 14 px. Show a clear focus ring and error message close to the field. Inputs must preserve keyboard operation and native validation where appropriate.

### Status badges

Use a compact pill with a small colored dot, short label, and pale status background. Standard labels are “In stock,” “Restock,” and “Count unavailable.” Never use color without text to communicate stock state.

## 7. Responsive layout

Use these breakpoints:

- **Up to 440 px:** stack role choices; tighten card padding; prevent titles and account details from overflowing.
- **Up to 760 px:** compact the header, stack sign-in panels, use one-column checkout, reduce page gutters, and show the mobile order summary.
- **761–1040 px:** use a two-column menu grid and a narrower order-summary column.
- **Above 1040 px:** use the full desktop content layout and wider page gutters.

On narrow screens, keep dense tables readable by scrolling inside their container rather than squeezing columns. Use `100svh` for viewport-height layouts with a `100vh` fallback where mobile browser chrome affects the available height. Respect safe-area insets for bottom-fixed controls.

## 8. States and feedback

Every data-driven screen must define loading, error, empty, disabled, and success states where relevant.

- **Loading:** state what is loading in brief text and announce it as a status.
- **Error:** describe the issue in plain language, place it beside the affected task, and allow recovery where possible.
- **Empty:** explain what is absent and the next useful action.
- **Disabled:** visibly indicate unavailable actions and make the reason clear nearby.
- **Success:** confirm the result and show the next useful action.
- **Unavailable:** preserve useful item context, show a text label, and prevent invalid selection.
