import { useState, useEffect } from "react";
import { API_BASEURL } from "./api";

export const useFetch = <Data>() => {
  const [data, setData] = useState<Data | null>(null);
  const [isPending, setIsPending] = useState(false);
  const [error, setError] = useState<unknown | null>(null);
  useEffect(() => {
    const fetchData = async () => {
      setIsPending(true);
      try {
        const response = await fetch(API_BASEURL + "/auth/user", {
          credentials: "include",
        });
        if (!response.ok) throw response;
        const data = await response.json();
        setIsPending(false);
        setData(data);
        setError(null);
      } catch (error) {
        setError(error);
        setIsPending(false);
      }
    };
    fetchData();
  }, []);
  return { data, isPending, error, setData };
};
