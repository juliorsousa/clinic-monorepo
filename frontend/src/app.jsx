/** biome-ignore-all lint/style/noNonNullAssertion: <explanation> */
import { createRouter, RouterProvider } from '@tanstack/react-router'
import { ThemeProvider } from '@/components/theme/theme-provider'
import { Toaster } from '@/components/ui/sonner'
import { routeTree } from './routeTree.gen'

const router = createRouter({
  routeTree,
  context: {
    auth: undefined,
  },
})

export function App() {
  return (
    <ThemeProvider defaultTheme="dark" storageKey="vite-ui-theme">
       {/* <QueryClientProvider client={queryClient}> */}
         {/* <AuthProvider> */}
            <RouterProvider context={{ auth: undefined }} router={router} />
            <Toaster richColors />
    {/*     </AuthProvider> */}
    {/*   </QueryClientProvider> */}
    </ThemeProvider>
  )
}