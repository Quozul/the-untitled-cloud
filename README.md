## Environment variables

```sh
VITE_STRIPE_PUBLIC_KEY=pk_test_ # Stripe's public key
VITE_API_BASE_URL=http://localhost:8080/ # Base URL of the API
VITE_API_WS_URL=ws://localhost:8080/ # Base Websocket URL
```

## Developing

Once you've created a project and installed dependencies with `npm install` (or `pnpm install` or `yarn`), start a development server:

```bash
npm run dev

# or start the server and open the app in a new browser tab
npm run dev -- --open
```

## Building

To create a production version of your app:

```bash
npm run build
```

You can preview the production build with `npm run preview`.

> To deploy your app, you may need to install an [adapter](https://kit.svelte.dev/docs/adapters) for your target environment.
