import { AuthContext } from "@/auth";
import { QueryClient } from "@tanstack/react-query";
import {
  Link,
  Outlet,
  createRootRouteWithContext,
} from "@tanstack/react-router";
import { Helmet } from "react-helmet-async";

interface RouterContext {
  queryClient: QueryClient;
  auth: AuthContext | undefined;
}

export const Route = createRootRouteWithContext<RouterContext>()({
  component: RootRoute,
});

function RootRoute() {
  return (
    <>
      <Helmet>
        <title>Compl Notes</title>
        <meta
          name="description"
          content="Effortlessly track your own notes and those of your friends."
        />
      </Helmet>
      <header className="container mx-auto py-4">
        <nav className="flex items-center justify-between gap-6">
          <Link to="/">
            <div className="font-light">compl</div>
            <div className="font-bold">notes</div>
          </Link>
          <Link className="underline" to="/users">
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
  );
}
