import { Card, CardContent } from "@/components/ui/card";
import { useReprofiling } from "@/hooks/use-reprofiling";
import { createFileRoute, redirect } from "@tanstack/react-router";
import { Clock } from "lucide-react";
import { useEffect } from "react";

export const Route = createFileRoute("/_app/reprofiling/done/pending/")({
	component: SubmitReprofilingPage,
	beforeLoad: ({ context }) => {
		const { auth } = context;

		if (!auth.user) {
			throw redirect({ to: "/auth/login" });
		}

		if (!auth.hasPendingIntentFor("DOCTOR")) {
			throw redirect({ to: "/" });
		}
	},
});

export default function SubmitReprofilingPage() {
	const { setCurrentStep } = useReprofiling();

	useEffect(() => {
		setCurrentStep("done");
	}, [setCurrentStep]);

	return (
		<div className="w-full max-w-xl mx-auto flex flex-col items-center justify-center min-h-[70vh] space-y-6 text-center">
			<Card className="w-full">
				<CardContent className="pt-10 pb-10 flex flex-col items-center gap-4">
					<Clock className="w-14 h-14 text-destructive mx-auto" />

					<h1 className="text-xl font-bold">
						Solicitação de perfil em análise
					</h1>

					<p className="text-muted-foreground max-w-md">
						Seu registro foi enviado com sucesso e agora precisa ser revisado
						por um administrador. Você receberá uma notificação por e-mail assim
						que sua solicitação for avaliada. Após a aprovação, seu acesso será
						estabelecido automaticamente de acordo com o perfil solicitado.
					</p>

					<div className="text-sm text-muted-foreground">
						Esse processo pode levar algumas horas ou dias, dependendo do volume
						de solicitações.
					</div>
				</CardContent>
			</Card>
		</div>
	);
}
