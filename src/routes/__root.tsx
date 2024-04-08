import { Outlet, createRootRoute } from "@tanstack/react-router";

export const Route = createRootRoute({
  component: () => (
    <>
      <h1>root</h1>
      <Outlet />
    </>
  ),
});
