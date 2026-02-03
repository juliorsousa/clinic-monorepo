import { Button } from "@/components/ui/button";
import { Card, CardContent } from "@/components/ui/card";
import { useAuth } from "@/hooks/use-auth";
import { useOnboarding } from "@/hooks/use-onboarding";
import { api } from "@/lib/api";
import { createFileRoute, redirect, useNavigate } from "@tanstack/react-router";
import { CheckCircle, CircleSlash, LoaderPinwheel } from "lucide-react";
import { useCallback, useEffect, useRef, useState } from "react";

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
	const { revalidate } = useAuth();
	const { setCurrentStep } = useOnboarding();

	const navigate = useNavigate();
	const [status, setStatus] = useState("processing");
	const [result, setResult] = useState(null);
	const [canGoBackAfterError, setCanGoBackAfterError] = useState(false);

	const intervalRef = useRef(null);
	const hasStoppedRef = useRef(false);

	useEffect(() => {
		setCurrentStep("done");
	}, [setCurrentStep]);

	const checkRegistrationStatus = useCallback(async () => {
		try {
			const response = await api.get("/profiling/profile-intent");

			if (response.status === 204) {
				return { status: "PROCESSED", result: null };
			}

			if (response.status === 200) {
				const parsed = JSON.parse(response.data?.response || "{}");

				return {
					status: response.data?.status,
					result: parsed,
				};
			}
		} catch {
			// silent fail
		}

		return { status: "PENDING", result: null };
	}, []);

	useEffect(() => {
		intervalRef.current = setInterval(async () => {
			if (hasStoppedRef.current) return;

			const { status, result } = await checkRegistrationStatus();

			if (status === "PROCESSED") {
				hasStoppedRef.current = true;
				clearInterval(intervalRef.current);

				setStatus("ready");

				setTimeout(async () => {
					await revalidate();
					navigate({ to: "/" });
				}, 1200);
			} else if (status === "ERRORED") {
				hasStoppedRef.current = true;
				clearInterval(intervalRef.current);

				setResult(result);
				setStatus("errored");

				// bloqueia botão por 5s
				setCanGoBackAfterError(false);
				setTimeout(() => {
					setCanGoBackAfterError(true);
				}, 5000);
			}
		}, 2000);

		return () => {
			if (intervalRef.current) {
				clearInterval(intervalRef.current);
			}
		};
	}, [checkRegistrationStatus, navigate, revalidate]);

	async function handleGoBack() {
		await revalidate();
		navigate({ to: "/" });
	}

	return (
		<div className="w-full max-w-xl mx-auto flex flex-col items-center justify-center min-h-[70vh] space-y-6 text-center">
			<Card className="w-full">
				<CardContent className="pt-10 pb-10 flex flex-col items-center gap-4">
					{status === "processing" ? (
						<LoaderPinwheel className="w-14 h-14 text-primary animate-spin mx-auto my-auto" />
					) : status === "ready" ? (
						<CheckCircle className="w-14 h-14 text-green-600 fade-in-once" />
					) : (
						<CircleSlash className="w-14 h-14 text-red-500" />
					)}

					<h1 className="text-xl font-bold">
						{status === "processing"
							? "Estamos finalizando seu cadastro"
							: status === "ready"
								? "Cadastro concluído!"
								: "Ocorreu um erro no cadastro"}
					</h1>

					<p className="text-muted-foreground max-w-md">
						{status === "processing"
							? "Estamos processando suas informações. Você será redirecionado(a) automaticamente para o painel assim que tudo estiver pronto."
							: status === "ready"
								? "Você será redirecionado(a) para o seu dashboard em instantes."
								: "Não foi possível concluir seu cadastro."}
					</p>

					{status === "processing" && (
						<div className="text-sm text-muted-foreground">
							Isso costuma levar apenas alguns segundos.
						</div>
					)}

					{status === "errored" && result?.message && (
						<div className="mt-4 p-4 text-red-700 rounded-md text-sm max-w-md border border-red-500">
							{result.message}
						</div>
					)}
				</CardContent>
			</Card>

			{status === "ready" && (
				<Button variant="outline" onClick={handleGoBack}>
					Ir para a página inicial agora
				</Button>
			)}

			{status === "errored" && (
				<Button
					variant="outline"
					onClick={handleGoBack}
					disabled={!canGoBackAfterError}
				>
					{canGoBackAfterError
						? "Voltar para a página inicial"
						: "Aguarde 5 segundos..."}
				</Button>
			)}
		</div>
	);
}
