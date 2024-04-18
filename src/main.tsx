import React from "react";
import ReactDOM from "react-dom/client";
import "@fontsource-variable/hanken-grotesk";
import "./index.css";
import { RouterProvider, createRouter } from "@tanstack/react-router";
import { routeTree } from "./routeTree.gen";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { ErrorPageComponent, NotFound } from "./components/errors";
import { HelmetProvider } from "react-helmet-async";
import { Icon } from "./components/ui/icon";
import { AuthProvider, useAuth } from "./auth";
import { ThemeProvider } from "./components/theme-provider";
import { Toaster } from "@/components/ui/sonner";

export const queryClient = new QueryClient({
  defaultOptions: { mutations: { throwOnError: true } },
});

const router = createRouter({
  routeTree,
  context: {
    queryClient,
    auth: undefined,
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
  const auth = useAuth();
  return <RouterProvider router={router} context={{ auth }} />;
}

ReactDOM.createRoot(document.getElementById("root")!).render(
  <React.StrictMode>
    <HelmetProvider>
      <QueryClientProvider client={queryClient}>
        <ThemeProvider>
          {/* <AuthProvider> */}
          {/* <AuthRouter /> */}
          <RouterProvider router={router} context={{ auth: undefined }} />;
          {/* </AuthProvider> */}
        </ThemeProvider>
      </QueryClientProvider>
    </HelmetProvider>
    <Toaster />
  </React.StrictMode>,
);
