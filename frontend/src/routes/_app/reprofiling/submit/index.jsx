import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { useAuth } from "@/hooks/use-auth";
import { useReprofiling } from "@/hooks/use-reprofiling";
import { api } from "@/lib/api";
import { getSpecialtyLabel } from "@/utils/specialties";
import {
	hasSelectedProfile,
	validateDoctorSpecificData,
} from "@/utils/validators";
import { createFileRoute, redirect } from "@tanstack/react-router";
import { BriefcaseMedical, CheckCircle } from "lucide-react";
import { toast } from "sonner";

export const Route = createFileRoute("/_app/reprofiling/submit/")({
	component: SubmitReprofilingPage,
	beforeLoad: ({ context }) => {
		const { reprofiling, auth } = context;

		const reprofilingData = reprofiling.reprofilingData;

		if (!hasSelectedProfile(reprofilingData)) {
			throw redirect({ to: "/reprofiling" });
		}

		const doctorValidation = validateDoctorSpecificData(reprofilingData);

		if (doctorValidation.length !== 0) {
			throw redirect({ to: "/reprofiling" });
		}
	},
});

export default function SubmitReprofilingPage() {
	const { revalidate } = useAuth();
	const { reprofilingData, previousStep, nextStep } = useReprofiling();

	async function onNext() {
		const response = await handleFinishRegistration();

		if (!response.success) return false;

		await revalidate();

		await new Promise((resolver) => setTimeout(resolver, 0));

		nextStep(response.data?.status === "IMPLICIT" ? "/handling" : "/pending");
		return true;
	}

	async function handleFinishRegistration() {
		try {
			const response = await api.post(
				"/profiling/profile-intent",
				reprofilingData,
			);

			if (response.status !== 202) return { success: false };

			return { success: true, data: response.data };
		} catch (error) {
			console.error("Error submitting onboarding data:", error);

			toast.error(
				error.response?.data?.message || "Erro ao enviar os dados de cadastro.",
			);

			return { success: false };
		}
	}

	const profile = reprofilingData.profile?.value;
	const specific = reprofilingData.specific;

	return (
		<div className="w-full max-w-4xl mx-auto space-y-6">
			<div className="text-center">
				<h1 className="text-xl font-bold">Revisão final</h1>
				<p className="text-muted-foreground text-sm">
					Confira suas informações antes de concluir o cadastro.
				</p>
			</div>

			{profile === "DOCTOR" && (
				<Card>
					<CardHeader>
						<CardTitle className="flex items-center gap-2">
							<BriefcaseMedical className="text-primary w-5 h-5" />
							Informações profissionais
						</CardTitle>
					</CardHeader>
					<CardContent className="grid grid-cols-1 md:grid-cols-2 gap-2 text-sm">
						<div>
							<strong>Credencial:</strong> {specific?.credential || "—"}
						</div>
						<div>
							<strong>Especialidade:</strong>{" "}
							{getSpecialtyLabel(specific?.specialty) || "—"}
						</div>
					</CardContent>
				</Card>
			)}

			{profile !== "DOCTOR" && (
				<Card>
					<CardHeader>
						<CardTitle className="flex items-center gap-2">
							<CheckCircle className="text-primary w-5 h-5" />
							Atualização de perfil
						</CardTitle>
					</CardHeader>
					<CardContent>
						<p className="text-sm">
							Caso a reprofilagem seja concluída com sucesso, você passará a ter
							<strong> tanto perfil de paciente quanto de médico</strong> na
							plataforma.
						</p>
					</CardContent>
				</Card>
			)}

			<div className="flex justify-between">
				<Button variant="outline" onClick={previousStep}>
					Voltar
				</Button>
				<Button size="lg" onClick={onNext}>
					Finalizar cadastro
				</Button>
			</div>
		</div>
	);
}
