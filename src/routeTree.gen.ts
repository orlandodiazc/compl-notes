/* prettier-ignore-start */

/* eslint-disable */

// @ts-nocheck

// noinspection JSUnusedGlobalSymbols

// This file is auto-generated by TanStack Router

// Import Routes

import { Route as rootRoute } from "./routes/__root";
import { Route as IndexImport } from "./routes/index";
import { Route as UsersIndexImport } from "./routes/users/index";
import { Route as UsersUsernameImport } from "./routes/users/$username";

// Create/Update Routes

const IndexRoute = IndexImport.update({
  path: "/",
  getParentRoute: () => rootRoute,
} as any);

const UsersIndexRoute = UsersIndexImport.update({
  path: "/users/",
  getParentRoute: () => rootRoute,
} as any);

const UsersUsernameRoute = UsersUsernameImport.update({
  path: "/users/$username",
  getParentRoute: () => rootRoute,
} as any);

// Populate the FileRoutesByPath interface

declare module "@tanstack/react-router" {
  interface FileRoutesByPath {
    "/": {
      preLoaderRoute: typeof IndexImport;
      parentRoute: typeof rootRoute;
    };
    "/users/$username": {
      preLoaderRoute: typeof UsersUsernameImport;
      parentRoute: typeof rootRoute;
    };
    "/users/": {
      preLoaderRoute: typeof UsersIndexImport;
      parentRoute: typeof rootRoute;
    };
  }
}

// Create and export the route tree

export const routeTree = rootRoute.addChildren([
  IndexRoute,
  UsersUsernameRoute,
  UsersIndexRoute,
]);

/* prettier-ignore-end */
