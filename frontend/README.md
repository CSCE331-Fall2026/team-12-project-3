# Panda Express Frontend

The frontend is a React application for the Panda Express kiosk and store operations workflows. Customers can browse menu items, build an order, choose a tip and payment method, and submit the order. Managers can review inventory by category and add stock to an item.

## Requirements

- Node.js and npm (use a current Node.js LTS release)
- The project backend running or reachable by URL
- A Google OAuth web client ID for Google sign-in

## Getting started

From this directory:

```sh
npm install
```

Create `frontend/.env.local` (or `.env.local` when your terminal is already in this directory) with the frontend configuration below. Then start the development server:

```sh
npm run dev
```

Vite prints the local URL in the terminal, usually `http://localhost:5173`. Keep the backend running at the URL configured in `BACKEND_URL`.

## Environment configuration

| Variable | Required | Description |
|---|---|---|
| `BACKEND_URL` | Yes | Base URL of the backend API. During development, Vite proxies `/api` requests to this server. In a production build, the frontend calls this URL directly. |
| `VITE_GOOGLE_CLIENT_ID` | Yes for sign-in | Google Identity Services web client ID used to render the Google sign-in button. |

Example `frontend/.env.local`:

```dotenv
BACKEND_URL=http://localhost:8080
VITE_GOOGLE_CLIENT_ID=your-google-oauth-web-client-id
```

Use the backend's actual local port if it differs from `8080`. Configure the deployed backend URL and Google client ID in the frontend hosting provider's environment settings before building/deploying. `BACKEND_URL` is embedded in the production frontend bundle; use only a public API URL and never put secrets in frontend environment variables. Restart Vite after changing environment files.

## Available commands

Run these from `frontend/`:

| Command | Purpose |
|---|---|
| `npm run dev` | Start the Vite development server with hot reload |
| `npm run build` | Type-check and create a production build in `dist/` |
| `npm run preview` | Serve the production build locally |
| `npm run lint` | Run ESLint on the frontend source |

## Main workflows

1. **Sign in:** Google Identity Services returns the signed-in profile; the frontend looks up the user's role through the backend.
2. **Choose a role:** new users choose Customer or Manager, and the selection is saved through the backend.
3. **Customer ordering:** browse Popular, Entrees, Sides, Appetizers, and Drinks; choose item sizes and quantities; review the order; choose a tip and payment method; submit.
4. **Manager inventory:** select a category, inspect on-hand quantities and restock thresholds, then add stock inline.

The frontend reports loading, empty, error, and success states in context. Backend connectivity is required for menu, account, order, and inventory data.

## Project structure

```text
src/
  api/           Backend API calls and shared fetch client
  assets/        Images and other static assets
  components/    Shared UI and customer/inventory components
  hooks/         Authentication and inventory state logic
  screens/       Sign-in, role selection, customer, and manager screens
  App.tsx        Top-level screen routing and shared application header
  index.css      Global styles and base accessibility rules
  App.css        Application layout, component, and responsive styles
  types.ts       Frontend data and role types
```

## Backend integration

The frontend sends API requests through `src/api/client.ts`. In development, Vite proxies `/api` to `BACKEND_URL` and removes the `/api` prefix before forwarding. In production, the frontend sends requests directly to `BACKEND_URL`.

If the backend is hosted on a different origin in production, configure it to allow requests from the deployed frontend origin. The frontend expects the backend routes and response data used by the API modules in `src/api/`.

## Design reference

Follow the [frontend style guide](STYLE_GUIDE.md) when changing or adding screens and components. It defines the color palette, typography, layout, responsive behavior, interaction patterns, and accessibility requirements.

## Troubleshooting

- **Google button is missing:** set `VITE_GOOGLE_CLIENT_ID` to a valid Google Identity Services web client ID, check that the current origin is allowed for that client, then restart Vite.
- **API says to set `BACKEND_URL`:** create or update `.env.local` with the backend's base URL and restart Vite.
- **API requests fail locally:** confirm the backend is running and that `BACKEND_URL` uses the correct scheme, host, and port. Check the backend logs and browser developer console for details.
- **Production API requests fail:** confirm the deployed build was created with the correct public `BACKEND_URL`, and that the backend permits requests from the frontend's origin.
- **Changes to environment values have no effect:** restart the development server; Vite reads environment files when it starts.
