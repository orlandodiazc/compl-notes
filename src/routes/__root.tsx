import { QueryClient } from "@tanstack/react-query";
import {
  Link,
  Outlet,
  createRootRouteWithContext,
} from "@tanstack/react-router";

export const Route = createRootRouteWithContext<{ queryClient: QueryClient }>()(
  {
    component: () => (
      <>
        <header className="container mx-auto py-6">
          <nav className="flex items-center justify-between gap-6">
            <Link to="/">
              <div className="font-light">compl</div>
              <div className="font-bold">notes</div>
            </Link>
            <Link className="underline" to="users">
              All Users
            </Link>
          </nav>
        </header>

        <div className="flex-1">
          <Outlet />
        </div>

        <footer className="container py-6">
          <p className="text-body-xs">Built with React and Spring Boot.</p>
        </footer>
      </>
    ),
  },
);
