import { useAuth } from "@/hooks/use-auth";
import { createContext, useRef, useState } from "react";

export const ReprofilingContext = createContext({});

export function ReprofilingProvider({ children }) {
	const { user, isAuthenticated, isAuthLoading } = useAuth();

	const isReprofilingLoading = isAuthLoading || user === null;
	const routerNavigateRef = useRef(null);

	const STEPS = {
		PROFILE: "profile",
		SPECIFIC: "specific",
		SUBMIT: "submit",
		DONE: "done",
	};

	const [currentStep, setCurrentStep] = useState(STEPS.PROFILE);

	const [reprofilingData, setReprofilingData] = useState({
		profile: {},
		specific: {},
	});

	const hasDoctor = user?.roles?.some((r) => r.role === "DOCTOR");
	const hasPatient = user?.roles?.some((r) => r.role === "PATIENT");

	const isReprofilingEligible =
		isAuthenticated &&
		!!user &&
		hasDoctor !== hasPatient &&
		!user.traits?.includes("PENDING_ONBOARDING");

	const targetReprofilingRole =
		hasDoctor && !hasPatient
			? "PATIENT"
			: hasPatient && !hasDoctor
				? "DOCTOR"
				: null;

	function updateReprofilingData(step, data) {
		setReprofilingData((prev) => ({
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
		goTo(STEPS.PROFILE, "/reprofiling");
	}

	function goToSpecific() {
		goTo(STEPS.SPECIFIC, "/reprofiling/specific");
	}

	function goToSubmit() {
		goTo(STEPS.SUBMIT, "/reprofiling/submit");
	}

	function goToDone(path = "/handling") {
		const correctedPath = path.replace(/^\//, "");
		goTo(STEPS.DONE, `/reprofiling/done/${correctedPath}`);
	}

	function nextStep(path = undefined) {
		if (currentStep === STEPS.PROFILE) {
			if (reprofilingData.profile?.value === "DOCTOR") {
				goToSpecific();
			} else {
				goToSubmit();
			}
		} else if (currentStep === STEPS.SPECIFIC) {
			goToSubmit();
		} else if (currentStep === STEPS.SUBMIT) {
			goToDone(path);
		}
	}

	function previousStep() {
		if (currentStep === STEPS.SPECIFIC) {
			goToProfile();
		} else if (currentStep === STEPS.SUBMIT) {
			if (reprofilingData.profile?.value === "DOCTOR") {
				goToSpecific();
			} else {
				goToProfile();
			}
		}
	}

	function resetReprofiling() {
		setCurrentStep(STEPS.PROFILE);
		setReprofilingData({
			profile: {},
			specific: {},
		});
	}

	return (
		<ReprofilingContext.Provider
			value={{
				_setNavigate,
				currentStep,
				reprofilingData,

				targetReprofilingRole,
				isReprofilingEligible,
				isReprofilingLoading,

				updateReprofilingData,
				resetReprofiling,
				nextStep,
				previousStep,
				setCurrentStep,

				isFirstStep: () => currentStep === STEPS.PROFILE,
				isLastStep: () => currentStep === STEPS.SUBMIT,

				goToProfile,
				goToSpecific,
				goToSubmit,
			}}
		>
			{children}
		</ReprofilingContext.Provider>
	);
}
