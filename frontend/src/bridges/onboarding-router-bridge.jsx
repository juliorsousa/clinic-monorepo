import { useOnboarding } from "@/hooks/use-onboarding";
import { useNavigate } from "@tanstack/react-router";
import { useEffect, useRef } from "react";

export function OnboardingRouterBridge() {
	const navigate = useNavigate();
	const onboarding = useOnboarding();
	const hasSet = useRef(false);

	useEffect(() => {
		if (!hasSet.current) {
			onboarding._setNavigate?.(navigate);
			hasSet.current = true;
		}
	}, [navigate, onboarding._setNavigate]);

	return null;
}
