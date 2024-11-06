import { Toaster } from "@/components/ui/sonner";
import "@fontsource-variable/hanken-grotesk";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { RouterProvider, createRouter } from "@tanstack/react-router";
import React from "react";
import ReactDOM from "react-dom/client";
import { HelmetProvider } from "react-helmet-async";
import { ErrorPageComponent, NotFound } from "./components/errors";
import { ThemeProvider } from "./components/theme-provider";
import { Icon } from "./components/ui/icon";
import "./index.css";
import { routeTree } from "./routeTree.gen";

export const queryClient = new QueryClient({
  defaultOptions: { mutations: { throwOnError: true } },
});

const router = createRouter({
  routeTree,
  context: {
    queryClient,
    // auth: undefined!,
  },
  defaultPreload: "intent",
  defaultErrorComponent: ({ error }) => <ErrorPageComponent error={error} />,
  defaultNotFoundComponent: NotFound,
  defaultPendingComponent: () => (
    <div className="w-full h-full grid place-content-center">
      <Icon name="update" size="xl" className="animate-spin" />
    </div>
  ),

  // Since we're using React Query, we don't want loader calls to ever be stale
  // This will ensure that the loader is always called when the route is preloaded or visited
  defaultPreloadStaleTime: 0,
});

declare module "@tanstack/react-router" {
  interface Register {
    router: typeof router;
  }
}

function AuthRouter() {
  return <RouterProvider router={router} />;
}

ReactDOM.createRoot(document.getElementById("root")!).render(
  <React.StrictMode>
    <HelmetProvider>
      <QueryClientProvider client={queryClient}>
        <ThemeProvider>
          <AuthRouter />
        </ThemeProvider>
      </QueryClientProvider>
    </HelmetProvider>
    <Toaster />
  </React.StrictMode>
);
