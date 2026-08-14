import { QueryClient } from '@tanstack/react-query';

/** Application-wide query client; server state is never mirrored in a global store. */
export const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      staleTime: 30_000,
      retry: 1,
      refetchOnWindowFocus: false,
    },
  },
});
