# Panda Express Frontend Code Style Guide

This guide defines how frontend code is written and reviewed. Visual design is set by product design; this document covers the implementation conventions for TypeScript, React, CSS, and HTML in this project.

The existing `src/` tree is the reference implementation. When code and this guide disagree, fix the code or update the guide in the same change.

## 1. Principles

- **Readable first:** optimize for the next person scanning a diff.
- **One way to do it:** follow an existing pattern before introducing a new one.
- **Explicit over clever:** name intermediate values instead of writing dense expressions.
- **Small units:** keep one clear responsibility per component, hook, and API module.
- **Accessible by default:** semantic markup, labels, and visible focus are part of done.
- **Verified:** `npm run lint` and `npm run build` must pass before a change is complete.

## 2. Formatting

These rules apply to TypeScript and TSX. CSS follows the compact authoring style in section 9.

- Use 2-space indentation; never tabs.
- Do not write semicolons.
- Use single quotes in TypeScript and TSX; use double quotes for JSX attribute values.
- Use trailing commas in multiline arrays, objects, parameters, and arguments.
- Always parenthesize arrow function parameters.
- Keep code lines at 120 characters or fewer. Long user-facing message literals are the exception; split a message only when it improves readability, never just to hit a column.
- Put one blank line after the import block, between logical groups of top-level declarations, and before a `return` when the function has leading statements.
- Do not leave trailing whitespace. End every file with a newline.

### JSX layout

- Put each attribute on its own line when an opening tag has more than three attributes, or when the tag would exceed the line limit.
- Simple elements with short content and three or fewer attributes may stay on one line.
- Keep short inline content on one line. Break an element's children onto separate lines when the content is long or the element contains block-level children.
- Use `{condition && <Element />}` for a single conditional. Do not write `{condition ? <Element /> : null}`.
- Do not nest ternary operators. Use an early return, a lookup map, or a helper function instead.
- Parenthesize multiline JSX returned by an arrow function or assigned to a variable.

## 3. Naming

| Item | Convention | Examples |
|---|---|---|
| Component file and function | PascalCase | `MenuItemCard.tsx`, `ManagerScreen.tsx` |
| Screen component | PascalCase ending in `Screen` | `SignInScreen`, `CustomerScreen` |
| Hook file and function | camelCase with `use` prefix | `useInventory.ts`, `useGoogleSignIn` |
| API module | camelCase domain name | `customerMenu.ts`, `inventory.ts` |
| Type alias | PascalCase | `MenuItem`, `AppScreen` |
| Component props type | Component name plus `Props` | `MenuItemCardProps` |
| Variable, function, constant | camelCase | `tipOptions`, `formatCurrency` |
| Event handler prop | `on` plus a verb | `onSelect`, `onPlaceOrder` |
| Local event handler | `handle` plus the event or action | `handleBrandClick`, `handlePlaceOrder` |
| CSS class | kebab-case | `customer-item-card` |
| CSS state modifier | base class plus state suffix | `customer-tab-active`, `selected` |
| HTML `id` | kebab-case | `google-button`, `restock-freezer-rice` |

- Name booleans as a condition (`available`, `loading`, `selected`) or with `is`/`has` when it reads better.
- Treat acronyms as words (`clientId`, `apiUrl`, `googleId`). Preserve backend payload spelling when mapping API data, such as `imageURL`.
- Use `a` and `b` for sort comparators and short identifiers such as `id`; avoid other single-letter names.
- Name async actions verb-first (`fetchInventoryItems`, `placeCustomerOrder`, `saveRestock`).
- Do not use `get` in a network function name when the function performs a request; `fetch` communicates the I/O.

## 4. TypeScript

- Use type aliases. Do not use `interface` or `enum`; finite values are string-literal unions because the build enables `erasableSyntaxOnly`. The one interface exception is global declaration merging for `Window` in `types.ts`.
- Use `import type` for type-only imports. The ESLint configuration enforces this.
- Avoid `any`. Use `unknown` for untrusted values and narrow before use.
- Use `| null` when absence is meaningful and an optional property (`?`) when a field may simply be omitted.
- Do not use non-null assertions. The only exception is the root mount element in `main.tsx`.
- Use type assertions only at JSON or browser API boundaries. Validate or narrow the asserted value before returning it.
- Use `const` by default. Never use `var`.
- Compare with strict equality (`===` / `!==`).
- Model keyed collections with `Record<K, V>` or `Map<K, V>`. Convert request payload maps with `Object.fromEntries`.
- Let TypeScript infer component and hook return types. Annotate API return types when the shape is part of the module contract.
- Put shared unions, shared static display data, and browser globals in `src/types.ts`.

## 5. Imports and module structure

- Import order:
  1. React and third-party packages.
  2. Project modules, from farthest to nearest (`../../`, then `../`, then `./`), alphabetical within the same depth.
  3. Stylesheet side effects, last.
- Separate package imports from project imports with one blank line.
- Import values before types from the same module.
- Use the `@/` alias only if one is configured; this project uses relative imports.
- Keep the dependency direction one-way:
  - `screens/` may import from `components/`, `hooks/`, `api/`, and `types.ts`.
  - `components/` may import from `types.ts` and other components. Presentational components do not call API modules.
  - `hooks/` may import from `api/` and `types.ts`.
  - `api/` may import from `client.ts` and `types.ts` only. API modules do not import React or components.
  - `types.ts` imports nothing at runtime.
- `App.tsx` owns the application shell and top-level screen switch. Screens own their internal steps.
- Keep one responsibility per module and avoid circular imports.

## 6. Components and JSX

- Write function components with a default export. Do not use `React.FC`.
- Define a `ComponentNameProps` type immediately above the component and destructure props in the signature.
- Do not declare a component inside another component; it remounts on every render.
- Private presentational helpers may live in the same module when they are not reused elsewhere. Move a helper to `components/` when a second feature needs it.
- Compute derived values before the `return`, including filtered lists, totals, and labels used more than once.
- Extract repeated markup into a small component rather than duplicating a block (`ItemGrid` in `CustomerScreen`).
- Extract an event handler into a `const handleX` declaration when it contains more than one statement or uses the same logic in more than one place.
- Use stable domain keys in lists. Do not use array indexes as keys.
- Add `type="button"` to every button that does not submit a form.
- Use `aria-pressed` for toggle-style tabs and `aria-label` for icon-only controls.
- Mark decorative images and glyphs with `aria-hidden="true"` or an empty `alt`.
- Do not use inline `style` attributes; use classes in `App.css`.
- Do not add keyboard handlers to non-interactive elements. Use a `button`, `a`, `input`, or `label`.

## 7. Hooks and state

- Put hooks at the top of the component, before derived values and early returns.
- Name hooks `useX` and export them as named exports. Return a flat object of state values and actions.
- Use effects only to synchronize with external systems: network requests, the Google Identity script, and browser APIs.
- Do not call `setState` synchronously in an effect body. Start effects from state that is already initial or derived, and update state from async callbacks. The React hooks ESLint rules enforce this.
- Derive state instead of storing a duplicate. For example, derive `loading` from whether the requested category has finished loading.
- Guard every async state update with an `active` or `cancelled` flag, and always return a cleanup function.
- Use functional updates (`setState((current) => ...)`) when the next value depends on the previous value.
- Keep dependency arrays complete. Do not suppress `react-hooks/exhaustive-deps`.
- Do not leave floating promises. Use `void promise` at an event boundary or `await` inside an async handler.
- Use `useMemo` only for non-trivial derived collections, such as the cart lines. Do not memoize simple booleans or strings.
- Keep fetching and business logic in hooks or screens, not in presentational components.

## 8. API layer

- Send every request through `apiFetch` from `src/api/client.ts`. Do not call `fetch` elsewhere.
- Group endpoints by feature in one API module per domain.
- Return typed data, not a raw `Response`.
- Throw `Error` objects with plain-language messages that name the failed action and a recovery step.
- When wrapping a low-level error, attach the original with `{ cause: error }`. Never swallow an error silently.
- Keep backend field spelling in payload types, such as `imageURL`.
- Keep HTTP status codes in diagnostics. Do not expose raw HTML in user-facing messages.

## 9. CSS

- `src/index.css` contains global base styles, reset rules, and focus treatment.
- `src/App.css` contains application layout, component styles, and responsive rules. Add a new stylesheet only for a new global layer.
- Use class selectors for styling. The `#root` selector is limited to the application shell.
- Use kebab-case class names. Prefix new component classes with their feature area (`customer-`, `manager-`, `auth-`). Existing shared primitives keep their names; do not rename them only to satisfy a prefix rule.
- State modifiers extend the base class (`customer-tab-active`, `status-low`); do not invent a separate naming system.
- Keep the compact authored style used in `App.css`: a short rule stays on one line, and a long or multi-property rule is split into one declaration per line.
- Group related rules together and put media queries at the end of the file.
- Do not use `!important`. Prefer specificity through the component class.
- Use `.sr-only` for text that is available to assistive technology but visually hidden.
- Preserve the global `:focus-visible` outline; never remove it without an equally visible replacement.

## 10. States, feedback, and accessibility

- Every data-driven screen defines loading, error, empty, disabled, and success states where they apply.
- Announce loading with `role="status"`; announce errors with `role="alert"`.
- Place an error beside the action that failed and describe what the user can do next.
- Disable controls only while work is in progress, and keep the reason nearby.
- Confirm success and offer the next useful action.
- Pair status color with a text label. Never communicate stock state with color alone.
- Use semantic landmarks (`main`, `section`, `nav`, `header`) and one `h1` per screen.
- Tie every input to a visible label or an `.sr-only` label through `htmlFor` and `id`.
- Keep all workflows usable with a keyboard and native form controls.

## 11. Comments

- Comments explain why, not what the code already says.
- Keep comments that capture backend or platform behavior, such as the note about Jackson converting JSON object keys to integers.
- Remove commented-out code. Do not commit dead experiments.
- Do not add a TODO without a concrete follow-up; prefer an issue reference.

## 12. Verification checklist

Before completing a change:

1. Run `npm run lint` and fix every error.
2. Run `npm run build` and confirm the TypeScript build and Vite bundle succeed.
3. Check the affected file against this guide: import order, naming, line width, JSX layout, and state handling.
4. If behavior changed, exercise the affected loading, error, empty, and success paths.
