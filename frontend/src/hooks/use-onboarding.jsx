import { OnboardingContext } from "@/contexts/onboarding-provider";
import { useContext } from "react";

export function useOnboarding() {
	return useContext(OnboardingContext);
}
