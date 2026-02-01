import {
	Card,
	CardContent,
	CardDescription,
	CardHeader,
	CardTitle,
} from "@/components/ui/card";
import { useAuth } from "@/hooks/use-auth";
import { useOnboarding } from "@/hooks/use-onboarding";
import { cn } from "@/lib/utils";
import { createFileRoute } from "@tanstack/react-router";
import { BriefcaseMedical, CheckCircle, HeartPulse } from "lucide-react";
import { useEffect } from "react";
import { NavigationControls } from "./-components/navigation-controls";

export const Route = createFileRoute("/onboarding/")({
	component: ProfileOnboardingPage,
});

export default function ProfileOnboardingPage() {
	const { user } = useAuth();
	const { onboardingData, updateStepData, setCurrentStep } = useOnboarding();

	useEffect(() => {
		setCurrentStep("profile");
	}, [setCurrentStep]);

	function handleSetProfile(profile) {
		updateStepData("profile", { value: profile });
	}

	return (
		<>
			<div className="text-center space-y-2">
				<h1 className="text-xl font-bold">Escolha seu perfil</h1>
				<p className="text-muted-foreground text-sm">
					Isso define como será sua experiência inicial na plataforma.
				</p>
				<p className="text-center text-xs text-muted-foreground/70">
					Você está logado como: <strong>{user?.email}</strong>
				</p>
			</div>

			<div className="grid grid-cols-1 gap-4 mt-6">
				<Card
					className={cn(
						"cursor-pointer transition-all hover:shadow-md",
						onboardingData?.profile?.value === "DOCTOR" &&
							"ring-2 ring-primary",
					)}
					onClick={() => handleSetProfile("DOCTOR")}
				>
					<CardHeader>
						<CardTitle className="flex items-center justify-between">
							<div className="flex flex-row gap-1">
								<BriefcaseMedical width={16} height={16} />
								Médico(a)
							</div>
							<CheckCircle
								className={cn(
									"h-5 w-5 text-primary transition-opacity",
									onboardingData?.profile?.value === "DOCTOR"
										? "opacity-100"
										: "opacity-0",
								)}
							/>
						</CardTitle>
						<CardDescription>
							Gerencie seus pacientes, consultas e prontuários de forma prática
							e segura.
						</CardDescription>
					</CardHeader>
				</Card>

				<Card
					className={cn(
						"cursor-pointer transition-all hover:shadow-md",
						onboardingData?.profile?.value === "PATIENT" &&
							"ring-2 ring-primary",
					)}
					onClick={() => handleSetProfile("PATIENT")}
				>
					<CardHeader>
						<CardTitle className="flex items-center justify-between">
							<div className="flex flex-row gap-1">
								<HeartPulse width={16} height={16} />
								Paciente
							</div>
							<CheckCircle
								className={cn(
									"h-5 w-5 text-primary transition-opacity",
									onboardingData?.profile?.value === "PATIENT"
										? "opacity-100"
										: "opacity-0",
								)}
							/>
						</CardTitle>
						<CardDescription>
							Agende consultas, acompanhe seu histórico de saúde e receba
							orientações personalizadas.
						</CardDescription>
					</CardHeader>
				</Card>

				<p className="text-center text-xs text-muted-foreground">
					Não se preocupe — você pode se cadastrar como outro perfil depois, se
					quiser.
				</p>

				<NavigationControls
					canProceed={onboardingData?.profile?.value !== undefined}
				/>
			</div>
		</>
	);
}
