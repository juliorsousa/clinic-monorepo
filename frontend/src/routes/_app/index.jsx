import { useAuth } from "@/hooks/use-auth";
import { createFileRoute } from "@tanstack/react-router";

export const Route = createFileRoute("/_app/")({
	component: () => {
		const { user } = useAuth();

		return (
			<div className="flex flex-col">
				<span>Welcome, {user?.email}!</span>

				{user?.roles.map((role) => (
					<span key={role} className="text-muted-foreground">
						{role.role}
					</span>
				))}
				<span className="text-muted-foreground">{user?.traits.join(", ")}</span>
				<span className="text-red-300">{user?.id}</span>
			</div>
		);
	},
});
