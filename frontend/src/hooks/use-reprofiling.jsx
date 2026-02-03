import { ReprofilingContext } from "@/contexts/reprofiling-provider";
import { useContext } from "react";

export function useReprofiling() {
	return useContext(ReprofilingContext);
}
