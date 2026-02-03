import { Card, CardContent } from "@/components/ui/card";
import { useOnboarding } from "@/hooks/use-onboarding";
import { createFileRoute, redirect } from "@tanstack/react-router";
import { Clock } from "lucide-react";
import { useEffect } from "react";

export const Route = createFileRoute("/onboarding/done/pending/")({
	component: SubmitOnboardingPage,
	beforeLoad: ({ context }) => {
		const { auth } = context;

		if (!auth.user) {
			throw redirect({ to: "/auth/login" });
		}

		if (!auth.hasTrait("AWAITING_INTENT_APPROVAL")) {
			throw redirect({ to: "/" });
		}
	},
});

export default function SubmitOnboardingPage() {
	const { setCurrentStep } = useOnboarding();

	useEffect(() => {
		setCurrentStep("done");
	}, [setCurrentStep]);

	return (
		<div className="w-full max-w-xl mx-auto flex flex-col items-center justify-center min-h-[70vh] space-y-6 text-center">
			<Card className="w-full">
				<CardContent className="pt-10 pb-10 flex flex-col items-center gap-4">
					<Clock className="w-14 h-14 text-destructive mx-auto" />

					<h1 className="text-xl font-bold">Cadastro em análise</h1>

					<p className="text-muted-foreground max-w-md">
						Seu registro foi enviado com sucesso e agora precisa ser revisado
						por um administrador. Você receberá uma notificação por e-mail assim
						que sua solicitação for avaliada.
					</p>

					<div className="text-sm text-muted-foreground">
						Esse processo pode levar algumas horas, dependendo do volume de
						solicitações.
					</div>
				</CardContent>
			</Card>
		</div>
	);
}
