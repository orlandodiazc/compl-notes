import { queryOptions } from "@tanstack/react-query";
import { fetchFilteredUsers } from ".";

export const usersQuery = (filter?: string) =>
  queryOptions({
    queryKey: ["users", "search", filter],
    queryFn: () => fetchFilteredUsers(filter),
  });
