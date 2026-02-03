import { useReprofiling } from "@/hooks/use-reprofiling";
import { useNavigate } from "@tanstack/react-router";
import { useEffect, useRef } from "react";

export function ReprofilingRouterBridge() {
	const navigate = useNavigate();
	const reprofiling = useReprofiling();
	const hasSet = useRef(false);

	useEffect(() => {
		if (!hasSet.current) {
			reprofiling._setNavigate?.(navigate);
			hasSet.current = true;
		}
	}, [navigate, reprofiling._setNavigate]);

	return null;
}
