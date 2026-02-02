import { Button } from "@/components/ui/button";
import { useReprofiling } from "@/hooks/use-reprofiling";
import { cn } from "@/lib/utils";
import { useNavigate } from "@tanstack/react-router";

export function ReprofilingNavigationControls({
	canProceed,
	preNextHook = async () => true,
	prePreviousHook = async () => true,
}) {
	const navigate = useNavigate();
	const { nextStep, previousStep, isFirstStep, isLastStep } = useReprofiling();

	const handleNext = async () => {
		const canProceed = await preNextHook();

		if (canProceed) {
			nextStep();
		}
	};

	const handlePrevious = async () => {
		const canProceed = await prePreviousHook();

		if (canProceed) {
			previousStep();
		}
	};

	const handleCancel = () => {
		navigate({ to: "/" });
	};

	return (
		<div className={cn("flex mt-2", "justify-between")}>
			{isFirstStep ? (
				<Button variant="outline" onClick={handleCancel}>
					Cancelar
				</Button>
			) : (
				<Button variant="outline" onClick={handlePrevious}>
					Voltar
				</Button>
			)}

			{!isLastStep() && (
				<Button onClick={handleNext} disabled={!canProceed} type="submit">
					Próximo
				</Button>
			)}
		</div>
	);
}
