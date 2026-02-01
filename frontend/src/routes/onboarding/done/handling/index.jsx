import { Button } from "@/components/ui/button";
import { Card, CardContent } from "@/components/ui/card";
import { useOnboarding } from "@/hooks/use-onboarding";
import { api } from "@/lib/api";
import { createFileRoute, redirect, useNavigate } from "@tanstack/react-router";
import { CheckCircle, LoaderPinwheel } from "lucide-react";
import { useCallback, useEffect, useState } from "react";

export const Route = createFileRoute("/onboarding/done/handling/")({
	component: SubmitOnboardingPage,
	beforeLoad: ({ context }) => {
		const { auth } = context;

		if (!auth.user) {
			throw redirect({ to: "/auth/login" });
		}

		if (!auth.hasTrait("AWAITING_PROFILE_CREATION")) {
			throw redirect({ to: "/" });
		}
	},
});

export default function SubmitOnboardingPage() {
	const navigate = useNavigate();
	const { setCurrentStep } = useOnboarding();
	const [status, setStatus] = useState("processing");

	useEffect(() => {
		setCurrentStep("done");
	}, [setCurrentStep]);

	const checkRegistrationStatus = useCallback(async () => {
		try {
			const response = await api.get("/profiling/profile-intent");

			if (response.status === 204) {
				return true;
			}

			if (response.status === 200) {
				return response.data?.status === "PROCESSED";
			}
		} catch (error) {
			console.error("Error checking registration status:", error);
		}

		return false;
	}, []);
	// =============================================================

	useEffect(() => {
		const interval = setInterval(async () => {
			const isReady = await checkRegistrationStatus();

			if (isReady) {
				setStatus("ready");
				clearInterval(interval);

				setTimeout(() => {
					// navigate({ to: "/" });
					alert("Redirecionando para a página inicial...");
				}, 1200);
			}
		}, 2000);

		return () => clearInterval(interval);
	}, [checkRegistrationStatus]);

	return (
		<>
			<div className="w-full max-w-xl mx-auto flex flex-col items-center justify-center min-h-[70vh] space-y-6 text-center">
				<Button
					className="fade-in-once"
					onClick={() => setStatus("processing")}
					variant="ghost"
				>
					TESTE: RESETAR POLLING
				</Button>
				<Card className="w-full">
					<CardContent className="pt-10 pb-10 flex flex-col items-center gap-4">
						{status === "processing" ? (
							<LoaderPinwheel className="w-14 h-14 text-primary animate-spin mx-auto my-auto" />
						) : (
							<CheckCircle className="w-14 h-14 text-green-600 fade-in-once" />
						)}

						<h1 className="text-xl font-bold">
							{status === "processing"
								? "Estamos finalizando seu cadastro"
								: "Cadastro concluído!"}
						</h1>

						<p className="text-muted-foreground max-w-md">
							{status === "processing"
								? "Estamos processando suas informações. Você será redirecionado(a) automaticamente para o painel assim que tudo estiver pronto."
								: "Você será redirecionado(a) para o seu dashboard em instantes."}
						</p>

						{status === "processing" && (
							<div className="text-sm text-muted-foreground">
								Isso costuma levar apenas alguns segundos.
							</div>
						)}
					</CardContent>
				</Card>

				{status === "ready" && (
					<Button variant="outline" onClick={() => navigate({ to: "/" })}>
						Ir para a página inicial agora
					</Button>
				)}
			</div>
		</>
	);
}
