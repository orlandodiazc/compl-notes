import { Link } from "@tanstack/react-router";
import { ReactNode } from "react";

function getStatusText(code: number) {
  if (code === 403) {
    return "Forbidden";
  }
}

function getErrorMessage(error: unknown) {
  if (error instanceof Error) {
    return error.message;
  } else if (error instanceof Response) {
    return { detail: getStatusText(error.status), status: error.status };
  } else if (typeof error === "string" || typeof error === "object") {
    return error;
  }
  {
    return "Unknown error";
  }
}

export function ErrorPageComponent({ error }: { error: unknown }) {
  const errorMessage = getErrorMessage(error) as
    | {
        status: string;
        detail: string;
        errors?: Array<string>;
      }
    | string;
  return (
    <ErrorWrapper>
      <h1 className="text-3xl font-bold">Something went wrong!</h1>

      {typeof errorMessage === "string" ? (
        <p>
          <i>{errorMessage}</i>
        </p>
      ) : (
        <>
          <h2 className="text-2xl font-bold">{errorMessage.status}</h2>
          <p className="text-lg font-semibold">{errorMessage.detail}.</p>
          {errorMessage.errors?.length && (
            <ul>
              {errorMessage.errors.map((message) => (
                <li>{message}</li>
              ))}
            </ul>
          )}
        </>
      )}
    </ErrorWrapper>
  );
}

function ErrorWrapper({ children }: { children: ReactNode }) {
  return (
    <main className="container mx-auto flex h-full w-full items-center justify-center bg-destructive p-20 text-destructive-foreground">
      <div className="flex flex-col gap-6">
        <div className="flex flex-col gap-3">{children}</div>
        <Link to="/" className="text-body-md underline">
          Back to home
        </Link>
      </div>
    </main>
  );
}

export function NotFound() {
  return (
    <ErrorWrapper>
      <h1 className="text-4xl font-bold">Oops!</h1>
      <h2 className="text-3xl font-bold">404</h2>
      <p className="text-xl font-semibold">Not Found</p>
    </ErrorWrapper>
  );
}
