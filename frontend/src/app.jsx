import { ThemeProvider } from "@/components/theme/theme-provider";
import { Toaster } from "@/components/ui/sonner";
import { RouterProvider, createRouter } from "@tanstack/react-router";
import { AuthProvider } from "./contexts/auth-provider";
import { OnboardingProvider } from "./contexts/onboarding-provider";
import { useAuth } from "./hooks/use-auth";
import { useOnboarding } from "./hooks/use-onboarding";
import { routeTree } from "./routeTree.gen";

function InnerApp() {
	const auth = useAuth();
	const onboarding = useOnboarding();

	const router = createRouter({
		routeTree,
		context: {
			auth,
			onboarding,
		},
	});

	return <RouterProvider router={router} />;
}

export function App() {
	return (
		<ThemeProvider defaultTheme="dark" storageKey="vite-ui-theme">
			{/* <QueryClientProvider client={queryClient}> */}
			<AuthProvider>
				<OnboardingProvider>
					<InnerApp />
					<Toaster richColors />
				</OnboardingProvider>
			</AuthProvider>
			{/*   </QueryClientProvider> */}
		</ThemeProvider>
	);
}
