import { ThemeProvider } from "@/components/theme/theme-provider";
import { Toaster } from "@/components/ui/sonner";
import { RouterProvider, createRouter } from "@tanstack/react-router";
import { AuthProvider } from "./contexts/auth-provider";
import { useAuth } from "./hooks/use-auth";
import { routeTree } from "./routeTree.gen";

function InnerApp() {
	const auth = useAuth();

	const router = createRouter({
		routeTree,
		context: {
			auth,
		},
	});

	return <RouterProvider router={router} />;
}

export function App() {
	return (
		<ThemeProvider defaultTheme="dark" storageKey="vite-ui-theme">
			{/* <QueryClientProvider client={queryClient}> */}
			<AuthProvider>
				<InnerApp />
				<Toaster richColors />
			</AuthProvider>
			{/*   </QueryClientProvider> */}
		</ThemeProvider>
	);
}
