import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { useAuth } from "@/hooks/use-auth";
import { useOnboarding } from "@/hooks/use-onboarding";
import { api } from "@/lib/api";
import { maskCPF } from "@/utils/cpf-utils";
import { maskPhone } from "@/utils/phone-utils";
import { getSpecialtyLabel } from "@/utils/specialties";
import {
	hasSelectedProfile,
	validateCustomerData,
	validateDoctorSpecificData,
} from "@/utils/validators";
import { createFileRoute, redirect } from "@tanstack/react-router";
import {
	BriefcaseMedical,
	CheckCircle,
	ContactRound,
	MapPinHouse,
} from "lucide-react";
import { toast } from "sonner";

export const Route = createFileRoute("/onboarding/submit/")({
	component: SubmitOnboardingPage,
	beforeLoad: ({ context }) => {
		const { onboarding } = context;

		const onboardingData = onboarding.onboardingData;

		if (!hasSelectedProfile(onboardingData)) {
			throw redirect({ to: "/onboarding" });
		}

		if (validateCustomerData(onboardingData).length !== 0) {
			throw redirect({ to: "/onboarding" });
		}

		if (validateDoctorSpecificData(onboardingData).length !== 0) {
			throw redirect({ to: "/onboarding" });
		}
	},
});

export default function SubmitOnboardingPage() {
	const { revalidate } = useAuth();
	const { onboardingData, previousStep, nextStep } = useOnboarding();

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
				onboardingData,
			);

			if (response.status !== 202) return { success: false };

			return { success: true, data: response.data };
		} catch (error) {
			console.error("Error submitting onboarding data:", error);

			toast.error(
				error.data?.message || "Erro ao enviar os dados de cadastro.",
			);

			return { success: false };
		}
	}

	const profile = onboardingData.profile?.value;
	const personal = onboardingData.personal?.personal;
	const address = onboardingData.personal?.address;
	const specific = onboardingData.specific;

	return (
		<div className="w-full max-w-4xl mx-auto space-y-6">
			<div className="text-center">
				<h1 className="text-xl font-bold">Revisão final</h1>
				<p className="text-muted-foreground text-sm">
					Confira suas informações antes de concluir o cadastro.
				</p>
			</div>

			<Card>
				<CardHeader>
					<CardTitle className="flex items-center gap-2">
						<CheckCircle className="text-primary w-5 h-5" />
						Perfil escolhido
					</CardTitle>
				</CardHeader>
				<CardContent>
					<p className="font-medium">
						{profile === "DOCTOR" ? "Médico(a)" : "Paciente"}
					</p>
				</CardContent>
			</Card>

			<Card>
				<CardHeader>
					<CardTitle className="flex items-center gap-2">
						<ContactRound className="text-primary w-5 h-5" />
						Dados pessoais
					</CardTitle>
				</CardHeader>
				<CardContent className="grid grid-cols-1 md:grid-cols-2 gap-2 text-sm">
					<div>
						<strong>Nome:</strong> {personal?.name || "—"}
					</div>
					<div>
						<strong>CPF:</strong> {maskCPF(personal?.document) || "—"}
					</div>
					<div>
						<strong>Telefone:</strong> {maskPhone(personal?.phone) || "—"}
					</div>
				</CardContent>
			</Card>

			<Card>
				<CardHeader>
					<CardTitle className="flex items-center gap-2">
						<MapPinHouse className="text-primary w-5 h-5" />
						Endereço
					</CardTitle>
				</CardHeader>
				<CardContent className="grid grid-cols-1 md:grid-cols-2 gap-2 text-sm">
					<div>
						<strong>Rua:</strong> {address?.street || "—"}
					</div>
					<div>
						<strong>Número:</strong> {address?.house || "—"}
					</div>
					<div>
						<strong>Complemento:</strong> {address?.complement || "—"}
					</div>
					<div>
						<strong>Bairro:</strong> {address?.neighborhood || "—"}
					</div>
					<div>
						<strong>Cidade:</strong> {address?.city || "—"}
					</div>
					<div>
						<strong>Estado:</strong> {address?.state || "—"}
					</div>
					<div>
						<strong>CEP:</strong> {address?.zipCode || "—"}
					</div>
				</CardContent>
			</Card>

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
