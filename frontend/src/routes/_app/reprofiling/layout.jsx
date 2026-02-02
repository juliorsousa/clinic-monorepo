import { Card } from "@/components/ui/card";
import { useReprofiling } from "@/hooks/use-reprofiling";
import { cn } from "@/lib/utils";
import {
	Outlet,
	createFileRoute,
	redirect,
	useLocation,
} from "@tanstack/react-router";
import { AnimatePresence, motion } from "framer-motion";
import { useEffect, useRef } from "react";

export const Route = createFileRoute("/_app/reprofiling")({
	component: ReprofilingLayout,
	head: () => ({
		meta: [
			{
				title: "Reperfilação: Perfil | Clínica",
			},
		],
	}),
	beforeLoad: ({ context }) => {
		const { auth } = context;

		if (!auth.isAuthenticated) {
			throw redirect({ to: "/auth/login" });
		}

		if (auth.user?.traits?.includes("MUST_CHANGE_PASSWORD")) {
			throw redirect({ to: "/auth/password" });
		}
	},
});

const STEP_ORDER = ["profile", "personal", "specific", "submit"];

function ReprofilingLayout() {
	const { currentStep, reprofilingData, targetReprofilingRole } =
		useReprofiling();
	const location = useLocation();
	const prevStepRef = useRef(null);

	const steps = [
		{ key: "profile", label: "Perfil", conclusionRate: "17%" },
		{
			key: "specific",
			label: "Informações específicas",
			conclusionRate: "50%",
		},
		{ key: "submit", label: "Finalizar", conclusionRate: "83%" },
		{ key: "done", label: "Concluído", conclusionRate: "100%", hidden: true },
	];

	const progressWidth =
		steps.find((step) => step.key === currentStep)?.conclusionRate || "0%";

	const prevIndex = STEP_ORDER.indexOf(prevStepRef.current ?? currentStep);
	const currentIndex = STEP_ORDER.indexOf(currentStep);

	const direction = currentIndex >= prevIndex ? 1 : -1;

	useEffect(() => {
		prevStepRef.current = currentStep;
	}, [currentStep]);

	return (
		<div className="h-screen w-full flex items-center justify-center bg-muted/30 p-6">
			<Card className="flex flex-row w-full max-w-5xl overflow-hidden rounded-xl shadow-lg p-6">
				<div className="w-full flex flex-col gap-6">
					<div className="w-full">
						<div className="flex justify-between text-xs text-muted-foreground mb-2">
							{steps
								.filter((step) => !step.hidden)
								.map((step, index) => {
									const isCurrentStep = step.key === currentStep;
									const isSpecificStepDisabled =
										reprofilingData?.profile?.value === "PATIENT" &&
										step.key === "specific";

									const isPassed =
										index < steps.findIndex((s) => s.key === currentStep);

									return (
										<span
											className={cn(
												"flex-1 text-center transition-opacity",
												isCurrentStep && "text-primary",
												!isCurrentStep && !isPassed && "opacity-50",
												isSpecificStepDisabled && "opacity-25 line-through",
											)}
											key={step.key}
										>
											{step.label}
										</span>
									);
								})}
						</div>

						<div className="relative h-2 w-full bg-muted rounded-full overflow-hidden">
							<motion.div
								key={currentStep}
								initial={{ width: progressWidth }}
								animate={{ width: progressWidth }}
								transition={{
									duration: 0.35,
									ease: direction === 1 ? "easeOut" : "easeInOut",
								}}
								className="absolute left-0 top-0 h-2 bg-primary"
							/>
						</div>
					</div>

					<AnimatePresence mode="wait">
						<motion.div
							key={location.pathname}
							initial={{ x: 120 * direction, opacity: 0 }}
							animate={{ x: 0, opacity: 1 }}
							exit={{ x: -120 * direction, opacity: 0 }}
							transition={{ duration: 0.28, ease: "easeOut" }}
							className="w-full gap-6"
						>
							<Outlet />
						</motion.div>
					</AnimatePresence>
				</div>
			</Card>
		</div>
	);
}
