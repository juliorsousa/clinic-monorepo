import { useAuth } from "@/hooks/use-auth";
import { createContext, useRef, useState } from "react";

export const OnboardingContext = createContext({});

export function OnboardingProvider({ children }) {
	const { user, isAuthenticated, isAuthLoading } = useAuth();

	const isOnboardingLoading = isAuthLoading || user === null;

	const routerNavigateRef = useRef(null);

	const STEPS = {
		PROFILE: "profile",
		PERSONAL: "personal",
		SPECIFIC: "specific",
		SUBMIT: "submit",
		DONE: "done",
	};

	const [currentStep, setCurrentStep] = useState(STEPS.PROFILE);

	const [onboardingData, setOnboardingData] = useState({
		profile: {},
		personal: {},
		specific: {},
	});

	const isOnboardingEligible =
		isAuthenticated && user && user.traits?.includes("PENDING_ONBOARDING");

	function updateStepData(step, data) {
		setOnboardingData((prev) => ({
			...prev,
			[step]: {
				...prev[step],
				...data,
			},
		}));
	}

	function _setNavigate(nav) {
		routerNavigateRef.current = nav;
	}

	function goTo(step, path) {
		if (!routerNavigateRef.current) {
			console.warn("Router navigate function is not set yet.");
			return;
		}

		setCurrentStep(step);
		routerNavigateRef.current({ to: path });
	}

	function goToProfile() {
		goTo(STEPS.PROFILE, "/onboarding");
	}

	function goToPersonal() {
		goTo(STEPS.PERSONAL, "/onboarding/personal");
	}

	function goToSpecific() {
		goTo(STEPS.SPECIFIC, "/onboarding/specific");
	}

	function goToSubmit() {
		goTo(STEPS.SUBMIT, "/onboarding/submit");
	}

	function goToDone(path = "/handling") {
		const correctedPath = path.replace(/^\//, "");

		goTo(STEPS.DONE, `/onboarding/done/${correctedPath}`);
	}

	function nextStep(path = undefined) {
		console.log(" ==== GOING TO NEXT STEP =====");
		if (currentStep === STEPS.PROFILE) goToPersonal();
		else if (currentStep === STEPS.PERSONAL) {
			if (onboardingData.profile?.value === "DOCTOR") goToSpecific();
			else goToSubmit();
		} else if (currentStep === STEPS.SPECIFIC) goToSubmit();
		else if (currentStep === STEPS.SUBMIT) goToDone(path);
	}

	function previousStep() {
		if (currentStep === STEPS.PERSONAL) goToProfile();
		else if (currentStep === STEPS.SPECIFIC) goToPersonal();
		else if (currentStep === STEPS.SUBMIT) {
			if (onboardingData.profile?.value === "DOCTOR") goToSpecific();
			else goToPersonal();
		}
	}

	function resetOnboarding() {
		setCurrentStep(STEPS.PROFILE);
		setOnboardingData({
			profile: {},
			personal: {},
			specific: {},
		});
	}

	return (
		<OnboardingContext.Provider
			value={{
				_setNavigate,
				currentStep,
				onboardingData,
				isOnboardingEligible,
				isOnboardingLoading,
				updateStepData,
				resetOnboarding,
				nextStep,
				previousStep,
				setCurrentStep,
				isFirstStep: () => currentStep === STEPS.PROFILE,
				isLastStep: () => currentStep === STEPS.SUBMIT,
				goToProfile,
				goToPersonal,
				goToSpecific,
				goToSubmit,
			}}
		>
			{children}
		</OnboardingContext.Provider>
	);
}
