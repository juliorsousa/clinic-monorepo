import { AuthContext } from "@/contexts/auth-provider";
import { use } from "react";

export function useAuth() {
	return use(AuthContext);
}
