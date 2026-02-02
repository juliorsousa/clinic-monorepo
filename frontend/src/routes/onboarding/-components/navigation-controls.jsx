import { Button } from "@/components/ui/button";
import { useOnboarding } from "@/hooks/use-onboarding";
import { cn } from "@/lib/utils";

export function NavigationControls({
	canProceed,
	preNextHook = async () => true,
	prePreviousHook = async () => true,
}) {
	const { nextStep, previousStep, isFirstStep, isLastStep } = useOnboarding();

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

	return (
		<div
			className={cn(
				"flex mt-2",
				isFirstStep() ? "justify-end" : "justify-between",
			)}
		>
			{!isFirstStep() && (
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
