import { createRootRouteWithContext, HeadContent, Outlet } from '@tanstack/react-router'
import { TanStackRouterDevtools } from '@tanstack/react-router-devtools'
import { NotFound } from '@/routes/404'

export const Route = createRootRouteWithContext()({
  component: () => (
    <>
      <HeadContent />
      <Outlet />
      <TanStackRouterDevtools />
    </>
  ),
  notFoundComponent: () => <NotFound />
})