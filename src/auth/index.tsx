import { ErrorPageComponent } from "@/components/errors";
import Spinner from "@/components/spinner";
import { ApiSchema } from "@/lib/api/apiSchema";
import { useFetch } from "@/lib/useFetch";
import * as React from "react";
import { flushSync } from "react-dom";

type AuthUser = ApiSchema["AuthUserResponse"]["user"];

export interface AuthContext {
  isAuthenticated: boolean;
  user: AuthUser;
  setUser: (user: AuthUser) => void;
}

const AuthContext = React.createContext<AuthContext | null>(null);

export function AuthProvider({ children }: { children: React.ReactNode }) {
  const { data, error, isPending, setData } =
    useFetch<ApiSchema["AuthUserResponse"]>();
  const user = data?.user;
  if (isPending)
    return (
      <div className="w-full h-full grid place-content-center">
        <Spinner loading={isPending} />
      </div>
    );
  if (error) return <ErrorPageComponent error={error} />;

  function setUser(user: AuthUser) {
    flushSync(() => {
      setData({ user });
    });
  }
  const isAuthenticated = !!user;
  return (
    <AuthContext.Provider value={{ isAuthenticated, user, setUser }}>
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  const context = React.useContext(AuthContext);
  if (!context) {
    throw new Error("useAuth must be used within an AuthProvider");
  }
  return context;
}
