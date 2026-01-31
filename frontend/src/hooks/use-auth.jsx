import { AuthContext } from "@/contexts/auth-provider";
import { useContext } from "react";

export function useAuth() {
	return useContext(AuthContext);
}
