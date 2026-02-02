import {
	Card,
	CardContent,
	CardDescription,
	CardHeader,
	CardTitle,
} from "@/components/ui/card";
import { useAuth } from "@/hooks/use-auth";
import { useReprofiling } from "@/hooks/use-reprofiling";
import { cn } from "@/lib/utils";
import { createFileRoute } from "@tanstack/react-router";
import { BriefcaseMedical, CheckCircle, HeartPulse } from "lucide-react";
import { useCallback, useEffect } from "react";
import { ReprofilingNavigationControls } from "./-components/reprofiling-navigation-controls";

export const Route = createFileRoute("/_app/reprofiling/")({
	component: ReprofilingPage,
});

export default function ReprofilingPage() {
	const { user } = useAuth();
	const {
		reprofilingData,
		updateReprofilingData,
		setCurrentStep,
		targetReprofilingRole,
	} = useReprofiling();

	const isDoctorTarget = targetReprofilingRole === "DOCTOR";
	const isPatientTarget = targetReprofilingRole === "PATIENT";
	const shouldLockChoice = !!targetReprofilingRole;

	useEffect(() => {
		setCurrentStep("profile");
	}, [setCurrentStep]);

	useEffect(() => {
		if (
			targetReprofilingRole &&
			reprofilingData?.profile?.value !== targetReprofilingRole
		) {
			handleSetProfile(targetReprofilingRole);
		}
	}, [targetReprofilingRole, reprofilingData?.profile?.value]);

	const handleSetProfile = useCallback(
		(profile) => {
			updateReprofilingData("profile", { value: profile });
		},
		[updateReprofilingData],
	);

	return (
		<>
			<div className="text-center space-y-2">
				<h1 className="text-xl font-bold">Escolha seu novo perfil</h1>
				<p className="text-muted-foreground text-sm">
					Você está cadastrando um novo perfil de uso na sua conta.
				</p>

				<p className="text-center text-xs text-muted-foreground/70">
					Você está logado como: <strong>{user?.email}</strong>
				</p>
			</div>

			<div className="grid grid-cols-1 gap-4 mt-6">
				<Card
					className={cn(
						"transition-all hover:shadow-md",
						reprofilingData?.profile?.value === "DOCTOR" &&
							"ring-2 ring-primary",
						shouldLockChoice &&
							!isDoctorTarget &&
							"opacity-40 blur-[0.3px] pointer-events-none",
						!shouldLockChoice && "cursor-pointer",
					)}
					onClick={() => {
						if (!shouldLockChoice || isDoctorTarget) {
							handleSetProfile("DOCTOR");
						}
					}}
				>
					<CardHeader>
						<CardTitle className="flex items-center justify-between">
							<div className="flex flex-row gap-2 items-center">
								<BriefcaseMedical width={16} height={16} />
								Médico(a)
								{isDoctorTarget && (
									<span className="ml-2 text-xs bg-primary/10 text-primary px-2 py-0.5 rounded-full">
										Recomendado para você
									</span>
								)}
							</div>
							<CheckCircle
								className={cn(
									"h-5 w-5 text-primary transition-opacity",
									reprofilingData?.profile?.value === "DOCTOR"
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
						"transition-all hover:shadow-md",
						reprofilingData?.profile?.value === "PATIENT" &&
							"ring-2 ring-primary",
						shouldLockChoice &&
							!isPatientTarget &&
							"opacity-40 blur-[0.3px] pointer-events-none",
						!shouldLockChoice && "cursor-pointer",
					)}
					onClick={() => {
						if (!shouldLockChoice || isPatientTarget) {
							handleSetProfile("PATIENT");
						}
					}}
				>
					<CardHeader>
						<CardTitle className="flex items-center justify-between">
							<div className="flex flex-row gap-2 items-center">
								<HeartPulse width={16} height={16} />
								Paciente
								{isPatientTarget && (
									<span className="ml-2 text-xs bg-primary/10 text-primary px-2 py-0.5 rounded-full">
										Recomendado para você
									</span>
								)}
							</div>
							<CheckCircle
								className={cn(
									"h-5 w-5 text-primary transition-opacity",
									reprofilingData?.profile?.value === "PATIENT"
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

				<ReprofilingNavigationControls
					canProceed={reprofilingData?.profile?.value !== undefined}
				/>
			</div>
		</>
	);
}
