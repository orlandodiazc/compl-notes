import { AuthContext, useAuth } from "@/auth";
import ThemeToggle from "@/components/theme-toggle";
import { Button } from "@/components/ui/button";
import UserAvatar from "@/components/user-avatar";
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
  const { user } = useAuth();
  return (
    <>
      <Helmet>
        <title>Compl Notes</title>
        <meta
          name="description"
          content="Effortlessly track your own notes and those of your friends."
        />
      </Helmet>
      <header className="container mx-auto py-2 sm:py-4">
        <nav className="flex items-center justify-between gap-6">
          <Link to="/">
            <div className="font-light">compl</div>
            <div className="font-bold">notes</div>
          </Link>
          <div className="flex gap-4 items-center">
            <Link className="underline" to="/users">
              All Users
            </Link>

            {user ? (
              <div className="flex items-center gap-2">
                <Button asChild variant="secondary">
                  <Link
                    to="/users/$username"
                    params={{ username: user.username }}
                    className="flex items-center gap-2"
                  >
                    <UserAvatar user={user} className="h-7 w-7 sm:h-8 sm:w-8" />
                    <span className="hidden text-body-sm font-bold sm:block">
                      {user.name ?? user.username}
                    </span>
                  </Link>
                </Button>
              </div>
            ) : (
              <Button asChild>
                <Link to="/login" search={{ redirect: "/" }}>
                  Log in
                </Link>
              </Button>
            )}
          </div>
        </nav>
      </header>

      <div className="flex-1">
        <Outlet />
      </div>

      <footer className="container py-3 sm:py-4 flex justify-between items-center">
        <p className="text-body-xs">Built with React and Spring Boot.</p>
        <ThemeToggle />
      </footer>
    </>
  );
}
